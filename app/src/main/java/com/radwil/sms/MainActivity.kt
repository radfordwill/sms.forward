package com.radwil.sms

import android.Manifest.permission.*
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
//import com.firebase.ui.auth.AuthUI
//import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
//import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
//import com.google.firebase.auth.FirebaseAuth
import com.radwil.sms.model.Repository
import com.radwil.sms.ui.ConfigsView
import com.radwil.sms.ui.SettingsActivity
import com.radwil.sms.ui.logs.LogsActivity
import kotlinx.android.synthetic.main.activity_main.*
import radwil.sms.R
import java.util.*


class MainActivity: AppCompatActivity() {


   private val signInLauncher = registerForActivityResult(
      FirebaseAuthUIActivityResultContract()
   ) { res ->
      this.onSignInResult(res)
   }

   private fun createSignInIntent() {
      // [START auth_fui_create_intent]
      // Choose authentication providers
      val providers = arrayListOf(
         AuthUI.IdpConfig.GoogleBuilder().build()
      )

      // Create and launch sign-in intent
      val signInIntent = AuthUI.getInstance()
         .createSignInIntentBuilder()
         .setAvailableProviders(providers)
         .build()
      signInLauncher.launch(signInIntent)
      // [END auth_fui_create_intent]
   }

   private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
      val response = result.idpResponse
      if (result.resultCode == RESULT_OK) {
         // Successfully signed in
         val user = FirebaseAuth.getInstance().currentUser
         // ...
      } else {
         // Sign in failed. If response is null the user canceled the
         // sign-in flow using the back button. Otherwise check
         // response.getError().getErrorCode() and handle the error.
         // ...
      }
   }

   private fun signOut() {
      // [START auth_fui_signout]
      AuthUI.getInstance()
         .signOut(this)
         .addOnCompleteListener {
            // ...
         }
      // [END auth_fui_signout]
   }


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

      //firebaseActivity.signOut()
      //createSignInIntent()
      //signOut()





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


