package com.example.booktrackerapp.model

import android.net.Uri


data class User(
    val id: String = "",
    val username: String = "",
    val userEmail: String = "",
    val userPhotoUrl: Uri = Uri.parse("")
)