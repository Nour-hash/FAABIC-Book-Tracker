import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.booktrackerapp.model.service.ImageUri

@Composable
fun PreviewScreen(navController: NavController, imageUriHolder: ImageUri) {
    val context = LocalContext.current
    val bitmapUri = imageUriHolder.imageUri.value

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        bitmapUri?.let { uri ->
            // Load the image from the URI
            val imageBitmap = loadBitmapFromUri(uri, context)

            // Display the image
            Image(
                bitmap = imageBitmap.asImageBitmap(),
                contentDescription = "Preview",
                modifier = Modifier.fillMaxSize()
            )
        }

        // Bottom Bar with Retake and Done buttons
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(onClick = {
                navController.navigate("cameraScreen")
            }) {
                Text("Retake")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = {}) {
                Text("Done")
            }
        }
    }
}

// Function to load bitmap from URI
fun loadBitmapFromUri(uri: Uri, context: Context): Bitmap {
    return if (Build.VERSION.SDK_INT < 28) {
        MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
    } else {
        val source = ImageDecoder.createSource(context.contentResolver, uri)
        ImageDecoder.decodeBitmap(source)
    }
}