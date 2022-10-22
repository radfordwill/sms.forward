package com.radwil.sms.ui.logs

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import radwil.sms.R
import kotlinx.android.synthetic.main.activity_main.*

class LogsActivity: AppCompatActivity() {
   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      setContentView(R.layout.logs)
      setSupportActionBar(toolbar)

      setupRefreshButton()
   }

   override fun onStart() {
      super.onStart()
      setupLogsList()
   }

   private fun setupLogsList() {
      findViewById<RecyclerView>(R.id.logsRecyclerView).apply {
         layoutManager = LinearLayoutManager(this@LogsActivity, VERTICAL, false)
         addItemDecoration(DividerItemDecoration(this@LogsActivity, VERTICAL))
         adapter = LogsAdapter()
      }
   }

   private fun setupRefreshButton() {
      findViewById<Button>(R.id.logsRefreshButton).apply {
         setOnClickListener { setupLogsList() }
      }
   }
}