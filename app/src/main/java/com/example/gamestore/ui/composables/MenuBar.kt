package com.example.gamestore.ui.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.ChatBubble
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.material.icons.outlined.Devices

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
                Icon(Icons.Outlined.Category, contentDescription = null)
            }
        )
        DropdownMenuItem(
            text = { Text("Platform") },
            onClick = {
                onDismissRequest()
                onNavigateToPlatform()
            },
            leadingIcon = {
                Icon(Icons.Outlined.Devices, contentDescription = null)
            }
        )


        DropdownMenuItem(
            text = { Text("Forum Page") },
            onClick = {
                onDismissRequest()
                onNavigateToForumPage()
            },
            leadingIcon = {
                Icon(Icons.Outlined.ChatBubble, contentDescription = null)
            }
        )
    }
}
