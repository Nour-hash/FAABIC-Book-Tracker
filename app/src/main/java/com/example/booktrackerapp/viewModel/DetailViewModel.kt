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
import android.util.Log
import com.example.booktrackerapp.BuildConfig


@HiltViewModel
class DetailViewModel @Inject constructor(
    private val accountService: AccountService
) : BookTrackerViewModel() {

    val bookDetailState = mutableStateOf<BookItem?>(null)
    val errorState = mutableStateOf<String?>(null)
    private val _readState = mutableStateOf(false)// Zustand für gelesen/nicht gelesen
    val readState: State<Boolean> = _readState
    private val _ratingState = mutableStateOf<Int?>(null) // Zustand für Bewertung
    val ratingState: State<Int?> = _ratingState
    val isLoading = mutableStateOf(false) // Zustand für Ladeanzeige
    val isBookInLibrary = mutableStateOf(false)



    private val db = FirebaseFirestore.getInstance()


    fun getBookDetails(isbn: String) {
        viewModelScope.launch {
            try {
                val apiKey = BuildConfig.API_KEY
                val bookResponse = GoogleBooksApiClient.service.searchBooksByISBN("isbn:$isbn", apiKey)

                if (bookResponse.items.isNotEmpty()) {
                    val book = bookResponse.items.first()
                    bookDetailState.value = book
                    Log.d("DetailViewModel", "Book details fetched: ${book.volumeInfo.title}")

                    //Check if the book is in the library - Fetch the read status and rating from Firestore
                    checkBookInLibraryAndFetchBookData(book)
                } else {
                    errorState.value = "No book found for this ISBN."
                }
            } catch (e: Exception) {
                e.printStackTrace()
                errorState.value = "Error fetching book data."
            }
            isLoading.value = false
        }
    }

    private fun checkBookInLibraryAndFetchBookData(book: BookItem) {
        val bookId = book.volumeInfo.industryIdentifiers?.firstOrNull()?.identifier ?: return
        val userId = accountService.currentUserId
        val libraryId = "defaultLibrary"

        Log.d("DetailViewModel", "Checking if book is in library: ${book.volumeInfo.title}")

        val docRef = db.collection("Users").document(userId)
            .collection("Libraries").document(libraryId)
            .collection("Books").document(bookId)

        docRef.get().addOnSuccessListener { snapshot ->
            if (snapshot != null && snapshot.exists()) {
                isBookInLibrary.value = true
                _readState.value = snapshot.getBoolean("volumeInfo.isRead") ?: false
                _ratingState.value = snapshot.getLong("volumeInfo.userRating")?.toInt()
            } else {
                isBookInLibrary.value = false
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
                isBookInLibrary.value = true
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

    // Method to update rating
    fun updateRating(bookId: String, rating: Int) {
        val userId = accountService.currentUserId
        val libraryId = "defaultLibrary"

        db.collection("Users").document(userId)
            .collection("Libraries").document(libraryId)
            .collection("Books").document(bookId)
            .update("volumeInfo.userRating", rating)
            .addOnSuccessListener {
                Log.d("DetailViewModel", "Rating updated successfully to $rating")
                _ratingState.value = rating
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                errorState.value = "Error updating rating."
                Log.e("DetailViewModel", "Error updating rating", e)
            }
    }

    fun deleteBook(bookId: String) {
        val userId = accountService.currentUserId
        val libraryId = "defaultLibrary"

        db.collection("Users").document(userId)
            .collection("Libraries").document(libraryId)
            .collection("Books").document(bookId)
            .delete()
            .addOnSuccessListener {
                isBookInLibrary.value = false
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                errorState.value = "Error deleting book."
            }
    }

}

