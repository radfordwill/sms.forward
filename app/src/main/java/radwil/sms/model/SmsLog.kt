package radwil.sms.model

import android.annotation.SuppressLint
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import java.text.SimpleDateFormat
import java.util.*


@Entity
data class SmsLog(
   @Id var id: Long = 0,
   val message: String = "",
   val timeStamp: Long = System.currentTimeMillis()
) {
   val formattedTimestamp: String = timestampFormat.format(Date(timeStamp))

   companion object {
      @SuppressLint("SimpleDateFormat")
      var timestampFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
   }
}