package com.example.booktrackerapp.api

//enthält die Gesamtantwort der API, besteht aus einzelnen Büchern(BookItem)
data class BookResponse(
    val items: List<BookItem>
)

//Repräsentiert ein einzelnes Buch
data class BookItem(
    val volumeInfo: VolumeInfo
)

// Detaillierte Informationen über ein Buch.
data class VolumeInfo(
    val title: String,
    val authors: List<String>,
    val publisher: String?,
    val publishedDate: String?,
    val description: String?,
    val pageCount: Int?,
    val dimensions: Dimensions?,
    val mainCategory: String?,
    val averageRating: Double?,
    val ratingsCount: Int?,
    val retailPrice: RetailPrice?,
    val imageLinks: ImageLinks?,
    val industryIdentifiers: List<IndustryIdentifier>?,
    val categories: List<String>?
    // Weitere relevante Felder hier hinzufügen
)

//URL-Links zu den Buchcover-Bildern.
data class ImageLinks(
    val thumbnail: String
)

// Identifikato für ISBN, welches verwendet wird, um ein Buch eindeutig zu identifizieren.
data class IndustryIdentifier(
    val type: String, // Art des Identifikators, z.B. ISBN_10 oder ISBN_13.
    val identifier: String // Der Identifikatorwert z.B bei ISBN_10 (3257602642)
)

// Abmessungen des physischen Buches.
data class Dimensions(
    val height: String,
    val width: String,
    val thickness: String
)

// Einzelhandelspreis des Buches
data class RetailPrice(
    val amount: Double,
    val currencyCode: String
)
