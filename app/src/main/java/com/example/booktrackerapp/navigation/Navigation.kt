package com.example.booktrackerapp.navigation

import CameraScreen
import ImageViewModel
import PreviewScreen
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
    val imageViewModel: ImageViewModel = viewModel()

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
            CameraScreen(navController = navController, imageViewModel = imageViewModel)
        }
        composable(Screen.PreviewScreen.route)
        {
            PreviewScreen(navController = navController, imageViewModel = imageViewModel)
        }
    }
}
