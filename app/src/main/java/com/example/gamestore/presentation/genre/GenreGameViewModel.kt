package com.example.gamestore.presentation.genre

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.gamestore.data.model.Game
import com.example.gamestore.data.repository.GenreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest


class GenreGameViewModel(
    private val genreSlug: String,
    private val repository: GenreRepository = GenreRepository()
) : ViewModel() {

    private val _sort = MutableStateFlow<String?>(null)
    val sort: StateFlow<String?> = _sort

    fun onSortChanged(newSort: String?) {
        _sort.value = newSort
    }

    val games: Flow<PagingData<Game>> = _sort
        .flatMapLatest { sort ->
            repository.getGamesByGenrePaged(genreSlug, sort)
        }
        .cachedIn(viewModelScope)

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    fun onQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }
}
