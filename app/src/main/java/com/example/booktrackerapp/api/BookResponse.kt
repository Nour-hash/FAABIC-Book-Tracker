package com.example.booktrackerapp.api

// Represents the overall response from the API, consisting of individual books (BookItem)
data class BookResponse(
    val items: List<BookItem>
) {
    // Default no-argument constructor required by Firestore
    constructor() : this(emptyList())
}

// Represents a single book
data class BookItem(
    val volumeInfo: VolumeInfo
) {
    // Default no-argument constructor required by Firestore
    constructor() : this(VolumeInfo())
}

// Detailed information about a book
data class VolumeInfo(
    val title: String = "",
    val authors: List<String> = emptyList(),
    val publisher: String? = null,
    val publishedDate: String? = null,
    val description: String? = null,
    val dimensions: Dimensions? = null,
    val mainCategory: String? = null,
    val averageRating: Double? = null,
    val ratingsCount: Int? = null,
    val retailPrice: RetailPrice? = null,
    val imageLinks: ImageLinks? = null,
    val industryIdentifiers: List<IndustryIdentifier>? = null,
    val categories: List<String>? = null,
    val notes: String? = null,  // Personal notes about the book
    val isRead: Boolean? = false,  // Reading status
    val pageCount: Int? = null, // Total number of pages
    val pagesRead: Int? = 0,  // Number of pages read
    val isFavorite: Boolean? = false,  // Favorite status
    val userRating: Int? = null
) {
    // Default no-argument constructor required by Firestore
    constructor() : this("", emptyList())
}

// URL links to book cover images
data class ImageLinks(
    val thumbnail: String = ""
) {
    // Default no-argument constructor required by Firestore
    constructor() : this("")
}

// Identifier for ISBN used to uniquely identify a book
data class IndustryIdentifier(
    val type: String = "",
    val identifier: String = ""
) {
    // Default no-argument constructor required by Firestore
    constructor() : this("", "")
}

// Dimensions of the physical book
data class Dimensions(
    val height: String = "",
    val width: String = "",
    val thickness: String = ""
) {
    // Default no-argument constructor required by Firestore
    constructor() : this("", "", "")
}

// Retail price of the book
data class RetailPrice(
    val amount: Double = 0.0,
    val currencyCode: String = ""
) {
    // Default no-argument constructor required by Firestore
    constructor() : this(0.0, "")
}
