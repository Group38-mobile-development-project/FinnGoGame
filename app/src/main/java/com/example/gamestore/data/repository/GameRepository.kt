package com.example.gamestore.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.gamestore.data.mapper.RawGameMapper
import com.example.gamestore.data.model.Game
import com.example.gamestore.data.network.ApiClient
import com.example.gamestore.data.paging.GamePagingSource
import kotlinx.coroutines.flow.Flow

class GameRepository {

    fun getGames(): Flow<PagingData<Game>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { GamePagingSource(ApiClient.apiService) }
        ).flow
    }

    suspend fun getGameById(gameId: Int): Game? {
        return try {
            val rawGame = ApiClient.apiService.fetchGameById(gameId)
            RawGameMapper.fromDto(rawGame)
        } catch (e: Exception) {
            null
        }
    }
}
