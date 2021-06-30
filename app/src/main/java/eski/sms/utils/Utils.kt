package eski.sms.utils

import android.util.Log
import eski.sms.model.Repository
import eski.sms.model.SmsLog


fun Any.log(message: String) {
   if (Repository.settings.logcatEnabled) Log.d("sms->logs", message)
   if (Repository.settings.logsEnabled) Repository.addLog(SmsLog(message = message))
}