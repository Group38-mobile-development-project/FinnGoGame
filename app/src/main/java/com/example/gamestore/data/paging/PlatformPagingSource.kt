package com.example.gamestore.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.gamestore.data.mapper.RawGameMapper
import com.example.gamestore.data.model.Game
import android.util.Log
import com.example.gamestore.data.network.GameApi


class PlatformPagingSource(
    private val api: GameApi,
    private val platformSlug: String
) : PagingSource<Int, Game>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Game> {
        val page = params.key ?: 1
        return try {
            val response = api.fetchGamesByPlatform(platformSlug, page)
            val games = response.results.map { RawGameMapper.fromDto(it) }
            LoadResult.Page(
                data = games,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (games.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Game>): Int? = null
}
