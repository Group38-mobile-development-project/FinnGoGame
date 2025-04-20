
package com.example.gamestore.data.repository

import android.util.Log

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.gamestore.data.mapper.RawGameMapper
import com.example.gamestore.data.model.Game
import com.example.gamestore.data.model.Genre
import com.example.gamestore.data.network.ApiClient
import com.example.gamestore.data.paging.GenrePagingSource
import kotlinx.coroutines.flow.Flow

//
import com.example.gamestore.data.mapper.RawGenreMapper


class GenreRepository {

    suspend fun getGenres(): List<Genre> {
        return try {
            val response = ApiClient.apiService.fetchGenres()
            response.results.map { RawGenreMapper.fromDto(it) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    //load all with paging
    fun getGamesByGenrePaged(slug: String): Flow<PagingData<Game>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { GenrePagingSource(ApiClient.apiService, slug) }
        ).flow
    }
}
