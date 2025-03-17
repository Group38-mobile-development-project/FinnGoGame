package com.example.gamestore.ui.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier

@Composable
fun LeftAppBar(onNavigateToSignIn: () -> Unit) {
    ModalDrawerSheet {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(12.dp))
            Text("Account name placeholder", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleLarge)
            HorizontalDivider()

            Text("Shop", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleMedium)
            NavigationDrawerItem(
                label = { Text("ShoppingCart") },
                selected = false,
                icon = { Icon(Icons.Outlined.ShoppingCart, contentDescription = null) },
                onClick = { /* Handle click */ }
            )
            NavigationDrawerItem(
                label = { Text("WishList") },
                selected = false,
                icon = { Icon(Icons.Outlined.Favorite, contentDescription = null) },
                onClick = { /* Handle click */ }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Text("Management", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleMedium)
            NavigationDrawerItem(
                label = { Text("Settings") },
                selected = false,
                icon = { Icon(Icons.Outlined.Settings, contentDescription = null) },
                badge = { Text("20") }, // Placeholder
                onClick = { /* Handle click */ }
            )
            NavigationDrawerItem(
                label = { Text("Login") },
                selected = false,
                icon = { Icon(Icons.Outlined.AccountCircle, contentDescription = null) },
                onClick = {
                    onNavigateToSignIn()  // Call the callback function
                }
            )
            Spacer(Modifier.height(12.dp))
        }
    }
}
