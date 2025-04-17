
package com.example.gamestore.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.gamestore.data.model.Game
import com.example.gamestore.data.network.ApiClient
import com.example.gamestore.data.paging.GameSearchPagingSource
import com.example.gamestore.data.repository.GameRepository
import kotlinx.coroutines.flow.*
import android.util.Log

class GameSearchViewModel(
    private val repository: GameRepository = GameRepository()
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    fun onQueryChange(newQuery: String) {
        //Log.d("QUERY", "New query input: $newQuery")
        _query.value = newQuery
    }

    private fun cleanQuery(input: String): String {
        return input.trim()
            .lowercase()
            .replace(Regex("[^a-z0-9\\s]"), "") // Remove special characters, keep spaces
            .replace(Regex("\\s+"), " ") // Replace multiple spaces with a single space
    }

    val searchResults: StateFlow<Flow<PagingData<Game>>> = query
        .debounce(300)
        .distinctUntilChanged()
        .map { originalQuery ->
            val cleanedQuery = cleanQuery(originalQuery)
            //Log.d("QUERY", "Cleaned query: $cleanedQuery")
            if (cleanedQuery.isBlank()) {
                //Log.d("QUERY", "Empty query, returning empty Pager")
                Pager(PagingConfig(pageSize = 60, enablePlaceholders = false)) {
                    GameSearchPagingSource(ApiClient.apiService, "")
                }.flow
            } else {
                //Log.d("QUERY", "Searching for: $cleanedQuery")
                repository.searchGames(cleanedQuery)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), repository.searchGames(""))
}