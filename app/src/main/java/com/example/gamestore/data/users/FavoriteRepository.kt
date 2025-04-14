package com.example.gamestore.data.users

import android.util.Log
import com.example.gamestore.data.mapper.RawGameMapper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.gamestore.data.model.Game
import com.example.gamestore.data.network.ApiClient

class FavoritesRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Adds a game to the user's favorites
    fun addToFavorites(game: Game, onComplete: (Boolean) -> Unit) {
        val userId = auth.currentUser?.uid ?: return onComplete(false)  // Check if user is logged in
        val favGameData = mapOf(
            "id" to game.id,
            "title" to game.title,
            "imageUrl" to game.imageUrl,
            "timestamp" to System.currentTimeMillis()
        )

        db.collection("users")
            .document(userId)
            .collection("favorites")
            .document(game.id.toString())
            .set(favGameData)
            .addOnSuccessListener {
                Log.d("FAV_SUCCESS", "Game added to favorites successfully")
                onComplete(true)
            }
            .addOnFailureListener { e ->
                Log.w("FAV_ERROR", "Error adding to favorites: ${e.message}")
                onComplete(false)
            }
    }

    // Checks if a game is in the user's favorites
    fun isFavorite(gameId: Int, onResult: (Boolean) -> Unit) {
        val userId = auth.currentUser?.uid ?: return onResult(false)  // Check if user is logged in
        val docRef = db.collection("users").document(userId).collection("favorites").document(gameId.toString())
        docRef.get()
            .addOnSuccessListener { document ->
                onResult(document.exists())  // Return true if the document exists
            }
            .addOnFailureListener { e ->
                Log.w("FAV_ERROR", "Error checking if favorite: ${e.message}")
                onResult(false)
            }
    }

    // Removes a game from the user's favorites
    fun removeFromFavorites(gameId: Int, onResult: (Boolean) -> Unit) {
        val userId = auth.currentUser?.uid ?: return onResult(false)  // Check if user is logged in
        db.collection("users").document(userId).collection("favorites").document(gameId.toString())
            .delete()
            .addOnSuccessListener {
                Log.d("FAV_SUCCESS", "Game removed from favorites successfully")
                onResult(true)  // Successfully removed from favorites
            }
            .addOnFailureListener { e ->
                Log.w("FAV_ERROR", "Error removing from favorites: ${e.message}")
                onResult(false)  // Failed to remove from favorites
            }
    }

    // Fetch a game by its ID (in case it's needed for other purposes)
    suspend fun getGameById(id: Int): Game? {
        return try {
            val response = ApiClient.apiService.fetchGameById(id)
            RawGameMapper.fromDto(response)  // Assuming RawGameMapper is correct
        } catch (e: Exception) {
            Log.e("FavoritesRepo", "Failed to fetch game $id: ${e.message}")
            null
        }
    }
}

