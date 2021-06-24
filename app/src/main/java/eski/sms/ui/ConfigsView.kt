package eski.sms.ui

import android.view.View
import android.widget.LinearLayout
import eski.sms.R
import eski.sms.model.Repository
import eski.sms.model.SmsConfig
import eski.sms.model.SmtpConfig


class ConfigsView(root: View) {
   val container = root.findViewById<LinearLayout>(R.id.configurationsContainer)

   init {
      root.findViewById<View>(R.id.addSmsConfigButton).setOnClickListener { addSmsConfig() }
      root.findViewById<View>(R.id.addSmtpConfigButton).setOnClickListener { addSmtpConfig() }

      Repository.smsConfigs.forEach { addSmsConfig(it) }
   }

   fun addSmsConfig(config: SmsConfig? = null) {
      if (config == null) {
         val newConfig = SmsConfig()
         Repository.updateConfig(newConfig)
         container.addView(SmsConfigView.fromConfig(newConfig))
      } else {
         container.addView(SmsConfigView.fromConfig(config))
      }
   }

   fun addSmtpConfig(config: SmtpConfig? = null) {

   }
}