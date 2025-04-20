package com.example.gamestore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.gamestore.ui.composables.LeftAppBar
import com.example.gamestore.ui.composables.TopAppBar
import com.example.gamestore.ui.theme.AppThemeViewModel
import com.example.gamestore.presentation.navigation.AppNavHost
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat
import com.example.gamestore.ui.theme.Typography
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val themeViewModel: AppThemeViewModel = viewModel()
            val isDark by themeViewModel.isDarkTheme.collectAsState()

            GameStoreApp(
                isDarkTheme = isDark,
                onToggleTheme = { themeViewModel.toggleTheme() }
            )
        }
    }
}

@Composable
fun GameStoreApp(
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = if (isDarkTheme) darkColorScheme() else lightColorScheme()

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography
    ) {
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val navController = rememberNavController()

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                LeftAppBar(
                    onNavigateToHome = {
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                        }
                    },
                    onNavigateToFavorites = { navController.navigate("favorites") },
                    onNavigateToSettings = { navController.navigate("settings") },
                    onNavigateToSignIn = { navController.navigate("sign") },
                    onSignOut = {
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                        }
                    },
                    drawerState = drawerState
                )
            },
            gesturesEnabled = true
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        drawerState = drawerState,
                        onNavigateToHome = {
                            navController.navigate("home") {
                                popUpTo("home") { inclusive = true }
                            }
                        },
                        onNavigateToGenre = {
                            navController.navigate("genre_list")
                        },
                        onNavigateToPlatform = {
                            navController.navigate("platform_list")
                        },
                        onNavigateToForumPage = {
                            navController.navigate("forum_list")
                        },
                        onToggleTheme = onToggleTheme,
                        isDarkTheme = isDarkTheme
                    )
                },
                modifier = modifier.fillMaxSize()
            ) { innerPadding ->
                AppNavHost(
                    navController = navController,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}
