package com.example.gamestore.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamestore.data.model.Game
import com.example.gamestore.data.users.FavoriteRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FavoritesViewModel : ViewModel() {

    private val _favoriteGames = MutableStateFlow<List<Game>>(emptyList())
    val favoriteGames = _favoriteGames.asStateFlow()

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val repository = FavoriteRepository()

    init {
        fetchFavoriteGames()
    }

    private fun fetchFavoriteGames() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("users").document(uid).collection("favorites")
            .get()
            .addOnSuccessListener { result ->
                val ids = result.mapNotNull { it.id.toIntOrNull() }
                viewModelScope.launch {
                    val games = ids.mapNotNull { repository.getGameById(it) }
                    _favoriteGames.value = games
                }
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }
}

