package com.example.gamestore.data.dto

import com.google.gson.annotations.SerializedName

data class RawGame(
    val id: Int = 0,

    @SerializedName("slug")
    val slug: String = "",

    @SerializedName("name")
    val title: String = "Unknown Title",

    @SerializedName("released")
    val releaseDate: String = "N/A",

    @SerializedName("background_image")
    val imageUrl: String = "",

    @SerializedName("rating")
    val averageRating: Double = 0.0,

    @SerializedName("rating_top")
    val topRating: Int = 0,

    @SerializedName("ratings_count")
    val totalRatings: Int = 0,

    @SerializedName("metacritic")
    val metacriticScore: Int? = null,

    @SerializedName("playtime")
    val estimatedPlaytime: Int = 0,

    @SerializedName("platforms")
    val platforms: List<RawPlatformWrapper> = emptyList(),

    @SerializedName("developers")
    val developers: List<RawCompany>? = emptyList(),

    @SerializedName("publishers")
    val publishers: List<RawCompany>? = emptyList(),

    @SerializedName("stores")
    val stores: List<RawStore>? = emptyList(),

    @SerializedName("description_raw")
    val description: String = "",

    @SerializedName("genres")
    val genres: List<RawGenre> = emptyList(),

    val addedCount: Int = 0
)
