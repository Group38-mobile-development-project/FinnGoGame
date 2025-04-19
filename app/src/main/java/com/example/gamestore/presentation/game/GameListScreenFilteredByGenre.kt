//package com.example.gamestore.presentation.game
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.navigation.NavController
//import androidx.paging.compose.collectAsLazyPagingItems
//import androidx.paging.compose.itemContentType
//import androidx.paging.compose.itemKey
//import com.example.gamestore.presentation.genre.GenreGameViewModel
//import com.example.gamestore.presentation.utils.SearchBar
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun GameListScreenFilteredByGenre(
//    navController: NavController,
//    genreSlug: String,
//    onGameClick: (Int) -> Unit
//) {
//    val viewModel = remember { GenreGameViewModel(genreSlug) }
//    val pagingItems = viewModel.games.collectAsLazyPagingItems()
//    val searchQuery by viewModel.searchQuery.collectAsState()
//
//    Scaffold(
//        topBar = {
//            SearchBar(
//                title = "Search for game",
//                navController = navController,
//                value = searchQuery,
//                onValueChange = { viewModel.onQueryChanged(it) }
//            )
//        }
//    ) { padding ->
//        LazyColumn(modifier = Modifier.padding(padding)) {
//            items(
//                count = pagingItems.itemCount,
//                key = pagingItems.itemKey(),
//                contentType = pagingItems.itemContentType()
//            ) { index ->
//                val item = pagingItems[index]
//                if (item != null) {
//                    GameListItem(
//                        game = item,
//                        onClick = {
//                            navController.navigate("game_detail/${item.id}")
//                        }
//                    )
//                }
//            }
//        }
//    }
//}
//

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
    onGameClick: (Int) -> Unit
) {
    val viewModel = remember { GenreGameViewModel(genreSlug) }
    val pagingItems = viewModel.games.collectAsLazyPagingItems()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedSort by viewModel.sort.collectAsState()

    Scaffold(
        topBar = {
            Column {
                SearchBar(
                    title = "Search for game",
                    navController = navController,
                    value = searchQuery,
                    onValueChange = { viewModel.onQueryChanged(it) }
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    SortDropdown(
                        selectedSort = selectedSort,
                        onSortSelected = { viewModel.onSortChanged(it) }
                    )
                }
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

@Composable
fun SortDropdown(
    selectedSort: String?,
    onSortSelected: (String?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val sortOptions = listOf(null, "name", "rating")

    Box {
        OutlinedButton(onClick = { expanded = true }) {
            Text("Sort: ${selectedSort?.replaceFirstChar { it.uppercaseChar() } ?: "Default"}")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            sortOptions.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(option?.replaceFirstChar { it.uppercaseChar() } ?: "Default")
                    },
                    onClick = {
                        onSortSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
