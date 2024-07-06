package com.example.booktrackerapp.viewModel

import android.net.Uri
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

    // Entfernt alle Nicht-Ziffern aus der ISBN
    fun normalizeISBN(isbn: String): String {
        return isbn.filter { it.isDigit() }
    }

    // Überprüft, ob eine ISBN gültig ist.
    fun isValidISBN(isbn: String): Boolean {
        return  isbn.length == 10 || isbn.length == 13 && isbn.all { it.isDigit()  }
    }

    //Sucht nach Büchern mit ISBN
    fun searchBookByISBN(
        rawIsbn: String,
        onSuccess: (BookItem) -> Unit, // Callback-Funktion, die bei Erfolg aufgerufen wird.
        onError: (String) -> Unit
    ) {
        val isbn = normalizeISBN(rawIsbn)

        if (!isValidISBN(isbn)) {
            onError("Invalid ISBN. Please check the number again.")
            return
        }

        viewModelScope.launch {
            try {
                val apiKey = BuildConfig.API_KEY
                // val apiKey = BuildConfig.
                val bookResponse = GoogleBooksApiClient.service.searchBooksByISBN("isbn:$isbn", apiKey)

                // Debugging: Ausgabe der Ergebnisse prüfen
                Log.d("BookSearch", "Total items found: ${bookResponse.items.size}")
                Log.d("BookSearch", "Book title: ${bookResponse.items.first().volumeInfo.title}")
                Log.d("BookSearch", "Thumbnail URL: ${bookResponse.items.first().volumeInfo.imageLinks?.thumbnail}")

                // Bei erfolgreicher Suche wird der erste Bucheintrag an den onSuccess Callback übergeben.
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
