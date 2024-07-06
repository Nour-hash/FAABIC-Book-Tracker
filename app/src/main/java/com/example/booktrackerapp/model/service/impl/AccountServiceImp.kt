package com.example.booktrackerapp.model.service.impl

import android.net.Uri
import com.example.booktrackerapp.model.User
import com.example.booktrackerapp.model.service.AccountService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Implementation of AccountService interface using Firebase Authentication.
 */
class AccountServiceImpl @Inject constructor() : AccountService {

    // Flow to observe the current authenticated user
    override val currentUser: Flow<User?>
        get() = callbackFlow {
            // Listener for Firebase Authentication state changes
            val listener =
                FirebaseAuth.AuthStateListener { auth ->
                    // Try sending the current user as a User object through the flow
                    this.trySend(auth.currentUser?.let { User(it.uid) })
                }
            // Add the listener to Firebase Authentication
            Firebase.auth.addAuthStateListener(listener)
            // When the flow is closed, remove the listener
            awaitClose { Firebase.auth.removeAuthStateListener(listener) }
        }

    // Retrieves the current user ID or an empty string if null
    override val currentUserId: String
        get() = Firebase.auth.currentUser?.uid.orEmpty()

    // Retrieves the current user's display name or null if not available
    override val userName: String?
        get() = Firebase.auth.currentUser?.displayName

    // Retrieves the current user's email address or null if not available
    override val userEmail: String?
        get() = Firebase.auth.currentUser?.email

    // Retrieves the current user's photo URI or null if not available
    override val userPhotoUrl: Uri?
        get() = Firebase.auth.currentUser?.photoUrl

    // Checks if there is currently an authenticated user
    override fun hasUser(): Boolean {
        return Firebase.auth.currentUser != null
    }

    // Signs in the user with the provided email and password
    override suspend fun signIn(email: String, password: String) {
        Firebase.auth.signInWithEmailAndPassword(email, password).await()
    }

    // Signs up a new user with the provided email and password
    override suspend fun signUp(email: String, password: String) {
        Firebase.auth.createUserWithEmailAndPassword(email, password).await()
    }

    // Signs out the currently authenticated user
    override suspend fun signOut() {
        Firebase.auth.signOut()
    }

    // Deletes the current user's account
    override suspend fun deleteAccount() {
        Firebase.auth.currentUser!!.delete().await()
    }

    // Checks if the provided user credentials (email and password) are correct
    override suspend fun isUserCredentialsCorrect(email: String, password: String): Boolean {
        return try {
            // Attempt to sign in with the provided email and password
            Firebase.auth.signInWithEmailAndPassword(email, password).await()
            // If successful, return true
            true
        } catch (e: FirebaseAuthException) {
            // If an exception occurs (authentication fails), return false
            false
        }
    }
}
