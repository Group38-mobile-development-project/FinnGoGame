
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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.outlined.StarHalf
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavHostController
import java.util.Locale
import com.example.gamestore.data.repository.ReviewRepository
import com.example.gamestore.ui.ReviewViewModel
import com.example.gamestore.ui.theme.DeepBlue
import androidx.compose.runtime.collectAsState
import kotlin.math.roundToInt
import androidx.compose.ui.graphics.Color as ComposeColor



@Composable
fun GameDetailScreen( navController: NavHostController, gameId: Int) {
    val repository = remember { GameRepository() }
    val favoritesRepository = remember { FavoritesRepository() }
    var game by remember { mutableStateOf<Game?>(null) }
    val scope = rememberCoroutineScope()
    var showFullDescription by remember { mutableStateOf(false) }
    var reviewContent by remember { mutableStateOf("") }
    val context = LocalContext.current
    val auth = remember { FirebaseAuth.getInstance() }
    var isFavorite by remember { mutableStateOf<Boolean?>(null) }
    val currentUser = auth.currentUser
    val userId = currentUser?.uid

    val ratingViewModel: RatingViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return RatingViewModel(RatingRepository(FirebaseFirestore.getInstance())) as T
            }
        }
    )
    val gameRatingState by ratingViewModel.gameRating.observeAsState()

    val reviewViewModel: ReviewViewModel = viewModel(factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ReviewViewModel(ReviewRepository()) as T
        }
    })
    val reviews by reviewViewModel.reviews.collectAsState()
    val errorMessage by reviewViewModel.reviewErrorMessage.collectAsState()

    LaunchedEffect(gameId) {
        scope.launch {
            game = repository.getGameById(gameId)
            game?.let {
                ratingViewModel.fetchGameRating(it.id.toString())
                favoritesRepository.isFavorite(it.id) { result ->
                    isFavorite = result
                }
                reviewViewModel.fetchReviews(gameId)
            }
        }
    }

    Scaffold { padding ->
        game?.let { g ->
            val genresText = g.genres.joinToString(", ") { it.title }
            val platformsText = g.platforms.joinToString(", ")
            val developersText = g.developers.joinToString(", ")
            val publishersText = g.publishers.joinToString(", ")
            val storePairs = g.stores.zip(g.storesDomain)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(padding)
            ) {

                // Game Image
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

                // Game Title
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = g.title,
                        fontSize = 27.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 0.dp),
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

                    HorizontalDivider(
                        modifier = Modifier
                            .padding(vertical = 6.dp)
                            .height(1.dp),
                        color = Color.LightGray
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Information",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Platforms", fontWeight = FontWeight.Bold)
                                Text(platformsText)
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("Genre", fontWeight = FontWeight.Bold)
                                Text(genresText)
                            }
                            Spacer(modifier = Modifier.width(20.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Release date", fontWeight = FontWeight.Bold)
                                Text(g.releaseDate)
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("Developer", fontWeight = FontWeight.Bold)
                                Text(developersText)
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("Estimated Playtime", fontWeight = FontWeight.Bold)
                                Text(
                                    text = if (g.estimatedPlaytime > 0)
                                        "${g.estimatedPlaytime} hours"
                                    else
                                        "—",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text("Publisher", fontWeight = FontWeight.Bold)
                        Text(publishersText)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween // Space out the content between the left and right sides
                        ) {
                            // Left side: Rating number with "Rating" label above it
                            Column(
                                horizontalAlignment = Alignment.Start
                            ) {
                                Text(
                                    text = "Rating (RAWG)", // Label above the rating number
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Gray // A neutral color for the label
                                )
                                Text(
                                    text = String.format(Locale.getDefault(),"%.1f", g.averageRating), // Display the rating number
                                    fontSize = 20.sp, // Make it big
                                    fontWeight = FontWeight.Bold,
                                    //color = Color.Black // Black color for the rating number
                                    color = MaterialTheme.colorScheme.onBackground
                                    )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                val rounded = (g.averageRating * 2).roundToInt() / 2.0
                                val filledStars = rounded.toInt()
                                val hasHalfStar = (rounded - filledStars) == 0.5
                                val emptyStars = 5 - filledStars - if (hasHalfStar) 1 else 0
                                // Full stars (Yellow)
                                repeat(filledStars) {
                                    Icon(
                                        imageVector = Icons.Rounded.Star,
                                        contentDescription = "Full Star",
                                        tint = ComposeColor(0xFFFFD700),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                if (hasHalfStar) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Outlined.StarHalf,
                                        contentDescription = "Half Star",
                                        tint = ComposeColor(0xFFFFD700),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                repeat(emptyStars) {
                                    Icon(
                                        imageVector = Icons.Rounded.Star,
                                        contentDescription = "Empty Star",
                                        tint = Color.Gray,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                        }
                    }

                    HorizontalDivider(
                        modifier = Modifier
                            .padding(vertical = 6.dp)
                            .height(1.dp),
                        color = Color.LightGray
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(horizontalAlignment = Alignment.Start) {
                            Text(
                                text = "Rating (FinnGoGame)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                            Text(
                                text = String.format(Locale.getDefault(), "%.1f", gameRatingState?.averageRating ?: 0.0),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                //color = Color.Black
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val appRounded = ((gameRatingState?.averageRating ?: 0.0) * 2).roundToInt() / 2.0
                            val appFull = appRounded.toInt()
                            val appHalf = (appRounded - appFull) == 0.5
                            val appEmpty = 5 - appFull - if (appHalf) 1 else 0

                            repeat(appFull) {
                                Icon(Icons.Rounded.Star, "Full Star", tint = ComposeColor(0xFFFFD700), modifier = Modifier.size(20.dp))
                            }
                            if (appHalf) {
                                Icon(Icons.AutoMirrored.Outlined.StarHalf, "Half Star", tint = ComposeColor(0xFFFFD700), modifier = Modifier.size(20.dp))
                            }
                            repeat(appEmpty) {
                                Icon(Icons.Rounded.Star, "Empty Star", tint = Color.Gray, modifier = Modifier.size(20.dp))
                            }
                        }
                    }

                    HorizontalDivider(
                        modifier = Modifier
                            .padding(vertical = 6.dp)
                            .height(1.dp),
                        color = Color.LightGray
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Store section
                    Text("Buy on", fontWeight = FontWeight.Bold)

                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        items(storePairs) { (storeName, storeDomain) ->
                            Button(
                                onClick = {
                                    val intent = Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("https://$storeDomain")
                                    )
                                    context.startActivity(intent)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF87CEFA)
                                ),
                                modifier = Modifier
                                    .height(40.dp)
                                    .clip(RoundedCornerShape(1.dp))
                            ) {
                                Text(" $storeName", color = Color.White)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    HorizontalDivider(
                        modifier = Modifier
                            .padding(vertical = 6.dp)
                            .height(1.dp),
                        color = Color.LightGray
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    isFavorite?.let { fav ->
                        Button(
                            onClick = {
                                if (fav) {
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
                                    favoritesRepository.addToFavorites(g) { success ->
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
                            colors = ButtonDefaults.buttonColors(containerColor = if (fav) Color.Gray else DeepBlue),
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
                    Spacer(modifier = Modifier.height(20.dp))
                    //rating section (Final rating area)
                    val hasRatedState by ratingViewModel.userHasRated.observeAsState()
                    val hasRated = hasRatedState?.first ?: false
                    val previousRating = hasRatedState?.second
                    if (userId != null) {
                        ratingViewModel.checkUserRated(userId, g.id.toString())
                    }
                    GameRatingSection(
                        currentRating = previousRating ?: gameRatingState?.averageRating,
                        enabled = true,
                        onRatingSelected = { newRatingValue ->
                            val userIdCheck = userId ?: return@GameRatingSection
                            val gameIdString = g.id.toString()
                            ratingViewModel.submitRating(
                                userIdCheck,
                                gameIdString,
                                newRatingValue,
                                previousRating // pass previousRating for edit detection
                            )
                            Toast.makeText(
                                context,
                                if (hasRated) "Rating updated!" else "Thanks for your rating!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    ReviewInputSection(
                        reviewContent = reviewContent,
                        onContentChange = { reviewContent = it },
                        onSubmit = {
                            reviewViewModel.submitReview(g.id, reviewContent)
                            reviewContent = ""
                        },
                        errorMessage = errorMessage,
                        onErrorShown = { reviewViewModel.clearReviewError() }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    reviews.forEach { review ->
                        // Track the upvote and downvote states for each review
                        var hasUpvoted by remember { mutableStateOf(false) }
                        var hasDownvoted by remember { mutableStateOf(false) }
                        // Fetch upvote/downvote status when the review is first displayed
                        LaunchedEffect(review.id) {
                            reviewViewModel.hasUpvoted(review.gameId, review.id, userId.toString()) { upvoted ->
                                hasUpvoted = upvoted
                            }
                            reviewViewModel.hasDownvoted(review.gameId, review.id, userId.toString()) { downvoted ->
                                hasDownvoted = downvoted
                            }
                        }
                        ReviewCard(
                            review = review,
                            isOwn = review.userId == userId, // Compare user IDs to check if it's the current user's review
                            isUserLoggedIn = userId != null,
                            onUpdate = { updatedContent -> reviewViewModel.updateReview(review.gameId, review.id, updatedContent) },
                            onDelete = { reviewViewModel.deleteReview(review.gameId, review.id) },
                            onUpvote = { reviewViewModel.voteReview(review.gameId, review.id, upvote = true) },
                            onDownvote = { reviewViewModel.voteReview(review.gameId, review.id, upvote = false) },
                            hasUpvoted = hasUpvoted,
                            hasDownvoted = hasDownvoted,
                            onReply = { content -> reviewViewModel.submitReply(gameId, review.id, content) },
                            onEditReply = { replyId, newContent ->
                                reviewViewModel.updateReply(gameId, review.id, replyId, newContent)
                            },
                            onDeleteReply = { replyId ->
                                reviewViewModel.deleteReply(gameId, review.id, replyId)
                            }
                        )
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

@Composable
fun ReviewInputSection(
    reviewContent: String,
    onContentChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onErrorShown: () -> Unit,
    errorMessage: String? // <- add this parameter
) {
    val snackbarHostState = remember { SnackbarHostState() }

    // Show the error message as a Snackbar
    LaunchedEffect(errorMessage) {
        if (!errorMessage.isNullOrEmpty()) {
            snackbarHostState.showSnackbar(errorMessage)
            onErrorShown()
        }
    }

    Column {
        SnackbarHost(hostState = snackbarHostState)
        Text("Write a Review", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        OutlinedTextField(
            value = reviewContent,
            onValueChange = onContentChange,
            label = { Text("Your review") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(onClick = onSubmit, modifier = Modifier.padding(top = 8.dp)) {
            Text("Submit Review")
        }
    }
}

@Composable
fun ReviewCard(
    review: com.example.gamestore.data.model.Review,
    isOwn: Boolean,
    isUserLoggedIn: Boolean,
    onUpdate: (String) -> Unit,
    onDelete: () -> Unit,
    onUpvote: () -> Unit,
    onDownvote: () -> Unit,
    hasUpvoted: Boolean,
    hasDownvoted: Boolean,
    onReply: (String) -> Unit,
    onEditReply: (replyId: String, newContent: String) -> Unit,
    onDeleteReply: (replyId: String) -> Unit
) {
    var editing by remember { mutableStateOf(false) }
    var updatedContent by remember { mutableStateOf(review.content) }

    var replying by remember { mutableStateOf(false) }
    var replyContent by remember { mutableStateOf("") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(review.userEmail, fontWeight = FontWeight.Bold)
            if (editing) {
                OutlinedTextField(
                    value = updatedContent,
                    onValueChange = { updatedContent = it },
                    modifier = Modifier.fillMaxWidth()
                )
                Row {
                    Button(onClick = {
                        onUpdate(updatedContent)
                        editing = false
                    }) { Text("Save") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { editing = false }) { Text("Cancel") }
                }
            } else {
                Text(review.content)
                // Voting
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onUpvote) {
                        Icon(
                            imageVector = Icons.Filled.ArrowUpward,
                            contentDescription = "Upvote",
                            tint = if (hasUpvoted) Color.Green else Color.Gray
                        )
                    }
                    Text("${review.upvotes}")

                    IconButton(onClick = onDownvote) {
                        Icon(
                            imageVector = Icons.Filled.ArrowDownward,
                            contentDescription = "Downvote",
                            tint = if (hasDownvoted) Color.Red else Color.Gray
                        )
                    }
                    Text("${review.downvotes}")
                }
                Row {
                    if (isOwn) {
                        TextButton(onClick = { editing = true }) { Text("Edit") }
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(onClick = onDelete) { Text("Delete") }
                    }
                    // ✅ Add reply button
                    if (isUserLoggedIn) {
                        TextButton(onClick = { replying = !replying }) {
                            Text(if (replying) "Cancel" else "Reply")
                        }
                    }
                }
                // ✅ Reply input section
                if (replying) {
                    OutlinedTextField(
                        value = replyContent,
                        onValueChange = { replyContent = it },
                        label = { Text("Write a reply...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp)
                    )
                    Button(
                        onClick = {
                            onReply(replyContent)
                            replyContent = ""
                            replying = false
                        },
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Text("Submit Reply")
                    }
                }
            }
            // ✅ Show Replies
            if (review.replies.isNotEmpty()) {
                Column(modifier = Modifier.padding(start = 8.dp)) {
                    review.replies.forEach { reply ->
                        var isEditingReply by remember { mutableStateOf(false) }
                        var editedReplyContent by remember { mutableStateOf(reply.content) }
                        Column(modifier = Modifier.padding(start = 8.dp)) {
                            if (isEditingReply) {
                                OutlinedTextField(
                                    value = editedReplyContent,
                                    onValueChange = { editedReplyContent = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    label = { Text("Edit Reply") }
                                )
                                Row {
                                    Button(onClick = {
                                        onEditReply(reply.id, editedReplyContent)
                                        isEditingReply = false
                                    }) {
                                        Text("Save")
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    TextButton(onClick = { isEditingReply = false }) {
                                        Text("Cancel")
                                    }
                                }
                            } else {
                                Text("- ${reply.userEmail}: ${reply.content}")
                                if (reply.userId == FirebaseAuth.getInstance().currentUser?.uid) {
                                    ReplyActionButtons(
                                        onEditClick = { isEditingReply = true },
                                        onDeleteClick = { onDeleteReply(reply.id) },
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReplyActionButtons(
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        TextButton(onClick = onEditClick) {
            Text("Edit")
        }
        Spacer(modifier = Modifier.width(8.dp))
        TextButton(onClick = onDeleteClick) {
            Text("Delete")
        }
    }
}