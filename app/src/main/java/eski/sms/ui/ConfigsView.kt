package eski.sms.ui

import android.view.View
import android.widget.LinearLayout
import eski.sms.R
import eski.sms.model.Config
import eski.sms.model.Repository
import eski.sms.model.SmsConfig
import eski.sms.model.SmtpConfig


class ConfigsView(root: View) {
   private val container: LinearLayout = root.findViewById(R.id.configurationsContainer)

   init {
      Repository.smsConfigs.forEach { addConfigView(it) }
      Repository.smtpConfigs.forEach { addConfigView(it) }

      root.findViewById<View>(R.id.addSmsConfigButton).setOnClickListener {
         val newConfig = SmsConfig().apply { Repository.updateConfig(this) }
         addConfigView(newConfig)
      }

      root.findViewById<View>(R.id.addSmtpConfigButton).setOnClickListener {
         val newConfig = SmtpConfig().apply { Repository.updateConfig(this) }
         addConfigView(newConfig)
      }
   }

   private fun addConfigView(config: Config) = container.addView(ConfigView.fromConfig(config))
}