package com.example.booktrackerapp.widgets

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.booktrackerapp.navigation.Screen

@Composable
fun SimpleBottomAppBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        NavigationBarItem(
            label = { Text("Home") },
            selected = currentRoute == Screen.HomeScreen.route,
            onClick = {
                navController.navigate(Screen.HomeScreen.route) {
                    popUpTo(navController.graph.startDestinationId)
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = "Go to home"
                )
            }
        )
        NavigationBarItem(
            label = { Text("Library") },
            selected = currentRoute == Screen.LibraryScreen.route,
            onClick = {
                navController.navigate(Screen.LibraryScreen.route) {
                    popUpTo(navController.graph.startDestinationId)
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = "Go to Library"
                )
            }
        )
        NavigationBarItem(
            label = { Text("User") },
            selected = currentRoute == Screen.UserScreen.route,
            onClick = {
                navController.navigate(Screen.UserScreen.route) {
                    popUpTo(navController.graph.startDestinationId)
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "Go to UserScreen"
                )
            }
        )
    }
}
