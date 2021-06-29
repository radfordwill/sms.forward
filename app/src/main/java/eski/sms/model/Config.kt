package eski.sms.model

import eski.sms.utils.LazyMutable
import io.objectbox.annotation.BaseEntity
import io.objectbox.annotation.Id
import org.json.JSONArray
import org.json.JSONException


@BaseEntity
abstract class Config(
   @Id var id: Long = 0,
   var name: String = "",
   var numberFiltersJson: String = "[]",
   var blockNumberFilters: Boolean = false
) {
   @delegate:Transient
   var numberFilters: List<String> by LazyMutable { parseNumberFilters() }
      private set

   private fun parseNumberFilters(): List<String> {
      val numberFiltersJsonArray = try {
         JSONArray(numberFiltersJson)
      } catch (error: JSONException) {
         null
      }

      return (0..(numberFiltersJsonArray?.length() ?: 0)).mapNotNull {
         numberFiltersJsonArray?.optString(it, null)
      }
   }

   fun updateNumberFilter(index: Int, number: String?) {
      numberFilters = if (numberFilters.size == index && number != null) {
         numberFilters.toMutableList().apply { add(number) }
      } else {
         numberFilters.mapIndexedNotNull { oldIndex, s ->
            if (oldIndex == index) {
               number
            } else {
               s
            }
         }
      }

      updateNumberFiltersJson()
   }

   fun removeNumberFilter(index: Int) {
      if (numberFilters.size <= index) return

      numberFilters = numberFilters.toMutableList().apply { removeAt(index) }
      updateNumberFiltersJson()
   }

   private fun updateNumberFiltersJson() {
      numberFiltersJson = JSONArray(numberFilters).toString()
   }

   abstract fun validate(): Boolean

   override fun hashCode() = id.hashCode()
   override fun equals(other: Any?) = other is SmsConfig && id == other.id
}