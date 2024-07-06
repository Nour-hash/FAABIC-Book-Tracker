package com.example.booktrackerapp.viewModel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.booktrackerapp.api.BookItem
import com.example.booktrackerapp.api.GoogleBooksApiClient
import com.example.booktrackerapp.model.service.AccountService
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.booktrackerapp.BuildConfig

/**
 * ViewModel for managing book details, read status, and user interactions.
 */
@HiltViewModel
class DetailViewModel @Inject constructor(
    private val accountService: AccountService
) : ViewModel() {

    // Mutable state for holding book details, read status, and error messages
    val bookDetailState = mutableStateOf<BookItem?>(null)
    val errorState = mutableStateOf<String?>(null)
    private val _readState = mutableStateOf(false) // State for read status (read or not read)
    val readState: State<Boolean> = _readState
    private val _ratingState = mutableStateOf<Int?>(null) // State for rating
    val ratingState: State<Int?> = _ratingState
    val isLoading = mutableStateOf(false) // Loading indicator state
    val isBookInLibrary = mutableStateOf(false) // State to indicate if the book is in the user's library

    // Firestore instance
    private val db = FirebaseFirestore.getInstance()

    /**
     * Function to fetch book details using Google Books API based on ISBN.
     *
     * @param isbn ISBN of the book to fetch details for.
     */
    fun getBookDetails(isbn: String) {
        viewModelScope.launch {
            try {
                val apiKey = BuildConfig.API_KEY
                val bookResponse = GoogleBooksApiClient.service.searchBooksByISBN("isbn:$isbn", apiKey)

                if (bookResponse.items.isNotEmpty()) {
                    val book = bookResponse.items.first()
                    bookDetailState.value = book
                    Log.d("DetailViewModel", "Book details fetched: ${book.volumeInfo.title}")

                    // Check if the book is already in the user's library and fetch additional data
                    checkBookInLibraryAndFetchBookData(book)
                } else {
                    errorState.value = "No book found for this ISBN."
                }
            } catch (e: Exception) {
                e.printStackTrace()
                errorState.value = "Error fetching book data."
            }
            isLoading.value = false // Set loading state to false when done fetching
        }
    }

    /**
     * Function to check if the book is already in the user's library and fetch read status and rating.
     *
     * @param book The BookItem object representing the book to check.
     */
    private fun checkBookInLibraryAndFetchBookData(book: BookItem) {
        val bookId = book.volumeInfo.industryIdentifiers?.firstOrNull()?.identifier ?: return
        val userId = accountService.currentUserId
        val libraryId = "defaultLibrary"

        Log.d("DetailViewModel", "Checking if book is in library: ${book.volumeInfo.title}")

        // Reference to the book document in Firestore
        val docRef = db.collection("Users").document(userId)
            .collection("Libraries").document(libraryId)
            .collection("Books").document(bookId)

        // Fetch the book document
        docRef.get().addOnSuccessListener { snapshot ->
            if (snapshot != null && snapshot.exists()) {
                isBookInLibrary.value = true // Book is in the library
                _readState.value = snapshot.getBoolean("volumeInfo.isRead") ?: false // Fetch read status
                _ratingState.value = snapshot.getLong("volumeInfo.userRating")?.toInt() // Fetch rating
            } else {
                isBookInLibrary.value = false // Book is not in the library
            }
        }
    }

    /**
     * Function to toggle the read status of a book.
     *
     * @param bookId The ID of the book in Firestore.
     */
    fun toggleReadStatus(bookId: String) {
        val newStatus = !_readState.value
        _readState.value = newStatus // Update read status state
        updateReadStatus(bookId, newStatus) // Update read status in Firestore
    }

    /**
     * Function to save a book to the user's library in Firestore.
     *
     * @param libraryId The ID of the user's library.
     * @param book The BookItem object representing the book to save.
     */
    fun saveBook(libraryId: String, book: BookItem) {
        val bookId = book.volumeInfo.industryIdentifiers?.firstOrNull()?.identifier ?: return
        val userId = accountService.currentUserId

        // Reference to the book document in Firestore
        val docRef = db.collection("Users").document(userId)
            .collection("Libraries").document(libraryId)
            .collection("Books").document(bookId)

        // Set book data in Firestore with merge options
        docRef.set(book, SetOptions.merge())
            .addOnSuccessListener {
                isBookInLibrary.value = true // Successfully saved the book
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }

    /**
     * Function to update the read status of a book in Firestore.
     *
     * @param bookId The ID of the book in Firestore.
     * @param isRead Boolean indicating whether the book is read or not.
     */
    fun updateReadStatus(bookId: String, isRead: Boolean) {
        val userId = accountService.currentUserId
        val libraryId = "defaultLibrary"

        // Update read status in Firestore
        db.collection("Users").document(userId)
            .collection("Libraries").document(libraryId)
            .collection("Books").document(bookId)
            .update("volumeInfo.isRead", isRead)
            .addOnSuccessListener {
                _readState.value = isRead // Update read status state
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                errorState.value = "Error updating read status."
            }
    }

    /**
     * Function to update the rating of a book in Firestore.
     *
     * @param bookId The ID of the book in Firestore.
     * @param rating The new rating value to set.
     */
    fun updateRating(bookId: String, rating: Int) {
        val userId = accountService.currentUserId
        val libraryId = "defaultLibrary"

        // Update rating in Firestore
        db.collection("Users").document(userId)
            .collection("Libraries").document(libraryId)
            .collection("Books").document(bookId)
            .update("volumeInfo.userRating", rating)
            .addOnSuccessListener {
                Log.d("DetailViewModel", "Rating updated successfully to $rating")
                _ratingState.value = rating // Update rating state
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                errorState.value = "Error updating rating."
                Log.e("DetailViewModel", "Error updating rating", e)
            }
    }

    /**
     * Function to delete a book from the user's library in Firestore.
     *
     * @param bookId The ID of the book in Firestore.
     */
    fun deleteBook(bookId: String) {
        val userId = accountService.currentUserId
        val libraryId = "defaultLibrary"

        // Delete book from Firestore
        db.collection("Users").document(userId)
            .collection("Libraries").document(libraryId)
            .collection("Books").document(bookId)
            .delete()
            .addOnSuccessListener {
                isBookInLibrary.value = false // Successfully deleted the book
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                errorState.value = "Error deleting book."
            }
    }
}
