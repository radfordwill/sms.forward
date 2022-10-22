package com.radwil.sms.ui

import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.switchmaterial.SwitchMaterial
import kotlinx.android.synthetic.main.activity_main.*
import radwil.sms.R
import com.radwil.sms.model.Repository
import com.radwil.sms.utils.GmailUtilsStep


class SettingsActivity: AppCompatActivity() {
   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      setContentView(R.layout.settings)
      setSupportActionBar(toolbar)
   }

   override fun onStart() {
      super.onStart()
      val settings = Repository.settings

      findViewById<SwitchMaterial>(R.id.settingsLogsEnabledSwitch).apply {
         isChecked = settings.logsEnabled

         setOnCheckedChangeListener { _, isChecked ->
            settings.logsEnabled = isChecked
            Repository.updateSettings(settings)
         }
      }

      findViewById<SwitchMaterial>(R.id.settingsLogcatEnabledSwitch).apply {
         isChecked = settings.logcatEnabled

         setOnCheckedChangeListener { _, isChecked ->
            settings.logcatEnabled = isChecked
            Repository.updateSettings(settings)
         }
      }

      findViewById<SwitchMaterial>(R.id.settingsShowMessageInLogsSwitch).apply {
         isChecked = settings.showMessageInLogs

         setOnCheckedChangeListener { _, isChecked ->
            settings.showMessageInLogs = isChecked
            Repository.updateSettings(settings)
         }
      }

      findViewById<Button>(R.id.GmailUtils).apply {
         setOnClickListener{
            val policy = ThreadPolicy.Builder().permitAll().build()
      StrictMode.setThreadPolicy(policy)
      GmailUtilsStep().runGmailUtils(arrayOf("inbox"))
   }
      }

}
}