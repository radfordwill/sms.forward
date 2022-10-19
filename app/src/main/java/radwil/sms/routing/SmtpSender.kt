package radwil.sms.routing

import android.os.Build
import android.os.Environment
import android.telephony.SmsMessage
import androidx.annotation.RequiresApi
import com.alexstyl.contactstore.ContactColumn
import com.alexstyl.contactstore.ContactPredicate
import com.alexstyl.contactstore.ContactStore
import radwil.sms.App
import radwil.sms.R
import radwil.sms.model.Repository
import radwil.sms.model.SmtpConfig
import radwil.sms.utils.log
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.activation.DataHandler
import javax.activation.FileDataSource
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart
import radwil.sms.utils.GmailUtils


/**
 * This class is used to send the email with javamail smtp from the receiver classes
 */
class SmtpSender {
   companion object {
      val executor: ExecutorService = Executors.newFixedThreadPool(3)
   }

   fun forwardMessage(message: SmsMessage) {

      Repository.smtpConfigs
         .filter { it.passesForwardingRequirements(message.originatingAddress) }
         .forEach { config ->
            val properties = Properties().apply {
               put("mail.smtp.auth", "true")
               put("mail.smtp.host", config.host)
               put("mail.smtp.port", config.port.toString())

               when (config.protocol) {
                  SmtpConfig.Protocol.SSL -> put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
                  SmtpConfig.Protocol.TLS -> {
                  }
                  SmtpConfig.Protocol.STARTTLS -> put("mail.smtp.starttls.enable", "true")
               }
            }
            val session = Session.getInstance(properties, authenticator(config.username, config.password))
            val mimeMessage = MimeMessage(session).apply {
               setFrom(InternetAddress(config.fromAddress))
               setRecipients(Message.RecipientType.TO, InternetAddress.parse(config.forwardAddress))

               subject = config.subject
               // create subject with contact name lookup if available
               val builder = StringBuilder()
               val store = ContactStore.newInstance(App.instance)
               store.fetchContacts(
                  predicate = ContactPredicate.PhoneLookup(message.originatingAddress.toString()),
                  columnsToFetch = listOf(
                     ContactColumn.Names,
                     ContactColumn.Phones
                  )
               )
                  .collect { contacts ->
                     // grab the first contact matched
                     val contact = contacts.firstOrNull()
                     subject = if (contact == null) {
                        log("Contact not found")
                        val subjectAppended = " $subject ("+message.originatingAddress.toString()+")"
                        builder.append(subjectAppended)
                        subjectAppended
                     } else {
                        log("Contact found: ${contact.displayName}")
                        val contactDName = contact.displayName
                        val subjectAppended = " $subject ($contactDName)"
                        builder.append(subjectAppended)
                        subjectAppended
                     }
                  }
               setText(formatMessage(message, config = config))
            }

            log(
               "attempting to forward a message from ${message.originatingAddress} to ${config.forwardAddress}" +
                     if (Repository.settings.showMessageInLogs) ": ${formatMessage(message, config = config)}" else ""
            )

            executor.execute {
               try {
                  Transport.send(mimeMessage)
               } catch (error: MessagingException) {
                  log("${error::class.java.simpleName} thrown while trying to send an SMTP message: ${error.message}")
               }
            }
         }//foreach end
   }

