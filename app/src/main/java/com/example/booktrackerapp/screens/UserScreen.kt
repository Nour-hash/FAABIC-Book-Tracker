package com.example.booktrackerapp.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.booktrackerapp.ui.theme.BookTrackerAppTheme
import com.example.booktrackerapp.viewModel.HomeViewModel
import com.example.booktrackerapp.widgets.SimpleBottomAppBar
import com.example.booktrackerapp.widgets.SimpleTopAppBar

@Composable
fun Userscreen(navController: NavController,viewModel: HomeViewModel) {

    viewModel.initialize(navController)
    val username = viewModel.getUsername()
    val userEmail = viewModel.getUserEmail()
    val userPhotoUrl = viewModel.getUserPhotoUrl()


    BookTrackerAppTheme {
        Scaffold(
            topBar = { SimpleTopAppBar(navController, title = "User", backButton = true) },
            bottomBar = { SimpleBottomAppBar(navController) }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                //TODO PhotoURL
                // Round picture for username
                AsyncImage(
                    model = "https://i.natgeofe.com/n/548467d8-c5f1-4551-9f58-6817a8d2c45e/NationalGeographic_2572187_square.jpg",
                    contentDescription = "User profile picture",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .padding(8.dp)
                )

//TODO username
                // Label for username
                Text(text = "Username: Nour", modifier = Modifier.padding(vertical = 8.dp))

                // Label for email
                Text(text = "Email: ${userEmail}", modifier = Modifier.padding(vertical = 8.dp))

                // Button with camera icon
                FloatingActionButton(
                    onClick = {
                   viewModel.onSignOutClick()
                    },
                    modifier = Modifier
                        .padding(50.dp)
                        .size(100.dp)
                        .align(Alignment.CenterHorizontally),
                    shape = CircleShape,
                    contentColor = Color.White,
                    containerColor = Color.Black
                ) {
                    Icon(
                        imageVector = Icons.Filled.ExitToApp,
                        contentDescription = "Camera",
                        modifier = Modifier
                            .size(50.dp)
                    )
                }

                // Button with camera icon
                FloatingActionButton(
                    onClick = {
                        viewModel.onDeleteAccountClick()
                    },
                    modifier = Modifier
                        .padding(50.dp)
                        .size(100.dp)
                        .align(Alignment.CenterHorizontally),
                    shape = CircleShape,
                    contentColor = Color.White,
                    containerColor = Color.Black
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Camera",
                        modifier = Modifier
                            .size(50.dp)
                    )
                }
            }
        }
    }
}
