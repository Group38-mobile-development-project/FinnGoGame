package com.example.gamestore.data.dto

data class GenreListResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<RawGenre>
)
