package com.example.booktrackerapp.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Singleton-Objekt zur Konfiguration und Bereitstellung eines Retrofit-Clients (object statt class)
object GoogleBooksApiClient{
    private const val BASE_URL = "https://www.googleapis.com/books/v1/"

    // Erstellt und konfiguriert eine Retrofit-Instanz.
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create()) // Konverter für die Serialisierung und Deserialisierung von Objekten.
        .build()

    // Erstellt verzögert(lazy) einen API-Service aus dem Retrofit-Client.
    val service: GoogleBooksService by lazy {
        retrofit.create(GoogleBooksService::class.java) // Erstellt eine Implementierung des GoogleBooksService-Interfaces.
    }
}