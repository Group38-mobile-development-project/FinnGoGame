package com.example.gamestore.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.gamestore.presentation.genre.GenreListScreen
import com.example.gamestore.presentation.game.GameDetailScreen
import com.example.gamestore.presentation.game.GameListScreenFilteredByGenre
import com.example.gamestore.presentation.search.GameSearchScreen
import com.example.gamestore.ui.screens.FavoritesScreen
import com.example.gamestore.ui.screens.LoginScreen
import com.example.gamestore.ui.screens.MainScreen
import com.example.gamestore.ui.screens.SettingsScreen

import com.example.gamestore.presentation.game.GameListScreenFilteredByPlatfrom
import com.example.gamestore.presentation.platform.PlatformListScreen
import com.example.gamestore.ui.screens.ForumPageScreen


@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController = navController, startDestination = "home", modifier = modifier) {

        composable("home") {
            MainScreen(navController = navController)
        }

        composable("favorites") {
            FavoritesScreen(navController = navController,
                onGameClick = { gameId ->
                    navController.navigate("game_detail/$gameId")
                }
            )
        }

        composable("settings") {
            SettingsScreen(navController = navController)
        }
        composable("forum_list") {
            ForumPageScreen(navController = navController)
        }


        composable("sign") {
            LoginScreen(navController = navController)
        }

        composable("search") {
            GameSearchScreen(
                navController = navController,
                onGameClick = { gameId ->
                    navController.navigate("game_detail/$gameId")
                }
            )
        }

        composable("genre_list") {
            GenreListScreen(
                navController = navController,
                onGenreClick = { genre ->
                    navController.navigate("game_list/${genre.identifier}")
                }
            )
        }

        composable(
            route = "game_list/{genreSlug}",
            arguments = listOf(navArgument("genreSlug") { type = NavType.StringType })
        ) { backStackEntry ->
            val genreSlug = backStackEntry.arguments?.getString("genreSlug") ?: ""
            GameListScreenFilteredByGenre(
                genreSlug = genreSlug,
                navController = navController,
                onGameClick = { gameId ->
                    navController.navigate("game_detail/$gameId")
                }
            )
        }

        composable(
            route = "game_detail/{gameId}",
            arguments = listOf(navArgument("gameId") { type = NavType.IntType })
        ) { backStackEntry ->

            GameDetailScreen(
                navController = navController,
                gameId = backStackEntry.arguments?.getInt("gameId") ?: 0
            )
        }

        //platform
        composable("platform_list") {
            PlatformListScreen(
                navController = navController,
                onPlatformClick = { platform ->
                    navController.navigate("game_list_platform/${platform.id}/${platform.name}")
                }
            )
        }

        composable(
            "game_list_platform/{platformId}/{platformName}",
            arguments = listOf(
                navArgument("platformId") { type = NavType.IntType },
                navArgument("platformName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val platformId = backStackEntry.arguments?.getInt("platformId") ?: 0
            val platformName = backStackEntry.arguments?.getString("platformName") ?: ""

            GameListScreenFilteredByPlatfrom(
                navController = navController,
                platformId = platformId,
                platformName = platformName,
                onGameClick = { gameId ->
                    navController.navigate("game_detail/$gameId")
                }
            )
        }
        
    }
}
