package com.example.gamestore.ui.repository

import com.example.gamestore.ui.api.RawgApi
import com.example.gamestore.ui.model.Game
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GameRepository {

    private val apiKey = "e0ba500bfa8e4992b4d7ee3f7c5d5b6a"  // <-- Replace with your RAWG.io key

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.rawg.io/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val rawgApi = retrofit.create(RawgApi::class.java)

    // Original: fetch a standard list of games
    suspend fun getGames(): List<Game> {
        return try {
            val response = rawgApi.getGames(apiKey = apiKey, pageSize = 20)
            if (response.isSuccessful) {
                response.body()?.results ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // New: fetch top-rated games (page_size=8, sorted by rating, etc.)
    suspend fun getTopRatedGames(): List<Game> {
        return try {
            val response = rawgApi.getGamesSorted(
                apiKey = apiKey,
                ordering = "-rating",
                pageSize = 8
            )
            if (response.isSuccessful) {
                response.body()?.results ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // New: fetch games by search query
    suspend fun searchGames(query: String): List<Game> {
        return try {
            val response = rawgApi.searchGames(apiKey = apiKey, query = query, pageSize = 20)
            if (response.isSuccessful) {
                response.body()?.results ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}

