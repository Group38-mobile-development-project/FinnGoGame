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

    private lateinit var auth: FirebaseAuth
    private lateinit var credentialManager: CredentialManager

    fun launchCredentialManager(context: Context, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        auth = Firebase.auth
        credentialManager = CredentialManager.create(context)
        CoroutineScope(Dispatchers.Main).launch {
            signInWithGoogle(context, onSuccess, onFailure)
        }
    }

    private suspend fun signInWithGoogle(context: Context, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId(context.getString(R.string.default_web_client_id))
            .setFilterByAuthorizedAccounts(false)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        try {
            val result = credentialManager.getCredential(context, request)
            handleSignIn(result.credential, onSuccess, onFailure)
        } catch (e: GetCredentialException) {
            Log.e(TAG, "Couldn't retrieve user's credentials: ${e.localizedMessage}")
            onFailure("Couldn't retrieve user's credentials: ${e.localizedMessage}")
        }
    }

    private fun handleSignIn(credential: Credential, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            firebaseAuthWithGoogle(googleIdTokenCredential.idToken, onSuccess, onFailure)
        } else {
            Log.w(TAG, "Credential is not of type Google ID!")
            onFailure("Credential is not of type Google ID!")
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    onSuccess()
                } else {
                    Log.w(TAG, "signInWithCredential:failure")
                    onFailure(task.exception?.message ?: "Unknown error")
                }
            }
    }

    fun deleteAccountWithGoogle(onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        user?.delete()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "Account successfully deleted")
                onSuccess()
            } else {
                Log.w(TAG, "Failed to delete account: ${task.exception?.message}")
                onFailure(task.exception?.message ?: "Unknown error")
            }
        }
    }

    companion object {
        private const val TAG = "GoogleActivity"
    }
}

