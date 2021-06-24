package eski.sms.db

import eski.sms.App
import eski.sms.model.MyObjectBox
import eski.sms.model.SmsConfig
import eski.sms.model.SmtpConfig
import io.objectbox.Box
import io.objectbox.BoxStore
import io.objectbox.kotlin.boxFor


object ObjectBox {
   lateinit var store: BoxStore private set

   lateinit var smsConfigBox: Box<SmsConfig> private set
   lateinit var smtpConfigBox: Box<SmtpConfig> private set

   fun init() {
      store = MyObjectBox.builder()
         .androidContext(App.instance)
         .build()

      smsConfigBox = store.boxFor()
   }
}