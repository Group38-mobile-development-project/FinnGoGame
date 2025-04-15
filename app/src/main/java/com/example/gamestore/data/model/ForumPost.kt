package com.example.gamestore.data.model

data class ForumPost(
    val userId: String = "",
    val username: String = "",
    val gameId: String = "",
    val title: String = "",
    val content: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
