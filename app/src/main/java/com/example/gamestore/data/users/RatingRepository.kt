package com.example.gamestore.data.users

import com.google.firebase.firestore.FirebaseFirestore
import com.example.gamestore.data.model.GameRating

class RatingRepository(private val db: FirebaseFirestore) {

    fun rateGame(userId: String, gameId: String, rating: Double, previousRating: Double? = null, onComplete: (Boolean) -> Unit) {
        val gameRatingRef = db.collection("gameRatings").document(gameId)
        val userRatingRef =
            db.collection("users").document(userId).collection("ratings").document(gameId)

        db.runTransaction { transaction ->
            val snapshot = transaction.get(gameRatingRef)
            var total = snapshot.getLong("totalRatings") ?: 0
            var sum = snapshot.getDouble("totalRatingValue") ?: 0.0
            if (previousRating != null) {
                sum = sum - previousRating + rating
            } else {
                // First time rating
                total += 1
                sum += rating
            }
            val newAverage = if (total > 0) sum / total else 0.0

            transaction.set(
                gameRatingRef, mapOf(
                    "totalRatings" to total,
                    "totalRatingValue" to sum,
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
        }.addOnFailureListener {
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