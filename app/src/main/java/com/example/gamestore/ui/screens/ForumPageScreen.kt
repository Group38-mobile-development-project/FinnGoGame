package com.example.gamestore.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.gamestore.data.model.ForumPost
import com.example.gamestore.data.repository.fetchForumPosts
import com.example.gamestore.data.repository.postNewForumMessage

@Composable
fun ForumPageScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    var posts by remember { mutableStateOf<List<ForumPost>>(emptyList()) }
    var isPosting by remember { mutableStateOf(false) }  // Handle loading state for posting
    var postsLiveData = fetchForumPosts()
    val postsFromLiveData by postsLiveData.observeAsState(emptyList())

    // Observing posts live data
    LaunchedEffect(postsFromLiveData) {
        posts = postsFromLiveData
    }

    // State for title and content of the post
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Header
        Text("Forum Page", fontSize = 30.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(20.dp))

        // Post Title Input
        BasicTextField(
            value = title,
            onValueChange = { title = it },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = { /* Move to next field */ }),
            modifier = Modifier.fillMaxWidth().padding(8.dp).border(1.dp, Color.Gray).padding(16.dp),
            decorationBox = { innerTextField ->
                Box(Modifier.padding(8.dp)) {
                    if (title.isEmpty()) {
                        Text("Enter title...", color = Color.Gray)
                    }
                    innerTextField()
                }
            }
        )

        // Post Content Input
        Spacer(modifier = Modifier.height(10.dp))
        BasicTextField(
            value = content,
            onValueChange = { content = it },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { /* Handle posting */ }),
            modifier = Modifier.fillMaxWidth().height(200.dp).border(1.dp, Color.Gray).padding(16.dp),
            decorationBox = { innerTextField ->
                Box(Modifier.padding(8.dp)) {
                    if (content.isEmpty()) {
                        Text("Enter content...", color = Color.Gray)
                    }
                    innerTextField()
                }
            }
        )

        // Submit Button
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = {
                if (title.isNotEmpty() && content.isNotEmpty()) {
                    isPosting = true
                    postNewForumMessage(title, content)  // Post the new message to Firestore
                    title = ""  // Clear title after posting
                    content = ""  // Clear content after posting
                    isPosting = false  // Reset the posting flag
                    // Trigger the data refresh
                    postsLiveData = fetchForumPosts()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isPosting
        ) {
            Text("Post Message")
        }


        // Loading State when posting
        if (isPosting) {
            Text("Posting...", color = Color.Gray)
        }

        // List of forum posts
        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn (modifier = Modifier.fillMaxSize()) {
            items(posts) { post ->
                ForumPostItem(post)
            }
        }
    }
}

@Composable
fun ForumPostItem(post: ForumPost) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(post.title, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Text("By: ${post.username}", fontSize = 14.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(8.dp))
        Text(post.content, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider()
    }
}

