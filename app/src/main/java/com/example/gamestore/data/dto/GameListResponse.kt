package com.example.gamestore.data.dto

data class GameListResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<RawGame>
)
