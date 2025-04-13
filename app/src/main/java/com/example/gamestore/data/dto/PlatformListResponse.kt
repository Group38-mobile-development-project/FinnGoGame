package com.example.gamestore.data.dto


data class PlatformListResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<RawPlatform>
)
