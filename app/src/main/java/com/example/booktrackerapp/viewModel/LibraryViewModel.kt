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
    val sortState = mutableStateOf<SortOrder>(SortOrder.None)
    val filterState = mutableStateOf(FilterCriteria())

    private val db = FirebaseFirestore.getInstance()

    fun fetchBooks() {
        val userId = accountService.currentUserId
        val libraryId = "defaultLibrary"

        db.collection("Users").document(userId)
            .collection("Libraries").document(libraryId)
            .collection("Books")
            .get()
            .addOnSuccessListener { result ->
                var books = result.toObjects(BookItem::class.java)
                books = applyFilters(books)
                books = applySorting(books)
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
            .update("volumeInfo.isFavorite", isFavorite)
            .addOnSuccessListener {
                fetchBooks()
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                errorState.value = "Error updating favorite status."
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
                booksState.value = booksState.value.filter { it.volumeInfo.industryIdentifiers?.firstOrNull()?.identifier != bookId }
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                errorState.value = "Error deleting book."
            }
    }

    fun sortBooksAscending() {
        sortState.value = SortOrder.Ascending
        booksState.value = booksState.value.sortedBy { it.volumeInfo.title }
    }

    fun sortBooksDescending() {
        sortState.value = SortOrder.Descending
        booksState.value = booksState.value.sortedByDescending { it.volumeInfo.title }
    }

    fun applySorting(books: List<BookItem>): List<BookItem> {
        return when (sortState.value) {
            SortOrder.Ascending -> books.sortedBy { it.volumeInfo.title }
            SortOrder.Descending -> books.sortedByDescending { it.volumeInfo.title }
            else -> books
        }
    }

    fun applyFilters(books: List<BookItem>): List<BookItem> {
        return books.filter { book ->
            val matchesName = filterState.value.name?.let { book.volumeInfo.title.contains(it, ignoreCase = true) } ?: true
            val matchesGenre = filterState.value.genre?.let { genre ->
                book.volumeInfo.categories?.any { it.contains(genre, ignoreCase = true) } ?: false
            } ?: true
            val matchesReadStatus = filterState.value.readStatus?.let { book.volumeInfo.isRead == it } ?: true
            val matchesAuthor = filterState.value.author?.let { author ->
                book.volumeInfo.authors?.any { it.contains(author, ignoreCase = true) } ?: false
            } ?: true

            matchesName && matchesGenre && matchesReadStatus && matchesAuthor
        }
    }

    fun setFilterCriteria(name: String? = null, genre: String? = null, readStatus: Boolean? = null, author: String? = null) {
        filterState.value = FilterCriteria(name, genre, readStatus, author)
        fetchBooks()
    }


    data class FilterCriteria(
        val name: String? = null,
        val genre: String? = null,
        val readStatus: Boolean? = null,
        val author: String? = null
    )

    enum class SortOrder {
        None, Ascending, Descending
    }
}
