package com.example.booktrackerapp.viewModel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.booktrackerapp.api.BookItem
import com.example.booktrackerapp.api.GoogleBooksApiClient
import com.example.booktrackerapp.model.service.AccountService
import com.example.booktrackerapp.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val accountService: AccountService):BookTrackerViewModel() {

    fun onSignOutClick() {
        launchCatching {
            accountService.signOut()
        }
    }

    fun onDeleteAccountClick() {
        launchCatching {
            accountService.deleteAccount()
        }
    }

    fun getUsername(): String? {
        return accountService.userName
    }

    fun getUserEmail(): String? {
        return accountService.userEmail
    }

    fun getUserPhotoUrl(): Uri? {
        return accountService.userPhotoUrl
    }


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
