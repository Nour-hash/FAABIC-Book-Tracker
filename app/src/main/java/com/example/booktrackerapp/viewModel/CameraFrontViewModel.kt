package com.example.booktrackerapp.viewModel

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
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
import com.example.booktrackerapp.ai.BookRecognitionModel
import com.example.booktrackerapp.model.service.ImageUri
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.UUID
import android.graphics.Matrix

/**
 * ViewModel for the front camera screen.
 */
class CameraFrontViewModel : ViewModel() {
    // MutableLiveData to hold the URI of the chosen image
    private val chosenImageUri = MutableLiveData<Uri?>()

    /**
     * Function to capture an image using the front camera.
     *
     * @param context The application context.
     * @param cameraController The camera controller instance.
     * @param imageUriHolder The holder for the image URI.
     * @param navController The NavController used for navigation.
     * @param bookRecognitionModel The model used for book recognition.
     */
    fun takePicture(
        context: Context,
        cameraController: LifecycleCameraController,
        imageUriHolder: ImageUri,
        navController: NavController,
        bookRecognitionModel: BookRecognitionModel
    ) {
        val mainExecutor = ContextCompat.getMainExecutor(context)
        cameraController.takePicture(mainExecutor, object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                // Convert captured image to Bitmap
                val bitmap = image.toBitmap()
                // Rotate bitmap based on image metadata
                val rotatedBitmap = rotateBitmapBasedOnMetadata(bitmap, image)
                // Convert rotated bitmap to URI
                val uri = bitmapToUriConverter(rotatedBitmap, context)
                // Close the ImageProxy
                image.close()
                // Store the URI in imageUriHolder for later use
                imageUriHolder.setImageUri(uri)

                // Check if the captured image is of a book using AI model
                if (bookRecognitionModel.isBookImage(rotatedBitmap)) {
                    navController.navigate("previewScreenFront")
                    Log.d("AI_PREDICTION", "is book")
                } else {
                    // Handle non-book image case
                    navController.navigate("cameraScreenFront?showMessage=true")
                    Log.d("AI_PREDICTION", "is not book")
                }
            }
        })
    }

    /**
     * Function to switch between front and back cameras.
     *
     * @param lensFacing The mutable state holding the camera selector (front/back).
     * @param cameraController The camera controller instance.
     */
    fun switchCamera(lensFacing: MutableState<CameraSelector>, cameraController: LifecycleCameraController) {
        // Toggle between front and back camera selectors
        lensFacing.value = if (lensFacing.value == CameraSelector.DEFAULT_BACK_CAMERA) {
            CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            CameraSelector.DEFAULT_BACK_CAMERA
        }
        // Set the new camera selector to the camera controller
        cameraController.cameraSelector = lensFacing.value
    }

    /**
     * Function to convert Bitmap to Uri and save it in the device's cache directory.
     *
     * @param bitmap The Bitmap to convert.
     * @param context The application context.
     * @return The Uri of the saved image.
     */
    fun bitmapToUriConverter(bitmap: Bitmap, context: Context): Uri {
        // Get the cache directory where the image will be stored
        val cacheDir = context.cacheDir
        // Create a new File in the cache directory with a unique filename
        val image = File(cacheDir, "${UUID.randomUUID()}.jpg")

        try {
            // Write the Bitmap data to the OutputStream
            val stream: OutputStream = FileOutputStream(image)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        // Return the Uri of the saved image file
        return Uri.fromFile(image)
    }

    /**
     * Function to rotate a Bitmap based on image metadata.
     *
     * @param bitmap The Bitmap to rotate.
     * @param image The ImageProxy containing image metadata.
     * @return The rotated Bitmap.
     */
    private fun rotateBitmapBasedOnMetadata(bitmap: Bitmap, image: ImageProxy): Bitmap {
        val rotationDegrees = image.imageInfo.rotationDegrees
        return if (rotationDegrees != 0) {
            // Create a Matrix and rotate the Bitmap based on rotationDegrees
            val matrix = Matrix()
            matrix.postRotate(rotationDegrees.toFloat())
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        } else {
            // No rotation needed
            bitmap
        }
    }

    /**
     * Function to select an image from the device's gallery.
     *
     * @param launcher The ActivityResultLauncher used to launch the gallery intent.
     */
    fun selectImageFromGallery(launcher: ActivityResultLauncher<Intent>) {
        // Create an intent to pick an image from the gallery
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        // Launch the intent using the ActivityResultLauncher
        launcher.launch(intent)
    }

    /**
     * Function to handle the result of selecting an image from the gallery.
     *
     * @param data The Intent containing the selected image data.
     * @param imageUriHolder The holder for the image URI.
     * @param navController The NavController used for navigation.
     */
    fun handleGalleryResult(data: Intent?, imageUriHolder: ImageUri, navController: NavController) {
        // Extract the URI of the selected image from the Intent data
        data?.data?.let { uri ->
            // Update the MutableLiveData with the chosen image URI
            chosenImageUri.value = uri
            // Store the URI in imageUriHolder for later use
            imageUriHolder.setImageUri(uri)
            // Navigate to the preview screen
            navController.navigate("previewScreenFront")
        }
    }
}
