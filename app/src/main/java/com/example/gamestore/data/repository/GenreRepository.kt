
package com.example.gamestore.data.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.gamestore.data.mapper.RawGameMapper
import com.example.gamestore.data.mapper.RawGenreMapper
import com.example.gamestore.data.model.Game
import com.example.gamestore.data.model.Genre
import com.example.gamestore.data.network.ApiClient
import com.example.gamestore.data.paging.GenrePagingSource
import kotlinx.coroutines.flow.Flow

class GenreRepository {

    suspend fun getGenres(): List<Genre> {
        return try {
            val response = ApiClient.apiService.fetchGenres()
            response.results.map { RawGenreMapper.fromDto(it) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    //load one page
//    suspend fun getGamesByGenreSlug(slug: String): List<Game> {
//        return try {
//            val response = ApiClient.apiService.fetchGamesByGenre(
//                genre = slug,
//                page = 1,
//                pageSize = 20
//            )
//
//            val mapped = response.results.map {
//                try {
//                    RawGameMapper.fromDto(it)
//                } catch (e: Exception) {
//                    Log.e("DEBUG_MAPPER_ERROR", "Error mapping game ${it.title}: ${e.message}")
//                    null
//                }
//            }.filterNotNull()
//
//            mapped
//        } catch (e: Exception) {
//            Log.e("DEBUG_API", "API error: ${e.message}")
//            emptyList()
//        }
//    }

    //load all with paging
    fun getGamesByGenrePaged(slug: String): Flow<PagingData<Game>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { GenrePagingSource(ApiClient.apiService, slug) }
        ).flow
    }
}
