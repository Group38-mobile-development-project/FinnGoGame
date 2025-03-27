
package com.example.gamestore.data.model

data class Game(
    val id: Int,
    val slug: String,
    val title: String,
    val releaseDate: String,
    val imageUrl: String,
    val averageRating: Double,
    val topRating: Int,
    val totalRatings: Int,
    val metacriticScore: Int?,
    val estimatedPlaytime: Int,
    val platforms: List<String>,
    val developers: List<String>,
    val publishers: List<String>,
    val stores: List<String>,
    val storesDomain: List<String>,
    val description: String,
    val genres: List<Genre>
)