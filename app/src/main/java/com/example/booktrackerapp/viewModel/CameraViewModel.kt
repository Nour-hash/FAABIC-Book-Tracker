package com.example.booktrackerapp.viewModel

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
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

/**
 * ViewModel for handling camera operations, capturing images, and selecting images from the gallery.
 */
class CameraViewModel : ViewModel() {

    // LiveData to hold the URI of the chosen image
    val chosenImageUri = MutableLiveData<Uri?>()

    /**
     * Function to capture an image using the camera.
     *
     * @param context The context used to access resources and services.
     * @param cameraController The LifecycleCameraController instance to control the camera lifecycle.
     * @param imageUriHolder An ImageUri instance to hold the captured image URI.
     * @param navController The NavController to navigate to another destination after capturing the image.
     */
    fun takePicture(
        context: Context,
        cameraController: LifecycleCameraController,
        imageUriHolder: ImageUri,
        navController: NavController,
    ) {
        val mainExecutor = ContextCompat.getMainExecutor(context)
        cameraController.takePicture(mainExecutor, object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                // Convert ImageProxy to Bitmap and rotate if necessary
                val bitmap = image.toBitmap()
                val rotatedBitmap = rotateBitmapBasedOnMetadata(bitmap, image)
                // Convert Bitmap to URI and save it in imageUriHolder
                val uri = bitmapToUriConverter(rotatedBitmap, context)
                image.close() // Close ImageProxy
                imageUriHolder.setImageUri(uri) // Set the chosen image URI in ImageUri instance
                navController.navigate("previewScreen") // Navigate to preview screen
            }
        })
    }

    /**
     * Function to switch between front and back cameras.
     *
     * @param lensFacing MutableState to hold the currently selected camera lens facing.
     * @param cameraController The LifecycleCameraController instance to control the camera lifecycle.
     */
    fun switchCamera(lensFacing: MutableState<CameraSelector>,
                     cameraController: LifecycleCameraController) {
        lensFacing.value = if (lensFacing.value == CameraSelector.DEFAULT_BACK_CAMERA) {
            CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            CameraSelector.DEFAULT_BACK_CAMERA
        }
        cameraController.cameraSelector = lensFacing.value // Set the new camera selector
    }

    /**
     * Function to convert a Bitmap image to a Uri.
     *
     * @param bitmap The Bitmap image to convert.
     * @param context The context used to access cache directory.
     * @return The Uri of the converted Bitmap.
     */
    fun bitmapToUriConverter(bitmap: Bitmap, context: Context): Uri {
        val cacheDir = context.cacheDir // Get cache directory
        val image = File(cacheDir, "${UUID.randomUUID()}.jpg") // Create new image file

        try {
            val stream: OutputStream = FileOutputStream(image)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream) // Compress Bitmap to JPEG format
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace() // Handle IOException
        }

        return Uri.fromFile(image) // Return Uri of the saved image file
    }

    /**
     * Function to rotate a Bitmap image based on metadata.
     *
     * @param bitmap The Bitmap image to rotate.
     * @param image The ImageProxy containing metadata for rotation degrees.
     * @return The rotated Bitmap image.
     */
    private fun rotateBitmapBasedOnMetadata(bitmap: Bitmap, image: ImageProxy): Bitmap {
        val rotationDegrees = image.imageInfo.rotationDegrees // Get rotation degrees from ImageProxy
        return if (rotationDegrees != 0) {
            val matrix = Matrix()
            matrix.postRotate(rotationDegrees.toFloat()) // Create rotation matrix
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true) // Rotate Bitmap
        } else {
            bitmap // Return original Bitmap if no rotation is needed
        }
    }

    /**
     * Function to select an image from the gallery.
     *
     * @param launcher ActivityResultLauncher used to launch the intent to pick an image.
     */
    fun selectImageFromGallery(launcher: ActivityResultLauncher<Intent>) {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        launcher.launch(intent) // Launch intent to pick an image from gallery
    }

    /**
     * Function to handle the result after selecting an image from the gallery.
     *
     * @param data The Intent containing the selected image data.
     * @param imageUriHolder An ImageUri instance to hold the selected image URI.
     * @param navController The NavController to navigate to another destination after selecting the image.
     */
    fun handleGalleryResult(data: Intent?, imageUriHolder: ImageUri, navController: NavController) {
        data?.data?.let { uri ->
            chosenImageUri.value = uri // Set the chosen image URI in LiveData
            imageUriHolder.setImageUri(uri) // Set the chosen image URI in ImageUri instance
            navController.navigate("previewScreen") // Navigate to preview screen
        }
    }
}
