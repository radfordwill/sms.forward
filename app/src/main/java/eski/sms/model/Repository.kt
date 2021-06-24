package eski.sms.model

import eski.sms.db.ObjectBox


object Repository {
   var smsConfigs: HashSet<SmsConfig> = ObjectBox.smsConfigBox.all.toHashSet()
      private set

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

   private fun <T> HashSet<T>.updateSafely(item: T): HashSet<T> = this.toMutableSet().apply {
      remove(item)
      add(item)
   }.toHashSet()

   private fun <T> HashSet<T>.removeSafely(item: T): HashSet<T> = this.toMutableSet().apply {
      remove(item)
   }.toHashSet()
}