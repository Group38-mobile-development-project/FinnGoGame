
package com.example.gamestore.data.model

data class Genre(
    val id: Int,
    val title: String,
    val identifier: String,
    val totalGames: Int,
    val backgroundImage: String,
    val associatedGames: List<Game>? = null
)
