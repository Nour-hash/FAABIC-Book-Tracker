package com.example.booktrackerapp.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import com.example.booktrackerapp.viewModel.DetailViewModel
import com.example.booktrackerapp.viewModel.LibraryViewModel
import com.example.booktrackerapp.widgets.BookDetails
import com.example.booktrackerapp.widgets.BookRowSimple
import com.example.booktrackerapp.widgets.PagesReadInput
import com.example.booktrackerapp.widgets.RatingBar
import com.example.booktrackerapp.widgets.ReadStatusButton
import com.example.booktrackerapp.widgets.SimpleTopAppBar

//Bildschirm, der Buchdetails anzeigt, basierend auf einer ISBN.
@Composable
fun DetailScreen(
    navController: NavController,
    isbn: String,
    libraryId:String,
    viewModel: DetailViewModel = hiltViewModel(),
    libraryViewModel: LibraryViewModel = hiltViewModel()
) {
    val bookDetailState = viewModel.bookDetailState
    val errorState = viewModel.errorState
    val isRead = viewModel.readState.value ?: false  // Abrufen des Lesestatus als lokale Variable
    val rating = viewModel.ratingState.value ?: 0  // Abrufen der Bewertung als lokale Variable
    val isLoading = viewModel.isLoading.value
    val isBookInLibrary = viewModel.isBookInLibrary.value


    LaunchedEffect(isbn) {
        viewModel.getBookDetails(isbn)
    }

    Scaffold(
        topBar = {
            SimpleTopAppBar(navController = navController, title = "Book Details", backButton = true)
        }
    ) { innerPadding ->
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    if (errorState.value != null) {
                        Text(text = errorState.value ?: "", color = MaterialTheme.colorScheme.error)
                    } else {
                        // Show book details
                        bookDetailState.value?.let { book ->
                            BookRowSimple(
                                book = book,
                                navController = navController,
                                isClickable = false
                            ) { bookId, isFavorite ->
                                libraryViewModel.updateFavoriteStatus(bookId, isFavorite)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            BookDetails(Modifier, book = book)
                            Spacer(modifier = Modifier.height(8.dp))
                            if (isBookInLibrary) {
                                // Show read status button
                                ReadStatusButton(isRead = isRead) {
                                    viewModel.toggleReadStatus(
                                        book.volumeInfo.industryIdentifiers?.firstOrNull()?.identifier
                                            ?: ""
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                // Show delete book button
                                Button(onClick = {
                                    book.volumeInfo.industryIdentifiers?.firstOrNull()
                                        ?.let { viewModel.deleteBook(it.identifier) }
                                    navController.popBackStack()  // Navigate back after deletion
                                }) {
                                    Text("Delete Book")
                                }
                                Spacer(modifier = Modifier.height(8.dp))

                                // Show input for pages read
                                PagesReadInput(
                                    pagesRead = book.volumeInfo.pagesRead ?: 0,
                                    totalPageCount = book.volumeInfo.pageCount ?: 0,
                                    onPagesReadChange = { pagesRead ->
                                        // Get the identifier safely
                                        val identifier = book.volumeInfo.industryIdentifiers?.firstOrNull()?.identifier
                                        if (identifier != null) {
                                            viewModel.updatePagesRead(identifier, pagesRead)
                                        }
                                    }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                RatingBar(rating = rating) { newRating ->
                                    viewModel.updateRating(
                                        book.volumeInfo.industryIdentifiers?.firstOrNull()?.identifier
                                            ?: "", newRating
                                    )
                                }
                            } else {
                                Button(onClick = { viewModel.saveBook(libraryId, book) }) {
                                    Text("Save Book")
                                }
                            }
                        }
                    }

                }

            }
        }
    }
}