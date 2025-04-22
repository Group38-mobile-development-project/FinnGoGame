package com.example.gamestore.data.model

data class ForumPost(
    val id: String = "",         // ← new
    val userId: String = "",
    val username: String = "",
    val gameId: String = "",
    val title: String = "",
    val content: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val comments: List<String> = emptyList()  // Add this line if comments is a list of strings
)
