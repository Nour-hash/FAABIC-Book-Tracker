package com.example.booktrackerapp.screens

import android.util.Patterns
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.booktrackerapp.R
import com.example.booktrackerapp.navigation.Screen
import com.example.booktrackerapp.ui.theme.BookTrackerAppTheme
import com.example.booktrackerapp.viewModel.SignInViewModel
import kotlinx.coroutines.launch

@Composable
fun SignInScreen(navController: NavController, viewModel: SignInViewModel = hiltViewModel()) {
    val emailState = viewModel.email.collectAsState()
    val passwordState = viewModel.password.collectAsState()
    val passwordVisibilityState = remember { mutableStateOf(false) }

    // State for error messages
    val emailErrorState = remember { mutableStateOf<String?>(null) }
    val passwordErrorState = remember { mutableStateOf<String?>(null) }

    BookTrackerAppTheme {
        Scaffold() { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        TextField(
                            value = emailState.value,
                            onValueChange = {
                                viewModel.updateEmail(it)
                                // Validate email
                                emailErrorState.value = if (it.isValidEmail()) null else "Invalid email"
                            },
                            label = { Text("E-Mail") },
                            isError = emailErrorState.value != null,
                            singleLine = true
                        )
                        // Display email error message if present
                        emailErrorState.value?.let { error ->
                            Text(text = error, color = Color.Red)
                        }

                        TextField(
                            value = passwordState.value,
                            onValueChange = {
                                viewModel.updatePassword(it)
                                // Validate password
                                passwordErrorState.value = if (it.length >= 6) null else "Password must be at least 6 characters long"
                            },
                            label = { Text("Password") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            visualTransformation = if (passwordVisibilityState.value) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(
                                    onClick = { passwordVisibilityState.value = !passwordVisibilityState.value }
                                ) {
                                    Icon(
                                        painter = if (passwordVisibilityState.value)
                                            painterResource(id = R.drawable.visible)
                                        else
                                            painterResource(id = R.drawable.visible_off),
                                        contentDescription = "Toggle password visibility"
                                    )
                                }
                            },
                            isError = passwordErrorState.value != null,
                            singleLine = true
                        )
                        // Display password error message if present
                        passwordErrorState.value?.let { error ->
                            Text(text = error, color = Color.Red)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Inside SignInScreen
                            Button(
                                onClick = {
                                    // Validate email and password before signing in
                                    if (emailState.value.isValidEmail() && passwordState.value.length >= 6) {
                                        // Check if Credentials are correct
                                        viewModel.viewModelScope.launch {
                                            val userExists = viewModel.checkUserCredentials(emailState.value, passwordState.value)
                                            if (userExists) {
                                                // Call the ViewModel function
                                                viewModel.onSignInClick(navController)
                                            } else {
                                                // Show error message for wrong credentials
                                                passwordErrorState.value = "Email or Password wrong. Please check your credentials."
                                            }
                                        }
                                    } else {
                                        // Show error messages
                                        emailErrorState.value = if (!emailState.value.isValidEmail()) "Invalid email" else null
                                        passwordErrorState.value = if (passwordState.value.length < 6) "Password must be at least 6 characters long" else null
                                    }
                                }
                            ) {
                                Text("Sign in")
                            }


                            TextButton(onClick = { navController.navigate(Screen.SignUpScreen.route) {
                                popUpTo(navController.graph.startDestinationId)
                            } }) {
                                Text(text = "Don't have an account? Click here to Sign Up!")
                            }
                        }
                    }
                }
            }
        }
    }
}
// Extension function to validate email format
fun String.isValidEmail(): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(this).matches()
}