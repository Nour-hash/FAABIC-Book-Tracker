import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.booktrackerapp.R

@Composable
fun PreviewScreen(navController: NavController) {
    val context = LocalContext.current
    val bitmapUri = // Get the bitmap URI using LocalContext.current

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            androidx.compose.foundation.Image(
                painter = painterResource(id = R.drawable.visible), // Placeholder image
                contentDescription = "Captured Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )

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

