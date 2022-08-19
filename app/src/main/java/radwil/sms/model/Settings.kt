package radwil.sms.model

import radwil.sms.BuildConfig
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class Settings(
   @Id var id: Long = 0,
   var logsEnabled: Boolean = BuildConfig.DEBUG,
   var logcatEnabled: Boolean = BuildConfig.DEBUG,
   var showMessageInLogs: Boolean = BuildConfig.DEBUG
)