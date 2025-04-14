package com.example.gamestore.presentation.rating

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun GameRatingSection(
    currentRating: Double?,
    onRatingSelected: (Double) -> Unit,
    enabled: Boolean = true
) {
    var selectedRating by remember { mutableStateOf(currentRating ?: 0.0) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Your Rating", fontWeight = FontWeight.Bold)
        Row(verticalAlignment = Alignment.CenterVertically) {
            for (i in 1..5) {
                IconButton(
                    onClick = {
                        if (enabled) {
                            selectedRating = i.toDouble()
                            onRatingSelected(i.toDouble())
                        }
                    },
                )
                {
                    Icon(
                        imageVector = Icons.Rounded.Star,
                        contentDescription = "Star $i",
//                        tint = if (i <= selectedRating) Color.Yellow else Color.Gray
                        tint = if (i <= (currentRating ?: 0.0)) Color(0xFFFFD700) else Color.Gray

                    )
                }
            }
        }

//        if (!enabled && currentRating != null) {
//            Text("You rated: ${currentRating.toInt()} star(s)")
//        }
    }
}
