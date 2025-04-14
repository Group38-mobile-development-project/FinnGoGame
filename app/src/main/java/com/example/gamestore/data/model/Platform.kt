package com.example.gamestore.data.model

data class Platform(
    val id: Int,
    val name: String,
    val slug: String,
    val totalGames: Int,
    val backgroundImage: String,
    val associatedGames: List<Game>? = null
)
