package com.example.gamestore.data.users

//import com.example.gamestore.data.mapper.RawGameMapper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import com.example.gamestore.data.model.GameRating

import com.example.gamestore.data.model.Game
import com.example.gamestore.data.network.ApiClient

//
import android.util.Log




class RatingRepository(private val db: FirebaseFirestore) {

    fun rateGame(userId: String, gameId: String, rating: Double, onComplete: (Boolean) -> Unit) {
        val gameRatingRef = db.collection("gameRatings").document(gameId)
        val userRatingRef =
            db.collection("users").document(userId).collection("ratings").document(gameId)

        db.runTransaction { transaction ->
            val snapshot = transaction.get(gameRatingRef)
            val (newTotal, newSum) = if (snapshot.exists()) {
                val total = snapshot.getLong("totalRatings") ?: 0
                val sum = snapshot.getDouble("totalRatingValue") ?: 0.0
                Pair(total + 1, sum + rating)
            } else {
                Pair(1L, rating)
            }
            val newAverage = newSum / newTotal

            transaction.set(
                gameRatingRef, mapOf(
                    "totalRatings" to newTotal,
                    "totalRatingValue" to newSum,
                    "averageRating" to newAverage
                )
            )

            transaction.set(
                userRatingRef, mapOf(
                    "rating" to rating,
                    "timestamp" to com.google.firebase.firestore.FieldValue.serverTimestamp()
                )
            )
            //Log.d("RATE", "Saved user rating to ${userRatingRef.path}")
        }.addOnSuccessListener {
            //Log.d("RATE", "Transaction success")
            onComplete(true)
        }.addOnFailureListener { e ->
            //Log.e("RATE", "Transaction failed: ${e.message}")
            onComplete(false)
        }
    }

    fun getAverageRating(gameId: String, onResult: (GameRating?) -> Unit) {
        db.collection("gameRatings").document(gameId)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val rating = GameRating(
                        totalRatings = doc.getLong("totalRatings")?.toInt() ?: 0,
                        totalRatingValue = doc.getDouble("totalRatingValue") ?: 0.0,
                        averageRating = doc.getDouble("averageRating") ?: 0.0
                    )
                    onResult(rating)
                } else {
                    onResult(null)
                }
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    fun hasUserRated(userId: String, gameId: String, callback: (Boolean, Double?) -> Unit) {
        db.collection("users").document(userId)
            .collection("ratings").document(gameId)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val rating = doc.getDouble("rating")
                    callback(true, rating)
                } else {
                    callback(false, null)
                }
            }
            .addOnFailureListener {
                callback(false, null)
            }
    }
}