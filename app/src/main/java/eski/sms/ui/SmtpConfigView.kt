package eski.sms.ui

import android.content.Context
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import androidx.core.widget.addTextChangedListener
import eski.sms.App
import eski.sms.R
import eski.sms.model.Repository
import eski.sms.model.SmtpConfig


class SmtpConfigView @JvmOverloads constructor(
   context: Context = App.instance,
   attrs: AttributeSet? = null,
   defStyle: Int = 0
): LinearLayout(context, attrs, defStyle) {

   private lateinit var config: SmtpConfig

   companion object {
      fun fromConfig(config: SmtpConfig): SmtpConfigView {
         val contextTheme = ContextThemeWrapper(App.instance, R.style.AppTheme)
         val view = View.inflate(contextTheme, R.layout.smtp_config, null) as SmtpConfigView
         view.init(config)

         return view
      }
   }

   private fun init(config: SmtpConfig) {
      this.config = config

      setupHostField()
      setupPortField()
      setupUsernameField()
      setupPasswordField()
      setupFromAddressField()
      setupForwardAddressField()
      setupSubjectLineField()
   }

   private fun setupHostField() {
      findViewById<EditText>(R.id.smtpConfigHost).apply {
         setText(config.host)

         addTextChangedListener {
            config.host = it.toString()
            Repository.updateConfig(config)
         }
      }
   }

   private fun setupPortField() {
      findViewById<EditText>(R.id.smtpConfigPort).apply {
         setText(config.port.toString())

         addTextChangedListener {
            config.port = it.toString().toInt()
            Repository.updateConfig(config)
         }
      }
   }

   private fun setupUsernameField() {
      findViewById<EditText>(R.id.smtpConfigUsername).apply {
         setText(config.username)

         addTextChangedListener {
            config.username = it.toString()
            Repository.updateConfig(config)
         }
      }
   }

   private fun setupPasswordField() {
      findViewById<EditText>(R.id.smtpConfigPassword).apply {
         setText(config.password)

         addTextChangedListener {
            config.password = it.toString()
            Repository.updateConfig(config)
         }
      }
   }

   private fun setupFromAddressField() {
      findViewById<EditText>(R.id.smtpConfigFromAddress).apply {
         setText(config.fromAddress)

         addTextChangedListener {
            config.fromAddress = it.toString()
            Repository.updateConfig(config)
         }
      }
   }

   private fun setupForwardAddressField() {
      findViewById<EditText>(R.id.smtpConfigForwardAddress).apply {
         setText(config.forwardAddress)

         addTextChangedListener {
            config.forwardAddress = it.toString()
            Repository.updateConfig(config)
         }
      }
   }

   private fun setupSubjectLineField() {
      findViewById<EditText>(R.id.smtpConfigSubjectLine).apply {
         setText(config.subject)

         addTextChangedListener {
            config.subject = it.toString()
            Repository.updateConfig(config)
         }
      }
   }
}