package com.example.gamestore.data.network

import com.example.gamestore.data.dto.RawGame
import com.example.gamestore.data.dto.GameListResponse
import com.example.gamestore.data.dto.GenreListResponse
import com.example.gamestore.data.dto.PlatformListResponse

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GameApi {
    @GET("games")
    suspend fun fetchGames(
        @Query("page") page: Int,
        @Query("page_size") pageSize: Int = 20 //number maximum games in one page like 40 games/page
    ): GameListResponse

    @GET("games/{id}")
    suspend fun fetchGameById(@Path("id") gameId: Int): RawGame

    @GET("genres")
    suspend fun fetchGenres(): GenreListResponse

    @GET("games")
    suspend fun fetchGamesByGenre(
        @Query("genres") genre: String,
        @Query("page") page: Int,
        @Query("page_size") pageSize: Int = 20
    ): GameListResponse

    //add search

    @GET("games")
    suspend fun searchGamesAdvanced(
        @Query("search") query: String,
        @Query("genres") genre: String? = null,
        @Query("platforms") platform: String? = null,
        @Query("search_precise") precise: Boolean = false,
        @Query("page") page: Int,
        @Query("page_size") pageSize: Int = 20
    ): GameListResponse


    // Additional: fetch with sorting, e.g. &ordering=-rating for top rated //merge code Maryam
    @GET("games")
    suspend fun getGamesSorted(
        @Query("ordering") ordering: String = "-rating",
        @Query("page_size") pageSize: Int = 10
    ): GameListResponse

    //platform
    @GET("platforms")
    suspend fun fetchPlatforms(): PlatformListResponse

    @GET("games")
    suspend fun fetchGamesByPlatform(
        @Query("platform") platformSlug: String,
        @Query("page") page: Int,
        @Query("page_size") pageSize: Int = 20
    ): GameListResponse

}

