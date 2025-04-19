
package com.example.gamestore.presentation.game

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.gamestore.data.model.Game
import com.example.gamestore.data.repository.GameRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class GameViewModel(
    private val repository: GameRepository = GameRepository()
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    fun onQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    val games: Flow<PagingData<Game>> = _searchQuery
        .debounce(300)
        .distinctUntilChanged()
        .flatMapLatest { query ->
            if (query.isBlank()) {
                repository.getGames()
            } else {
                repository.searchGames(query = query, genre = null, platform = null)

            }
        }
        .cachedIn(viewModelScope)

    private val _topRatedGames = MutableStateFlow<List<Game>>(emptyList())
    val topRatedGames: StateFlow<List<Game>> = _topRatedGames

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        fetchTopRatedGames()
    }

    private fun fetchTopRatedGames() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val topGames = repository.getTopRatedGames()
                _topRatedGames.value = topGames
                //Log.d("GameViewModel", "Fetched ${topGames.size} top-rated games")
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load top-rated games"
                //Log.e("GameViewModel", "Error loading top-rated games", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}
