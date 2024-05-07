package com.example.booktrackerapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.booktrackerapp.R
import com.example.booktrackerapp.ui.theme.BookTrackerAppTheme
import com.example.booktrackerapp.widgets.SimpleBottomAppBar
import com.example.booktrackerapp.widgets.SimpleTopAppBar
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(navController: NavController) {

    //Permissions
    val cameraPermissionState: PermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)
    val hasPermission = cameraPermissionState.status.isGranted
    val onRequestPermission = cameraPermissionState::launchPermissionRequest

    // State to hold the value of the search query
    val searchTextState = remember { mutableStateOf("") }

    BookTrackerAppTheme {
        Scaffold(
            topBar = { SimpleTopAppBar(navController, title = "Home", backButton = false) },
            bottomBar = { SimpleBottomAppBar(navController) }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Search bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = searchTextState.value,
                        onValueChange = { searchTextState.value = it },
                        label = { Text("Search") },
                        placeholder = { Text("Enter ISBN") },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        shape = RoundedCornerShape(16.dp),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { /* Handle search action */ })
                    )

                    // Icon to search
                    IconButton(
                        onClick = {
                            //TODO
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Camera",
                            tint = Color.Black
                        )
                    }
                }

                // Button with camera icon
                FloatingActionButton(
                    onClick = {
                        //Zu CameraScreen wechseln, wenn Permission erteilt wurde
                        if (hasPermission) {
                            navController.navigate("camerascreen")
                        } else {
                            onRequestPermission()
                        }
                    },
                    modifier = Modifier
                        .padding(top = 100.dp)
                        .size(100.dp)
                        .align(Alignment.CenterHorizontally),
                    shape = CircleShape,
                    contentColor = Color.White,
                    containerColor = Color.Black
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_camera), // Placeholder icon, replace with your camera icon
                        contentDescription = "Camera",
                        modifier = Modifier
                            .size(50.dp)
                    )
                }


            }
        }
    }
}

