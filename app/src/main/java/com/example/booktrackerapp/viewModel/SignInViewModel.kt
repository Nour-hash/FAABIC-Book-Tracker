package com.example.booktrackerapp.viewModel

import androidx.navigation.NavController
import com.example.booktrackerapp.model.service.AccountService
import com.example.booktrackerapp.navigation.Screen
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

/**
 * ViewModel for handling user sign-in functionality.
 *
 * @param accountService The service for accessing user account information.
 */
@HiltViewModel
class SignInViewModel @Inject constructor(
    private val accountService: AccountService
) : BookTrackerViewModel() {

    // MutableStateFlows to hold email and password inputs
    val email = MutableStateFlow("")
    val password = MutableStateFlow("")


    /**
     * Updates the email input.
     *
     * @param newEmail The new email input value.
     */
    fun updateEmail(newEmail: String) {
        email.value = newEmail
    }

    /**
     * Updates the password input.
     *
     * @param newPassword The new password input value.
     */
    fun updatePassword(newPassword: String) {
        password.value = newPassword
    }

    /**
     * Suspended function to check if user credentials are correct.
     *
     * @param email The email to check.
     * @param password The password to check.
     * @return true if credentials are correct, false otherwise.
     */
    suspend fun checkUserCredentials(email: String, password: String): Boolean {
        return try {
            accountService.isUserCredentialsCorrect(email, password)
        } catch (e: Exception) {
            false // Handle exceptions if necessary
        }
    }

    /**
     * Handles sign-in button click event.
     *
     * @param navController The NavController for navigating to other screens.
     */
    fun onSignInClick(navController: NavController) {
        launchCatching {
            // Check if the user exists
            val userExists = accountService.isUserCredentialsCorrect(email.value, password.value)
            if (userExists) {
                // Proceed with sign-in
                accountService.signIn(email.value, password.value)
                // Navigate to HomeScreen after successful sign-in
                navController.navigate(Screen.HomeScreen.route) {
                    popUpTo(navController.graph.startDestinationId)
                }
            } else {
                // Display error message or handle user not found case
            }
        }
    }

}
