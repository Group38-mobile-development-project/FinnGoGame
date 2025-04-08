package com.example.gamestore.presentation.game

import androidx.compose.material3.ExperimentalMaterial3Api

package com.example.gamestore.presentation.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.gamestore.data.model.Game
import com.example.gamestore.data.model.GameRating // ✅ thêm dòng này
import com.example.gamestore.data.users.RatingRepository
import kotlinx.coroutines.flow.*

@OptIn(ExperimentalMaterial3Api::class)
class RatingViewModel(private val repository: RatingRepository) : ViewModel() {

    private val _gameRating = MutableLiveData<GameRating?>()
    val gameRating: LiveData<GameRating?> = _gameRating

    fun fetchGameRating(gameId: String) {
        repository.getAverageRating(gameId) {
            _gameRating.postValue(it)
        }
    }

    fun submitRating(userId: String, gameId: String, rating: Double) {
        repository.rateGame(userId, gameId, rating) {
            if (it) fetchGameRating(gameId)
        }
    }
}
