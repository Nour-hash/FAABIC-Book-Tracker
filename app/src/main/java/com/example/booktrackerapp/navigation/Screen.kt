package com.example.booktrackerapp.navigation

sealed class Screen(val route: String) {
    data object HomeScreen : Screen("homescreen")
    data object LibraryScreen : Screen("libraryscreen")
    data object UserScreen :  Screen("userscreen")
    data object SignUpScreen : Screen("signUpscreen")
    data object SignInScreen : Screen("signInscreen")
    data object SplashScreen : Screen("splashscreen")
    data object CameraScreen : Screen("camerascreen")
    data object PreviewScreen : Screen("previewscreen")


}