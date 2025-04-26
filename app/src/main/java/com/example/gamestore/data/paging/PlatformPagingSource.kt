package com.example.gamestore.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.gamestore.data.mapper.RawGameMapper
import com.example.gamestore.data.model.Game
import android.util.Log
import com.example.gamestore.data.network.GameApi

class PlatformPagingSource(
    private val apiService: GameApi,
    private val platformId: Int   //  Int
) : PagingSource<Int, Game>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Game> {
        val page = params.key ?: 1
        val pageSize = params.loadSize

        return try {
            val response = apiService.fetchGamesByPlatform(
                platformSlug = platformId.toString(),
                page = page,
                pageSize = pageSize
            )
            val mappedGames = response.results.map { RawGameMapper.fromDto(it) }

            //fixbug
            //Log.d("PLATFORM_PAGING", "Loaded page $page with ${mappedGames.size} games for platform '$platformSlug'")

            LoadResult.Page(
                data = mappedGames,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (mappedGames.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            Log.e("PLATFORM_PAGING", "Error: ${e.message}")
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Game>): Int? {
        return state.anchorPosition
    }
}
