package com.example.booktrackerapp.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.booktrackerapp.R
import com.example.booktrackerapp.api.BookItem
import com.example.booktrackerapp.ui.theme.BookTrackerAppTheme
import com.example.booktrackerapp.viewModel.HomeViewModel
import com.example.booktrackerapp.viewModel.LibraryViewModel
import com.example.booktrackerapp.widgets.BookRowSimple
import com.example.booktrackerapp.widgets.SimpleBottomAppBar
import com.example.booktrackerapp.widgets.SimpleTopAppBar
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(navController: NavController,viewModel: HomeViewModel, libraryViewModel: LibraryViewModel = hiltViewModel(), isbn: String? = null) {

    viewModel.initialize(navController)

    // State to hold the value of the search query
    val searchTextState = remember { mutableStateOf("") }
    val errorState = remember { mutableStateOf("") }
    val singlebookState = remember { mutableStateOf<BookItem?>(null) }

    //Permissions
    val cameraPermissionState: PermissionState =
        rememberPermissionState(android.Manifest.permission.CAMERA)
    val hasPermission = cameraPermissionState.status.isGranted
    val onRequestPermission = cameraPermissionState::launchPermissionRequest

    LaunchedEffect(isbn) {
        isbn?.let {
            viewModel.searchBookByISBN(
                rawIsbn = it,
                onSuccess = { book ->
                    singlebookState.value = book
                    errorState.value = ""
                },
                onError = { error ->
                    errorState.value = error
                }
            )
        }
    }

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

                        //hier für keyboardActions wurde was gemacht.
                        keyboardActions = KeyboardActions(onDone = {
                            if (searchTextState.value.isNotEmpty()) {
                                viewModel.searchBookByISBN(
                                    searchTextState.value,
                                    onSuccess = { books ->
                                        singlebookState.value = books
                                        errorState.value = ""
                                    },
                                    onError = { error ->
                                        errorState.value = error
                                    }
                                )
                            } else {
                                singlebookState.value = null
                            }
                        })
                    )


                    // Icon to search
                    IconButton(
                        onClick = {
                            if (searchTextState.value.isNotEmpty()) {
                                viewModel.searchBookByISBN(
                                    searchTextState.value,

                                    //hier wurde was gemacht.
                                    onSuccess = { books ->
                                        singlebookState.value = books
                                        errorState.value = ""
                                    },
                                    onError = { error ->
                                        errorState.value = error
                                    }
                                )
                            } else {
                                singlebookState.value = null
                            }

                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Camera",
                            tint = Color.Black
                        )
                    }
                }

                // Display error messages
                if (errorState.value.isNotEmpty()) {
                    Text(text = errorState.value, color = Color.Red)
                }


                // Display the single book result using the SingleBookView composable
                if (singlebookState.value != null) {
                    BookRowSimple(book = singlebookState.value!!,navController = navController, isClickable = true) { bookId, isFavorite ->
                        libraryViewModel.updateFavoriteStatus(bookId, isFavorite)
                    }
                }


                // Button with camera icon
                FloatingActionButton(
                    onClick = {
                        //Zu CameraScreen wechseln, wenn Permission erteilt wurde
                        if (hasPermission) {
                            navController.navigate("camerascreenfront")
                        } else {
                            onRequestPermission()
                        }
                    },
                    modifier = Modifier
                        .padding(100.dp)
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
