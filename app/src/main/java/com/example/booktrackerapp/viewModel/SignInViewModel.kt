package com.example.booktrackerapp.viewModel

import androidx.navigation.NavController
import com.example.booktrackerapp.model.service.AccountService
import com.example.booktrackerapp.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val accountService: AccountService
) : BookTrackerViewModel() {
    val email = MutableStateFlow("")
    val password = MutableStateFlow("")

    fun updateEmail(newEmail: String) {
        email.value = newEmail
    }

    fun updatePassword(newPassword: String) {
        password.value = newPassword
    }

    fun onSignInClick(navController:NavController) {
        launchCatching {
            accountService.signIn(email.value, password.value)
            navController.navigate(Screen.HomeScreen.route) {
                popUpTo(navController.graph.startDestinationId)
            }
        }

    }

}