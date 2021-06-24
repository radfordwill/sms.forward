package eski.sms.ui

import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import eski.sms.App
import eski.sms.R
import eski.sms.model.Config
import eski.sms.model.Repository


class FiltersView(
   private val config: Config,
   configView: View,
   addFilterButton: View
) {
   private val header: TextView = configView.findViewById(R.id.filtersHeader)
   private val filtersContainer: LinearLayout = configView.findViewById(R.id.filtersContainer)

   init {
      setFiltersHeaderText()
      config.numberFilters.forEach { addFilterView(it) }

      addFilterButton.setOnClickListener {
         addFilterView("")
      }
   }

   private fun addFilterView(number: String) {
      val index = filtersContainer.childCount

      val filterView = View.inflate(App.themedInstance, R.layout.filter, null)
      filtersContainer.addView(filterView)

      filterView.findViewById<EditText>(R.id.filterNumber).apply {
         setText(number)

         addTextChangedListener {
            config.updateNumberFilter(index, this.text.toString())
            Repository.updateConfig(config)
         }
      }
   }

   private fun setFiltersHeaderText() {
      header.text = when {
         config.numberFilters.isEmpty() -> CommonStrings.filtersForwardingAll
         config.blockNumberFilters -> CommonStrings.filtersBlocking
         else -> CommonStrings.filters
      }
   }
}