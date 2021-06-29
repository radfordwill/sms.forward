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
   var blockNumberFilters: Boolean = false,
   var enabled: Boolean = true
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

   fun addNumberFilter(number: String = "") {
      numberFilters = numberFilters.toMutableList().apply { add(number) }
      updateNumberFiltersJson()
   }

   fun updateNumberFilter(index: Int, newNumber: String) {
      if (numberFilters.size <= index) return

      numberFilters = numberFilters.mapIndexed { oldIndex, oldNumber ->
         if (oldIndex == index) newNumber  else oldNumber
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