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
    private val query: String
) : PagingSource<Int, Game>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Game> {
        return try {
            val page = params.key ?: 1
            val pageSize = params.loadSize
            Log.d("SEARCH_API", "Loading page $page for query: $query, pageSize: $pageSize")

            // Always use search_precise=false for broader results
            val response = api.searchGames(
                query = query,
                precise = false,
                page = page,
                pageSize = pageSize
            )


            // Log the raw response to debug
            //Log.d("SEARCH_API", "Raw response: count=${response.count}, results=${response.results?.size ?: 0}")

            // Map results with null safety and error handling
            val games = response.results?.mapNotNull { dto ->
                try {
                    RawGameMapper.fromDto(dto)
                } catch (e: Exception) {
                    null
                }
            } ?: emptyList()

            //Log.d("SEARCH_API", "Mapped ${games.size} games for query: $query")

            LoadResult.Page(
                data = games,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (games.isEmpty()) null else page + 1
            )
        } catch (e: HttpException) {
            //Log.e("SEARCH_API", "HTTP error: ${e.code()} - ${e.message()}", e)
            LoadResult.Error(e)
        } catch (e: IOException) {
            //Log.e("SEARCH_API", "Network error: ${e.message}", e)
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