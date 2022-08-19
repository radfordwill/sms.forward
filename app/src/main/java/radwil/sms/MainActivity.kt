package radwil.sms

import android.Manifest.permission.RECEIVE_SMS
import android.Manifest.permission.SEND_SMS
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import radwil.sms.ui.ConfigsView
import radwil.sms.model.Repository
import radwil.sms.ui.SettingsActivity
import radwil.sms.ui.logs.LogsActivity
import radwil.sms.utils.log
import kotlinx.android.synthetic.main.activity_main.*


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
      }

      return when(item.itemId) {
         R.id.actionSettings, R.id.actionLogs, R.id.actionDeleteLogs -> true
         else -> super.onOptionsItemSelected(item)
      }
   }

   override fun onStart() {
      super.onStart()
      checkForSmsPermission()
   }

   private fun checkForSmsPermission() {
      if (ActivityCompat.checkSelfPermission(this, SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
         log("send sms permission is not granted, requesting permission.")
         ActivityCompat.requestPermissions(this, arrayOf(SEND_SMS), 100)
      }

      if (ActivityCompat.checkSelfPermission(this, RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
         log("receive sms permission is not granted, requesting permission.")
         ActivityCompat.requestPermissions(this, arrayOf(RECEIVE_SMS), 200)
      }
   }

}
