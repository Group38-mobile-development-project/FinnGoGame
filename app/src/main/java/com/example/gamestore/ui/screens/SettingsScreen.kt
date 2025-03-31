package com.example.gamestore.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gamestore.EmailPasswordActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

@Composable
fun SettingsScreen(navController: NavController) {
    val context = LocalContext.current
    val auth = remember { FirebaseAuth.getInstance() }
    val user = auth.currentUser
    val isGoogleUser = user?.providerData?.any { it.providerId == GoogleAuthProvider.PROVIDER_ID } == true

    var email by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var currentPassword by remember { mutableStateOf("") }
    var showEmailField by remember { mutableStateOf(false) }
    var showPasswordField by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var showPassword by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val emailPasswordActivity = EmailPasswordActivity()

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Settings", style = MaterialTheme.typography.headlineMedium)

        if (user != null) {
            Spacer(modifier = Modifier.height(16.dp))
            if (!isGoogleUser) {
                // Update Email
                Button(onClick = {
                    showEmailField = !showEmailField
                    showPasswordField = false
                    showDeleteConfirmation = false
                }) {
                    Text("Update Email")
                }
                if (showEmailField) {
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("New Email") }
                    )
                    OutlinedTextField(
                        value = currentPassword,
                        onValueChange = { currentPassword = it },
                        label = { Text("Current Password") },
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword
                                Toast.makeText(context, if (showPassword) "visible" else "invisible", Toast.LENGTH_SHORT).show()
                            }) {
                                Icon(
                                    imageVector = if (showPassword) Icons.Outlined.CheckCircle else Icons.Filled.CheckCircle,
                                    contentDescription = "Toggle Password Visibility"
                                )
                            }
                        }
                    )
                    Button(onClick = {
                        if (email.isBlank() || currentPassword.isBlank()) {
                            println("Error: Email and password cannot be empty")
                            Toast.makeText(context, "Error: Email and password cannot be empty", Toast.LENGTH_SHORT).show()
                        } else {
                            emailPasswordActivity.updateEmail(context, currentPassword, email) { success, message ->
                                if (success) {
                                    showEmailField = false
                                } else {
                                    println("Error: $message")
                                }
                            }
                        }
                    }) {
                        Text("Submit Email Update")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Update Password
                Button(onClick = {
                    showPasswordField = !showPasswordField
                    showEmailField = false
                    showDeleteConfirmation = false
                }) {
                    Text("Update Password")
                }
                if (showPasswordField) {
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text("New Password") }
                    )
                    OutlinedTextField(
                        value = currentPassword,
                        onValueChange = { currentPassword = it },
                        label = { Text("Current Password") },
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword
                                Toast.makeText(context, if (showPassword) "visible" else "invisible", Toast.LENGTH_SHORT).show()
                            })  {
                                Icon(
                                    imageVector = if (showPassword) Icons.Outlined.CheckCircle else Icons.Filled.CheckCircle,
                                    contentDescription = "Toggle Password Visibility"
                                )
                            }
                        }
                    )
                    Button(onClick = {
                        if (newPassword.isBlank() || currentPassword.isBlank()) {
                            println("Error: Password cannot be empty")
                            Toast.makeText(context, "Error: Password cannot be empty", Toast.LENGTH_SHORT).show()
                        } else {
                            emailPasswordActivity.updatePassword(context, currentPassword, newPassword) { success, message ->
                                if (success) {
                                    showPasswordField = false
                                } else {
                                    println("Error: $message")
                                }
                            }
                        }
                    }) {
                        Text("Submit Password Update")
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Delete Account
            Button(onClick = {
                showDeleteConfirmation = !showDeleteConfirmation
                showEmailField = false
                showPasswordField = false
            },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
                Text("Delete Account")
            }
            if (showDeleteConfirmation) {
                OutlinedTextField(
                    value = currentPassword,
                    onValueChange = { currentPassword = it },
                    label = { Text("Enter Current Password") },
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword
                            Toast.makeText(context, if (showPassword) "visible" else "invisible", Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(
                                imageVector = if (showPassword) Icons.Outlined.CheckCircle else Icons.Filled.CheckCircle,
                                contentDescription = "Toggle Password Visibility"
                            )
                        }
                    }
                )
                Button(
                    onClick = {
                        if (currentPassword.isBlank()) {
                            println("Error: Password cannot be empty")
                            Toast.makeText(context, "Error: Password cannot be empty", Toast.LENGTH_SHORT).show()
                        } else {
                            showDeleteDialog = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Confirm Delete")
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirm Deletion") },
            text = { Text("Are you sure you want to delete your account? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        emailPasswordActivity.deleteAccount(context, currentPassword) { success, message ->
                            if (!success) {
                                println("Error: $message")
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Proceed")
                }
            },
            dismissButton = {
                Button(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
