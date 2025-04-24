package com.example.gamestore.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
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
import com.example.gamestore.presentation.utils.SearchBar
import com.example.gamestore.data.model.Game
import com.example.gamestore.presentation.game.GameViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.LazyPagingItems
import androidx.compose.foundation.clickable

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val viewModel: GameViewModel = viewModel()
    val gamesPaging: LazyPagingItems<Game> = viewModel.games.collectAsLazyPagingItems()
    val topRatedGames = viewModel.topRatedGames.collectAsState().value
    val isLoading = viewModel.isLoading.collectAsState().value
    val errorMessage = viewModel.errorMessage.collectAsState().value
// just checking my email
    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        item {
            Column(modifier = Modifier.padding(start = 16.dp, top = 16.dp)) {
                Text(
                    text = "Welcome",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    //color = Color.Black
                    color = MaterialTheme.colorScheme.onBackground //change following mode
                )
                Text(
                    text = "What would you like to play?",
                    fontSize = 20.sp,
                    //color = Color.Black
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            //SearchBar(title = "Search for game", navController = navController)
            val searchQuery by viewModel.searchQuery.collectAsState()
            SearchBar(
                title = "Search for game",
                navController = navController,
                value = searchQuery,
                onValueChange = { viewModel.onQueryChanged(it) }
            )

            Text(
                text = "Top Rated Games",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                //color = Color.Black,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
            )

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(start = 8.dp)
            ) {
                items(topRatedGames) { game ->
                    TopRatedGameItem(game = game, onClick = {
                        navController.navigate("game_detail/${game.id}")
                    })
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        }

        stickyHeader {
            Surface(tonalElevation = 4.dp) {
                Text(
                    text = "All Games",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    //color = Color.Black,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(start = 16.dp, bottom = 8.dp, top = 8.dp)
                )
            }
        }

        if (isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        } else if (errorMessage != null) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Error: $errorMessage", color = Color.Red)
                }
            }
        } else {
            items(
                count = gamesPaging.itemCount,
                key = { index -> gamesPaging[index]?.id ?: index },
                contentType = { "Game" }
            ) { index ->
                val game = gamesPaging[index]
                if (game != null) {
                    GameItem(game = game, onClick = {
                        navController.navigate("game_detail/${game.id}")
                    })
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun TopRatedGameItem(game: Game, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .fillMaxHeight()
            .clickable { onClick() },
    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            val imageUrl = game.imageUrl.ifBlank { "https://via.placeholder.com/150" }
            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current)
                        .data(data = imageUrl)
                        .apply { crossfade(true) }
                        .build()
                ),
                contentDescription = game.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
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
                    text = game.title,
                    fontSize = 14.sp,
                    maxLines = 2,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun GameItem(game: Game, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            val imageUrl = game.imageUrl.ifBlank { "https://via.placeholder.com/150" }
            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current)
                        .data(imageUrl)
                        .apply { crossfade(true) }
                        .build()
                ),
                contentDescription = game.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

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
                    text = game.title,
                    fontSize = 18.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
        }
    }
}