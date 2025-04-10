
package com.example.gamestore.presentation.game

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.gamestore.data.model.Game
import com.example.gamestore.data.repository.GameRepository
import com.example.gamestore.data.users.FavoritesRepository
import com.example.gamestore.data.users.RatingRepository
import com.example.gamestore.presentation.rating.GameRatingSection
import com.example.gamestore.presentation.rating.RatingViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavHostController
import com.example.gamestore.data.model.GameRating



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameDetailScreen( navController: NavHostController,  // Add NavController parameter
                      gameId: Int,
                      onRatingUpdated: ((Double, Int) -> Unit)? = null) {

    val repository = remember { GameRepository() }
    val favoritesRepository = remember { FavoritesRepository() }
    var game by remember { mutableStateOf<Game?>(null) }
    val scope = rememberCoroutineScope()
    var showFullDescription by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val auth = remember { FirebaseAuth.getInstance() }
    var isFavorite by remember { mutableStateOf<Boolean?>(null) }


    val ratingViewModel: RatingViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return RatingViewModel(RatingRepository(FirebaseFirestore.getInstance())) as T
            }
        }
    )

    LaunchedEffect(gameId) {
        scope.launch {
            game = repository.getGameById(gameId)
            game?.let {
                favoritesRepository.isFavorite(it.id) { result ->
                    isFavorite = result
                }
            }
        }
    }

    Scaffold(


    ) { padding ->
        game?.let { g ->
            val painter = rememberAsyncImagePainter(model = g.imageUrl)
            val genresText = g.genres.joinToString(", ") { it.title }
            val platformsText = g.platforms.joinToString(", ")
            val developersText = g.developers.joinToString(", ")
            val publishersText = g.publishers.joinToString(", ")
            val storePairs = g.stores.zip(g.storesDomain)

            //rating
//            LaunchedEffect(g.id) {
////                ratingViewModel.fetchGameRating(g.id.toString())
//                val userId = auth.currentUser?.uid
//                if (userId != null) {
//                    ratingViewModel.checkUserRated(userId, g.id.toString())
//                }
//            }
//rating
            LaunchedEffect(g.id) {
                ratingViewModel.fetchGameRating(g.id.toString())
                val userId = auth.currentUser?.uid
                if (userId != null) {
                    ratingViewModel.checkUserRated(userId, g.id.toString())
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Box {
                    Image(
                        painter = rememberAsyncImagePainter(model = g.imageUrl),
                        contentDescription = g.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp),
                        contentScale = ContentScale.Crop
                    )

                    // Back button positioned on top of the image
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.TopStart)
                            .background(
                                color = Color.Black.copy(alpha = 0.4f),
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = g.title,
                        fontSize = 27.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 0.dp),  // Optional vertical padding
                        textAlign = TextAlign.Center
                        )
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

                    //
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        repeat((g.averageRating).toInt()) {
                            Icon(Icons.Rounded.Star, contentDescription = null, tint = Color.Yellow)
                        }
                        Text(" Rating: ${String.format("%.1f", g.averageRating)}")
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
                                    val intent = Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("https://$storeDomain")
                                    )
                                    context.startActivity(intent)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(
                                        0xFF007BFF
                                    )
                                )
                            ) {
                                Text(" $storeName", color = Color.White)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    //Favorite button shows only if user is logged in
                    if (auth.currentUser != null) {
                        isFavorite?.let { fav ->
                            Button(
                                onClick = {
                                    if (fav) {
                                        // Remove from favorites
                                        favoritesRepository.removeFromFavorites(g.id) { success ->
                                            if (success) {
                                                isFavorite = false
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "Failed to remove from favorites",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    } else {
                                        // Add to favorites
                                        favoritesRepository.addToFavourites(g) { success ->
                                            if (success) {
                                                isFavorite = true
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "Failed to add to favorites",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = if (fav) Color.Gray else Color.Red),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = if (fav) Icons.Filled.Favorite else Icons.Outlined.Favorite,
                                    contentDescription = "Favorite",
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    if (fav) "Remove from Favorites" else "Add to Favorites",
                                    color = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        //rating
                        val gameRatingState by ratingViewModel.gameRating.observeAsState()
                        val hasRatedState by ratingViewModel.userHasRated.observeAsState()
                        val hasRated = hasRatedState?.first ?: false
                        val previousRating = hasRatedState?.second
                        val userId = auth.currentUser?.uid
                        if (userId != null) {
                            ratingViewModel.checkUserRated(userId, g.id.toString())
                        }

                        GameRatingSection(
                            currentRating = previousRating ?: gameRatingState?.averageRating,
                            enabled = true, //
                            onRatingSelected = { ratingValue ->
                                if (!hasRated) {
                                    val userId = auth.currentUser?.uid ?: return@GameRatingSection
                                    val gameIdString = g.id.toString()
                                    ratingViewModel.submitRating(userId, gameIdString, ratingValue)
                                    Toast.makeText(context, "Thanks for your rating!", Toast.LENGTH_SHORT).show()
                                } else {
                                    val ratedValue = previousRating?.toInt() ?: 0
                                    Toast.makeText(context, "You already rated $ratedValue ⭐!", Toast.LENGTH_SHORT).show()
                                }
                            }

                        )
                        Spacer(modifier = Modifier.height(32.dp))
                    }


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