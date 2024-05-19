package com.example.booktrackerapp.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.booktrackerapp.api.BookItem
import com.example.booktrackerapp.api.GoogleBooksApiClient
import kotlinx.coroutines.launch
import javax.inject.Inject

class DetailViewModel @Inject constructor() : ViewModel() {

    val bookDetailState = mutableStateOf<BookItem?>(null)
    val errorState = mutableStateOf<String?>(null)
    val readState = mutableStateOf<Boolean?>(null)  // Zustand für gelesen/nicht gelesen

    fun getBookDetails(isbn: String) {
        viewModelScope.launch {
            try {
                val apiKey = "AIzaSyD0k6a0htp8NSBRC0229itvsTaQ4DPLipE"
                val bookResponse = GoogleBooksApiClient.service.searchBooksByISBN("isbn:$isbn", apiKey)

                if (bookResponse.items.isNotEmpty()) {
                    bookDetailState.value = bookResponse.items.first()
                    readState.value = false // Initialwert für Lesestatus setzen
                } else {
                    errorState.value = "No book found for this ISBN."
                }
            } catch (e: Exception) {
                e.printStackTrace()
                errorState.value = "Error fetching book data."
            }
        }
    }
    fun toggleReadStatus() {
        readState.value = readState.value != true
    }
}