package eski.sms.model

import android.telephony.PhoneNumberUtils
import io.objectbox.annotation.Entity


@Entity
class SmsConfig(
   name: String = "",
   id: Long = 0,
   numberFiltersJson: String = "[]",
   blockNumberFilters: Boolean = false,
   var forwardNumber: String = "",
   var includeSenderNumber: Boolean = false

): Config(id, name, numberFiltersJson, blockNumberFilters) {

   override fun validate() = PhoneNumberUtils.isGlobalPhoneNumber(forwardNumber)
}