//package com.example.gamestore.data.model
//
//data class Genre(
//    val id: Int,
//    val title: String,
//    val identifier: String,
//    val totalGames: Int,
//    val backgroundImage: String,
//    val associatedGames: List<Game>?
//)

package com.example.gamestore.data.dto

import com.google.gson.annotations.SerializedName

data class RawGenre(
    val id: Int,

    @SerializedName("name")
    val title: String,

    @SerializedName("slug")
    val identifier: String,

    @SerializedName("games_count")
    val totalGames: Int,

    @SerializedName("image_background")
    val backgroundImage: String,

    @SerializedName("games")
    val associatedGames: List<RawGame>? = null
)
