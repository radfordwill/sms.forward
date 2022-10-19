package radwil.sms.routing

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.annotation.RequiresApi
import radwil.sms.App
import radwil.sms.utils.log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import radwil.sms.utils.Constants


/**
 * This class is used to monitor multi media messages via receiver
 */
class MmsReceiver: BroadcastReceiver() {
      private val smtpSender = SmtpSender()
      private val constants = Constants()

      override fun onReceive(context: Context?, intent: Intent?) {

      val result = goAsync()
      val thread: Thread = object: Thread() {
         @RequiresApi(Build.VERSION_CODES.R)
         override fun run() {
            val begin = System.nanoTime()
            log("async task start")
            // async thread processing
            val query = context?.contentResolver?.query(constants.MMS_MESSAGE_URI, constants.MMS_PROJECTION, null, null, null)
            if (query!!.moveToNext()) {
               do {
                  val string = query.getString(query.getColumnIndexOrThrow("ct_t"))
                  if ("application/vnd.wap.multipart.related" == string || "application/vnd.wap.multipart.mixed" == string) {

                     //TODO
                     // add another thread for processing images

                     // MMS related
                     val partId: String = query.getString(query.getColumnIndexOrThrow("_id"))
                     log("Part ID = $partId")
                     val sb = StringBuilder()
                     val sb2 = StringBuilder()
                     val sb3 = StringBuilder()
                     var bitmap: Bitmap? // bitmap class for
                     val selectionPart = "mid=$partId"
                     val cursor: Cursor? = context.contentResolver.query(
                        constants.MMS_MESSAGE_PART_URI, null,
                        selectionPart, null, null
                     )

                     if (cursor!!.moveToNext()) {
                        val selectionPartAddress = "msg_id=$partId"
                        val mmsTextUriAddress: Uri = Uri.parse("content://mms/$partId/addr")
                        val cursorAddress: Cursor? = context.contentResolver.query(
                           mmsTextUriAddress, null,
                           selectionPartAddress, null, null
                        )
                        if (cursorAddress!!.moveToNext()) {
                           val address: String? = cursorAddress.getString(cursorAddress.getColumnIndexOrThrow("address"))
                           sb.append(address)
                        }
                        cursorAddress.close()

                        do {
                           val mId: String? = cursor.getString(cursor.getColumnIndexOrThrow("_id"))
                           val type: String? = cursor.getString(cursor.getColumnIndexOrThrow("ct"))

                           if ("application/smil" != type) {
                           // TEXT BODY ->.
                           log("Secondary MID  = $mId")
                           log("Type = $type")
                           if ("text/plain" == type) {
                              val messageBody: String? = cursor.getString(cursor.getColumnIndexOrThrow("text"))
                              sb2.append("$messageBody")
                              log("Body = $messageBody")
                           }

                           //TODO
                           // add count limit to images, four seems to be fast enough to run in a thread

                           // IMAGE ->.
                           if ("image/jpeg" == type || "image/bmp" == type ||
                              "image/gif" == type || "image/jpg" == type ||
                              "image/png" == type
                           ) {
                              val externalStorageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) //getting external storage directory
                              val folder = File("$externalStorageDirectory/smsgoforward/temp//")
                              try {
                                 folder.mkdirs()                                 // Make sure the new image directory exists.
                              } catch (e: IOException) {
                                 e.printStackTrace()
                              }
                              val ext: String = if ("image/gif" == type) {
                                 "WEBP"
                              } else {
                                 "PNG"
                              }
                              val fileName = "$mId.$ext"
                              val pathFn = "$folder/$fileName"
                              sb3.append("$fileName,")
                              log("Path + Filename = $pathFn")
                              bitmap = getMmsImageBitmap(mId.toString())
                              // save image to temp folder
                              saveMmsToFile(pathFn, ext, bitmap)
                           }
                           } //else {
                           // if ("application/smil" != type || "text/plain" != type){
                           // sb2.replace(0, sb.length, "See original message for attached file") // unsupported file type
                           // }}
                        } while (cursor.moveToNext())



                     }
                     cursor.close()

                     val fileArray = filenamesToArray(sb3)
                     smtpSender.forwardMessageMms(sb2.toString(), sb.toString(), fileArray)

                     // TODO
                     //  fix it with a loop maybe?
                     break
                  }
               } while (query.moveToNext())
               query.close()
            }

            val end = System.nanoTime()
            val duration = (end - begin) / 1_000_000_000.0
            log("Elapsed thread time in seconds: $duration")
            result.finish()
         }
      }
      thread.start()
      // thread end
   }

   /**
    * This class is used to save the MMS image to file
    */
   @RequiresApi(Build.VERSION_CODES.R)
   private fun saveMmsToFile(fn: String, ext: String, bitmap: Bitmap?) {
      try {
         FileOutputStream(fn).use { out ->
            if (ext == "PNG") {
               val bitmapCompression = CompressFormat.PNG
               bitmap!!.compress(bitmapCompression, 100, out) // Bitmap compression
            } else {
               val bitmapCompression = CompressFormat.WEBP_LOSSLESS
               bitmap!!.compress(bitmapCompression, 100, out) // Bitmap compression
            }
         }
      } catch (error: IOException) {
         log("${error::class.java.simpleName} thrown while trying to clean up files from an SMTP message: ${error.message}")
      }
   }
   }

   /**
    * This class is used to return a bitmap from an MMS image stream
    */
   private fun getMmsImageBitmap(_id: String): Bitmap? {
      val partURI = Uri.parse("content://mms/part/$_id")
      var `is`: InputStream? = null
      var bitmap: Bitmap? = null
      try {
         `is` = App.instance.contentResolver.openInputStream(partURI)
         bitmap = BitmapFactory.decodeStream(`is`)
      } catch (e: IOException) {
      } finally {
         if (`is` != null) {
            try {
               `is`.close()
            } catch (e: IOException) {
            }
         }
      }
      return bitmap
   }

   /**
    * This class is used to return file names in an array list
    */
   private fun filenamesToArray(strBuilder: StringBuilder): List<String> {
      return strBuilder.split(",").toTypedArray().filter { it.isNotBlank() }
   }