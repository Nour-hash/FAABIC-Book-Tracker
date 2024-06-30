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
                    val book = bookResponse.items.first()
                    bookDetailState.value = book

                    // Fetch the read status from Firestore
                    val bookId = book.volumeInfo.industryIdentifiers?.firstOrNull()?.identifier ?: return@launch
                    val userId = accountService.currentUserId
                    val libraryId = "defaultLibrary"

                    val docRef = db.collection("Users").document(userId)
                        .collection("Libraries").document(libraryId)
                        .collection("Books").document(bookId)

                    docRef.get().addOnSuccessListener { document ->
                        if (document.exists()) {
                            val isRead = document.getBoolean("volumeInfo.isRead") ?: false
                            _readState.value = isRead
                        }
                    }.addOnFailureListener { e ->
                        e.printStackTrace()
                        errorState.value = "Error fetching read status."
                    }
                } else {
                    errorState.value = "No book found for this ISBN."
                }
            } catch (e: Exception) {
                e.printStackTrace()
                errorState.value = "Error fetching book data."
            }
        }
    }
    fun toggleReadStatus(bookId: String) {
        val newStatus = !_readState.value
        _readState.value = newStatus
        updateReadStatus(bookId, newStatus)
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

    //Method to update is read
    fun updateReadStatus(bookId: String, isRead: Boolean) {
        val userId = accountService.currentUserId
        val libraryId = "defaultLibrary"

        db.collection("Users").document(userId)
            .collection("Libraries").document(libraryId)
            .collection("Books").document(bookId)
            .update("volumeInfo.isRead", isRead)  // Feldpfad anpassen
            .addOnSuccessListener {
                  _readState.value = isRead
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                errorState.value = "Error updating read status."
            }
    }


}

