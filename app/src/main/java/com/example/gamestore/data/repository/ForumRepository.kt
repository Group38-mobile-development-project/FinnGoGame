// com/example/gamestore/data/repository/ForumRepository.kt
package com.example.gamestore.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.gamestore.data.model.ForumPost
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query



fun fetchForumPosts(): LiveData<List<ForumPost>> {
    val liveData = MutableLiveData<List<ForumPost>>()
    FirebaseFirestore.getInstance()
        .collection("forum_posts")
        .orderBy("timestamp", Query.Direction.DESCENDING)
        .addSnapshotListener { snapshots, error ->
            if (error != null) {
                Log.e("ForumRepo", "Listen failed", error)
                return@addSnapshotListener
            }
            snapshots?.documents?.let { docs ->
                val posts = docs.mapNotNull { doc ->
                    // Deserialize into your data class, then inject the Firestore doc ID
                    doc.toObject(ForumPost::class.java)
                        ?.copy(id = doc.id)
                }
                liveData.value = posts
            }
        }
    return liveData
}


fun postNewForumMessage(title: String, content: String) {
    // Log userId and username to check if they are being correctly retrieved
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "Unknown User"
    val user = FirebaseAuth.getInstance().currentUser
    val username = user?.displayName?.takeIf { it.isNotBlank() }
        ?: user?.email?.takeIf { it.isNotBlank() }
        ?: "Anonymous"

    // Log the values of userId and username to make sure they're correct
    Log.d("Forum", "userId: $userId, username: $username")

    val timestamp = System.currentTimeMillis()

    // Create a new ForumPost object with the retrieved user info
    val newPost = ForumPost(
        userId = userId,
        username = username,
        gameId = "game123",  // You can replace this with actual game ID if needed
        title = title,
        content = content,
        timestamp = timestamp
    )

    val firestore = FirebaseFirestore.getInstance()
    firestore.collection("forum_posts")
        .add(newPost)
        .addOnSuccessListener { documentReference ->
            // Handle success
            Log.d("Forum", "Post added with ID: ${documentReference.id}")
        }
        .addOnFailureListener { exception ->
            // Handle failure
            Log.e("Forum", "Error adding post: ", exception)
        }
}

fun deleteForumPost(postId: String, onComplete: (Boolean) -> Unit) {
    FirebaseFirestore.getInstance()
        .collection("forum_posts")
        .document(postId)
        .delete()
        .addOnSuccessListener { onComplete(true) }
        .addOnFailureListener { onComplete(false) }
}





