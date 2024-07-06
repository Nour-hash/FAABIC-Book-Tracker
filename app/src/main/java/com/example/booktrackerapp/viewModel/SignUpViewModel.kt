package com.example.booktrackerapp.viewModel

import androidx.navigation.NavController
import com.example.booktrackerapp.model.service.AccountService
import com.example.booktrackerapp.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

/**
 * ViewModel for handling user sign-up functionality.
 *
 * @param accountService The service for accessing user account information.
 */
@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val accountService: AccountService
) : BookTrackerViewModel() {

    // MutableStateFlows to hold email, password, and confirm password inputs
    val email = MutableStateFlow("")
    val password = MutableStateFlow("")
    val confirmPassword = MutableStateFlow("")

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
     * Updates the confirm password input.
     *
     * @param newConfirmPassword The new confirm password input value.
     */
    fun updateConfirmPassword(newConfirmPassword: String) {
        confirmPassword.value = newConfirmPassword
    }

    /**
     * Handles sign-up button click event.
     *
     * @param navController The NavController for navigating to other screens.
     */
    fun onSignUpClick(navController: NavController) {
        launchCatching {
            // Check if passwords match
            if (password.value != confirmPassword.value) {
                throw Exception("Passwords do not match")
            }

            // Sign up the user using accountService
            accountService.signUp(email.value, password.value)

            // Navigate to UserScreen after successful sign-up
            navController.navigate(Screen.UserScreen.route) {
                popUpTo(navController.graph.startDestinationId)
            }
        }
    }
}
