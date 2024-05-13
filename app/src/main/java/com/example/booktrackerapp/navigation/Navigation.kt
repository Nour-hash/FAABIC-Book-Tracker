package com.example.booktrackerapp.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.booktrackerapp.screens.CameraScreen
import com.example.booktrackerapp.screens.DetailScreen
import com.example.booktrackerapp.screens.HomeScreen
import com.example.booktrackerapp.screens.LibraryScreen
import com.example.booktrackerapp.screens.SignInScreen
import com.example.booktrackerapp.screens.SignUpScreen
import com.example.booktrackerapp.screens.SplashScreen
import com.example.booktrackerapp.screens.Userscreen
import com.example.booktrackerapp.viewModel.HomeViewModel

@Composable
fun Navigation() {
    val navController = rememberNavController()
    val viewModel: HomeViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.SplashScreen.route
    ) {
        composable(Screen.HomeScreen.route) {
            HomeScreen(navController = navController,viewModel )
        }
        composable(Screen.LibraryScreen.route) {
            LibraryScreen(navController = navController,viewModel)
        }
        composable(Screen.UserScreen.route)
        {
            Userscreen(navController = navController,viewModel)
        }

        composable(Screen.SignInScreen.route) {
            SignInScreen(navController = navController)
        }
        composable(Screen.SignUpScreen.route) {
            SignUpScreen(navController = navController)
        }
        composable(Screen.SplashScreen.route) {
            SplashScreen(navController = navController)
        }
        composable(Screen.CameraScreen.route)
        {
            CameraScreen(navController = navController)
        }
        composable(
            route = Screen.DetailScreen.route + "/{isbn}",
            arguments = listOf(navArgument("isbn") { type = NavType.StringType })
        ) { backStackEntry ->
            DetailScreen(
                navController = navController,
                isbn = backStackEntry.arguments?.getString("isbn") ?: ""
            )
        }
    }
}
