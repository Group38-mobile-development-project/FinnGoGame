package com.example.gamestore.ui.composables

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import com.example.gamestore.EmailPasswordActivity // Import your EmailPasswordActivity

@Composable
fun LeftAppBar(
    onNavigateToSignIn: () -> Unit,
    onSignOut: () -> Unit,
    drawerState: DrawerState
) {
    // Initialize Firebase Auth
    val auth = Firebase.auth
    val user = Firebase.auth.currentUser
    user?.let {
        for (profile in it.providerData) {
            // Id of the provider (ex: google.com)
            val providerId = profile.providerId

            // UID specific to the provider
            val uid = profile.uid

            // Name, email address, and profile photo Url
            val name = profile.displayName
            val email = profile.email
            val photoUrl = profile.photoUrl
        }
    }
    val scope = rememberCoroutineScope()
    ModalDrawerSheet {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(12.dp))
            // Display user email if logged in, otherwise show "Not logged in"
            // does not work properly
            Text(
                text = user?.email?: "Not logged in",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.titleLarge
            )

            HorizontalDivider()

            // Navigation items
            Text("Shop", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleMedium)
            NavigationDrawerItem(
                label = { Text("ShoppingCart") },
                selected = false,
                icon = { Icon(Icons.Outlined.ShoppingCart, contentDescription = null) },
                onClick = { /* Handle shopping cart */ }
            )
            NavigationDrawerItem(
                label = { Text("WishList") },
                selected = false,
                icon = { Icon(Icons.Outlined.Favorite, contentDescription = null) },
                onClick = { /* Handle wishlist */ }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Text("Management", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleMedium)
            NavigationDrawerItem(
                label = { Text("Settings") },
                selected = false,
                icon = { Icon(Icons.Outlined.Settings, contentDescription = null) },
                badge = { Text("20") }, // Placeholder badge
                onClick = { /* Handle settings */ }
            )

            // Show Login or Logout button based on auth state
            if (user == null) {
                NavigationDrawerItem(
                    label = { Text("Login") },
                    selected = false,
                    icon = { Icon(Icons.Outlined.AccountCircle, contentDescription = null) },
                    onClick = {
                        onNavigateToSignIn()
                        scope.launch {
                            drawerState.apply { close() }
                        }
                    } // Call parent function
                )
            } else {
                NavigationDrawerItem(
                    label = { Text("Logout") },
                    selected = false,
                    icon = { Icon(Icons.Outlined.AccountCircle, contentDescription = null) },
                    onClick = {
                        FirebaseAuth.getInstance().signOut() // Sign out user
                        onSignOut()
                        scope.launch {
                            drawerState.apply { close() }
                        }
                    }// Call parent function
                )
            }

            Spacer(Modifier.height(12.dp))
        }
    }
}
