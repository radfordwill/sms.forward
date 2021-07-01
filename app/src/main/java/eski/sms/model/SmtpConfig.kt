package eski.sms.model

import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.converter.PropertyConverter


@Entity
class SmtpConfig(
   name: String = "",
   id: Long = 0,
   numberFiltersJson: String = "[]",
   blockNumberFilters: Boolean = false,
   includeSenderNumber: Boolean = true,
   enabled: Boolean = true,
   var host: String = "",
   var port: Int = 0,
   var username: String = "",
   var password: String = "",
   @Convert(converter = Protocol.Converter::class, dbType = Int::class) var protocol: Protocol = Protocol.TLS,
   var fromAddress: String = "",
   var subject: String = "",
   var forwardAddress: String = ""
): Config(
   id = id,
   name = name,
   numberFiltersJson = numberFiltersJson,
   blockNumberFilters = blockNumberFilters,
   includeSenderNumber = includeSenderNumber,
   enabled = enabled
) {

   override fun validate() = true

   enum class Protocol(val stableId: Int) {
      SSL(0),
      TLS(1),
      STARTTLS(2);

      class Converter: PropertyConverter<Protocol, Int> {
         override fun convertToDatabaseValue(entityProperty: Protocol?) = entityProperty?.stableId ?: STARTTLS.stableId

         override fun convertToEntityProperty(databaseValue: Int?): Protocol {
            values().forEach { if (it.stableId == databaseValue) return it }
            return TLS
         }
      }
   }
}