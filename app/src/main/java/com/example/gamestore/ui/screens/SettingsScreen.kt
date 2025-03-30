package com.example.gamestore.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gamestore.EmailPasswordActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

@Composable
fun SettingsScreen(navController: NavController) {

    val context = LocalContext.current // Get the current context
    val auth = remember { FirebaseAuth.getInstance() }
    val user = auth.currentUser
    val isGoogleUser = user?.providerData?.any { it.providerId == GoogleAuthProvider.PROVIDER_ID } == true
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showEmailField by remember { mutableStateOf(false) }
    var showPasswordField by remember { mutableStateOf(false) }
    val emailPasswordActivity = EmailPasswordActivity()

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Settings", style = MaterialTheme.typography.headlineMedium)

        if (user != null) {
            Spacer(modifier = Modifier.height(16.dp))

            if (!isGoogleUser) {
                Button(onClick = { showEmailField = true }) {
                    Text("Update Email")
                }
                if (showEmailField) {
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("New Email") }
                    )
                    Button(onClick = {
                        emailPasswordActivity.updateEmail(context, email) { success, message ->
                            if (success) {
                                showEmailField = false
                            } else {
                                println("Error: $message")
                            }
                        }
                    }) {
                        Text("Submit Email Update")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { showPasswordField = true }) {
                    Text("Update Password")
                }
                if (showPasswordField) {
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("New Password") }
                    )
                    Button(onClick = {
                        emailPasswordActivity.updatePassword(context, password) { success, message ->
                            if (success) {
                                showPasswordField = false
                            } else {
                                println("Error: $message")
                            }
                        }
                    }) {
                        Text("Submit Password Update")
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = {
                emailPasswordActivity.deleteAccount { success, message ->
                    if (!success) {
                        println("Error: $message")
                    }
                }
            }) {
                Text("Delete Account")
            }
        }
    }
}