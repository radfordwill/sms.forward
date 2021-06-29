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
import eski.sms.model.Repository
import eski.sms.model.SmsConfig


class SmsConfigView @JvmOverloads constructor(
   context: Context = App.instance,
   attrs: AttributeSet? = null,
   defStyle: Int = 0
): FrameLayout(context, attrs, defStyle) {

   private lateinit var config: SmsConfig
   private lateinit var filtersView: FiltersView

   companion object {
      fun fromConfig(config: SmsConfig): SmsConfigView {
         val contextTheme = ContextThemeWrapper(App.instance, R.style.AppTheme)
         val view = View.inflate(contextTheme, R.layout.sms_config, null) as SmsConfigView
         view.init(config)

         return view
      }
   }

   private fun init(config: SmsConfig) {
      this.config = config
      this.filtersView = FiltersView(config, this, findViewById(R.id.smsConfigAddNumberFilter))

      setupNameField()
      setupEnabledSwitch()
      setupForwardNumberField()
      setupIncludeSenderNumberField()
      setupIncludeSubjectLineField()
      setupSubjectLineField()
      setupDeleteButton()
   }

   private fun setupNameField() {
      findViewById<EditText>(R.id.smsConfigName).apply {
         setText(config.name)

         addTextChangedListener {
            config.name = it.toString()
            Repository.updateConfig(config)
         }
      }
   }

   private fun setupEnabledSwitch() {
      findViewById<SwitchMaterial>(R.id.smsConfigEnabledSwitch).apply {
         isChecked = config.enabled

         setOnCheckedChangeListener { buttonView, isChecked ->
            config.enabled = isChecked
            Repository.updateConfig(config)
         }
      }
   }

   private fun setupForwardNumberField() {
      findViewById<EditText>(R.id.smsConfigForwardNumber).apply {
         setText(config.forwardNumber)

         addTextChangedListener {
            config.forwardNumber = it.toString()
            Repository.updateConfig(config)
         }
      }
   }

   private fun setupIncludeSenderNumberField() {
      findViewById<CheckBox>(R.id.smsConfigIncludeSenderNumber).apply {
         isChecked = config.includeSenderNumber

         setOnCheckedChangeListener { buttonView, isChecked ->
            config.includeSenderNumber = isChecked
            Repository.updateConfig(config)
         }
      }
   }

   private fun setupIncludeSubjectLineField() {
      findViewById<CheckBox>(R.id.smsConfigIncludeSubjectLine).apply {
         isChecked = config.includeSubjectLine

         setOnCheckedChangeListener { buttonView, isChecked ->
            config.includeSubjectLine = isChecked
            Repository.updateConfig(config)
         }
      }
   }

   private fun setupSubjectLineField() {
      findViewById<EditText>(R.id.smsConfigSubjectLine).apply {
         setText(config.subjectLine)

         addTextChangedListener {
            config.subjectLine = it.toString()
            Repository.updateConfig(config)
         }
      }
   }


   private fun setupDeleteButton() {
      findViewById<View>(R.id.smsConfigDeleteButton).apply {
         setOnClickListener {
            (this@SmsConfigView.parent as? ViewGroup)?.removeView(this@SmsConfigView)
            Repository.deleteConfig(config)
         }
      }
   }
}