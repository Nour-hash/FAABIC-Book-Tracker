package com.example.booktrackerapp.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.example.booktrackerapp.api.BookItem
import com.example.booktrackerapp.api.GoogleBooksApiClient
import com.example.booktrackerapp.model.service.AccountService
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import androidx.compose.runtime.State
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val accountService: AccountService
) : BookTrackerViewModel() {

    val bookDetailState = mutableStateOf<BookItem?>(null)
    val errorState = mutableStateOf<String?>(null)
    private val _readState = mutableStateOf(false)// Zustand für gelesen/nicht gelesen
    val readState: State<Boolean> = _readState


    private val db = FirebaseFirestore.getInstance()


    fun getBookDetails(isbn: String) {
        viewModelScope.launch {
            try {
                val apiKey = "AIzaSyD0k6a0htp8NSBRC0229itvsTaQ4DPLipE"
                val bookResponse = GoogleBooksApiClient.service.searchBooksByISBN("isbn:$isbn", apiKey)

                if (bookResponse.items.isNotEmpty()) {
                    bookDetailState.value = bookResponse.items.first()
                    //readState.value = false // Initialwert für Lesestatus setzen
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
        _readState.value = !_readState.value
    }

    fun saveBook(libraryId: String, book: BookItem) {
        val bookId = book.volumeInfo.industryIdentifiers?.firstOrNull()?.identifier ?: return
        val userId = accountService.currentUserId

        val docRef = db.collection("Users").document(userId)
            .collection("Libraries").document(libraryId)
            .collection("Books").document(bookId)

        docRef.set(book, SetOptions.merge())
            .addOnSuccessListener {
                // Successfully saved the book
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }


}

