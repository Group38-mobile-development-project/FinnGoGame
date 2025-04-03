package com.example.gamestore.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.example.gamestore.ui.GameViewModel
import com.example.gamestore.ui.model.Game

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val viewModel: GameViewModel = viewModel()

    // Observe data from ViewModel
    val games = viewModel.games.collectAsState().value
    val topRatedGames = viewModel.topRatedGames.collectAsState().value
    val isLoading = viewModel.isLoading.collectAsState().value
    val errorMessage = viewModel.errorMessage.collectAsState().value

    // For controlling the search bar
    var searchQuery by remember { mutableStateOf("") }

    Column(modifier = modifier.fillMaxSize()) {

        // 1. Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                viewModel.search(searchQuery) // Trigger search as user types
            },
            label = { Text("Search Games...") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        // 2. Top-Rated Section
        Text(
            text = "Top Rated Games",
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
        )

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp) // Enough height for the card + image
                .padding(start = 8.dp)
        ) {
            items(topRatedGames) { game ->
                TopRatedGameItem(game)
                Spacer(modifier = Modifier.width(8.dp))
            }
        }

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        // 3. The main vertical list
        Text(
            text = "All Games",
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
        )

        // Display loading / error / data
        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f), // fill available vertical space
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            errorMessage != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Error: $errorMessage", color = Color.Red)
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                ) {
                    items(games) { game ->
                        GameItem(game = game)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun TopRatedGameItem(game: Game) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .fillMaxHeight(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column {
            // Image
            Image(
                painter = rememberImagePainter(
                    data = game.background_image ?: "",
                    builder = { crossfade(true) }
                ),
                contentDescription = game.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            )
            // Title & rating
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = game.name,
                    fontSize = 14.sp,
                    maxLines = 2,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Rating: ${game.rating}",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun GameItem(game: Game) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column {
            // Image
            Image(
                painter = rememberImagePainter(
                    data = game.background_image ?: "",
                    builder = { crossfade(true) }
                ),
                contentDescription = game.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            )
            // Info
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = game.name,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Rating: ${game.rating}",
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}
