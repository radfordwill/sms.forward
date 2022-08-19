package radwil.sms.routing

import android.telephony.SmsManager
import android.telephony.SmsMessage
import radwil.sms.App
import radwil.sms.R
import radwil.sms.model.Repository
import radwil.sms.model.SmsConfig
import radwil.sms.utils.log


class SmsSender {
   private val manager: SmsManager = SmsManager.getDefault()

   fun forwardMessage(message: SmsMessage) {
      Repository.smsConfigs
         .filter { it.passesForwardingRequirements(message.originatingAddress) }
         .filterNot { it.forwardNumber == message.originatingAddress }
         .forEach { config ->
            log(
               "attempting to forward a message from ${message.originatingAddress} to ${config.forwardNumber}" +
                     if (Repository.settings.showMessageInLogs) ": ${formatMessage(message, config)}" else ""
            )

            manager.sendTextMessage(config.forwardNumber, null, formatMessage(message, config), null, null)
         }
   }

   private fun formatMessage(message: SmsMessage, config: SmsConfig): String {
      val builder = StringBuilder()

      if (config.includeSubjectLine) builder.append("${config.subjectLine}\n")
      if (config.includeSenderNumber) builder.append(App.instance.getString(R.string.sentFrom, message.originatingAddress) + "\n")
      builder.append(message.messageBody)

      return builder.toString()
   }
}