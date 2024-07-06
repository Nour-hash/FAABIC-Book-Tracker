package com.example.booktrackerapp.widgets

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleTopAppBar(navController: NavController, title: String, backButton: Boolean) {
    val isDialogVisible = remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.align(Alignment.Center),
                    textAlign = TextAlign.Center,
                )
            }
        },
        navigationIcon = {
            if (backButton) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            } else null
        },
        actions = {
            IconButton(
                onClick = { isDialogVisible.value = true },
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Icon(Icons.Default.Info, contentDescription = "Help")
            }
        }
    )

    if (isDialogVisible.value) {
        AlertDialog(
            onDismissRequest = { isDialogVisible.value = false },
            title = {
                Text(
                    text = "Guide",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            },
            text = {
                Text(
                    text =  "HOW TO USE FAABIC:                                           If you want to search a book that you like you have " +
                            "two options to do so. First would be to write the isbn of your book in the search bar and " +
                            "the Second option would be to use our own AI to search for your book " +
                            "just Click on the Camera Icon in the home-screen and take pictures of the front and back of the book and then it should give you details to your desired book " +
                            "then you can save it in your library for later viewing",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            },
            confirmButton = {
                Button(
                    onClick = { isDialogVisible.value = false }
                ) {
                    Text(text = "OK")
                }
            }
        )
    }
}
