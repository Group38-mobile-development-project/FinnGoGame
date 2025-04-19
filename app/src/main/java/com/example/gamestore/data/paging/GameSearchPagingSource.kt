package com.example.gamestore.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.gamestore.data.mapper.RawGameMapper
import com.example.gamestore.data.model.Game
import com.example.gamestore.data.network.GameApi
import android.util.Log
import retrofit2.HttpException
import java.io.IOException

class GameSearchPagingSource(
    private val api: GameApi,
    private val query: String,
    private val genre: String? = null,
    private val platform: String? = null
) : PagingSource<Int, Game>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Game> {
        return try {
            val page = params.key ?: 1
            val response = api.searchGamesAdvanced(
                query = query,
                genre = genre,
                platform = platform,
                page = page,
                pageSize = params.loadSize,
                precise = false
            )

            val games = response.results?.mapNotNull { dto ->
                try {
                    RawGameMapper.fromDto(dto)
                } catch (e: Exception) {
                    null
                }
            } ?: emptyList()

            LoadResult.Page(
                data = games,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (games.isEmpty()) null else page + 1
            )
        } catch (e: HttpException) {
            LoadResult.Error(e)
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: Exception) {
            //Log.e("SEARCH_API", "Unexpected error: ${e.message}", e)
            LoadResult.Error(e)
        }
    }
    override fun getRefreshKey(state: PagingState<Int, Game>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}