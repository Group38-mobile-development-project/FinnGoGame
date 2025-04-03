
package com.example.gamestore.presentation.game

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.gamestore.data.model.Game
import com.example.gamestore.data.repository.GameRepository
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.ui.layout.ContentScale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameDetailScreen(gameId: Int) {
    val repository = remember { GameRepository() }
    var game by remember { mutableStateOf<Game?>(null) }
    val scope = rememberCoroutineScope()
    var showFullDescription by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(gameId) {
        scope.launch {
            game = repository.getGameById(gameId)
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
                    Text(text = g.title, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))

                    val maxLines = if (showFullDescription) Int.MAX_VALUE else 4
                    Text(
                        text = g.description,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = maxLines
                    )
                    if (!showFullDescription) {
                        TextButton(onClick = { showFullDescription = true }) {
                            Text("Read more")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Platforms", fontWeight = FontWeight.Bold)
                            Text(platformsText)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Genre", fontWeight = FontWeight.Bold)
                            Text(genresText)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Release date", fontWeight = FontWeight.Bold)
                            Text(g.releaseDate)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Developer", fontWeight = FontWeight.Bold)
                            Text(developersText)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Publisher", fontWeight = FontWeight.Bold)
                    Text(publishersText)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        repeat((g.averageRating / 1).toInt()) {
                            Icon(Icons.Rounded.Star, contentDescription = null, tint = Color.Yellow)
                        }
                        Text(" Rating: ${g.averageRating}")
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    //store
                    Text("Buy on", fontWeight = FontWeight.Bold)
                    @OptIn(ExperimentalLayoutApi::class)
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        storePairs.forEach { (storeName, storeDomain) ->
                            Button(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://$storeDomain"))
                                    context.startActivity(intent)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007BFF))
                            ) {
                                Text(" $storeName", color = Color.White)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { /* Favorite action */ },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Favorite, contentDescription = "Favorite", tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Favorite", color = Color.White)
                    }

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