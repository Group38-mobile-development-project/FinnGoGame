//package com.example.gamestore
//
//import android.os.Bundle
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.activity.enableEdgeToEdge
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.rememberNavController
//import com.example.gamestore.ui.composables.LeftAppBar
//import com.example.gamestore.ui.composables.TopAppBar
//import com.example.gamestore.ui.screens.LoginScreen
//import com.example.gamestore.ui.screens.MainScreen
//import com.example.gamestore.ui.screens.SettingsScreen
//import com.example.gamestore.ui.theme.GamestoreTheme
//
//class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContent {
//            GamestoreTheme {
//                GameStoreApp()
//            }
//        }
//    }
//}
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun GameStoreApp(modifier: Modifier = Modifier) {
//    val drawerState = rememberDrawerState(initialValue = DrawerValue.Open)
//    val navController = rememberNavController()
//
//    ModalNavigationDrawer(
//        drawerState = drawerState,
//        drawerContent = {
//            LeftAppBar(
//                onNavigateToHome = {
//                    navController.navigate("home") {
//                        popUpTo("home") { inclusive = true }
//                    }
//                },
//                onNavigateToSignIn = { navController.navigate("sign") },
//                onSignOut = {
//                    navController.navigate("home") {
//                        popUpTo("home") { inclusive = true } // Avoid multiple "home" stacking
//                    }
//                },
//                onNavigateToSettings = { navController.navigate("settings") },
//                drawerState = drawerState
//            )
//        },
//        gesturesEnabled = true
//    ) {
//        Scaffold(
//            topBar = { TopAppBar(drawerState) },
//            modifier = Modifier.fillMaxSize()
//        ) { innerPadding ->
//            NavHost(
//                navController = navController,
//                startDestination = "home",
//                modifier = Modifier.padding(innerPadding)
//            ) {
//                // In MainActivity's NavHost:
//                composable(route = "home") {
//                    MainScreen(navController) // or MainScreen(modifier = Modifier.fillMaxSize())
//                }
//
//                composable(route = "settings") {
//                    SettingsScreen(navController)
//                }
//                composable(route = "sign") {
//                    LoginScreen(navController)
//                }
//            }
//        }
//    }
//}





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
import androidx.navigation.compose.rememberNavController
import com.example.gamestore.ui.composables.LeftAppBar
import com.example.gamestore.ui.composables.TopAppBar
import com.example.gamestore.ui.theme.GamestoreTheme
import com.example.gamestore.presentation.navigation.AppNavHost

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GamestoreTheme {
                GameStoreApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameStoreApp(modifier: Modifier = Modifier) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed) // change Open to closed apper mainscreen first
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
                onNavigateToSignIn = { navController.navigate("sign") },
                onSignOut = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                onNavigateToSettings = { navController.navigate("settings") },
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
                    }
                )
            },
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            AppNavHost(
                navController = navController,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}
