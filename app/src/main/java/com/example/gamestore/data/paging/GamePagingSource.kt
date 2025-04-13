package com.example.gamestore.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.gamestore.data.mapper.RawGameMapper
import com.example.gamestore.data.model.Game
//debug
import android.util.Log
import com.example.gamestore.data.network.GameApi

class GamePagingSource(
    private val apiService: GameApi
) : PagingSource<Int, Game>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Game> {
        return try {
            val page = params.key ?: 1
            val response = apiService.fetchGames(page = page, pageSize = params.loadSize)
            val mappedGames = response.results.map { RawGameMapper.fromDto(it) }

            // debug paging
           // Log.d("PAGING", "Loaded page: $page, items: ${mappedGames.size}")
            LoadResult.Page(
                data = mappedGames,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (mappedGames.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Game>): Int? {
        return state.anchorPosition
    }
}
