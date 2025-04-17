
package com.example.gamestore.presentation.search

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.example.gamestore.presentation.game.GameListItem

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
            TopAppBar(title = { Text("Search") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            GlobalSearchBar(
                query = query,
                onQueryChange = {
                    //Log.d("QUERY", "Searching: $it")
                    viewModel.onQueryChange(it)
                }
            )

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                when {
                    pagingItems.loadState.refresh is LoadState.Loading -> {
                        item {
                            Box(
                                modifier = Modifier.fillParentMaxSize(),
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
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    else -> {
                        items(
                            count = pagingItems.itemCount,
                            key = pagingItems.itemKey(),
                            contentType = pagingItems.itemContentType()
                        ) { index ->
                            val item = pagingItems[index]
                            item?.let {
                                GameListItem(game = it, onClick = { onGameClick(it.id) })
                            }
                        }
                    }
                }
            }
        }
    }
}
