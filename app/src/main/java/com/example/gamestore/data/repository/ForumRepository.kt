package com.example.gamestore.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.gamestore.data.model.ForumPost
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

fun fetchForumPosts(): LiveData<List<ForumPost>> {
    val firestore = FirebaseFirestore.getInstance()
    val postsLiveData = MutableLiveData<List<ForumPost>>()

    firestore.collection("forum_posts")
        .orderBy("timestamp", Query.Direction.DESCENDING)  // Sort posts by the timestamp
        .get()
        .addOnSuccessListener { result ->
            val posts = result.map { document ->
                document.toObject(ForumPost::class.java) // Convert Firestore document to ForumPost
            }
            postsLiveData.postValue(posts)
        }
        .addOnFailureListener { exception ->
            Log.e("Forum", "Error getting documents: ", exception)
        }

    return postsLiveData
}

fun postNewForumMessage(title: String, content: String) {
    // Log userId and username to check if they are being correctly retrieved
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "Unknown User"
    val username = FirebaseAuth.getInstance().currentUser?.displayName ?: "Anonymous"

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





