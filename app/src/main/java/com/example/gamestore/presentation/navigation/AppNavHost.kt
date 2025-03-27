package com.example.gamestore.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gamestore.presentation.genre.GenreListScreen
import com.example.gamestore.presentation.game.GameDetailScreen
import com.example.gamestore.presentation.game.GameListScreen
import com.example.gamestore.presentation.game.GameListScreenFilteredByGenre

//debug
import android.util.Log


@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "genre_list") {
        composable("genre_list") {
            GenreListScreen(onGenreClick = { genre ->
                //Log.d("GENRE_CLICK", "You clicked on ${genre.title}")
                navController.navigate("game_list/${genre.identifier}")
            })
        }

        composable(
            route = "game_list/{genreSlug}",
            arguments = listOf(navArgument("genreSlug") { type = NavType.StringType })
        ) { backStackEntry ->
            val genreSlug = backStackEntry.arguments?.getString("genreSlug") ?: ""
            GameListScreenFilteredByGenre(
                genreSlug = genreSlug,
                onGameClick = { gameId ->
                    navController.navigate("game_detail/$gameId")
                }
            )
        }

        composable(
            route = "game_detail/{gameId}",
            arguments = listOf(navArgument("gameId") { type = NavType.IntType })
        ) { backStackEntry ->
            val gameId = backStackEntry.arguments?.getInt("gameId") ?: 0
            GameDetailScreen(gameId)
        }

        composable("game_list") {
            GameListScreen(onGameClick = { gameId ->
                navController.navigate("game_detail/$gameId")
            })
        }
    }
}
