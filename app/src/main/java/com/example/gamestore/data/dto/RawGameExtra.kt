package com.example.gamestore.data.dto

import com.google.gson.annotations.SerializedName

//PLATFORM
data class RawPlatformWrapper(
    @SerializedName("platform")
    val platform: RawPlatform
)

//data class RawPlatform(
//    val id: Int,
//    val name: String,
//    val slug: String
//)
data class RawPlatform(
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("slug")
    val slug: String,

    @SerializedName("games_count")
    val totalGames: Int,

    @SerializedName("image_background")
    val backgroundImage: String,

    @SerializedName("games")
    val associatedGames: List<RawGame>? = null
)


// COMPANY
data class RawCompany(
    val id: Int,
    val name: String,
    val slug: String
)

//STORE
data class RawStore(
    @SerializedName("store")
    val store: StoreInfo
)

data class StoreInfo(
    val id: Int,
    val name: String,
    val slug: String,
    val domain: String,
)
