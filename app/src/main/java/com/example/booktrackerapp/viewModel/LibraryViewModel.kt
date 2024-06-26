package com.example.booktrackerapp.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.booktrackerapp.api.BookItem
import com.example.booktrackerapp.model.service.AccountService
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val accountService: AccountService
) : ViewModel() {

    val booksState = mutableStateOf<List<BookItem>>(emptyList())
    val errorState = mutableStateOf<String?>(null)


    private val db = FirebaseFirestore.getInstance()

    fun fetchBooks() {
        val userId = accountService.currentUserId
        val libraryId = "defaultLibrary"

        db.collection("Users").document(userId)
            .collection("Libraries").document(libraryId)
            .collection("Books")
            .get()
            .addOnSuccessListener { result ->
                val books = result.toObjects(BookItem::class.java)
                booksState.value = books
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                errorState.value = "Error fetching books."
            }
    }

    fun updateFavoriteStatus(bookId: String, isFavorite: Boolean) {
        val userId = accountService.currentUserId
        val libraryId = "defaultLibrary"

        db.collection("Users").document(userId)
            .collection("Libraries").document(libraryId)
            .collection("Books").document(bookId)
            .update("volumeInfo.isFavorite", isFavorite)  // Correct field path
            .addOnSuccessListener {
                fetchBooks()
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                errorState.value = "Error updating favorite status."
            }
    }


    // New method to delete a book
    fun deleteBook(bookId: String) {
        val userId = accountService.currentUserId
        val libraryId = "defaultLibrary"

        db.collection("Users").document(userId)
            .collection("Libraries").document(libraryId)
            .collection("Books").document(bookId)
            .delete()
            .addOnSuccessListener {
                // Remove the deleted book from the local state
                booksState.value = booksState.value.filter { it.volumeInfo.industryIdentifiers?.firstOrNull()?.identifier!= bookId }
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                errorState.value = "Error deleting book."
            }
    }
}
