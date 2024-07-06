package com.example.booktrackerapp.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.booktrackerapp.api.BookItem
import com.example.booktrackerapp.model.service.AccountService
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * ViewModel for managing library-related data and operations.
 *
 * @param accountService The service for accessing user account information.
 */
@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val accountService: AccountService
) : ViewModel() {

    // State to hold the list of books
    val booksState = mutableStateOf<List<BookItem>>(emptyList())

    // State to hold error messages
    val errorState = mutableStateOf<String?>(null)

    // State to manage sorting order
    val sortState = mutableStateOf<SortOrder>(SortOrder.None)

    // State to manage filter criteria
    private val filterState = mutableStateOf(FilterCriteria())

    // Firebase Firestore instance
    private val db = FirebaseFirestore.getInstance()

    /**
     * Fetches books from Firestore based on the current user and library ID.
     */
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

    /**
     * Updates the favorite status of a book in Firestore.
     *
     * @param bookId The ID of the book to update.
     * @param isFavorite The new favorite status to set.
     */
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

    /**
     * Applies sorting to the list of books based on the current sort state.
     *
     * @param books The list of books to sort.
     * @return The sorted list of books.
     */
    fun applySorting(books: List<BookItem>): List<BookItem> {
        return when (sortState.value) {
            SortOrder.Ascending -> books.sortedBy { it.volumeInfo.title }
            SortOrder.Descending -> books.sortedByDescending { it.volumeInfo.title }
            else -> books
        }
    }

    /**
     * Applies filters to the list of books based on the current filter criteria.
     *
     * @param books The list of books to filter.
     * @return The filtered list of books.
     */
    fun applyFilters(books: List<BookItem>): List<BookItem> {
        val genreFilter = filterState.value.genre

        return if (genreFilter.isNullOrEmpty()) {
            // No genre filter applied, return all books including those with null genre
            books.filter { book ->
                val matchesName = filterState.value.name?.let {
                    book.volumeInfo.title.contains(it, ignoreCase = true)
                } ?: true

                val matchesReadStatus = filterState.value.readStatus?.let {
                    book.volumeInfo.isRead == it
                } ?: true

                val matchesAuthor = filterState.value.author?.let { author ->
                    book.volumeInfo.authors?.any { it.contains(author ?: "", ignoreCase = true) } ?: true
                } ?: true

                matchesName && matchesReadStatus && matchesAuthor
            }
        } else {
            // Genre filter applied, return only books with the specified genre or partial match
            books.filter { book ->
                val matchesName = filterState.value.name?.let {
                    book.volumeInfo.title.contains(it, ignoreCase = true)
                } ?: true

                val matchesGenre = book.volumeInfo.categories?.any { category ->
                    category.contains(genreFilter, ignoreCase = true) ||
                            category.startsWith(genreFilter, ignoreCase = true)
                } ?: false

                val matchesReadStatus = filterState.value.readStatus?.let {
                    book.volumeInfo.isRead == it
                } ?: true

                val matchesAuthor = filterState.value.author?.let { author ->
                    book.volumeInfo.authors?.any { it.contains(author ?: "", ignoreCase = true) } ?: true
                } ?: true

                matchesName && matchesGenre && matchesReadStatus && matchesAuthor
            }
        }
    }

    /**
     * Sets the filter criteria and fetches books accordingly.
     *
     * @param name The name to filter by.
     * @param genre The genre to filter by.
     * @param readStatus The read status to filter by.
     * @param author The author to filter by.
     */
    fun setFilterCriteria(name: String? = null, genre: String? = null, readStatus: Boolean? = null, author: String? = null) {
        filterState.value = FilterCriteria(name, genre, readStatus, author)
        fetchBooks()
    }

    /**
     * Data class representing filter criteria for books.
     *
     * @property name The name to filter by.
     * @property genre The genre to filter by.
     * @property readStatus The read status to filter by.
     * @property author The author to filter by.
     */
    data class FilterCriteria(
        val name: String? = null,
        val genre: String? = null,
        val readStatus: Boolean? = null,
        val author: String? = null
    )

    /**
     * Enum representing sorting order for books.
     */
    enum class SortOrder {
        None, Ascending, Descending
    }
}
