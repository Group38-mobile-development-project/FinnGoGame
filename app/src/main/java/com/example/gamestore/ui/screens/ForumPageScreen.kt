package com.example.gamestore.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions as KA
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.gamestore.data.model.ForumPost
import com.example.gamestore.data.repository.deleteForumPost
import com.example.gamestore.data.repository.fetchForumPosts
import com.example.gamestore.data.repository.postNewForumMessage
import com.example.gamestore.ui.helpers.humanize
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ForumPageScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    var posts by remember { mutableStateOf<List<ForumPost>>(emptyList()) }
    var isPosting by remember { mutableStateOf(false) }
    var postsLiveData = fetchForumPosts()
    val postsFromLiveData by postsLiveData.observeAsState(emptyList())

    LaunchedEffect(postsFromLiveData) {
        posts = postsFromLiveData
    }

    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Forum Page",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(Modifier.height(16.dp))

                // Title input
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.Gray, MaterialTheme.shapes.small)
                        .padding(8.dp)
                ) {
                    if (title.isEmpty()) {
                        Text("Enter title…", color = Color.Gray)
                    }
                    BasicTextField(
                        value = title,
                        onValueChange = { title = it },
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                        keyboardActions = KA(onNext = { }),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(Modifier.height(12.dp))

                // Content input
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .border(1.dp, Color.Gray, MaterialTheme.shapes.small)
                        .padding(8.dp)
                ) {
                    if (content.isEmpty()) {
                        Text("Enter content…", color = Color.Gray)
                    }
                    BasicTextField(
                        value = content,
                        onValueChange = { content = it },
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                        keyboardActions = KA(onDone = { }),
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(Modifier.height(12.dp))

                Button(
                    onClick = {
                        if (title.isNotBlank() && content.isNotBlank()) {
                            isPosting = true
                            postNewForumMessage(title, content)
                            title = ""
                            content = ""
                            isPosting = false
                            postsLiveData = fetchForumPosts()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isPosting
                ) {
                    Text("Post Message")
                }

                if (isPosting) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(strokeWidth = 2.dp)
                        Spacer(Modifier.width(8.dp))
                        Text("Posting…", color = MaterialTheme.colorScheme.onBackground)
                    }
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "Recent Posts",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }

        // List posts
        items(posts) { post ->
            ForumPostItem(
                post = post,
                onDelete = { postId ->
                    deleteForumPost(postId) { success -> }
                }
            )
        }
    }
}

@Composable
fun ForumPostItem(
    post: ForumPost,
    onDelete: (String) -> Unit
) {
    val currentUserUid = FirebaseAuth.getInstance()
        .currentUser
        ?.uid

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(Modifier.width(8.dp))
                Column {
                    Text(
                        post.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        post.username,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(Modifier.weight(1f))
                if (post.userId == currentUserUid) {
                    IconButton(onClick = { onDelete(post.id) }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete post"
                        )
                    }
                }
            }

            Text(
                post.timestamp.humanize(),
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(top = 8.dp)
            )

            HorizontalDivider(
                thickness = 1.dp,
                color = Color.LightGray,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Text(
                post.content,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
    }
}

