package com.example.gamestore

import android.app.Activity
import android.util.Log
import android.widget.Toast
import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.Firebase

class EmailPasswordActivity : Activity() {

    // [START declare_auth]
    private var auth: FirebaseAuth = Firebase.auth
    // [END declare_auth]

    private fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    // [START on_start_check_user]
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = getCurrentUser()
        if (currentUser != null) {
            reload()
        }
    }
    // [END on_start_check_user]

    fun createAccount(context: Context, email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        // [START create_user_with_email]
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = getCurrentUser()
                    user?.sendEmailVerification()?.addOnCompleteListener {
                        Toast.makeText(context, "Verification email sent. Please verify within 5 minutes before logging in.", Toast.LENGTH_LONG).show()
                    }
                    startEmailVerificationPolling(context, user, onResult) // Start checking for email verification
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(context, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    onResult(false, task.exception?.localizedMessage)
                }
            }// [END create_user_with_email]
    }

    fun signIn(context: Context, email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        // [START sign_in_with_email]
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = getCurrentUser()
                    if (user?.isEmailVerified == true) {
                        onResult(true, null)
                    } else {
                        onResult(false, "Please verify your email before logging in.")
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        context,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                    onResult(false, task.exception?.localizedMessage)
                }
            }
        // [END sign_in_with_email]
    }

    fun updateEmail(context: Context, newEmail: String, onResult: (Boolean, String?) -> Unit) {
        val user = getCurrentUser()
        user?.verifyBeforeUpdateEmail(newEmail)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("EmailPassword", "Email update successful. Verification email sent.")
                Toast.makeText(context, "Verification email sent to $newEmail. Please verify to complete the update.", Toast.LENGTH_LONG).show()
                // No need for separate polling, the update is complete after verification
                onResult(true, null)
            } else {
                Log.w("EmailPassword", "Failed to update email: ${task.exception?.message}")
                task.exception?.printStackTrace()
                onResult(false, "Failed to update email: ${task.exception?.message}")
            }
        }
    }

    fun updatePassword(context: Context, newPassword: String, onResult: (Boolean, String?) -> Unit) {
        val user = getCurrentUser()
        //val credential = EmailAuthProvider.getCredential(email, password)
        //user!!.reauthenticate(credential).addOnCompleteListener { Log.d(TAG, "User re-authenticated.") }
        //need to implement reauthentication
        if(newPassword.isBlank()) {
            Log.w(TAG, "Password is empty")
            onResult(false, "Password cannot be empty")
            Toast.makeText(context, "Password cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }
        user?.updatePassword(newPassword)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "User password updated.")
                Toast.makeText(context, "Updated password.", Toast.LENGTH_SHORT).show()
                onResult(true, null)
            } else {
                Log.w(TAG, "Failed to update password", task.exception)
                onResult(false, task.exception?.localizedMessage)
            }
        }
    }

    fun deleteAccount(onResult: (Boolean, String?) -> Unit) {
        val user = getCurrentUser()
        user?.delete()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "User account deleted.")
                onResult(true, null)
            } else {
                Log.w(TAG, "Failed to delete account", task.exception)
                onResult(false, task.exception?.localizedMessage)
            }
        }
    }

    private fun startEmailVerificationPolling(context: Context, user: FirebaseUser?, onResult: (Boolean, String?) -> Unit) {
        // [START send_email_verification]
        if (user == null) {
            onResult(false, "User not found.")
            return
        }
        val handler = android.os.Handler(android.os.Looper.getMainLooper())
        val checkInterval = 3000L // Check every 3 seconds
        val maxRetries = 100 // total 300 seconds
        var attempt = 0
        val checkTask = object : Runnable {
            override fun run() {
                user.reload().addOnCompleteListener {
                    if (user.isEmailVerified) {
                        Toast.makeText(context, "Email verified! Logging in", Toast.LENGTH_SHORT).show()
                        onResult(true, null)
                    } else {
                        attempt++
                        if (attempt < maxRetries) {
                            handler.postDelayed(this, checkInterval)
                        } else {
                            Toast.makeText(context, "Email verification timeout. Please verify manually.", Toast.LENGTH_LONG).show()
                            onResult(false, "Email verification timeout.")
                        }
                    }
                }
            }
        }
        handler.postDelayed(checkTask, checkInterval)
        // [END send_email_verification]
    }

    private fun reload() {
    }

    companion object {
        private const val TAG = "EmailPassword"
    }
}