package com.example.booktrackerapp.screens

import android.util.Log
import androidx.compose.foundation.Image
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import com.example.booktrackerapp.R
import com.example.booktrackerapp.viewModel.HomeViewModel
import com.example.booktrackerapp.api.BookItem
import com.example.booktrackerapp.ui.theme.BookTrackerAppTheme
import com.example.booktrackerapp.widgets.SimpleBottomAppBar
import com.example.booktrackerapp.widgets.SimpleTopAppBar




@Composable
fun HomeScreen(navController: NavController) {
    // State to hold the value of the search query
    val searchTextState = remember { mutableStateOf("") }
    val viewModel: HomeViewModel = viewModel()
    val bookListState = remember { mutableStateOf<List<BookItem>>(emptyList()) }
    val errorState = remember { mutableStateOf("") }
    
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
                            viewModel.searchBookByISBN(
                                searchTextState.value,
                                onSuccess = { books ->
                                    bookListState.value = books
                                    errorState.value = ""
                                },
                                onError = { error ->
                                    errorState.value = error
                                }
                            )
                        })
                    )

                    // Icon to search
                    IconButton(
                        onClick = {
                            //TODO
                            viewModel.searchBookByISBN(
                                searchTextState.value,

                                //hier wurde was gemacht.
                                onSuccess = {books ->
                                    bookListState.value = books
                                    errorState.value = ""
                                },
                                onError = { error ->
                                    errorState.value = error
                                }
                            )
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

                // Display book results (basic implementation)
                Column {
                    bookListState.value.forEach { bookItem ->
                        Text(text = bookItem.volumeInfo.title)
                        bookItem.volumeInfo.authors?.let { authors ->
                            Text(text = "Authors: ${authors.joinToString(", ")}")
                        }
                        bookItem.volumeInfo.imageLinks?.thumbnail?.let { thumbnail ->
                            Log.d("ThumbnailURL", thumbnail) // Add this for debugging purposes
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(thumbnail)
                                    .crossfade(true)
                                    .placeholder(R.drawable.works) // Replace with your placeholder drawable
                                    .error(R.drawable.error) // Replace with your error drawable
                                    .build(),
                                contentDescription = "Book Cover",
                                modifier = Modifier.size(100.dp)
                            )
                        }
                    }
                }

                // Button with camera icon
                FloatingActionButton(
                    onClick = {
                        // Handle opening camera
                              //TODO
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

fun displayBookDetails(books: List<BookItem>) {

}


