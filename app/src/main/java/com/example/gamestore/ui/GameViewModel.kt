package com.example.gamestore.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamestore.ui.model.Game
import com.example.gamestore.ui.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GameViewModel : ViewModel() {

    private val repository = GameRepository()

    // For the “All Games” list
    private val _games = MutableStateFlow<List<Game>>(emptyList())
    val games: StateFlow<List<Game>> = _games

    // For the “Top Rated” horizontal list
    private val _topRatedGames = MutableStateFlow<List<Game>>(emptyList())
    val topRatedGames: StateFlow<List<Game>> = _topRatedGames

    // Track loading/error states if you want
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val fetchedGames = repository.getGames()
                val fetchedTopRated = repository.getTopRatedGames()
                _games.value = fetchedGames
                _topRatedGames.value = fetchedTopRated
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun search(query: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val results = repository.searchGames(query)
                _games.value = results
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage
            } finally {
                _isLoading.value = false
            }
        }
    }
}
