package eski.sms.model

import eski.sms.db.ObjectBox


object Repository {
   var settings: Settings = ObjectBox.settingsBox.all.first()
      private set

   var smsConfigs: HashSet<SmsConfig> = ObjectBox.smsConfigBox.all.toHashSet()
      private set

   var logs: List<SmsLog> = ObjectBox.logsBox.all
      private set

   fun updateSettings(settings: Settings) {
      if (settings.id == this.settings.id) ObjectBox.settingsBox.put(settings)
   }

   fun updateConfig(config: Config) {
      if (config is SmsConfig) updateSmsConfig(config)
   }

   private fun updateSmsConfig(config: SmsConfig) {
      ObjectBox.smsConfigBox.put(config)
      smsConfigs = smsConfigs.updateSafely(config)
   }

   fun deleteConfig(config: Config) {
      if (config is SmsConfig) deleteSmsConfig(config)
   }

   private fun deleteSmsConfig(config: SmsConfig) {
      if (config.id != 0L) ObjectBox.smsConfigBox.remove(config)
      smsConfigs = smsConfigs.removeSafely(config)
   }

   fun addLog(log: SmsLog) {
      logs = logs.toMutableList().apply { add(log) }
      ObjectBox.logsBox.put(log)
   }

   fun clearLogs() {
      logs = emptyList()
      ObjectBox.logsBox.removeAll()
   }

   private fun <T> HashSet<T>.updateSafely(item: T): HashSet<T> = this.toMutableSet().apply {
      remove(item)
      add(item)
   }.toHashSet()

   private fun <T> HashSet<T>.removeSafely(item: T): HashSet<T> = this.toMutableSet().apply {
      remove(item)
   }.toHashSet()
}