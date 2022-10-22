package com.radwil.sms.ui

import android.content.Context
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import androidx.core.widget.addTextChangedListener
import com.radwil.sms.App
import radwil.sms.R
import com.radwil.sms.model.Repository
import com.radwil.sms.model.SmsConfig


class SmsConfigView @JvmOverloads constructor(
   context: Context = App.instance,
   attrs: AttributeSet? = null,
   defStyle: Int = 0
): LinearLayout(context, attrs, defStyle) {

   private lateinit var config: SmsConfig

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

      setupForwardNumberField()
      setupIncludeSubjectLineField()
      setupSubjectLineField()
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
}