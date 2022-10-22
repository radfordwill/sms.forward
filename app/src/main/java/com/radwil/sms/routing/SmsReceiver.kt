package com.radwil.sms.routing

import android.annotation.TargetApi
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.SmsMessage
import com.radwil.sms.model.Repository
import com.radwil.sms.utils.log


class SmsReceiver: BroadcastReceiver() {
   private val smsSender = SmsSender()
   private val smtpSender = SmtpSender()

   @TargetApi(Build.VERSION_CODES.M)
   override fun onReceive(context: Context, intent: Intent) {
      val format = intent.getStringExtra("format")
      val pdus = intent.extras?.get("pdus")

      if (pdus !is Array<*> || pdus.isEmpty()) return

      val messages = pdus.filterIsInstance<ByteArray>().map {
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            SmsMessage.createFromPdu(it, format)
         } else {
            @Suppress("DEPRECATION")
            SmsMessage.createFromPdu(it)
         }
      }

      messages.forEach { message ->
         log(
            "received message from ${message.originatingAddress}" +
                  ": ${if (Repository.settings.showMessageInLogs) message.messageBody else ""}"
         )
         smsSender.forwardMessage(message)
         smtpSender.forwardMessage(message)
      }
   }
}