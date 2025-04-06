package com.example.gamestore.data.model

data class Game(
    val id: Int = 0,
    val slug: String = "",
    val title: String = "",
    val releaseDate: String = "",
    val imageUrl: String = "",
    val averageRating: Double = 0.0,
    val topRating: Int = 0,
    val totalRatings: Int = 0,
    val metacriticScore: Int? = null,
    val estimatedPlaytime: Int = 0,
    val platforms: List<String> = emptyList(),
    val developers: List<String> = emptyList(),
    val publishers: List<String> = emptyList(),
    val stores: List<String> = emptyList(),
    val storesDomain: List<String> = emptyList(),
    val description: String = "",
    val genres: List<Genre> = emptyList()
)