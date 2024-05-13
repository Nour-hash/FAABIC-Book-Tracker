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
