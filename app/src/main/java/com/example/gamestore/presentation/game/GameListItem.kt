
package com.example.gamestore.presentation.game

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.StarHalf
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.gamestore.data.model.Game
import kotlin.math.roundToInt

@Composable
fun GameListItem(
    game: Game,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.LightGray
        )
        ) {
        Row(modifier = Modifier.padding(12.dp)) {
            Image(
                painter = rememberAsyncImagePainter(game.imageUrl),
                contentDescription = game.title,
                modifier = Modifier
                    .size(100.dp)
                    .padding(end = 12.dp),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = game.title,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val rounded = (game.averageRating * 2).roundToInt() / 2.0
                    val filledStars = rounded.toInt()
                    val hasHalfStar = (rounded - filledStars) == 0.5
                    val emptyStars = 5 - filledStars - if (hasHalfStar) 1 else 0

                    // Full stars (Yellow)
                    repeat(filledStars) {
                        Icon(
                            imageVector = Icons.Rounded.Star,
                            contentDescription = "Full Star",
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    if (hasHalfStar) {
                        Icon(
                            imageVector = Icons.Outlined.StarHalf,
                            contentDescription = "Half Star",
                            tint = Color(0xFFFFD700),
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
                Text(text = "Released: ${game.releaseDate}", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
