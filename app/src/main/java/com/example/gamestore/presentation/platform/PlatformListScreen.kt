package com.example.gamestore.presentation.platform

import androidx.compose.foundation.lazy.items
import com.example.gamestore.presentation.platform.PlatformViewModel


import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.gamestore.data.model.Platform
import androidx.compose.ui.Alignment
import androidx.navigation.NavController
import com.example.gamestore.presentation.utils.SearchBar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlatformListScreen(
    navController: NavController,
    viewModel: PlatformViewModel = viewModel(), // Fix tên class
    onPlatformClick: (Platform) -> Unit = {} // Đổi Genre thành Platform
) {
    val platforms = viewModel.platforms.collectAsState().value
    var query by remember { mutableStateOf("") }

    val filteredPlatforms = remember(query, platforms) {
        if (query.isBlank()) platforms
        else platforms.filter { it.name.contains(query, ignoreCase = true) }
    }

    Scaffold(
        topBar = {
            SearchBar(
                title = "Search for game...",
                navController = navController,
                value = query,
                onValueChange = { query = it }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(filteredPlatforms) { platform ->
                    PlatformItem(platform = platform, onClick = { onPlatformClick(platform) })
                }
            }
        }
    }
}

@Composable
fun PlatformItem(platform: Platform, onClick: () -> Unit) {
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
                    painter = rememberAsyncImagePainter(platform.backgroundImage),
                    contentDescription = platform.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Text(
                    text = platform.name,
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
