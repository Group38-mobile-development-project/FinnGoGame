package com.example.gamestore.presentation.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.example.gamestore.presentation.search.GlobalSearchBar
import com.example.gamestore.presentation.game.GameListItem
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.compose.ui.text.style.TextAlign



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameSearchScreen(
    navController: NavController,
    viewModel: GameSearchViewModel = viewModel(),
    onGameClick: (Int) -> Unit
) {
    val query by viewModel.query.collectAsState()
    val pagingFlow = viewModel.searchResults.collectAsState().value
    val pagingItems = pagingFlow.collectAsLazyPagingItems()

        Scaffold(
        topBar = {
            Column {
                TopAppBar(title = { Text("Search") })
            }
       }
    ) { padding ->

        Column(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {

            GlobalSearchBar(
                query = query,
                onQueryChange = { viewModel.onQueryChange(it) }
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                when {
                    pagingItems.loadState.refresh is LoadState.Loading -> {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillParentMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }

                    pagingItems.itemCount == 0 && query.isNotBlank() -> {
                        item {
                            Text(
                                text = "No games found for \"$query\"",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                textAlign = TextAlign.Center //
                            )

                        }
                    }

                    else -> {
                        items(pagingItems) { game ->
                            game?.let {
                                GameListItem(game = it, onClick = { onGameClick(it.id) })
                            }
                        }
                    }
                }
            }

        }
    }
}
