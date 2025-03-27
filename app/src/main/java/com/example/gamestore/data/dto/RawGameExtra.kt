package com.example.gamestore.data.dto

import com.google.gson.annotations.SerializedName

//PLATFORM
data class RawPlatformWrapper(
    @SerializedName("platform")
    val platform: RawPlatform
)

data class RawPlatform(
    val id: Int,
    val name: String,
    val slug: String
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
