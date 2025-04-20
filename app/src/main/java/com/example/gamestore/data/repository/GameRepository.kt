package com.example.gamestore.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.gamestore.data.mapper.RawGameMapper
import com.example.gamestore.data.model.Game
import com.example.gamestore.data.network.ApiClient
import com.example.gamestore.data.paging.GamePagingSource
import kotlinx.coroutines.flow.Flow
import com.example.gamestore.data.paging.GameSearchPagingSource
//debug
import android.util.Log


class GameRepository {

    fun getGames(): Flow<PagingData<Game>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { GamePagingSource(ApiClient.apiService) }
        ).flow
    }

    //add search

    fun searchGames(query: String, genre: String?, platform: String?): Flow<PagingData<Game>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = {
                GameSearchPagingSource(
                    api = ApiClient.apiService,
                    query = query,
                    genre = genre,
                    platform = platform
                )
            }
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



    // // New: fetch top-rated games (page_size=8, sorted by rating, etc.) //Merge code Maryam
    suspend fun getTopRatedGames(): List<Game> {
        return try {
            val result = ApiClient.apiService.getGamesSorted(
                ordering = "-rating",
                pageSize = 10
            )
            //Log.d("GameRepository", "Before mapping: ${result.results.size}")
            val mapped = result.results.map { RawGameMapper.fromDto(it) }
            //Log.d("GameRepository", "After mapping: ${mapped.size}")
            mapped
        } catch (e: Exception) {
            Log.e("GameRepository", "Error fetching top-rated", e)
            emptyList()
        }
    }
}

