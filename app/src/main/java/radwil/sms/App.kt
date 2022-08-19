package radwil.sms

import android.annotation.SuppressLint
import android.app.Application
import android.content.res.Resources
import android.view.ContextThemeWrapper
import radwil.sms.db.ObjectBox


class App: Application() {
   companion object {
      @SuppressLint("StaticFieldLeak")
      lateinit var instance: App

      @SuppressLint("StaticFieldLeak")
      lateinit var themedInstance: ContextThemeWrapper

      @SuppressLint("StaticFieldLeak")
      lateinit var resources: Resources
   }

   init {
      instance = this
      themedInstance = ContextThemeWrapper(instance, R.style.AppTheme)
   }

   override fun onCreate() {
      super.onCreate()
      ObjectBox.init()
      App.resources = resources
   }
}