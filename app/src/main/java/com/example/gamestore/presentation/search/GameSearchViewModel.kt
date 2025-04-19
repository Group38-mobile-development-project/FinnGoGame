
package com.example.gamestore.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.example.gamestore.data.model.Game
import com.example.gamestore.data.model.Genre
import com.example.gamestore.data.model.Platform
import com.example.gamestore.data.repository.GameRepository
import com.example.gamestore.data.repository.GenreRepository
import com.example.gamestore.data.repository.PlatformRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class GameSearchViewModel(
    private val gameRepository: GameRepository = GameRepository(),
    private val genreRepository: GenreRepository = GenreRepository(),
    private val platformRepository: PlatformRepository = PlatformRepository()
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    private val _genre = MutableStateFlow<String?>(null)
    val genre: StateFlow<String?> = _genre

    private val _platform = MutableStateFlow<String?>(null)
    val platform: StateFlow<String?> = _platform

    private val _genres = MutableStateFlow<List<Genre>>(emptyList())
    val genres: StateFlow<List<Genre>> = _genres

    private val _platforms = MutableStateFlow<List<Platform>>(emptyList())
    val platforms: StateFlow<List<Platform>> = _platforms

    init {
        viewModelScope.launch {
            _genres.value = genreRepository.getGenres()
            _platforms.value = platformRepository.getPlatforms()
        }
    }

    fun onQueryChange(newQuery: String) { _query.value = newQuery }
    fun onGenreSelected(newGenre: String?) { _genre.value = newGenre }
    fun onPlatformSelected(newPlatform: String?) { _platform.value = newPlatform }

    val searchResults: StateFlow<Flow<PagingData<Game>>> =
        combine(_query, _genre, _platform) { q, g, p -> Triple(q, g, p) }
            .debounce(300)
            .distinctUntilChanged()
            .map { (q, g, p) ->
                val cleaned = q.trim().lowercase().replace(Regex("[^a-z0-9\\s]"), "").replace(Regex("\\s+"), " ")
                gameRepository.searchGames(cleaned, g, p)
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyFlow())
}
