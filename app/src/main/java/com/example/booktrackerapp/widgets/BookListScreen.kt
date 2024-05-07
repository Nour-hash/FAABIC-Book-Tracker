package com.example.booktrackerapp.widgets

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.booktrackerapp.R
import com.example.booktrackerapp.api.BookItem

@Composable
fun BookListScreen(books: List<BookItem>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(books) { bookItem ->
            BookItemView(bookItem)
        }
    }
}

@Composable
fun BookItemView(bookItem: BookItem) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = bookItem.volumeInfo.title)
        bookItem.volumeInfo.authors?.let { authors ->
            Text(text = "Authors: ${authors.joinToString(", ")}")
        }
        bookItem.volumeInfo.imageLinks?.thumbnail?.let { thumbnail ->
            Log.d("BookCoverURL", thumbnail) // Debug-Ausgabe zur Kontrolle der URL
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(thumbnail)
                    .crossfade(true)
                    .placeholder(R.drawable.works) // Replace with your drawable
                    .error(R.drawable.error) // Replace with your drawable
                    .listener(
                        onStart = { Log.d("ImageLoad", "Start loading $thumbnail") },
                        onError = { _, _ -> Log.e("ImageLoad", "Error loading $thumbnail") },
                        onSuccess = { _, _ -> Log.d("ImageLoad", "Success loading $thumbnail") }
                    )
                    .build(),
                contentDescription = "Book Cover",
                modifier = Modifier.size(100.dp)
            )
        }
    }
}

@Composable
fun SingleBookView(bookItem: BookItem) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = bookItem.volumeInfo.title)
        bookItem.volumeInfo.authors?.let { authors ->
            Text(text = "Authors: ${authors.joinToString(", ")}")
        }
        bookItem.volumeInfo.imageLinks?.thumbnail?.let { thumbnail ->
            Log.d("BookCoverURL", thumbnail) // Debug-Ausgabe zur Kontrolle der URL
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(thumbnail)
                    .crossfade(true)
                    .placeholder(R.drawable.works) // Replace with your drawable
                    .error(R.drawable.error) // Replace with your drawable
                    .listener(
                        onStart = { Log.d("ImageLoad", "Start loading $thumbnail") },
                        onError = { _, _ -> Log.e("ImageLoad", "Error loading $thumbnail") },
                        onSuccess = { _, _ -> Log.d("ImageLoad", "Success loading $thumbnail") }
                    )
                    .build(),
                contentDescription = "Book Cover",
                modifier = Modifier.size(100.dp)
            )
        }
    }
}