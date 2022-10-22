/*
 * Copyright 2019, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.radwil.sms.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.IdpResponse
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import radwil.sms.R
import radwil.sms.databinding.*

@Suppress("DEPRECATION")
class LoginFragment : Fragment() {

    companion object {
        const val TAG = "LoginFragment"
        const val SIGN_IN_RESULT_CODE = 1001
    }

    // Get a reference to the ViewModel scoped to this Fragment.
    private val viewModel by viewModels<LoginViewModel>()

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment.
        val binding = DataBindingUtil.inflate<FragmentLoginBinding>(
            inflater, R.layout.fragment_login, container, false
        )

        binding.authButton.setOnClickListener { launchSignInFlow() }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
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

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.onSignInResult(res)
    }

    private fun launchSignInFlow() {
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SIGN_IN_RESULT_CODE) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in user.
                Log.i(
                    TAG,
                    "Successfully signed in user " +
                            "${FirebaseAuth.getInstance().currentUser?.displayName}!"
                )
            } else {
                // Sign in failed. If response is null the user canceled the sign-in flow using
                // the back button. Otherwise check response.getError().getErrorCode() and handle
                // the error.
                Log.i(TAG, "Sign in unsuccessful ${response?.error?.errorCode}")
            }
        }
    }
}
