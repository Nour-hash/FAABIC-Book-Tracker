package com.example.booktrackerapp.viewModel

import androidx.navigation.NavController
import com.example.booktrackerapp.model.service.AccountService
import com.example.booktrackerapp.navigation.Screen
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val accountService: AccountService
) : BookTrackerViewModel() {
    val email = MutableStateFlow("")
    val password = MutableStateFlow("")

    private val firebaseAuth = FirebaseAuth.getInstance()


    fun updateEmail(newEmail: String) {
        email.value = newEmail
    }

    fun updatePassword(newPassword: String) {
        password.value = newPassword
    }
/*
    fun onSignInClick(navController:NavController) {
        launchCatching {
            accountService.signIn(email.value, password.value)
            navController.navigate(Screen.HomeScreen.route) {
                popUpTo(navController.graph.startDestinationId)
            }

        }

    }
    */

    suspend fun checkUserCredentials(email: String, password: String): Boolean {
        return try {
            accountService.isUserCredentialsCorrect(email, password)
        } catch (e: Exception) {
            false // Handle exceptions if necessary
        }
    }

    fun onSignInClick(navController: NavController) {
        launchCatching {
            // Check if the user exists
            val userExists = accountService.isUserCredentialsCorrect(email.value,password.value)
            if (userExists) {
                // Proceed with sign-in
                accountService.signIn(email.value, password.value)
                navController.navigate(Screen.HomeScreen.route) {
                    popUpTo(navController.graph.startDestinationId)
                }
            } else {
                // Display error message or handle user not found case
            }
        }
    }

}