package com.example.gamestore.presentation.game

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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
    onGameClick: (Int) -> Unit
) {
    val viewModel = remember { GenreGameViewModel(genreSlug) }
    val pagingItems = viewModel.games.collectAsLazyPagingItems()
    val searchQuery by viewModel.searchQuery.collectAsState()

    Scaffold(
        topBar = {
            SearchBar(
                title = "Search for game",
                navController = navController,
                value = searchQuery,
                onValueChange = { viewModel.onQueryChanged(it) }
            )
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
