package com.example.booktrackerapp.viewModel

import androidx.navigation.NavController
import com.example.booktrackerapp.model.service.AccountService
import com.example.booktrackerapp.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * ViewModel for the Splash screen.
 *
 * @param accountService The service for accessing user account information.
 */
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val accountService: AccountService
) : BookTrackerViewModel() {

    /**
     * Determines the next screen based on whether a user is logged in or not.
     *
     * @param navController The NavController used for navigation.
     */
    fun onAppStart(navController: NavController) {
        if (accountService.hasUser()) {
            // User is logged in, navigate to HomeScreen
            navController.navigate(Screen.HomeScreen.route) {
                // Ensure that HomeScreen is the only destination in the back stack
                popUpTo(navController.graph.startDestinationId)
            }
        } else {
            // User is not logged in, navigate to SignInScreen
            navController.navigate(Screen.SignInScreen.route) {
                // Ensure that SignInScreen is the only destination in the back stack
                popUpTo(navController.graph.startDestinationId)
            }
        }
    }
}
