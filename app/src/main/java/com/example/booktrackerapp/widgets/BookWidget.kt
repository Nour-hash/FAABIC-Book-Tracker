package com.example.booktrackerapp.widgets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.booktrackerapp.api.BookItem

@Composable
fun BookListScreen(modifier: Modifier, books: List<BookItem>, navController : NavController) {
    LazyColumn(
        modifier = modifier
        //Modifier.fillMaxSize(),
        //contentPadding = PaddingValues(16.dp),
        //verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(books) { bookItem ->
            BookRow(bookItem, navController = navController)
        }
    }
}

/*@Composable
fun BookItemView(bookItem: BookItem) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = bookItem.volumeInfo.title)
        bookItem.volumeInfo.authors?.let { authors ->
            Text(text = "Authors: ${authors.joinToString(", ")}")
        }
        bookItem.volumeInfo.imageLinks?.thumbnail?.let { thumbnail ->
            Log.d("BookCoverURL", thumbnail) // Debug-Ausgabe zur Kontrolle der URL
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(thumbnail)
                    .crossfade(true)
                    .placeholder(R.drawable.works) // Replace with your drawable
                    .error(R.drawable.error) // Replace with your drawable
                    .listener(
                        onStart = { Log.d("ImageLoad", "Start loading $thumbnail") },
                        onError = { _, _ -> Log.e("ImageLoad", "Error loading $thumbnail") },
                        onSuccess = { _, _ -> Log.d("ImageLoad", "Success loading $thumbnail") }
                    )
                    .build(),
                contentDescription = "Book Cover",
                modifier = Modifier.size(100.dp)
            )
        }
    }
}*/

@Composable
fun BookRow(book: BookItem, navController: NavController) {
    var isFavorite by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .clickable {
                //val isbn = book.volumeInfo.title
                val isbn = book.volumeInfo.industryIdentifiers?.firstOrNull()?.identifier ?: ""
                navController.navigate("detail/$isbn")
            },
        elevation = CardDefaults.cardElevation(10.dp)
    ) {
        Column {
            Spacer(modifier = Modifier.height(8.dp))
            BookCardHeader(
                imageUrl = book.volumeInfo.imageLinks?.thumbnail ?: "",
                isFavorite = isFavorite,
                onFavoriteClick = { isFavorite = !isFavorite }
            )
            Spacer(modifier = Modifier.height(8.dp))
            BookDetails(modifier = Modifier.padding(12.dp), book = book)
        }
    }
}

@Composable
fun BookRowSimple(book: BookItem, navController: NavController) {
    var isFavorite by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .clickable {
                // Navigiere zum DetailScreen mit der ISBN des Buches
                val isbn = book.volumeInfo.industryIdentifiers?.firstOrNull()?.identifier ?: ""
                navController.navigate("detail/$isbn")
            },
        elevation = CardDefaults.cardElevation(10.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = book.volumeInfo.title, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))
            BookCardHeader(
                imageUrl = book.volumeInfo.imageLinks?.thumbnail ?: "",
                isFavorite = isFavorite,
                onFavoriteClick = { isFavorite = !isFavorite }
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("Authors: ${book.volumeInfo.authors.joinToString(", ")}", style = MaterialTheme.typography.bodySmall)
        }
    }
}


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

@Composable
fun FavoriteIcon(isFavorite: Boolean, onFavoriteClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        contentAlignment = Alignment.TopEnd
    ){
        Icon(
            modifier = Modifier.clickable(onClick = onFavoriteClick),
            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
            contentDescription = "Toggle Favorite",
            tint = if (isFavorite) Color.Red else Color.Gray
        )
    }
}

@Composable
fun BookDetails(modifier: Modifier, book: BookItem) {
    var showDetails by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = book.volumeInfo.title)
        Icon(
            modifier = Modifier.clickable { showDetails = !showDetails },
            imageVector = if (showDetails) Icons.Filled.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
            contentDescription = "show more"
        )
    }

    AnimatedVisibility(
        visible = showDetails,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Column(modifier = modifier) {
            Text("Authors: ${book.volumeInfo.authors.joinToString(", ")}", style = MaterialTheme.typography.bodySmall)
            Divider(modifier = Modifier.padding(3.dp))

            book.volumeInfo.publisher?.let { Text("Publisher: $it",style = MaterialTheme.typography.bodySmall) }
            book.volumeInfo.publishedDate?.let { Text("Published Date: $it",style = MaterialTheme.typography.bodySmall) }
            book.volumeInfo.pageCount?.let { Text("Page Count: $it",style = MaterialTheme.typography.bodySmall) }
            book.volumeInfo.dimensions?.let {
                Text("Dimensions: ${it.height} x ${it.width} x ${it.thickness}",style = MaterialTheme.typography.bodySmall)
            }
            book.volumeInfo.mainCategory?.let { Text("Main Category: $it",style = MaterialTheme.typography.bodySmall) }
            book.volumeInfo.averageRating?.let { Text("Rating: $it",style = MaterialTheme.typography.bodySmall) }
            book.volumeInfo.ratingsCount?.let { Text("Ratings Count: $it",style = MaterialTheme.typography.bodySmall) }
            book.volumeInfo.retailPrice?.let {
                Text("Price: ${it.amount} ${it.currencyCode}", style = MaterialTheme.typography.bodySmall)
            }
            book.volumeInfo.description?.let { Text("Description: $it",style = MaterialTheme.typography.bodySmall) }
        }
    }
}

/*
@Composable
fun SingleBookView(bookItem: BookItem) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = bookItem.volumeInfo.title)
        bookItem.volumeInfo.authors?.let { authors ->
            Text(text = "Authors: ${authors.joinToString(", ")}")
        }
        bookItem.volumeInfo.imageLinks?.thumbnail?.let { thumbnail ->
            Log.d("BookCoverURL", thumbnail) // Debug-Ausgabe zur Kontrolle der URL
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(thumbnail.replace("http://", "https://"))
                    .crossfade(true)
                    .placeholder(R.drawable.works) // Replace with your drawable
                    .error(R.drawable.error) // Replace with your drawable
                    .listener(
                        onStart = { Log.d("ImageLoad", "Start loading $thumbnail") },
                        onError = { _, _ -> Log.e("ImageLoad", "Error loading $thumbnail") },
                        onSuccess = { _, _ -> Log.d("ImageLoad", "Success loading $thumbnail") }
                    )
                    .build(),
                contentDescription = "Book Cover",
                modifier = Modifier.size(100.dp)
            )
        } ?: run {
            // Handle case where there is no thumbnail
            Text(text = "No cover image available.")
        }
    }
}*/
