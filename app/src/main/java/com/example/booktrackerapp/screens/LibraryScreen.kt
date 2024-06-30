package com.example.booktrackerapp.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.booktrackerapp.ui.theme.BookTrackerAppTheme
import com.example.booktrackerapp.viewModel.LibraryViewModel
import com.example.booktrackerapp.widgets.BookRowSimple
import com.example.booktrackerapp.widgets.SimpleBottomAppBar
import com.example.booktrackerapp.widgets.SimpleTopAppBar

@Composable
fun LibraryScreen(navController: NavController, libraryViewModel: LibraryViewModel = hiltViewModel()) {
    LaunchedEffect(Unit) {
        libraryViewModel.fetchBooks()
    }

    val booksState = libraryViewModel.booksState
    val errorState = libraryViewModel.errorState

    BookTrackerAppTheme {
        Scaffold(
            topBar = { SimpleTopAppBar(navController, title = "Library", backButton = false) },
            bottomBar = { SimpleBottomAppBar(navController) }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    if (errorState.value != null) {
                    Text(text = errorState.value ?: "", color = MaterialTheme.colorScheme.error)
                } else {
                    booksState.value.forEach { book ->
                        BookRowSimple(
                            book = book,
                            navController = navController,
                            isClickable = true
                        ) { bookId, isFavorite ->
                            libraryViewModel.updateFavoriteStatus(bookId, isFavorite)
                        }
                    }
                } }

            }
        }
    }
}
