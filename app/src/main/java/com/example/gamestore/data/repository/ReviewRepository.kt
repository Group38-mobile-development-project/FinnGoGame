package com.example.gamestore.data.repository

import com.example.gamestore.data.model.Reply
import com.example.gamestore.data.model.Review
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class ReviewRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun submitReview(gameId: Int, content: String, onComplete: (Boolean, String?) -> Unit) {
        val userId = auth.currentUser?.uid ?: return onComplete(false, "You need to log in first")
        val userEmail = auth.currentUser?.email ?: return onComplete(false, "No email found")
        val userReviewRef = db.collection("users")
            .document(userId)
            .collection("reviews")
            .document(gameId.toString())
        // Check if review already exists
        userReviewRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                onComplete(false, "You've already submitted a review for this game.")
            } else {
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
                db.runBatch { batch ->
                    batch.set(reviewRef, review)
                    batch.set(userReviewRef, review)
                }.addOnSuccessListener {
                    onComplete(true, null)
                }.addOnFailureListener {
                    onComplete(false, "Failed to submit review.")
                }
            }
        }.addOnFailureListener {
            onComplete(false, "Error checking existing review.")
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

        // Fetch and delete replies and votes first
        reviewRef.collection("replies").get().addOnSuccessListener { replySnap ->
            reviewRef.collection("votes").get().addOnSuccessListener { voteSnap ->
                db.runBatch { batch ->
                    // Delete replies
                    for (doc in replySnap) {
                        batch.delete(doc.reference)
                    }
                    // Delete votes
                    for (doc in voteSnap) {
                        batch.delete(doc.reference)
                    }
                    // Delete the review and its copy under user's reviews
                    batch.delete(reviewRef)
                    batch.delete(userReviewRef)
                }.addOnSuccessListener { onComplete(true) }
                    .addOnFailureListener { onComplete(false) }
            }.addOnFailureListener { onComplete(false) }
        }.addOnFailureListener { onComplete(false) }
    }

    fun submitReply(gameId: Int, reviewId: String, content: String, onComplete: (Boolean) -> Unit) {
        val userId = auth.currentUser?.uid ?: return onComplete(false)
        val userEmail = auth.currentUser?.email ?: return onComplete(false)
        val replyId = db.collection("reviews")
            .document(gameId.toString())
            .collection("userReviews")
            .document(reviewId)
            .collection("replies")
            .document().id
        val reply = mapOf(
            "id" to replyId,
            "userId" to userId,
            "userEmail" to userEmail,
            "content" to content,
            "timestamp" to System.currentTimeMillis()
        )
        db.collection("reviews")
            .document(gameId.toString())
            .collection("userReviews")
            .document(reviewId)
            .collection("replies")
            .document(replyId)
            .set(reply)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    fun editReply(
        gameId: Int,
        reviewId: String,
        replyId: String,
        newContent: String,
        onComplete: (Boolean) -> Unit
    ){
        val replyRef = db.collection("reviews")
            .document(gameId.toString())
            .collection("userReviews")
            .document(reviewId)
            .collection("replies")
        val updates = mapOf(
            "content" to newContent,
            "timestamp" to System.currentTimeMillis()
        )
        replyRef.document(replyId).update(updates)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    fun deleteReply(
        gameId: Int,
        reviewId: String,
        replyId: String,
        onComplete: (Boolean) -> Unit
    ) {
        val replyRef = db.collection("reviews")
            .document(gameId.toString())
            .collection("userReviews")
            .document(reviewId)
        replyRef.collection("replies")
            .document(replyId)
            .delete()
            .addOnSuccessListener { onComplete(true) }
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

        val voteRef = reviewRef
            .collection("votes")
            .document(userId)

        db.runTransaction { transaction ->
            val reviewSnap = transaction.get(reviewRef)
            val currentUpvotes = reviewSnap.getLong("upvotes") ?: 0
            val currentDownvotes = reviewSnap.getLong("downvotes") ?: 0
            val voteSnap = transaction.get(voteRef)

            if (voteSnap.exists()) {
                val previousVote = voteSnap.getBoolean("isUpvote")
                if (previousVote == isUpvote) {
                    // Cancel vote
                    transaction.update(
                        reviewRef,
                        if (isUpvote) "upvotes" else "downvotes",
                        (if (isUpvote) currentUpvotes else currentDownvotes) - 1
                    )
                    transaction.delete(voteRef)
                } else {
                    // Switch vote
                    transaction.update(reviewRef, mapOf(
                        "upvotes" to if (isUpvote) currentUpvotes + 1 else currentUpvotes - 1,
                        "downvotes" to if (isUpvote) currentDownvotes - 1 else currentDownvotes + 1
                    ))
                    transaction.update(voteRef, mapOf("isUpvote" to isUpvote))
                }
            } else {
                // First-time vote
                transaction.update(
                    reviewRef,
                    if (isUpvote) "upvotes" else "downvotes",
                    (if (isUpvote) currentUpvotes else currentDownvotes) + 1
                )
                transaction.set(voteRef, mapOf("userId" to userId, "isUpvote" to isUpvote))
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
                val reviewDocs = snapshot.documents
                val reviews = mutableListOf<Review>()

                if (reviewDocs.isEmpty()) {
                    onComplete(emptyList())
                    return@addOnSuccessListener
                }

                var loadedCount = 0
                for (doc in reviewDocs) {
                    val review = doc.toObject(Review::class.java)?.copy(id = doc.id)

                    if (review != null) {
                        doc.reference.collection("replies")
                            .orderBy("timestamp")
                            .get()
                            .addOnSuccessListener { repliesSnapshot ->
                                val replies = repliesSnapshot.documents.mapNotNull { replyDoc ->
                                    replyDoc.toObject(Reply::class.java)
                                }
                                review.replies = replies
                                reviews.add(review)
                                loadedCount++
                                if (loadedCount == reviewDocs.size) {
                                    onComplete(reviews.sortedByDescending { it.timestamp }) // Optional: sort by latest
                                }
                            }
                            .addOnFailureListener {
                                review.replies = emptyList()
                                reviews.add(review)
                                loadedCount++
                                if (loadedCount == reviewDocs.size) {
                                    onComplete(reviews.sortedByDescending { it.timestamp })
                                }
                            }
                    } else {
                        loadedCount++
                        if (loadedCount == reviewDocs.size) {
                            onComplete(reviews.sortedByDescending { it.timestamp })
                        }
                    }
                }
            }
            .addOnFailureListener {
                onComplete(emptyList())
            }
    }
}
