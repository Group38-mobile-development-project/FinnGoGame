
package com.example.gamestore.presentation.genre

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.gamestore.data.model.Genre
import androidx.compose.ui.Alignment
import androidx.navigation.NavController
import com.example.gamestore.presentation.utils.AppTopBar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenreListScreen(
    navController: NavController, // add navController
    viewModel: GenreViewModel = viewModel(),
    onGenreClick: (Genre) -> Unit = {}
) {
    val genres = viewModel.genres.collectAsState().value
    var query by remember { mutableStateOf("") }

    val filteredGenres = remember(query, genres) {
        if (query.isBlank()) genres
        else genres.filter { it.title.contains(query, ignoreCase = true) }
    }

    Scaffold(
        topBar = {
            AppTopBar(title = "Genres", navController = navController) // call AppTopBar have button Search
        }
    ) { padding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(padding)
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(filteredGenres) { genre ->
                    GenreItem(genre = genre, onClick = { onGenreClick(genre) })
                }
            }
        }
    }
}

@Composable
fun GenreItem(genre: Genre, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box {
                Image(
                    painter = rememberAsyncImagePainter(genre.backgroundImage),
                    contentDescription = genre.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Text(
                    text = genre.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp)
                )
            }
        }
    }
}


