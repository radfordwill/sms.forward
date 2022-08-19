package radwil.sms.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.switchmaterial.SwitchMaterial
import radwil.sms.R
import radwil.sms.model.Repository
import kotlinx.android.synthetic.main.activity_main.*

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

         setOnCheckedChangeListener { buttonView, isChecked ->
            settings.logsEnabled = isChecked
            Repository.updateSettings(settings)
         }
      }

      findViewById<SwitchMaterial>(R.id.settingsLogcatEnabledSwitch).apply {
         isChecked = settings.logcatEnabled

         setOnCheckedChangeListener { buttonView, isChecked ->
            settings.logcatEnabled = isChecked
            Repository.updateSettings(settings)
         }
      }

      findViewById<SwitchMaterial>(R.id.settingsShowMessageInLogsSwitch).apply {
         isChecked = settings.showMessageInLogs

         setOnCheckedChangeListener { buttonView, isChecked ->
            settings.showMessageInLogs = isChecked
            Repository.updateSettings(settings)
         }
      }
   }
}