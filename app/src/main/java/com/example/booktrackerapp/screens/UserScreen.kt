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
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.booktrackerapp.R
import com.example.booktrackerapp.ui.theme.BookTrackerAppTheme
import com.example.booktrackerapp.widgets.SimpleBottomAppBar
import com.example.booktrackerapp.widgets.SimpleTopAppBar

@Composable
fun Userscreen(navController: NavController) {
    val usernameState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }
    val passwordVisibilityState = remember { mutableStateOf(false) }

    BookTrackerAppTheme {
        Scaffold(
            topBar = { SimpleTopAppBar(navController, title = "User", backButton = true) },
            bottomBar = { SimpleBottomAppBar(navController) }
        ) { innerPadding ->
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
                            value = usernameState.value,
                            onValueChange = { usernameState.value = it },
                            label = { Text("Username") }
                        )

                        TextField(
                            value = passwordState.value,
                            onValueChange = { passwordState.value = it },
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
                            Button(onClick = { /* Handle login */ }) {
                                Text("Login")
                            }

                            Button(onClick = { /* Handle sign up */ }) {
                                Text("Sign Up")
                            }
                        }
                    }
                }
            }
        }
    }
}
