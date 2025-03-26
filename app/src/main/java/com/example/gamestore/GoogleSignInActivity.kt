package com.example.gamestore

import android.content.Context
import android.util.Log
import androidx.credentials.*
import com.google.android.libraries.identity.googleid.*
import com.google.firebase.auth.*
import com.google.firebase.Firebase
import androidx.credentials.exceptions.GetCredentialException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GoogleSignInActivity {

    private var auth: FirebaseAuth = Firebase.auth
    private lateinit var credentialManager: CredentialManager

    fun launchCredentialManager(context: Context) {
        credentialManager = CredentialManager.create(context)
        CoroutineScope(Dispatchers.Main).launch {
            signInWithGoogle(context)
        }
    }

    private suspend fun signInWithGoogle(context: Context) {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId(context.getString(R.string.default_web_client_id))
            .setFilterByAuthorizedAccounts(false)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        try {
            val result = credentialManager.getCredential(context, request)
            handleSignIn(result.credential)
        } catch (e: GetCredentialException) {
            Log.e(TAG, "Couldn't retrieve user's credentials: ${e.localizedMessage}")
        }
    }

    private fun handleSignIn(credential: Credential) {
        if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            firebaseAuthWithGoogle(googleIdTokenCredential.idToken)
        } else {
            Log.w(TAG, "Credential is not of type Google ID!")
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                }
            }
    }

    companion object {
        private const val TAG = "GoogleActivity"
    }
}

