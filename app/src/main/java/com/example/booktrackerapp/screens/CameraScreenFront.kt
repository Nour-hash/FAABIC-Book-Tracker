package com.example.booktrackerapp.screens

import android.app.Activity
import android.graphics.Color
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.booktrackerapp.ai.BookRecognitionModel
import com.example.booktrackerapp.model.service.ImageUri
import com.example.booktrackerapp.viewModel.CameraFrontViewModel
import com.example.booktrackerapp.viewModel.CameraViewModel
import com.example.booktrackerapp.widgets.CameraControls

@Composable
fun CameraScreenFront(navController: NavController, cameraViewModel: CameraFrontViewModel, imageUriHolder: ImageUri,showMessage: Boolean = false) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraController = remember { LifecycleCameraController(context) }
    val lensFacing = remember { mutableStateOf(CameraSelector.DEFAULT_BACK_CAMERA) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            cameraViewModel.handleGalleryResult(result.data, imageUriHolder, navController)
        }
    }

    val bookRecognitionModel = remember { BookRecognitionModel(context) }
    // Alert dialog state
    var showAlert by remember { mutableStateOf(showMessage) }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {

        // Camera Preview
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                PreviewView(context).apply {
                    layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                    setBackgroundColor(Color.BLACK)
                    scaleType = PreviewView.ScaleType.FILL_START
                }.also { previewView ->
                    previewView.controller = cameraController
                    cameraController.bindToLifecycle(lifecycleOwner)
                }
            }
        )

        // Show alert if no book is detected
        if (showAlert) {
            AlertDialog(
                onDismissRequest = { showAlert = false },
                title = { Text("No Book Detected") },
                text = { Text("Please make sure the book is clearly visible and try again.") },
                confirmButton = {
                    Button(onClick = { showAlert = false }) {
                        Text("Dismiss")
                    }
                }
            )
        }
        // Camera Button Functions
        CameraControls(
            modifier = Modifier.align(Alignment.BottomCenter),
            onTakePicture = {
                cameraViewModel.takePicture(
                    cameraController = cameraController,
                    context = context,
                    navController = navController,
                    imageUriHolder = imageUriHolder,
                    bookRecognitionModel = bookRecognitionModel
                )
            },
            onSwitchCamera = {
                cameraViewModel.switchCamera(
                    lensFacing = lensFacing,
                    cameraController = cameraController
                )
            },
            onCancelPreview = {
                navController.navigate("homescreen")
            },
            onOpenGallery = {
                cameraViewModel.selectImageFromGallery(galleryLauncher)
            }
        )
    }
}
