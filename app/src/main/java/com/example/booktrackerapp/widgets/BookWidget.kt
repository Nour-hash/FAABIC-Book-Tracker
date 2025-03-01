package com.example.booktrackerapp.widgets

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.booktrackerapp.api.BookItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

//Darstellung eines Buches mit Titel, Cover,Favoriten-Icon und Author
@Composable
fun BookRowSimple(
    book: BookItem,
    navController: NavController,
    isClickable: Boolean = true,
    onFavoriteClick: (String, Boolean) -> Unit
) {
    val favoriteState = produceState(initialValue = book.volumeInfo.isFavorite ?: false) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val libraryId = "defaultLibrary"
        val bookId = book.volumeInfo.industryIdentifiers?.firstOrNull()?.identifier ?: ""
        val db = FirebaseFirestore.getInstance()

        val docRef = db.collection("Users").document(userId!!)
            .collection("Libraries").document(libraryId)
            .collection("Books").document(bookId)

        docRef.addSnapshotListener { snapshot, _ ->
            if (snapshot != null && snapshot.exists()) {
                value = snapshot.getBoolean("volumeInfo.isFavorite") ?: false
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .then(if (isClickable) Modifier.clickable {
                val isbn = book.volumeInfo.industryIdentifiers?.firstOrNull()?.identifier ?: ""
                navController.navigate("detail/$isbn")
            } else Modifier),
        elevation = CardDefaults.cardElevation(10.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = book.volumeInfo.title, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))
            BookCardHeader(
                imageUrl = book.volumeInfo.imageLinks?.thumbnail ?: "",
                isFavorite = favoriteState.value,
                onFavoriteClick = { onFavoriteClick(book.volumeInfo.industryIdentifiers?.firstOrNull()?.identifier ?: "", !favoriteState.value) }
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("Authors: ${book.volumeInfo.authors.joinToString(", ")}", style = MaterialTheme.typography.bodySmall)
        }
    }
}

// Header für die Buchkarte mit Buchcover und Favoritenstatus
@Composable
fun BookCardHeader(imageUrl: String, isFavorite: Boolean, onFavoriteClick: () -> Unit) {
    Box(
        modifier = Modifier
            .height(200.dp)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        BookImage(imageUrl = imageUrl)
        FavoriteIcon(isFavorite = isFavorite, onFavoriteClick = onFavoriteClick)
    }
}

// Anzeige des Buchcovers
@Composable
fun BookImage(imageUrl: String) {
    SubcomposeAsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl.replace("http://", "https://"))
            .crossfade(true)
            .build(),
        contentDescription = "Book cover",
        contentScale = ContentScale.Fit,
        loading = { CircularProgressIndicator() },
        modifier = Modifier.fillMaxSize()
    )
}

// Anzeige des Favoriten-Icons
@Composable
fun FavoriteIcon(isFavorite: Boolean, onFavoriteClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        contentAlignment = Alignment.TopEnd
    ) {
        Icon(
            modifier = Modifier.clickable(onClick = onFavoriteClick),
            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
            contentDescription = "Toggle Favorite",
            tint = if (isFavorite) Color.Red else Color.Gray
        )
    }
}


// Detaillierte Ansicht eines Buches
@Composable
fun BookDetails(modifier: Modifier, book: BookItem) {
    Column(modifier = modifier.padding(horizontal = 16.dp)
        ) {

                book.volumeInfo.publisher?.let {
                    DetailText(label = "Publisher:", content = it)
                }
                book.volumeInfo.publishedDate?.let {
                    DetailText(label = "Published Date:", content = it)
                }
                book.volumeInfo.pageCount?.let {
                    DetailText(label = "Page Count:", content = "$it")
                }
                book.volumeInfo.dimensions?.let {
                    DetailText(
                        label = "Dimensions:",
                        content = "${it.height} x ${it.width} x ${it.thickness}"
                    )
                }
                book.volumeInfo.mainCategory?.let {
                    DetailText(label = "Main Category:", content = it)
                }
                book.volumeInfo.averageRating?.let {
                    DetailText(label = "Rating:", content = "$it")
                }
                book.volumeInfo.ratingsCount?.let {
                    DetailText(label = "Ratings Count:", content = "$it")
                }
                book.volumeInfo.retailPrice?.let {
                    DetailText(label = "Price:", content = "${it.amount} ${it.currencyCode}")
                }
                book.volumeInfo.categories?.let { categories ->
                    DetailText(label = "Categories:", content = categories.joinToString(", "))
                }
                book.volumeInfo.description?.let {
                    DetailText(label = "Description:", content = it)
                    Spacer(modifier = Modifier.height(16.dp)) // Optional: Für Abstand am Ende
                }

    }

}

