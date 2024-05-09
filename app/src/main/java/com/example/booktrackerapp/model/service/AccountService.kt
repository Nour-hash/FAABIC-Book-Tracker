package com.example.booktrackerapp.model.service

import android.net.Uri
import com.example.booktrackerapp.model.User
import kotlinx.coroutines.flow.Flow

interface AccountService {
    val currentUser: Flow<User?>
    val currentUserId: String
    val userName: String?
    val userEmail: String?
    val userPhotoUrl: Uri?

    fun hasUser(): Boolean
    suspend fun signIn(email: String, password: String)
    suspend fun signUp(email: String, password: String)
    suspend fun signOut()
    suspend fun deleteAccount()
}