package com.example.gamestore.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.Firebase

@Composable
fun LoginScreen(navController: NavController) {
    val auth = Firebase.auth
    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoginMode by remember { mutableStateOf(true) } // Toggle login/signup
    var passwordVisible by remember { mutableStateOf(false) } // Toggle password visibility

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (isLoginMode) "Login" else "Sign Up",
            style = MaterialTheme.typography.headlineMedium
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            placeholder = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            placeholder = { Text("Password") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Outlined.CheckCircle else Icons.Filled.CheckCircle,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password"
                    )
                }
            }
        )

        Button(
            onClick = {
                if (email.isBlank() || password.isBlank()) {
                    errorMessage = "Email and password cannot be empty"
                    return@Button
                }

                if (isLoginMode) {
                    signIn(auth, email, password, context, navController)
                } else {
                    createAccount(auth, email, password, context, navController)
                }
            },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        ) {
            Text(if (isLoginMode) "Login" else "Sign Up")
        }

        TextButton(
            onClick = { isLoginMode = !isLoginMode },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text(if (isLoginMode) "Don't have an account? Sign up" else "Already have an account? Login")
        }

        errorMessage?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
        }
    }
}

private fun createAccount(auth: FirebaseAuth, email: String, password: String, context: android.content.Context, navController: NavController) {
    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("Auth", "createUserWithEmail: success")
                val user = auth.currentUser
                sendEmailVerification(user)
                Toast.makeText(context, "Account created! Please verify your email.", Toast.LENGTH_LONG).show()
                navController.navigate("home")
            } else {
                Log.w("Auth", "createUserWithEmail: failure", task.exception)
                Toast.makeText(context, "Signup failed: ${task.exception?.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
}

private fun signIn(auth: FirebaseAuth, email: String, password: String, context: android.content.Context, navController: NavController) {
    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("Auth", "signInWithEmail: success")
                val user = auth.currentUser
                if (user != null && user.isEmailVerified) {
                    Toast.makeText(context, "Login successful!", Toast.LENGTH_SHORT).show()
                    navController.navigate("home")
                } else {
                    Toast.makeText(context, "Please verify your email before logging in.", Toast.LENGTH_LONG).show()
                }
            } else {
                Log.w("Auth", "signInWithEmail: failure", task.exception)
                Toast.makeText(context, "Login failed: ${task.exception?.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
}

private fun sendEmailVerification(user: FirebaseUser?) {
    user?.sendEmailVerification()?.addOnCompleteListener {
        if (it.isSuccessful) {
            Log.d("Auth", "Verification email sent.")
        }
    }
}

@Preview
@Composable
fun PreviewLoginScreen() {
    LoginScreen(navController = NavController(LocalContext.current))
}
