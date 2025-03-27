package com.example.gamestore.presentation.genre

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color

//import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.gamestore.data.model.Genre
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment


//debug
import android.util.Log


@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun GenreListScreen(
    viewModel: GenreViewModel = viewModel(),
    onGenreClick: (Genre) -> Unit = {}
) {
    val genres = viewModel.genres.collectAsState().value

    LaunchedEffect(Unit) {
        genres.forEach {
            //Log.d("GENRE_SLUG_DEBUG", "${it.title} -> ${it.identifier}")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Genres") })
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            items(genres) { genre ->
                GenreItem(genre = genre, onClick = { onGenreClick(genre) })
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
            .clickable {
                //Log.d("GENRE_ITEM", "Clicked on: ${genre.title}")
                onClick()
            }
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
                    //color = MaterialTheme.colorScheme.onPrimary,
                    color = Color.Green,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp)
                )
            }
        }
    }
}
