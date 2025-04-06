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

    fun addToFavourites(game: Game, onComplete: (Boolean) -> Unit) {
        val userId = auth.currentUser?.uid ?: return onComplete(false)

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
                onComplete(true)
            }
            .addOnFailureListener { e ->
                Log.w("FAV_ERROR", "Error adding to favorites", e)
                onComplete(false)
            }
    }
    fun isFavorite(gameId: Int, onResult: (Boolean) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return onResult(false)
        val docRef = db.collection("users").document(userId).collection("favorites").document(gameId.toString())
        docRef.get()
            .addOnSuccessListener { document ->
                onResult(document.exists())
            }
            .addOnFailureListener {
                onResult(false)
            }
    }
    fun removeFromFavorites(gameId: Int, onResult: (Boolean) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return onResult(false)
        db.collection("users").document(userId).collection("favorites").document(gameId.toString())
            .delete()
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }
    suspend fun getGameById(id: Int): Game? {
        return try {
            val response = ApiClient.apiService.fetchGameById(id)
            RawGameMapper.fromDto(response)
        } catch (e: Exception) {
            Log.e("FavoritesRepo", "Failed to fetch game $id: ${e.message}")
            null
        }
    }
}

