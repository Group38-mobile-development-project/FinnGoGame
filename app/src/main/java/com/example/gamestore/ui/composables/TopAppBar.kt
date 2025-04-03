package com.example.gamestore.ui.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    drawerState: DrawerState,
    onNavigateToHome: () -> Unit,
    onNavigateToGenre: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var menuExpanded by remember { mutableStateOf(false) }

    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = {
            Text("Store demo")
        },
        navigationIcon = {
            IconButton(onClick = {
                scope.launch {
                    if (drawerState.isClosed) drawerState.open() else drawerState.close()
                }
            }) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "User Icon"
                )
            }
        },
        actions = {
            IconButton(onClick = { menuExpanded = true }) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "Menu Icon"
                )
            }

            MenuDropdown(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false },
                onNavigateToHome = onNavigateToHome,
                onNavigateToGenre = onNavigateToGenre
            )
        }
    )
}
