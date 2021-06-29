package eski.sms.routing

import android.telephony.PhoneNumberUtils
import android.telephony.SmsManager
import android.telephony.SmsMessage
import eski.sms.model.Repository
import eski.sms.model.SmsConfig
import eski.sms.utils.log


class SmsSender {
   private val manager: SmsManager = SmsManager.getDefault()

   fun forwardMessage(message: SmsMessage) {
      Repository.smsConfigs
         .filter { it.enabled }
         .filter { it.validate() }
         .forEach configLoop@{ config ->
            when {
               config.numberFilters.isEmpty() -> {
                  forwardMessage(message, config)
               }
               config.blockNumberFilters -> {
                  config.numberFilters.forEach {
                     if (PhoneNumberUtils.compare(it, message.originatingAddress)) {
                        return@configLoop
                     }
                  }
               }
               else -> {
                  config.numberFilters.forEach {
                     if (PhoneNumberUtils.compare(it, message.originatingAddress)) {
                        forwardMessage(message, config)
                        return@configLoop
                     }
                  }
               }
            }
      }
   }

   private fun forwardMessage(message: SmsMessage, config: SmsConfig) {
      log(
         "attempting to send the following message from ${message.originatingAddress} to ${config.forwardNumber}: " +
               formatMessage(message, config)
      )

      manager.sendTextMessage(config.forwardNumber, null, formatMessage(message, config), null, null)
   }

   private fun formatMessage(message: SmsMessage, config: SmsConfig): String {
      val builder = StringBuilder()

      if (config.includeSenderNumber) builder.append("${message.originatingAddress}:\n")
      if (config.includeSubjectLine) builder.append("${config.subjectLine}\n")
      builder.append(message.messageBody)

      return builder.toString()
   }
}