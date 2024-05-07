package com.example.booktrackerapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.booktrackerapp.screens.HomeScreen
import com.example.booktrackerapp.screens.LibraryScreen
import com.example.booktrackerapp.screens.Userscreen
import com.example.booktrackerapp.screens.CameraScreen


@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.HomeScreen.route
    ) {
        composable(Screen.HomeScreen.route) {
            HomeScreen(navController = navController )
        }
        composable(
            route = Screen.LibraryScreen.route
        ) {
            LibraryScreen(navController = navController)
        }
        composable(Screen.UserScreen.route)
        {
            Userscreen(navController = navController)
        }
        composable(Screen.CameraScreen.route)
        {
            CameraScreen(navController = navController)
        }
    }
}
