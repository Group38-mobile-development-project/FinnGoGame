package com.example.gamestore.presentation.platform

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.gamestore.data.model.Game
import com.example.gamestore.data.repository.PlatformRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PlatformGameViewModel(
    platformSlug: String,
    repository: PlatformRepository = PlatformRepository()
) : ViewModel() {
    val games: Flow<PagingData<Game>> = repository
        .getGamesByPlatform(platformSlug)
        .cachedIn(viewModelScope)

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    fun onQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }
}

