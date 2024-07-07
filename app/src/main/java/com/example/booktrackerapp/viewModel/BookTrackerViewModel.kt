package com.example.booktrackerapp.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Base ViewModel class for handling coroutine launches with error handling.
 */
open class BookTrackerViewModel : ViewModel() {

    /**
     * Launches a coroutine in the viewModelScope with error handling.
     *
     * @param block The code block to execute as a suspend function within CoroutineScope.
     */
    fun launchCatching(block: suspend CoroutineScope.() -> Unit) =
        viewModelScope.launch(
            CoroutineExceptionHandler { _, throwable ->
                // Log any exceptions caught during coroutine execution
                Log.d(ERROR_TAG, throwable.message.orEmpty())
            },
            block = block
        )

    companion object {
        // Tag used for logging errors specific to the Book Tracking App
        const val ERROR_TAG = "BOOK TRACKING APP ERROR"
    }
}
