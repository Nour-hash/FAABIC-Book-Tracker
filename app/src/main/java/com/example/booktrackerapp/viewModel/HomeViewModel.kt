package com.example.booktrackerapp.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.booktrackerapp.api.BookItem
import com.example.booktrackerapp.api.GoogleBooksApiClient
import kotlinx.coroutines.launch
import androidx.media3.common.BuildConfig



class HomeViewModel : ViewModel() {
    fun normalizeISBN(isbn: String): String {
        return isbn.filter { it.isDigit() }
    }

    fun isValidISBN(isbn: String): Boolean {
        return isbn.length == 10 || isbn.length == 13 && isbn.all { it.isDigit() }
    }
    fun searchBookByISBN(
        rawIsbn: String,
        //onSuccess: (List<BookItem>) -> Unit,
        onSuccess: (BookItem) -> Unit,
        onError: (String) -> Unit
    ) {
        val isbn = normalizeISBN(rawIsbn)

        if (!isValidISBN(isbn)) {
            onError("Invalid ISBN. Please check the number again.")
            return
        }

        viewModelScope.launch {
            try {
                val apiKey = "AIzaSyD0k6a0htp8NSBRC0229itvsTaQ4DPLipE"
               // val apiKey = BuildConfig.
                val bookResponse = GoogleBooksApiClient.service.searchBooksByISBN("isbn:$isbn", apiKey)

                // Debugging: Ausgabe der Ergebnisse prüfen
                Log.d("BookSearch", "Total items found: ${bookResponse.items.size}")
                Log.d("BookSearch", "Book title: ${bookResponse.items.first().volumeInfo.title}")
                Log.d("BookSearch", "Thumbnail URL: ${bookResponse.items.first().volumeInfo.imageLinks?.thumbnail}")
                if(bookResponse.items.isNotEmpty()){
                    onSuccess(bookResponse.items.first())
                } else {
                    onError("No books found for this ISBN.")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onError("Error fetching book data.")
            }
        }
    }
}
