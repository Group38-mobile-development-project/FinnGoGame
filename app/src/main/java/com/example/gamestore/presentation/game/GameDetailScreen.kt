package com.example.gamestore.presentation.game

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.gamestore.data.model.Game
import com.example.gamestore.data.repository.GameRepository
import kotlinx.coroutines.launch
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext

//debug
import android.util.Log


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameDetailScreen(gameId: Int) {
    val repository = remember { GameRepository() }
    var game by remember { mutableStateOf<Game?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(gameId) {
        scope.launch {
            val result = repository.getGameById(gameId)
            game = result
            //Log.d("DETAIL", "Loaded game: ${result?.title}")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = game?.title ?: "Loading...") })
        }
    ) { padding ->
        game?.let { g ->
            val painter = rememberAsyncImagePainter(model = g.imageUrl)
            val genresText = g.genres.joinToString(", ") { it.title }
            val platformsText = g.platforms.joinToString(", ")
            val developersText = g.developers.joinToString(", ")
            val publishersText = g.publishers.joinToString(", ")
            val storePairs = g.stores.zip(g.storesDomain)


            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                Image(
                    painter = painter,
                    contentDescription = g.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .clip(MaterialTheme.shapes.medium),
                    contentScale = ContentScale.Crop
                )

                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = g.title, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(text = "Release Date: ${g.releaseDate}")
                    Text(text = "Metascore: ${g.metacriticScore ?: "N/A"}")
                    Text(text = "Genres: $genresText")
                    Text(text = "Estimated playtime: ${g.estimatedPlaytime} hours")
                    Text(text = "Platforms: $platformsText")
                    Text(text = "Developers: $developersText")
                    Text(text = "Publishers: $publishersText")

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        repeat((g.averageRating / 1).toInt()) {
                            Icon(Icons.Rounded.Star, contentDescription = null, tint = Color.Yellow)
                        }
                        Text("Rating:  ${g.averageRating}")
                    }
                    Spacer(modifier = Modifier.height(26.dp))

                    storePairs.forEach { (storeName, storeDomain) ->
                        val context = LocalContext.current

                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://$storeDomain"))
                                context.startActivity(intent)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007BFF))
                        ) {
                            Column {
                                Text(
                                    text = "Buy on $storeName",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White
                                )
                                Text(
                                    text = storeDomain,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.LightGray
                                )
                            }
                        }
                    }


                    Spacer(modifier = Modifier.height(26.dp))
                    Button(
                        onClick = { /* TODO: install */ },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007BFF)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Favorite", color = Color.White)
                    }
                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Description",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(text = g.description, style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        } ?: Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}
