package eski.sms.ui.logs

import android.view.View.inflate
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import eski.sms.App
import eski.sms.R
import eski.sms.model.Repository
import eski.sms.model.SmsLog

class LogsAdapter: RecyclerView.Adapter<LogsAdapter.ViewHolder>() {
   private var logs = Repository.logs.sortedByDescending { it.timeStamp }

   override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder()
   override fun getItemCount() = logs.size
   override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(logs[position])

   class ViewHolder: RecyclerView.ViewHolder(inflate(App.Companion.instance, R.layout.log, null)) {
      val timestampView = itemView.findViewById<TextView>(R.id.logTimestamp)
      val messageView: TextView = itemView.findViewById<TextView>(R.id.logMessage)

      fun bind(log: SmsLog) {
         timestampView.text = log.formattedTimestamp
         messageView.text = log.message
      }
   }
}