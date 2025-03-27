package com.example.gamestore.data.network

import com.example.gamestore.data.dto.RawGame
import com.example.gamestore.data.dto.GameListResponse
import com.example.gamestore.data.dto.GenreListResponse
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
}
