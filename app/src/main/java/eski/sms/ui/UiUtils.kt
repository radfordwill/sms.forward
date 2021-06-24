package eski.sms.ui

import android.telephony.PhoneNumberUtils
import android.widget.EditText


fun EditText.validatePhoneNumberView(): Boolean {
   val strippedText = PhoneNumberUtils.stripSeparators(this.text.toString())

   return PhoneNumberUtils.isGlobalPhoneNumber(strippedText).also {
      this.error = if (!it) CommonStrings.phoneNumberError else null
   }
}