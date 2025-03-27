package com.example.gamestore.presentation.genre

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamestore.data.model.Genre
import com.example.gamestore.data.repository.GenreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GenreViewModel(
    private val genreRepository: GenreRepository = GenreRepository()
) : ViewModel() {

    private val _genres = MutableStateFlow<List<Genre>>(emptyList())
    val genres: StateFlow<List<Genre>> = _genres

    init {
        loadGenres()
    }

    private fun loadGenres() {
        viewModelScope.launch {
            _genres.value = genreRepository.getGenres()
        }
    }
}
