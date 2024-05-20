package com.example.booktrackerapp.api

import com.example.booktrackerapp.api.GoogleBooksApiClient.service
import retrofit2.http.GET
import retrofit2.http.Query

// Definiert die verfügbaren Methoden im Google Books API-Service.
interface GoogleBooksService {
    // Ruft Bücher per ISBN ab.
    @GET("volumes")
    suspend fun searchBooksByISBN(@Query("q") isbn: String, @Query("key") apiKey: String): BookResponse{
        return service.searchBooksByISBN("isbn:$isbn", apiKey) // Query-Parameter, um nach ISBN zu suchen und API-Schlüssel zur Authentifizierung.
    }
}