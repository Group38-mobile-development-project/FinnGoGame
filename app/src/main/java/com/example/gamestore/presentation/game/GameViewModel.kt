package com.example.gamestore.presentation.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.gamestore.data.model.Game
import com.example.gamestore.data.repository.GameRepository
import kotlinx.coroutines.flow.Flow

class GameViewModel(
    private val gameRepository: GameRepository = GameRepository()
) : ViewModel() {

    val games: Flow<PagingData<Game>> = gameRepository.getGames()
        .cachedIn(viewModelScope)
}
