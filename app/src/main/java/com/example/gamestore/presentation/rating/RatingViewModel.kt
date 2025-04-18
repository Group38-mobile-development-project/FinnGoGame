package com.example.gamestore.presentation.rating

import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.gamestore.data.model.GameRating
import com.example.gamestore.data.users.RatingRepository

class RatingViewModel(private val repository: RatingRepository) : ViewModel() {

    private val _gameRating = MutableLiveData<GameRating?>()
    val gameRating: LiveData<GameRating?> = _gameRating

    fun fetchGameRating(gameId: String) {
        repository.getAverageRating(gameId) {
            _gameRating.postValue(it)
        }
    }

    fun submitRating(userId: String, gameId: String, rating: Double, previousRating: Double? = null) {
        repository.rateGame(userId, gameId, rating, previousRating) {
            fetchGameRating(gameId)
            checkUserRated(userId, gameId)
        }
    }

    private val _userHasRated = MutableLiveData<Pair<Boolean, Double?>>()
    val userHasRated: LiveData<Pair<Boolean, Double?>> = _userHasRated

    fun checkUserRated(userId: String, gameId: String) {
        repository.hasUserRated(userId, gameId) { hasRated, rating ->
            _userHasRated.postValue(Pair(hasRated, rating))
        }
    }
}
