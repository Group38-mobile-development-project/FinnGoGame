package com.example.gamestore.data.model

data class Review(
    val id: String = "",
    val userId: String = "",
    val userEmail: String = "",
    val gameId: Int = 0,
    val content: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val upvotes: Int = 0,
    val downvotes: Int = 0,
    var replies: List<Reply> = emptyList()
)
data class Reply(
    val id: String = "",
    val userId: String = "",
    val userEmail: String = "",
    val reviewOwnerId: String = "",
    val content: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
