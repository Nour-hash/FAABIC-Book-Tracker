package com.example.booktrackerapp.navigation

sealed class Screen(val route: String) {
    data object HomeScreen : Screen("homescreen")
    data object LibraryScreen : Screen("libraryscreen")
    data object UserScreen :  Screen("userscreen")
}