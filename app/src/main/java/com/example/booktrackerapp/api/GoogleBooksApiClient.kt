package com.example.booktrackerapp.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object GoogleBooksApiClient{
    private const val BASE_URL = "https://www.googleapis.com/books/v1/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service: GoogleBooksService by lazy {
        retrofit.create(GoogleBooksService::class.java)

    }
}