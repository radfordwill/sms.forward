package com.radwil.sms.utils

import android.net.Uri
import android.os.Environment
import java.io.File


private const val APPLICATION_DIRECTORY_PATH = "Documents/smsgoforward"
const val TOKENS_DIRECTORY_PATH = "tokens"

class Constants {
   val MMS_MESSAGE_URI:Uri = Uri.parse("content://mms")
   val MMS_MESSAGE_PART_URI:Uri = Uri.parse("content://mms/part")
   val MMS_PROJECTION = arrayOf( "_id", "ct_t" )


   val APPLICATIONFOLDER: File = File(Environment.getExternalStorageDirectory().toString() +
         File.separator + APPLICATION_DIRECTORY_PATH)
   val TOKENFOLDER: File = File(Environment.getExternalStorageDirectory().toString() +
         File.separator + APPLICATION_DIRECTORY_PATH + File.separator + TOKENS_DIRECTORY_PATH)

}