   @RequiresApi(Build.VERSION_CODES.O)
   fun forwardMessageMms(message: String, originatingAddress: String, filenames: List<String>) {
      Repository.smtpConfigs
         .filter { it.passesForwardingRequirements(originatingAddress) }
         .forEach { config ->
            val properties = Properties().apply {
               put("mail.smtp.auth", "true")
               put("mail.smtp.host", config.host)
               put("mail.smtp.port", config.port.toString())
               when (config.protocol) {
                  SmtpConfig.Protocol.SSL -> put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
                  SmtpConfig.Protocol.TLS -> {
                  }
                  SmtpConfig.Protocol.STARTTLS -> put("mail.smtp.starttls.enable", "true")
               }
            }

            val session = Session.getInstance(properties, authenticator(config.username, config.password))
            val mimeMessage = MimeMessage(session).apply {
               setFrom(InternetAddress(config.fromAddress))
               setRecipients(Message.RecipientType.TO, InternetAddress.parse(config.forwardAddress))

               subject = config.subject

               // create subject with contact name lookup if available
               val builder = StringBuilder()
               val store = ContactStore.newInstance(App.instance)
               store.fetchContacts(
                  predicate = ContactPredicate.PhoneLookup(originatingAddress),
                  columnsToFetch = listOf(
                     ContactColumn.Names,
                     ContactColumn.Phones
                  )
               )
                  .collect { contacts ->
                     // grab the first contact matched
                     val contact = contacts.firstOrNull()
                     subject = if (contact == null) {
                        log("Contact not found")
                        val subjectAppended = " $subject ($originatingAddress)"
                        builder.append(subjectAppended)
                        subjectAppended
                     } else {
                        log("Contact found: ${contact.displayName}")
                        val contactDName = contact.displayName
                        val subjectAppended = " $subject ($contactDName)"
                        builder.append(subjectAppended)
                        subjectAppended
                     }
                  }

               // create multipart message body
               val multipart: Multipart = MimeMultipart()

               // create bodypart with image and set content-id
               log("Filenames strings 2 = $filenames")
               val externalStorageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) //getting external storage directory
               val folder = File("$externalStorageDirectory/smsgoforward/temp//")
               log("Filenames strings 3 = $filenames")

               // string of filenames
               var i = 0
               val sb = StringBuilder()
               while (i < filenames.size) {
                  val cid = UUID.randomUUID().toString()
                  log(filenames[i])
                  val imagePart = MimeBodyPart()
                  val filename = filenames[i]
                  val file = "$folder/$filename"
                  val source = FileDataSource(File(file))
                  val ext = filename.substring(filename.lastIndexOf(".")+1)
                  //create body parts with html content and reference to the content-id
                  imagePart.dataHandler = DataHandler(source)
                  imagePart.fileName = filename
                  imagePart.disposition = MimeBodyPart.INLINE
                  // attach the image file
                  imagePart.attachFile(file, "image/$ext", "base64")
                  // content id(s)
                  imagePart.contentID = "<$cid>"
                  val htmlText = htmlText(cid, height = 320, width = 300)
                  //log("Html =  = $htmlText")
                  sb.append(htmlText)
                  multipart.addBodyPart(imagePart)
                  i++
               }

               val textPart = MimeBodyPart()
               val messageFormat = formatMessageMms(message, originatingAddress, config)
               // create body parts with html content and reference to the content-id
               //textPart.setContent(messageFormat, "text/html")
               //TODO:
               // this is not really the way to set both the text and image content
               textPart.setText(messageFormat + sb.toString(), "utf-8", "html")
               multipart.addBodyPart(textPart)
               // add multipart to message
               setContent(multipart)
            }

            log(
               "attempting to forward a message from $originatingAddress to ${config.forwardAddress}" +
                     if (Repository.settings.showMessageInLogs) ": ${formatMessageMms(message, originatingAddress, config)}" else ""
            )

            executor.execute {
               try {
                  Transport.send(mimeMessage)
               } catch (error: MessagingException) {
                  log("${error::class.java.simpleName} thrown while trying to send an SMTP message: ${error.message}")
               }
               // delete image files
               deleteFilesRec(filenames)
            }
         }//foreach end
   }

   /**
    * This class returns the authentication object for smtp
    */
   private fun authenticator(username: String, password: String) = object: Authenticator() {
      override fun getPasswordAuthentication(): PasswordAuthentication = PasswordAuthentication(username, password)
   }

   /**
    * This class is used to format the email message body from sms data
    */
   private fun formatMessage(message: SmsMessage, config: SmtpConfig): String {
      val builder = StringBuilder()

      if (config.includeSenderNumber) builder.append(App.instance.getString(R.string.sentFrom, message.originatingAddress) + "\n")
      builder.append(message.messageBody)

      return builder.toString()
   }

   /**
    * This class is used to format the email message body from mms data
    */
   private fun formatMessageMms(message: String, originatingAddress: String = "", config: SmtpConfig): String {
      val builder = StringBuilder()

      if (config.includeSenderNumber) builder.append(App.instance.getString(R.string.sentFrom, originatingAddress) + "\n")
      builder.append(message)

      return builder.toString()
   }

   /**
    * This class is used to return file names in an array list
    */
   private fun filenamesToArray(filenames: List<String>): List<String> {
      return filenames.toTypedArray().filter { it.isNotBlank() }
   }

   /**
    * This class returns the inline image html of the email body
    */
   private fun htmlText(cid: String, height: Int, width: Int): String {
      return "<div valign=\"top\"><img height=\"$height\" width=\"$width\" src=\"cid:$cid\"></div>"
   }

   /**
    * This class recursively deletes image files that were saved to the temp folder
    */
   @RequiresApi(Build.VERSION_CODES.O)
   private fun deleteFilesRec(filenames: List<String>) {
   try {
      log("Filenames delete strings 1 = $filenames")
      val externalStorageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) //getting external storage directory
      val folder = File("$externalStorageDirectory/smsgoforward/temp//")
      val filenamesplit = filenamesToArray(filenames)
      log("Filenames delete strings 2 = $filenamesplit")
      var i = 0
      while (i < filenamesplit.size) {
         log(filenamesplit[i])
         val filename = filenamesplit[i]
         val file = Paths.get("$folder/$filename")

         val result = Files.deleteIfExists(file)
         if (result) {
            log("Deletion succeeded.")
         } else {
            log("Deletion failed.")
         }
         i++
      }
   } catch (error: IOException) {
      log("${error::class.java.simpleName} thrown while trying to clean up files from an SMTP message: ${error.message}")
   }
   }

}




