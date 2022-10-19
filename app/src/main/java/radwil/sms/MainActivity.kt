package radwil.sms

import android.Manifest.permission.*
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*
import radwil.sms.model.Repository
import radwil.sms.ui.ConfigsView
import radwil.sms.ui.SettingsActivity
import radwil.sms.ui.logs.LogsActivity
import radwil.sms.utils.GmailUtils
import radwil.sms.utils.log


class MainActivity: AppCompatActivity() {
   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      setContentView(R.layout.activity_main)
      setSupportActionBar(toolbar)
      val configurationsView = ConfigsView(window.decorView)

   }

   override fun onCreateOptionsMenu(menu: Menu): Boolean {
      menuInflater.inflate(R.menu.menu, menu)
      return true
   }

   override fun onOptionsItemSelected(item: MenuItem): Boolean {
      when (item.itemId) {
         R.id.actionSettings -> startActivity(Intent(this, SettingsActivity::class.java))
         R.id.actionLogs -> startActivity(Intent(this, LogsActivity::class.java))
         R.id.actionDeleteLogs -> Repository.clearLogs()
         //R.id.settingsViewGmail -> startActivity(Intent(this, GmailFragment::class.java))

      }
      return when (item.itemId) {
         R.id.actionSettings, R.id.actionLogs, R.id.actionDeleteLogs -> true
         else -> super.onOptionsItemSelected(item)
      }
   }

   override fun onStart() {
      super.onStart()
      checkForSmsPermission()
   }

   private fun checkForSmsPermission() {
      val permissionsCode = 42
       // SEND_SMS, RECEIVE_WAP_PUSH,RECEIVE_SMS,RECEIVE_MMS, WRITE_EXTERNAL_STORAGE, READ_CONTACTS, BROADCAST_WAP_PUSH
      val permissions = arrayOf(INTERNET, SEND_SMS, RECEIVE_WAP_PUSH, RECEIVE_SMS, RECEIVE_MMS, WRITE_EXTERNAL_STORAGE, READ_CONTACTS)
      if (ActivityCompat.checkSelfPermission(this, permissions.toString()) != PackageManager.PERMISSION_GRANTED) {
         ActivityCompat.requestPermissions(this, permissions, permissionsCode);
   }
   }
}


