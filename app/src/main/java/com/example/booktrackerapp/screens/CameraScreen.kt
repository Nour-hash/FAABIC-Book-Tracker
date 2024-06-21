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
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.booktrackerapp.model.service.ImageUri
import com.example.booktrackerapp.viewModel.CameraViewModel
import com.example.booktrackerapp.widgets.CameraControls

@Composable
fun CameraScreen(navController: NavController, cameraViewModel: CameraViewModel, imageUriHolder: ImageUri){

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraController = remember { LifecycleCameraController(context)}
    val lensFacing = remember { mutableStateOf(CameraSelector.DEFAULT_BACK_CAMERA) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            cameraViewModel.handleGalleryResult(result.data, imageUriHolder, navController)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ){

        //Camera Preview
        AndroidView(
            modifier = Modifier,
            factory = { context ->
                PreviewView(context).apply{
                    layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                    setBackgroundColor(Color.BLACK)
                    scaleType = PreviewView.ScaleType.FILL_START
                }.also {previewView ->
                    previewView.controller = cameraController
                    cameraController.bindToLifecycle(lifecycleOwner)
                }
            })
        //Camera Button-Funktionen
        CameraControls(
            modifier = Modifier.align(Alignment.BottomCenter),

            onTakePicture = {
                cameraViewModel.takePicture(
                    cameraController = cameraController,
                    context = context,
                    navController = navController,
                    imageUriHolder = imageUriHolder
                )
            },
            onSwitchCamera = {
                cameraViewModel.switchCamera(
                    lensFacing = lensFacing,
                    cameraController = cameraController)
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




