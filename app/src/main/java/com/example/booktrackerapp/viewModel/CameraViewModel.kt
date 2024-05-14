package com.example.booktrackerapp.viewModel

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.compose.runtime.MutableState
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.booktrackerapp.model.service.ImageUri
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.UUID

class CameraViewModel: ViewModel() {

  fun takePicture(context: Context,
                            cameraController: LifecycleCameraController,
                            imageUriHolder: ImageUri,
                            navController: NavController) {
        val mainExecutor = ContextCompat.getMainExecutor(context)
        cameraController.takePicture(mainExecutor, object : ImageCapture.OnImageCapturedCallback(){
            override fun onCaptureSuccess(image: ImageProxy) {
                val uri = bitmapToUriConverter(image.toBitmap(), context)
                imageUriHolder.setImageUri(uri)
                navController.navigate("previewScreen")
            }
        })
    }

    fun switchCamera(lensFacing: MutableState<CameraSelector>,
                     cameraController: LifecycleCameraController) {
        lensFacing.value = if (lensFacing.value == CameraSelector.DEFAULT_BACK_CAMERA) {
            CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            CameraSelector.DEFAULT_BACK_CAMERA
        }
        cameraController.cameraSelector = lensFacing.value
    }

    fun bitmapToUriConverter(bitmap: Bitmap, context: Context): Uri {
        val imagesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File(imagesDir, "${UUID.randomUUID()}.jpg")

        try {
            val stream: OutputStream = FileOutputStream(image)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return Uri.fromFile(image)
    }
}


