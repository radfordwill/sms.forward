package com.radwil.sms.model

import android.telephony.PhoneNumberUtils
import io.objectbox.annotation.Entity


@Entity
class SmsConfig(
   name: String = "",
   id: Long = 0,
   numberFiltersJson: String = "[]",
   blockNumberFilters: Boolean = false,
   includeSenderNumber: Boolean = true,
   enabled: Boolean = true,
   var forwardNumber: String = "",
   var includeSubjectLine: Boolean = false,
   var subjectLine: String = ""
): Config(
   id = id,
   name = name,
   numberFiltersJson = numberFiltersJson,
   blockNumberFilters = blockNumberFilters,
   includeSenderNumber = includeSenderNumber,
   enabled = enabled
) {

   override fun validate() = PhoneNumberUtils.isGlobalPhoneNumber(forwardNumber)
}