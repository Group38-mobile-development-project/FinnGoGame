package com.example.gamestore.ui.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable

@Composable
fun MenuDropdown(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToGenre: () -> Unit,
    onNavigateToPlatform: () -> Unit,
    onNavigateToForumPage: () -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest
    ) {
        DropdownMenuItem(
            text = { Text("Home") },
            onClick = {
                onDismissRequest()
                onNavigateToHome()
            },
            leadingIcon = {
                Icon(Icons.Outlined.Home, contentDescription = null)
            }
        )

        DropdownMenuItem(
            text = { Text("Genre") },
            onClick = {
                onDismissRequest()
                onNavigateToGenre()
            },
            leadingIcon = {
                Icon(Icons.AutoMirrored.Outlined.List, contentDescription = null)
            }
        )
        DropdownMenuItem(
            text = { Text("Platform") },
            onClick = {
                onDismissRequest()
                onNavigateToPlatform()
            },
            leadingIcon = {
                Icon(Icons.AutoMirrored.Outlined.List, contentDescription = null)
            }
        )


        DropdownMenuItem(
            text = { Text("Forum Page") },
            onClick = {
                onDismissRequest()
                onNavigateToForumPage()
            },
            leadingIcon = {
                Icon(Icons.AutoMirrored.Outlined.List, contentDescription = null)
            }
        )
    }
}
