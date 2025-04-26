package com.example.gamestore.presentation.game

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.example.gamestore.presentation.genre.GenreGameViewModel
import com.example.gamestore.presentation.utils.SearchBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameListScreenFilteredByGenre(
    navController: NavController,
    genreSlug: String,
//    genreName: String, // <<< genreName
    onGameClick: (Int) -> Unit
) {
    val viewModel = remember { GenreGameViewModel(genreSlug) }
    val pagingItems = viewModel.games.collectAsLazyPagingItems()
    val searchQuery by viewModel.searchQuery.collectAsState()

    fun String.slugToTitle(): String {
        return this.split("-", "_")
            .joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }
    }

    Scaffold(
        topBar = {
            Column {
                SearchBar(
                    title = "Search for game",
                    navController = navController,
                    value = searchQuery,
                    onValueChange = { viewModel.onQueryChanged(it) }
                )
                Text(
                    text = genreSlug.slugToTitle(),   // convert
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(
                count = pagingItems.itemCount,
                key = pagingItems.itemKey(),
                contentType = pagingItems.itemContentType()
            ) { index ->
                val item = pagingItems[index]
                if (item != null) {
                    GameListItem(
                        game = item,
                        onClick = {
                            navController.navigate("game_detail/${item.id}")
                        }
                    )
                }
            }
        }
    }
}
