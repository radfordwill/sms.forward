package eski.sms.ui

import android.content.Context
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.FrameLayout
import androidx.core.widget.addTextChangedListener
import com.google.android.material.switchmaterial.SwitchMaterial
import eski.sms.App
import eski.sms.R
import eski.sms.model.Config
import eski.sms.model.Repository
import eski.sms.model.SmsConfig
import eski.sms.model.SmtpConfig


class ConfigView @JvmOverloads constructor(
   context: Context = App.instance,
   attrs: AttributeSet? = null,
   defStyle: Int = 0
): FrameLayout(context, attrs, defStyle) {

   private lateinit var config: Config
   private lateinit var filtersView: FiltersView

   companion object {
      fun fromConfig(config: Config): ConfigView {
         val contextTheme = ContextThemeWrapper(App.instance, R.style.AppTheme)
         val view = View.inflate(contextTheme, R.layout.config, null) as ConfigView
         view.init(config)

         return view
      }
   }

   private fun init(config: Config) {
      this.config = config
      this.filtersView = FiltersView(config, this, findViewById(R.id.configAddNumberFilter))

      setupNameField()
      setupEnabledSwitch()
      setupIncludeSenderNumberField()
      setupDeleteButton()
      setupSubtypeView(config)
   }

   private fun setupNameField() {
      findViewById<EditText>(R.id.configName).apply {
         setText(config.name)

         addTextChangedListener {
            config.name = it.toString()
            Repository.updateConfig(config)
         }
      }
   }

   private fun setupEnabledSwitch() {
      findViewById<SwitchMaterial>(R.id.configEnabledSwitch).apply {
         isChecked = config.enabled

         setOnCheckedChangeListener { buttonView, isChecked ->
            config.enabled = isChecked
            Repository.updateConfig(config)
         }
      }
   }

   private fun setupIncludeSenderNumberField() {
      findViewById<CheckBox>(R.id.configIncludeSenderNumber).apply {
         isChecked = config.includeSenderNumber

         setOnCheckedChangeListener { buttonView, isChecked ->
            config.includeSenderNumber = isChecked
            Repository.updateConfig(config)
         }
      }
   }

   private fun setupDeleteButton() {
      findViewById<View>(R.id.configDeleteButton).apply {
         setOnClickListener {
            (this@ConfigView.parent as? ViewGroup)?.removeView(this@ConfigView)
            Repository.deleteConfig(config)
         }
      }
   }

   private fun setupSubtypeView(config: Config) {
      findViewById<FrameLayout>(R.id.configSubtypeContent).apply {
         when (config) {
            is SmsConfig -> addView(SmsConfigView.fromConfig(config))
            is SmtpConfig -> addView(SmtpConfigView.fromConfig(config))
         }
      }
   }
}