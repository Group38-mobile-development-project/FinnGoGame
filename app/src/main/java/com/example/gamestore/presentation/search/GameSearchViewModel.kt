//
//package com.example.gamestore.presentation.search
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import androidx.paging.PagingData
//import androidx.paging.cachedIn
//import com.example.gamestore.data.model.Game
//import com.example.gamestore.data.repository.GameRepository
//import kotlinx.coroutines.flow.*
//
//import android.util.Log
//
//
//class GameSearchViewModel(
//    private val repository: GameRepository = GameRepository()
//) : ViewModel() {
//
//    private val _query = MutableStateFlow("")
//    val query: StateFlow<String> = _query
//
//    private fun normalizeQuery(query: String): String {
//        return query.trim().lowercase().replace(Regex("\\s+"), " ")
//    }
//
//
//    //private val _searchResults = MutableStateFlow<Flow<PagingData<Game>>>(emptyFlow())
////    val searchResults: StateFlow<Flow<PagingData<Game>>> = _searchResults
////
////    fun onQueryChange(newQuery: String) {
////        _query.value = newQuery
////        _searchResults.value = repository.searchGames(newQuery)
////    }
//
//    val searchResults: StateFlow<Flow<PagingData<Game>>> = query
//        .debounce(300)
//        .distinctUntilChanged()
//        .map { originalQuery ->
//            val cleanedQuery = normalizeQuery(originalQuery)
//            Log.d("QUERY", "Normalized: $cleanedQuery")
//            repository.searchGames(cleanedQuery)
//        }
//        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), repository.searchGames(""))
//
//}
//

package com.example.gamestore.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.gamestore.data.model.Game
import com.example.gamestore.data.repository.GameRepository
import kotlinx.coroutines.flow.*
import android.util.Log

class GameSearchViewModel(
    private val repository: GameRepository = GameRepository()
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    private fun normalizeQuery(query: String): String {
        return query.trim().lowercase().replace(Regex("\\s+"), " ")
    }

    val searchResults: StateFlow<Flow<PagingData<Game>>> = query
        .debounce(300)
        .distinctUntilChanged()
        .map { originalQuery ->
            val cleanedQuery = normalizeQuery(originalQuery)
            //Log.d("QUERY", "Normalized: $cleanedQuery")
            repository.searchGames(cleanedQuery)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), repository.searchGames(""))

    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
    }
}