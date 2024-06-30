package com.example.booktrackerapp.viewModel

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.compose.runtime.MutableState
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.booktrackerapp.model.service.ImageUri
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.UUID
import android.graphics.Matrix

class CameraViewModel: ViewModel() {

    val chosenImageUri = MutableLiveData<Uri?>()
    fun takePicture(
        context: Context,
        cameraController: LifecycleCameraController,
        imageUriHolder: ImageUri,
        navController: NavController
    ) {
        val mainExecutor = ContextCompat.getMainExecutor(context)
        cameraController.takePicture(mainExecutor, object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                val bitmap = image.toBitmap()
                val rotatedBitmap = rotateBitmapBasedOnMetadata(bitmap, image)
                val uri = bitmapToUriConverter(rotatedBitmap, context)
                image.close()
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
        val cacheDir = context.cacheDir
        val image = File(cacheDir, "${UUID.randomUUID()}.jpg")

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

    private fun rotateBitmapBasedOnMetadata(bitmap: Bitmap, image: ImageProxy): Bitmap {
        val rotationDegrees = image.imageInfo.rotationDegrees
        return if (rotationDegrees != 0) {
            val matrix = Matrix()
            matrix.postRotate(rotationDegrees.toFloat())
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        } else {
            bitmap
        }
    }


    fun selectImageFromGallery(launcher: ActivityResultLauncher<Intent>) {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        launcher.launch(intent)
    }

    fun handleGalleryResult(data: Intent?, imageUriHolder: ImageUri, navController: NavController) {
        data?.data?.let { uri ->
            chosenImageUri.value = uri
            imageUriHolder.setImageUri(uri)
            navController.navigate("previewScreen")
        }
    }

}


