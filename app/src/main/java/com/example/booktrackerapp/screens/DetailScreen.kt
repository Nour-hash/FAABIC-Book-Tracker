package com.example.booktrackerapp.screens

import android.gesture.GestureLibrary
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
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
import com.example.booktrackerapp.widgets.BookDetails
import com.example.booktrackerapp.widgets.BookRowSimple
import com.example.booktrackerapp.widgets.ReadStatusButton
import com.example.booktrackerapp.widgets.SimpleTopAppBar

//Bildschirm, der Buchdetails anzeigt, basierend auf einer ISBN.
@Composable
fun DetailScreen(
    navController: NavController,
    isbn: String,
    libraryId:String,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val bookDetailState = viewModel.bookDetailState
    val errorState = viewModel.errorState
    val isRead = viewModel.readState.value ?: false  // Abrufen des Lesestatus als lokale Variable

    LaunchedEffect(isbn) {
        viewModel.getBookDetails(isbn)
    }

    Scaffold(
        topBar = {
            SimpleTopAppBar(navController = navController, title = "Book Details", backButton = true)
        }
    ) { innerPadding ->
        LazyColumn(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)){
            item { if (errorState.value != null) {
                Text(text = errorState.value ?: "", color = MaterialTheme.colorScheme.error)
            } else {
                bookDetailState.value?.let { book ->
                    BookRowSimple(book = book, navController = navController, isClickable = false)
                    Spacer(modifier = Modifier.height(8.dp))
                    BookDetails(Modifier,book = book)
                    Spacer(modifier = Modifier.height(8.dp))
                    ReadStatusButton(isRead = isRead, onClick = { viewModel.toggleReadStatus() })
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.saveBook(libraryId,book) }) {
                        Text("Save Book")
                    }
                }
            } }

        }

    }
}