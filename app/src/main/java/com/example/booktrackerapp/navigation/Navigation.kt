package com.example.booktrackerapp.navigation

import PreviewScreen
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.booktrackerapp.model.service.ImageUri
import com.example.booktrackerapp.screens.CameraScreen
import androidx.navigation.navArgument
import com.example.booktrackerapp.screens.CameraScreenFront
import com.example.booktrackerapp.screens.DetailScreen
import com.example.booktrackerapp.screens.HomeScreen
import com.example.booktrackerapp.screens.LibraryScreen
import com.example.booktrackerapp.screens.PreviewScreenFront
import com.example.booktrackerapp.screens.SignInScreen
import com.example.booktrackerapp.screens.SignUpScreen
import com.example.booktrackerapp.screens.SplashScreen
import com.example.booktrackerapp.screens.Userscreen
import com.example.booktrackerapp.viewModel.CameraFrontViewModel
import com.example.booktrackerapp.viewModel.CameraViewModel
import com.example.booktrackerapp.viewModel.HomeViewModel
import com.example.booktrackerapp.viewModel.LibraryViewModel

// Manages all navigation paths.
@Composable
fun Navigation() {
    // Remember the Nav Controller for navigation within the app
    val navController = rememberNavController()

    // Initialize view models using Jetpack Compose's viewModel function
    val viewModel: HomeViewModel = viewModel()
    val cameraViewModel: CameraViewModel = viewModel()
    val cameraFrontViewModel: CameraFrontViewModel = viewModel()
    val libraryViewModel: LibraryViewModel = viewModel()

    // Holds the image URI for passing between screens
    val imageUriHolder = ImageUri()


    // Defines the navigation host that acts as a container for navigation screens
    NavHost(
        navController = navController,
        startDestination = Screen.SplashScreen.route// Sets the start destination to SplashScreen
    ) {
        // Defines various routes and their associated composables for navigation
        composable(Screen.HomeScreen.route) {
            HomeScreen(navController = navController,viewModel )
        }
        composable(Screen.HomeScreen.route + "/{isbn}",
            arguments = listOf(navArgument("isbn") { type = NavType.StringType })
        ) { backStackEntry ->
            val isbn = backStackEntry.arguments?.getString("isbn")
            HomeScreen(navController = navController, viewModel = viewModel, isbn = isbn)
        }
        composable(Screen.LibraryScreen.route) {
            LibraryScreen(navController = navController,libraryViewModel=libraryViewModel)
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
            CameraScreen(navController = navController, cameraViewModel = cameraViewModel, imageUriHolder = imageUriHolder)
        }
        // Add the showMessage parameter to the CameraScreenFront route
        composable(
            route = Screen.CameraScreenFront.route + "?showMessage={showMessage}",
            arguments = listOf(navArgument("showMessage") { type = NavType.BoolType; defaultValue = false })
        ) { backStackEntry ->
            val showMessage = backStackEntry.arguments?.getBoolean("showMessage") ?: false
            CameraScreenFront(navController = navController, cameraViewModel = cameraFrontViewModel, imageUriHolder = imageUriHolder, showMessage = showMessage)
        }
        composable(Screen.PreviewScreen.route)
        {
            PreviewScreen(navController = navController, imageUriHolder = imageUriHolder)
        }
        composable(Screen.PreviewScreenFront.route)
        {
            PreviewScreenFront(navController = navController, imageUriHolder = imageUriHolder, viewModel= cameraFrontViewModel)
        }
        composable(
            route = Screen.DetailScreen.route + "/{isbn}",
            arguments = listOf(navArgument("isbn") { type = NavType.StringType })
        ) { backStackEntry ->
            DetailScreen(
                navController = navController,
                isbn = backStackEntry.arguments?.getString("isbn") ?: "", // um die isbn vom backStack zu extrahieren
                libraryId = "defaultLibrary"
            )
        }
    }
}