// Anzeige eines Detailtextes mit einem Label und dem Inhalt
@Composable
fun DetailText(label: String, content: String) {
    Text(buildAnnotatedString {
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
            append(label)
        }
        append(" $content")
    }, style = MaterialTheme.typography.bodyMedium)
}

// Button zum Anzeigen und Ändern des Lesestatus eines Buches.
@Composable
fun ReadStatusButton(isRead: Boolean, onClick: () -> Unit) {
    Button(onClick = onClick) {
        Icon(
            imageVector = if (isRead) Icons.Filled.Check else Icons.Default.Close,
            contentDescription = if (isRead) "Gelesen" else "Nicht gelesen",
            tint = if (isRead) Color.Green else Color.Gray
        )
        Text(text = if (isRead) "Read" else "Marked as not read")
    }
}

// Bewertungskomponente für Bücher
@Composable
fun RatingBar(rating: Int?, onRatingChanged: (Int) -> Unit) {
    // Der aktuelle Bewertungsstatus
    val currentRating = remember { mutableIntStateOf(rating ?: 0) }

    // Log the current rating to verify it's being set correctly
    Log.d("RatingBar", "Initial rating: $rating, currentRating: ${currentRating.value}")


    Row {
        // Erstellt 5 Sterne-Icons für die Bewertung
        (1..5).forEach { star ->
            IconButton(onClick = {
                // Aktualisiert den aktuellen Bewertungsstatus, wenn ein Stern angeklickt wird
                currentRating.value = star
                onRatingChanged(star)
                Log.d("RatingBar", "Star clicked: $star, newRating: ${currentRating.value}")
            }) {
                Box(contentAlignment = Alignment.Center) {
                    if (star <= currentRating.value) {
                        // Zeigt gefüllte Sterne für die aktuelle Bewertung
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Star",
                            tint = Color.Yellow
                        )
                    } else {
                        // Zeigt leere Sterne für Bewertungen, die nicht ausgewählt wurden
                        Icon(
                            imageVector = Icons.Outlined.Star,
                            contentDescription = "Star Outline",
                            tint = Color.Gray
                        )
                    }
                    // Zeigt die Zahl des Sterns an
                    Text(
                        text = star.toString(),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@Composable
fun PagesReadInput(pagesRead: Int, totalPageCount: Int, onPagesReadChange: (Int) -> Unit) {
    val nonNullPagesRead = pagesRead ?: 0 // Provide a default value of 0 if pagesRead is null
    var inputText by remember { mutableStateOf(nonNullPagesRead.toString()) } // State to keep track of the input text
    val progress = if (totalPageCount > 0) (nonNullPagesRead / totalPageCount.toFloat()) * 100 else 0f  // Calculate progress percentage, ensuring no division by zero

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Display reading progress percentage only if totalPageCount > 0
        if (totalPageCount > 0) {
            Text("Reading Progress: ${progress.toInt()}%")
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Input field for pages read
        OutlinedTextField(
            value = inputText,
            onValueChange = { newText ->
                // Allow only numeric input
                if (newText.all { it.isDigit() }) {
                    val newPagesRead = newText.toIntOrNull() ?: 0

                    // Ensure pagesRead does not exceed totalPageCount if totalPageCount > 0
                    if (totalPageCount == 0 || newPagesRead <= totalPageCount) {
                        inputText = newText
                        onPagesReadChange(newPagesRead)
                    }
                }
            },
            label = { Text("Pages Read") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}


