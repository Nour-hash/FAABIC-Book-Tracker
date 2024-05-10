package com.example.booktrackerapp.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.booktrackerapp.viewModel.SplashViewModel
import kotlinx.coroutines.delay

private const val SPLASH_TIMEOUT = 1000L

@Composable
fun SplashScreen(navController: NavController,
                 viewModel: SplashViewModel = hiltViewModel())
{

    Column() {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.onBackground)
    }



    LaunchedEffect(true) {
        delay(SPLASH_TIMEOUT)
        viewModel.onAppStart(navController)
    }
}