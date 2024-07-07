package com.example.booktrackerapp.ai

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import android.graphics.Canvas

/**
 * Function to extract ISBN from a Bitmap image after preprocessing.
 *
 * @param context The application context.
 * @param bitmap The input Bitmap image containing text.
 * @param callback Callback function to receive the extracted ISBN string or null if not found.
 */
fun extractISBNFromBitmap(context: Context, bitmap: Bitmap, callback: (String?) -> Unit) {
    // Preprocess the bitmap to adjust brightness and contrast
    val processedBitmap = adjustBrightnessAndContrast(bitmap)

    // Convert the processed bitmap to InputImage
    val image = InputImage.fromBitmap(processedBitmap, 0)

    // Initialize the text recognizer
    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    // Process the image with text recognizer
    recognizer.process(image)
        .addOnSuccessListener { visionText ->
            val isbn = parseISBN(visionText)
            callback(isbn)
        }
        .addOnFailureListener { e ->
            e.printStackTrace()
            callback(null)
        }
}

/**
 * Function to parse ISBN from VisionText object.
 *
 * @param visionText The VisionText object containing recognized text blocks and lines.
 * @return The extracted ISBN string or null if not found.
 */
fun parseISBN(visionText: Text): String? {
    val isbnPattern = Regex("""\b97[89][-\d\s]{10,17}\b""")
    for (block in visionText.textBlocks) {
        for (line in block.lines) {
            val isbnMatch = isbnPattern.find(line.text)
            if (isbnMatch != null) {
                return isbnMatch.value.replace("[^\\d]".toRegex(), "")
            }
        }
    }
    return null
}

/**
 * Function to adjust brightness and contrast of a Bitmap image.
 *
 * @param bitmap The input Bitmap image to be processed.
 * @return The processed Bitmap with adjusted brightness and contrast.
 */
fun adjustBrightnessAndContrast(bitmap: Bitmap): Bitmap {
    // Create a mutable bitmap with ARGB_8888 configuration
    val adjustedBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)

    // Create a paint object with color matrix to adjust brightness and contrast
    val paint = Paint().apply {
        // Example adjustments: Increase brightness by 10% and contrast by 20%
        val brightness = 0.1f
        val contrast = 1.2f
        val cm = ColorMatrix(floatArrayOf(
            contrast, 0f, 0f, 0f, brightness * 255,
            0f, contrast, 0f, 0f, brightness * 255,
            0f, 0f, contrast, 0f, brightness * 255,
            0f, 0f, 0f, 1f, 0f
        ))
        colorFilter = ColorMatrixColorFilter(cm)
    }

    // Draw the original bitmap with the paint onto the adjustedBitmap
    val canvas = Canvas(adjustedBitmap)
    canvas.drawBitmap(adjustedBitmap, 0f, 0f, paint)

    return adjustedBitmap
}
