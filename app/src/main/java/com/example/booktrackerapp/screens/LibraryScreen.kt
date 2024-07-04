package com.example.booktrackerapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.booktrackerapp.ui.theme.BookTrackerAppTheme
import com.example.booktrackerapp.viewModel.LibraryViewModel
import com.example.booktrackerapp.viewModel.LibraryViewModel.SortOrder
import com.example.booktrackerapp.widgets.BookRowSimple
import com.example.booktrackerapp.widgets.SimpleBottomAppBar
import com.example.booktrackerapp.widgets.SimpleTopAppBar

@Composable
fun LibraryScreen(navController: NavController, libraryViewModel: LibraryViewModel = hiltViewModel()) {
    var sortOrder by rememberSaveable { mutableStateOf(SortOrder.None) }
    var filterName by rememberSaveable { mutableStateOf("") }
    var filterGenre by rememberSaveable { mutableStateOf("") }
    var filterReadStatus by rememberSaveable { mutableStateOf<Boolean?>(null) }
    var filterAuthor by rememberSaveable { mutableStateOf("") }

    var selectedGenre by rememberSaveable { mutableStateOf("") }


    LaunchedEffect(sortOrder, filterName, filterGenre, filterReadStatus, filterAuthor,selectedGenre) {
        libraryViewModel.setFilterCriteria(filterName, filterGenre, filterReadStatus, filterAuthor)
        libraryViewModel.sortState.value = sortOrder
        libraryViewModel.fetchBooks()
    }

    val booksState = libraryViewModel.booksState
    val errorState = libraryViewModel.errorState

    BookTrackerAppTheme {
        Scaffold(
            topBar = { SimpleTopAppBar(navController, title = "Library", backButton = false) },
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
                // Filter UI
                TextField(
                    value = filterName,
                    onValueChange = { filterName = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )
                TextField(
                    value = filterGenre,
                    onValueChange = { filterGenre = it },
                    label = { Text("Genre") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )
                TextField(
                    value = filterAuthor,
                    onValueChange = { filterAuthor = it },
                    label = { Text("Author") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    Text("Read Status")
                    Spacer(modifier = Modifier.width(8.dp))
                    RadioButton(
                        selected = filterReadStatus == true,
                        onClick = { filterReadStatus = true }
                    )
                    Text("Read")
                    Spacer(modifier = Modifier.width(8.dp))
                    RadioButton(
                        selected = filterReadStatus == false,
                        onClick = { filterReadStatus = false }
                    )
                    Text("Unread")
                    Spacer(modifier = Modifier.width(8.dp))
                    RadioButton(
                        selected = filterReadStatus == null,
                        onClick = { filterReadStatus = null }
                    )
                    Text("All")
                }

                // Sort Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = { sortOrder = SortOrder.Ascending }) {
                        Text("Sort Ascending")
                    }
                    Button(onClick = { sortOrder = SortOrder.Descending }) {
                        Text("Sort Descending")
                    }
                }

                if (errorState.value != null) {
                    Text(text = errorState.value ?: "", color = MaterialTheme.colorScheme.error)
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        items(booksState.value) { book ->
                            BookRowSimple(
                                book = book,
                                navController = navController,
                                isClickable = true
                            ) { bookId, isFavorite ->
                                libraryViewModel.updateFavoriteStatus(bookId, isFavorite)
                            }
                        }
                    }
                }
            }
        }
    }
}