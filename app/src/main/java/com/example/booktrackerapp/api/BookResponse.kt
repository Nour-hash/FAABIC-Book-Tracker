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
    // Weitere relevante Felder hier hinzufügen
)

data class ImageLinks(
    val thumbnail: String
)
