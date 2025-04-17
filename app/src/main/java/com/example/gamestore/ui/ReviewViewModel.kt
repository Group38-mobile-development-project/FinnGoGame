package com.example.gamestore.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamestore.data.model.Review
import com.example.gamestore.data.repository.ReviewRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReviewViewModel(
    private val reviewRepository: ReviewRepository = ReviewRepository()
) : ViewModel() {
    // Holds the state of reviews for a specific game
    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> get() = _reviews
    // Holds the state for review submission success
    private val _isReviewSubmitted = MutableStateFlow(false)
    private val _reviewErrorMessage = MutableStateFlow<String?>(null)
    val reviewErrorMessage: StateFlow<String?> = _reviewErrorMessage

    // Fetch reviews for a specific game
    fun fetchReviews(gameId: Int) {
        viewModelScope.launch {
            // Replace with actual FireStore query to fetch reviews
            reviewRepository.getReviewsForGame(gameId) { result ->
                _reviews.value = result
            }
        }
    }
    // Submit a new review
    fun submitReview(gameId: Int, content: String) {
        viewModelScope.launch {
            reviewRepository.submitReview(gameId, content) { success, errorMessage ->
                _isReviewSubmitted.value = success
                _reviewErrorMessage.value = errorMessage
                if (success) {
                    fetchReviews(gameId)  // Refresh the list
                }
            }
        }
    }
    // Update an existing review
    fun updateReview(gameId: Int, reviewId: String, content: String) {
        viewModelScope.launch {
            reviewRepository.editReview(gameId, reviewId, content) { success ->
                if (success) {
                    fetchReviews(gameId)  // Refresh after updating
                }
            }
        }
    }

    fun deleteReview(gameId: Int, reviewId: String) {
        viewModelScope.launch {
            reviewRepository.deleteReview(gameId, reviewId) { success ->
                if (success) {
                    fetchReviews(gameId)
                }
            }
        }
    }

    fun submitReply(gameId: Int, reviewId: String, content: String) {
        viewModelScope.launch {
            reviewRepository.submitReply(gameId, reviewId, content) { success ->
                if (success) {
                    fetchReviews(gameId)  // Refresh after submitting reply
                }
            }
        }
    }

    fun updateReply(gameId: Int, reviewId: String, replyId: String, newContent: String) {
        viewModelScope.launch {
            reviewRepository.editReply(gameId, reviewId, replyId, newContent) { success ->
                if (success) {
                    fetchReviews(gameId)  // Refresh after editing reply
                }
            }
        }
    }

    fun deleteReply(gameId: Int, reviewId: String, replyId: String) {
        viewModelScope.launch {
            reviewRepository.deleteReply(gameId, reviewId, replyId) { success ->
                if (success) {
                    fetchReviews(gameId)  // Refresh after deleting reply
                }
            }
        }
    }
    // Handle upvote or downvote on a review
    fun voteReview(gameId: Int, reviewId: String, upvote: Boolean) {
        viewModelScope.launch {
            reviewRepository.voteReview(gameId, reviewId, upvote) { success ->
                if (success) {
                    fetchReviews(gameId)  // Refresh after voting
                }
            }
        }
    }
    // Check if the current user has upvoted a review
    fun hasUpvoted(gameId: Int, reviewId: String, userId: String, onComplete: (Boolean) -> Unit) {
        // Query the votes subcollection for the review
        reviewRepository.getVoteStatus(gameId, reviewId, userId) { upvoted, _ ->
            onComplete(upvoted)
        }
    }
    // Check if the current user has downvoted a review
    fun hasDownvoted(gameId: Int, reviewId: String, userId: String, onComplete: (Boolean) -> Unit) {
        // Query the votes subcollection for the review
        reviewRepository.getVoteStatus(gameId, reviewId, userId) { _, downvoted ->
            onComplete(downvoted)
        }
    }

    fun clearReviewError() {
        _reviewErrorMessage.value = null
    }
}
