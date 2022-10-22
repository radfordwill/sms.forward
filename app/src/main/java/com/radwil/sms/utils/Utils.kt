package com.radwil.sms.utils

import android.util.Log
import com.radwil.sms.model.Repository
import com.radwil.sms.model.SmsLog


fun log(message: String) {
   if (Repository.settings.logcatEnabled) Log.d("sms->logs", message)
   if (Repository.settings.logsEnabled) Repository.addLog(SmsLog(message = message))
}