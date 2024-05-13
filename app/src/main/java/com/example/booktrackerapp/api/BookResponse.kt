package com.example.booktrackerapp.api

data class BookResponse(
    val items: List<BookItem>
)

data class BookItem(
    val volumeInfo: VolumeInfo
)

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
    val industryIdentifiers: List<IndustryIdentifier>?
    // Weitere relevante Felder hier hinzufügen
)

data class ImageLinks(
    val thumbnail: String
)

data class IndustryIdentifier(
    val type: String,
    val identifier: String
)

data class Dimensions(
    val height: String,
    val width: String,
    val thickness: String
)

data class RetailPrice(
    val amount: Double,
    val currencyCode: String
)
