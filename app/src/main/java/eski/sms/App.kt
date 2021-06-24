package eski.sms

import android.annotation.SuppressLint
import android.app.Application
import android.view.ContextThemeWrapper
import eski.sms.db.ObjectBox


class App: Application() {
   companion object {
      @SuppressLint("StaticFieldLeak")
      lateinit var instance: App

      @SuppressLint("StaticFieldLeak")
      lateinit var themedInstance: ContextThemeWrapper
   }

   init {
      instance = this
      themedInstance = ContextThemeWrapper(instance, R.style.AppTheme)
   }

   override fun onCreate() {
      super.onCreate()
      ObjectBox.init()
   }
}