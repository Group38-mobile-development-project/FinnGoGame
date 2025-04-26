package com.example.gamestore.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.gamestore.data.dto.PlatformListResponse
import com.example.gamestore.data.mapper.RawPlatformMapper
import com.example.gamestore.data.model.Game
import com.example.gamestore.data.model.Platform
import com.example.gamestore.data.network.ApiClient
import com.example.gamestore.data.paging.PlatformPagingSource
import kotlinx.coroutines.flow.Flow

class PlatformRepository {

    suspend fun getPlatforms(): List<Platform> {
        return ApiClient.apiService.fetchPlatforms().results.map {
            RawPlatformMapper.fromDto(it)
        }
    }

    fun getGamesByPlatform(platformId: Int): Flow<PagingData<Game>> {
        return Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = {
                PlatformPagingSource(ApiClient.apiService, platformId)
            }
        ).flow
}

}
