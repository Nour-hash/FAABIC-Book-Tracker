package com.example.booktrackerapp.screens

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.booktrackerapp.R
import com.example.booktrackerapp.navigation.Screen
import com.example.booktrackerapp.ui.theme.BookTrackerAppTheme
import com.example.booktrackerapp.viewModel.SignInViewModel
import com.example.booktrackerapp.widgets.SimpleBottomAppBar
import com.example.booktrackerapp.widgets.SimpleTopAppBar

@Composable
fun SignInScreen(navController: NavController, viewModel: SignInViewModel = hiltViewModel()) {
    val emailState = viewModel.email.collectAsState()
    val passwordState = viewModel.password.collectAsState()
    val passwordVisibilityState = remember { mutableStateOf(false) }

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
                            onValueChange = { viewModel.updateEmail(it)},
                            label = { Text("E-Mail") }
                        )

                        TextField(
                            value = passwordState.value,
                            onValueChange = { viewModel.updatePassword(it)},
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


                            }
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(onClick = {
                            /* Handle login */

                                viewModel.onSignInClick(navController)
                            }) {
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