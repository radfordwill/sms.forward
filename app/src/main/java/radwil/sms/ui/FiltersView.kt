package radwil.sms.ui

import android.view.View
import android.view.View.*
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.widget.addTextChangedListener
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputLayout
import radwil.sms.App
import radwil.sms.R
import radwil.sms.model.Config
import radwil.sms.model.Repository


class FiltersView(
   private val config: Config,
   configView: View,
   addFilterButton: View
) {
   private val header: TextView = configView.findViewById(R.id.filtersHeader)
   private val blockedSwitch: SwitchMaterial = configView.findViewById(R.id.filtersBlockedSwitch)
   private val filtersContainer: LinearLayout = configView.findViewById(R.id.filtersContainer)

   init {
      setFiltersHeaderText()
      setupBlockedSwitchView()

      config.numberFilters.forEach { addFilterView(it) }

      addFilterButton.setOnClickListener {
         config.addNumberFilter()
         addFilterView(config.numberFilters.last())
         updateBlockedSwitchView()
         setFiltersHeaderText()
      }
   }

   private fun addFilterView(number: String) {
      val filterView = inflate(App.themedInstance, R.layout.filter, null)
      filtersContainer.addView(filterView)

      filterView.findViewById<EditText>(R.id.filterNumber).apply {
         setText(number)

         addTextChangedListener {
            config.updateNumberFilter(filtersContainer.indexOfChild(filterView), this.text.toString())
            setFiltersHeaderText()
            Repository.updateConfig(config)
         }
      }

      filterView.findViewById<TextInputLayout>(R.id.filterLayout).apply {
         endIconDrawable = AppCompatResources.getDrawable(context, R.drawable.cancel)
         setEndIconActivated(true)

         setEndIconOnClickListener {
            config.removeNumberFilter(filtersContainer.indexOfChild(filterView))
            filtersContainer.removeView(filterView)
            setFiltersHeaderText()
            updateBlockedSwitchView()
            Repository.updateConfig(config)
         }
      }
   }

   private fun setupBlockedSwitchView() {
      blockedSwitch.isChecked = !config.blockNumberFilters
      updateBlockedSwitchView()

      blockedSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
         config.blockNumberFilters = !isChecked
         setFiltersHeaderText()
         Repository.updateConfig(config)
      }
   }

   private fun updateBlockedSwitchView() {
      blockedSwitch.visibility = if (config.numberFilters.isEmpty()) INVISIBLE else VISIBLE
   }

   private fun setFiltersHeaderText() {
      header.text = when {
         config.numberFilters.isEmpty() -> CommonStrings.filtersForwardingAll
         config.blockNumberFilters -> CommonStrings.filtersBlocking
         else -> CommonStrings.filters
      }
   }
}