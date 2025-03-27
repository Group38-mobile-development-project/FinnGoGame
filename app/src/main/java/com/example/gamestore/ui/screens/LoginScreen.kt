package com.example.gamestore.ui.screens

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.gamestore.EmailPasswordActivity
import com.example.gamestore.GoogleSignInActivity
import com.example.gamestore.R

@Composable
fun LoginScreen(navController: NavController) {
    val emailPasswordActivity = remember { EmailPasswordActivity() }
    val googleSignInActivity = remember { GoogleSignInActivity() }
    val context = LocalContext.current // Get the current context

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoginMode by remember { mutableStateOf(true) }
    var passwordVisible by remember { mutableStateOf(false) }

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
                    emailPasswordActivity.signIn(email, password) { success, message ->
                        if (success) {
                            navController.navigate("home")// Navigate to home on success
                        } else {
                            errorMessage = message
                        }
                    }
                } else {
                    emailPasswordActivity.createAccount(email, password) { success, message ->
                        if (success) {
                            navController.navigate("home")
                        } else {
                            errorMessage = message
                        }
                    }
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

        GoogleSignInButton(googleSignInActivity, context, navController)

        errorMessage?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
        }
    }
}

@Composable
fun GoogleSignInButton(googleSignInActivity: GoogleSignInActivity, context: Context, navController: NavController) {
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clickable {
                googleSignInActivity.launchCredentialManager(
                    context,
                    onSuccess = { navController.navigate("home") }, // Navigate to home on success
                    onFailure = { errorMessage = it }
                )
            },
    ) {
        Image(
            painter = painterResource(id = R.drawable.androidlightrdsu3x),
            contentDescription = "Sign in with Google",
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.FillHeight
        )
    }

    errorMessage?.let {
        Text(text = it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
    }
}



@Preview
@Composable
fun PreviewLoginScreen() {
    LoginScreen(navController = rememberNavController())
}