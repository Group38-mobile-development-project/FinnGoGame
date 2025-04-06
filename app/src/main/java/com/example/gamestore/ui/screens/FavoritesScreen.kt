package com.example.gamestore.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.compose.material3.TextFieldDefaults
import com.example.gamestore.presentation.game.GameListItem
import com.example.gamestore.ui.FavoritesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    navController: NavController,
    viewModel: FavoritesViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onGameClick: (Int) -> Unit
) {
    val allFavorites by viewModel.favoriteGames.collectAsState()
    var query by remember { mutableStateOf("") }

    // Sort options
    var expanded by remember { mutableStateOf(false) }
    var sortOption by remember { mutableStateOf("Name") }

    // Filter and sort favorites
    val filteredFavorites = allFavorites
        .filter { it.title.contains(query, ignoreCase = true) }
        .let {
            when (sortOption) {
                "Name" -> it.sortedBy { game -> game.title }
                "Rating" -> it.sortedByDescending { game -> game.averageRating }
                "Release Date" -> it.sortedByDescending { game -> game.releaseDate }
                "Added Date" -> it
                else -> it
            }
        }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextField(
                            value = query,
                            onValueChange = { query = it },
                            placeholder = { Text("Search favorites...") },
                            singleLine = true,
                            modifier = Modifier.weight(0.8f),
                            textStyle = MaterialTheme.typography.bodySmall,
                            colors = TextFieldDefaults.colors(
                                MaterialTheme.colorScheme.surfaceVariant
                            )
                        )
                        Box {
                            IconButton(onClick = { expanded = true }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = "Sort Options")
                            }
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Name") },
                                    onClick = {
                                        sortOption = "Name"
                                        expanded = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Rating") },
                                    onClick = {
                                        sortOption = "Rating"
                                        expanded = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Release Date") },
                                    onClick = {
                                        sortOption = "Release Date"
                                        expanded = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Added Date") },
                                    onClick = {
                                        sortOption = "Added Date"
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(filteredFavorites) { game ->
                GameListItem(game = game, onClick = { onGameClick(game.id) })
            }
        }
    }
}