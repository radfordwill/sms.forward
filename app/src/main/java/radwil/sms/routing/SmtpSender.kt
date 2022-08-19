package radwil.sms.routing

import android.telephony.SmsMessage
import radwil.sms.App
import radwil.sms.R
import radwil.sms.model.Repository
import radwil.sms.model.SmtpConfig
import radwil.sms.utils.log
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

               subject = config.subject+message.originatingAddress
               setText(formatMessage(message, config))
            }
            log(
               "attempting to forward a message from ${message.originatingAddress} to ${config.forwardAddress}" +
                     if (Repository.settings.showMessageInLogs) ": ${formatMessage(message, config)}" else ""
            )

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