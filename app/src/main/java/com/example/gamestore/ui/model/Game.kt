package com.example.gamestore.ui.model

// Basic data classes for RAWG responses

data class Game(
    val id: Int,
    val name: String,
    val background_image: String?,
    val rating: Double
)

data class GameResponse(
    val results: List<Game>
)
