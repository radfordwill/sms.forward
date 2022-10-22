package com.radwil.sms.db

import com.radwil.sms.App
import com.radwil.sms.model.Settings
import com.radwil.sms.model.SmsConfig
import com.radwil.sms.model.SmsLog
import com.radwil.sms.model.SmtpConfig
import com.radwil.sms.model.*
import io.objectbox.Box
import io.objectbox.BoxStore
import io.objectbox.kotlin.boxFor


object ObjectBox {
   lateinit var store: BoxStore private set

   lateinit var smsConfigBox: Box<SmsConfig> private set
   lateinit var smtpConfigBox: Box<SmtpConfig> private set

   lateinit var logsBox: Box<SmsLog> private set
   lateinit var settingsBox: Box<Settings> private set

   fun init() {
      store = MyObjectBox.builder()
         .androidContext(App.instance)
         .build()

      smsConfigBox = store.boxFor()
      smtpConfigBox = store.boxFor()

      logsBox = store.boxFor()
      settingsBox = store.boxFor()
      if (settingsBox.all.isEmpty()) settingsBox.put(Settings())
   }
}