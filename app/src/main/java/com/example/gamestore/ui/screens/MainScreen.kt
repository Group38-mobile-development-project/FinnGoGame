package com.example.gamestore.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.gamestore.ui.GameViewModel
import com.example.gamestore.ui.model.Game
import com.example.gamestore.presentation.utils.SearchBar // Add search bar here


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

        // "Welcome" and "What would you like to play?" text with specific styling
        Column(modifier = Modifier.padding(start = 16.dp, top = 16.dp)) {
            Text(
                text = "Welcome",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold, // Makes "Welcome" bold
                color = Color.Black,  // Set color to black
            )
            Text(
                text = "What would you like to play?",
                fontSize = 20.sp,
                color = Color.Black,  // Set color to black
            )
        }

        // Search bar
        SearchBar(title = "Search for game", navController = navController)

        // 1. Top-Rated Section
        Text(
            text = "Top Rated Games",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,  // Bold the title for better emphasis
            color = Color.Black,
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
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

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // 2. All Games Section
        Text(
            text = "All Games",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,  // Bold the title for better emphasis
            color = Color.Black,
            modifier = Modifier
                .padding(start = 16.dp, bottom = 8.dp)
        )

        // Display loading / error / data for All Games section
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
                        GameItem(game = game)  // Ensure images are loaded here
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
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            val imageUrl = game.background_image ?: "https://via.placeholder.com/150"
            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current)
                        .data(data = imageUrl)
                        .apply(block = fun ImageRequest.Builder.() {
                            crossfade(true)
                        }).build()
                ),
                contentDescription = game.name,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(0.dp),
                contentScale = ContentScale.Crop
            )

            // Semi-transparent gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)  // Shorter overlay for smaller cards
                    .align(Alignment.BottomStart)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.7f)
                            ),
                            startY = 0f,
                            endY = 60f
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
            ) {
                Text(
                    text = game.name,
                    fontSize = 14.sp,
                    maxLines = 2,
                    color = Color.White,  // Changed to white
                    fontWeight = FontWeight.Bold
                )

            }
        }
    }
}

@Composable
fun GameItem(game: Game) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            val imageUrl = game.background_image ?: "https://via.placeholder.com/150"
            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current)
                        .data(imageUrl)
                        .apply(block = fun ImageRequest.Builder.() {
                            crossfade(true)
                        }).build()
                ),
                contentDescription = game.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Semi-transparent gradient overlay at the bottom
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .align(Alignment.BottomStart)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.7f)
                            ),
                            startY = 0f,
                            endY = 100f
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
            ) {
                Text(
                    text = game.name,
                    fontSize = 18.sp,
                    color = Color.White,  // Changed to white
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

            }
        }
    }
}
