package com.example.booktrackerapp.viewModel

import android.util.Log
import com.example.booktrackerapp.BuildConfig
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.booktrackerapp.api.BookItem
import com.example.booktrackerapp.api.GoogleBooksApiClient
import com.example.booktrackerapp.model.service.AccountService
import com.example.booktrackerapp.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Home screen.
 *
 * @param accountService The service for accessing user account information.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val accountService: AccountService
) : BookTrackerViewModel() {

    /**
     * Handles sign-out button click.
     */
    fun onSignOutClick() {
        launchCatching {
            accountService.signOut()
        }
    }

    /**
     * Handles delete account button click.
     */
    fun onDeleteAccountClick() {
        launchCatching {
            accountService.deleteAccount()
        }
    }



    /**
     * Retrieves the email of the current logged-in user.
     *
     * @return The email of the user, or null if not available.
     */
    fun getUserEmail(): String? {
        return accountService.userEmail
    }


    /**
     * Initializes the HomeViewModel by checking if a user is logged in.
     * If no user is logged in, navigates to the SplashScreen.
     *
     * @param navController The NavController used for navigation.
     */
    fun initialize(navController: NavController) {
        launchCatching {
            accountService.currentUser.collect { user ->
                if (user == null) {
                    navController.navigate(Screen.SplashScreen.route) {
                        popUpTo(navController.graph.startDestinationId)
                    }
                }
            }
        }
    }

    /**
     * Removes all non-digit characters from the ISBN.
     *
     * @param isbn The ISBN to normalize.
     * @return The normalized ISBN containing only digits.
     */
    private fun normalizeISBN(isbn: String): String {
        return isbn.filter { it.isDigit() }
    }

    /**
     * Checks if the given ISBN is valid.
     *
     * @param isbn The ISBN to validate.
     * @return True if the ISBN is valid, false otherwise.
     */
    private fun isValidISBN(isbn: String): Boolean {
        return isbn.length == 10 || isbn.length == 13 && isbn.all { it.isDigit() }
    }

    /**
     * Searches for books using the provided ISBN.
     *
     * @param rawIsbn The raw ISBN input.
     * @param onSuccess Callback function invoked when book search succeeds with the found BookItem.
     * @param onError Callback function invoked when book search encounters an error with an error message.
     */
    fun searchBookByISBN(
        rawIsbn: String,
        onSuccess: (BookItem) -> Unit,
        onError: (String) -> Unit
    ) {
        val isbn = normalizeISBN(rawIsbn)

        // Validate ISBN
        if (!isValidISBN(isbn)) {
            onError("Invalid ISBN. Please check the number again.")
            return
        }

        viewModelScope.launch {
            try {
                val apiKey = BuildConfig.API_KEY // Retrieve API key from BuildConfig
                val bookResponse = GoogleBooksApiClient.service.searchBooksByISBN("isbn:$isbn", apiKey)

                // Log book search results for debugging
                Log.d("BookSearch", "Total items found: ${bookResponse.items.size}")
                if (bookResponse.items.isNotEmpty()) {
                    val firstBook = bookResponse.items.first()
                    Log.d("BookSearch", "Book title: ${firstBook.volumeInfo.title}")
                    Log.d("BookSearch", "Thumbnail URL: ${firstBook.volumeInfo.imageLinks?.thumbnail}")

                    // Invoke onSuccess callback with the first book item found
                    onSuccess(firstBook)
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
