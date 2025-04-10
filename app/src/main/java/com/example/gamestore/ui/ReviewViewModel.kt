package com.example.gamestore.ui

import androidx.lifecycle.ViewModel
import com.example.gamestore.data.model.Game
import com.example.gamestore.data.users.ReviewRepository

class ReviewViewModel (): ViewModel() {
    private val repository = ReviewRepository()
    fun submitReview(game: Game, content: String, onComplete: (Boolean) -> Unit) {
        repository.submitReview(game, content, onComplete)
    }
}