package com.example.gamestore.data.repository

import com.example.gamestore.data.model.Review
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class ReviewRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun submitReview(gameId: Int, content: String, onComplete: (Boolean) -> Unit) {
        val userId = auth.currentUser?.uid ?: return onComplete(false)
        val userEmail = auth.currentUser?.email ?: return onComplete(false)
        val reviewId = db.collection("reviews").document().id
        val review = Review(
            id = reviewId,
            userId = userId,
            userEmail = userEmail,
            gameId = gameId,
            content = content,
            timestamp = System.currentTimeMillis()
        )

        val reviewRef = db.collection("reviews")
            .document(gameId.toString())
            .collection("userReviews")
            .document(reviewId)

        val userReviewRef = db.collection("users")
            .document(userId)
            .collection("reviews")
            .document(gameId.toString())

        db.runBatch { batch ->
            batch.set(reviewRef, review)
            batch.set(userReviewRef, review)
        }.addOnSuccessListener {
            onComplete(true)
        }.addOnFailureListener {
            onComplete(false)
        }
    }

    fun editReview(
        gameId: Int,
        reviewId: String,
        newContent: String,
        onComplete: (Boolean) -> Unit
    ) {
        val userId = auth.currentUser?.uid ?: return onComplete(false)

        val updates = mapOf(
            "content" to newContent,
            "timestamp" to System.currentTimeMillis()
        )

        val gameReviewRef = db.collection("reviews")
            .document(gameId.toString())
            .collection("userReviews")
            .document(reviewId)

        val userReviewRef = db.collection("users")
            .document(userId)
            .collection("reviews")
            .document(gameId.toString())

        db.runBatch { batch ->
            batch.set(gameReviewRef, updates, SetOptions.merge())
            batch.set(userReviewRef, updates, SetOptions.merge())
        }.addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    fun deleteReview(
        gameId: Int,
        reviewId: String,
        onComplete: (Boolean) -> Unit
    ) {
        val userId = auth.currentUser?.uid ?: return onComplete(false)
        val reviewRef = db.collection("reviews")
            .document(gameId.toString())
            .collection("userReviews")
            .document(reviewId)

        val userReviewRef = db.collection("users")
            .document(userId)
            .collection("reviews")
            .document(gameId.toString())
        // Optional: delete all votes subCollection too
        db.runBatch { batch ->
            batch.delete(reviewRef)
            batch.delete(userReviewRef)
        }.addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    fun voteReview(
        gameId: Int,
        reviewId: String,
        isUpvote: Boolean,
        onComplete: (Boolean) -> Unit
    ) {
        val userId = auth.currentUser?.uid ?: return onComplete(false)

        val reviewRef = db.collection("reviews")
            .document(gameId.toString())
            .collection("userReviews")
            .document(reviewId)
        val userReviewRef = db.collection("users")
            .document(userId)
            .collection("reviews")
            .document(gameId.toString())
        val voteRef = reviewRef
            .collection("votes")
            .document(userId)

        db.runTransaction { transaction ->
            val reviewSnap = transaction.get(reviewRef)
            val currentUpvotes = reviewSnap.getLong("upvotes") ?: 0
            val currentDownvotes = reviewSnap.getLong("downvotes") ?: 0
            val voteSnap = transaction.get(voteRef)

            if (voteSnap.exists()) {
                // If vote exists, check if the user is changing the vote
                val previousVote = voteSnap.getBoolean("isUpvote")
                if (previousVote == isUpvote) {
                    // Cancel the vote
                    transaction.update(
                        reviewRef,
                        if (isUpvote) "upvotes" else "downvotes",
                        (if (isUpvote) currentUpvotes else currentDownvotes) - 1
                    )
                    transaction.delete(voteRef)
                    transaction.update(
                        userReviewRef,
                        if (isUpvote) "upvotes" else "downvotes",
                        (if (isUpvote) currentUpvotes else currentDownvotes) - 1
                    )
                } else {
                    // Switch vote
                    transaction.update(
                        reviewRef,
                        "upvotes", if (isUpvote) currentUpvotes + 1 else currentUpvotes - 1
                    )
                    transaction.update(
                        reviewRef,
                        "downvotes", if (isUpvote) currentDownvotes - 1 else currentDownvotes + 1
                    )
                    transaction.update(voteRef, mapOf("isUpvote" to isUpvote))
                    transaction.update(
                        userReviewRef,
                        "upvotes", if (isUpvote) currentUpvotes + 1 else currentUpvotes - 1
                    )
                    transaction.update(
                        userReviewRef,
                        "downvotes", if (isUpvote) currentDownvotes - 1 else currentDownvotes + 1
                    )
                }
            } else {
                // First-time vote
                transaction.update(
                    reviewRef,
                    if (isUpvote) "upvotes" else "downvotes",
                    (if (isUpvote) currentUpvotes else currentDownvotes) + 1
                )
                transaction.set(voteRef, mapOf("userId" to userId, "isUpvote" to isUpvote))
                transaction.update(
                    userReviewRef,
                    if (isUpvote) "upvotes" else "downvotes",
                    (if (isUpvote) currentUpvotes else currentDownvotes) + 1
                )
            }
        }.addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    fun getVoteStatus(
        gameId: Int,
        reviewId: String,
        userId: String,
        onComplete: (Boolean, Boolean) -> Unit
    ) {
        val voteRef = db.collection("reviews")
            .document(gameId.toString())
            .collection("userReviews")
            .document(reviewId)
            .collection("votes")
            .document(userId)

        voteRef.get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val isUpvote = snapshot.getBoolean("isUpvote") ?: false
                    onComplete(isUpvote, !isUpvote)  // if it's an upvote, downvote is false and vice versa
                } else {
                    onComplete(false, false)  // No vote found, neither upvoted nor downvoted
                }
            }
            .addOnFailureListener {
                onComplete(false, false)  // Error fetching, return false for both
            }
    }

    fun getReviewsForGame(gameId: Int, onComplete: (List<Review>) -> Unit) {
        db.collection("reviews")
            .document(gameId.toString())
            .collection("userReviews")
            .get()
            .addOnSuccessListener { snapshot ->
                val reviews = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Review::class.java)?.copy(id = doc.id)
                }
                onComplete(reviews)
            }
            .addOnFailureListener {
                onComplete(emptyList())
            }
    }

}
