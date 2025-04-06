package com.example.gamestore.presentation.game


import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.example.gamestore.data.model.Game



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameListScreen(
    viewModel: GameViewModel = viewModel(),
    onGameClick: (Game) -> Unit
) {
    val pagingItems = viewModel.games.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Game List") })
        }
    ) { padding ->
        LazyColumn(contentPadding = padding) {
            items(
        count = pagingItems.itemCount,
        key = pagingItems.itemKey(),
        contentType = pagingItems.itemContentType(
            )
    ) { index ->
        val item = pagingItems[index]
        if (item != null) {
            GameListItem(game = item, onClick = { onGameClick(item) })
        }
            }
        }
    }
}
