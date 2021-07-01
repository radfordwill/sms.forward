package eski.sms.routing

import android.telephony.SmsMessage
import eski.sms.App
import eski.sms.R
import eski.sms.model.Repository
import eski.sms.model.SmtpConfig
import eski.sms.utils.log
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage


class SmtpSender {
   companion object {
      val executor: ExecutorService = Executors.newFixedThreadPool(2)
   }

   fun forwardMessage(message: SmsMessage) {
      Repository.smtpConfigs
         .filter { it.passesForwardingRequirements(message.originatingAddress) }
         .forEach { config ->

            val properties = Properties().apply {
               put("mail.smtp.auth", "true")
               put("mail.smtp.starttls.enable", "true")
               put("mail.smtp.host", config.host)
               put("mail.smtp.port", config.port.toString())
            }

            val session = Session.getInstance(properties, authenticator(config.username, config.password))

            val mimeMessage = MimeMessage(session).apply {
               setFrom(InternetAddress(config.fromAddress))
               setRecipients(Message.RecipientType.TO, InternetAddress.parse(config.forwardAddress))

               subject = config.subject
               setText(formatMessage(message, config))
            }

            executor.execute {
               try {
                  Transport.send(mimeMessage)
               } catch (error: MessagingException) {
                  log("${error::class.java.simpleName} thrown while trying to send an SMTP message: ${error.message}")
               }
            }
         }
   }

   private fun authenticator(username: String, password: String) = object: Authenticator() {
      override fun getPasswordAuthentication(): PasswordAuthentication = PasswordAuthentication(username, password)
   }

   private fun formatMessage(message: SmsMessage, config: SmtpConfig): String {
      val builder = StringBuilder()

      if (config.includeSenderNumber) builder.append(App.instance.getString(R.string.sentFrom, message.originatingAddress) + "\n")
      builder.append(message.messageBody)

      return builder.toString()
   }
}