package com.example.gamestore.presentation.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.gamestore.ui.theme.ForSearchBarBackgroundColor  // Import the color from your Color.kt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    title: String,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(25.dp),
                clip = true
            )
            .background(
                color = ForSearchBarBackgroundColor,
                shape = RoundedCornerShape(25.dp)
            )
    ) {
        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp
                )
            },
            leadingIcon = {
                IconButton(
                    onClick = { navController.navigate("search") },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = MaterialTheme.colorScheme.primary,
                focusedLeadingIconColor = MaterialTheme.colorScheme.onSurface,
                unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurface
            ),
            shape = RoundedCornerShape(25.dp),
            singleLine = true,
            keyboardActions = KeyboardActions(
                onSearch = {
                    navController.navigate("search")
                }
            )
        )
    }
}
