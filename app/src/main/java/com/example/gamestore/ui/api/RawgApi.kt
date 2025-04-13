//package com.example.gamestore.ui.api
//
//import com.example.gamestore.ui.model.GameResponse
//import retrofit2.Response
//import retrofit2.http.GET
//import retrofit2.http.Query
//
//interface RawgApi {
//
//    // Original: basic list fetch
//    @GET("games")
//    suspend fun getGames(
//        @Query("key") apiKey: String,
//        @Query("page_size") pageSize: Int = 10
//    ): Response<GameResponse>
//
//    // Additional: fetch with sorting, e.g. &ordering=-rating for top rated
//    @GET("games")
//    suspend fun getGamesSorted(
//        @Query("key") apiKey: String,
//        @Query("ordering") ordering: String = "-rating",
//        @Query("page_size") pageSize: Int = 10
//    ): Response<GameResponse>
//
//    // Additional: search by query, e.g. &search=YOUR_SEARCH
//    @GET("games")
//    suspend fun searchGames(
//        @Query("key") apiKey: String,
//        @Query("search") query: String,
//        @Query("page_size") pageSize: Int = 10
//    ): Response<GameResponse>
//}

//// Merged -> Not use anymore