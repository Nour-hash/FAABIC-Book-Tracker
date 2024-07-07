package com.example.booktrackerapp.ai

import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel

/**
 * Class for performing book image recognition using a TensorFlow Lite model.
 *
 * @param context The application context.
 */
class BookRecognitionModel(context: Context) {

    private val interpreter: Interpreter

    // Initialize the TensorFlow Lite interpreter with the model file
    init {
        interpreter = Interpreter(loadModelFile(context, "optimized_model.tflite"))
    }

    /**
     * Function to determine if a given Bitmap represents a book image using a TensorFlow Lite model.
     *
     * @param bitmap The input Bitmap image to be analyzed.
     * @return True if the image is predicted to be a book, false otherwise.
     */
    fun isBookImage(bitmap: Bitmap): Boolean {
        // Convert the Bitmap to a ByteBuffer for input to the TensorFlow Lite model
        val inputBuffer = convertBitmapToByteBuffer(bitmap)

        // Create a TensorBuffer to hold the output from the TensorFlow Lite model
        val outputBuffer = TensorBuffer.createFixedSize(intArrayOf(1, 1), DataType.FLOAT32)

        // Run inference with the TensorFlow Lite model
        interpreter.run(inputBuffer, outputBuffer.buffer.rewind())

        // Retrieve the prediction result from the output TensorBuffer
        val outputArray = outputBuffer.floatArray
        val prediction = outputArray[0]

        // Return true if the prediction score is greater than 0.5, indicating a book image
        return prediction > 0.5
    }

    /**
     * Function to convert a Bitmap image to a ByteBuffer suitable for input to the TensorFlow Lite model.
     *
     * @param bitmap The input Bitmap image to convert.
     * @return ByteBuffer containing the converted pixel data.
     */
    private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        // Ensure we have a software-backed bitmap for safe processing
        val softwareBitmap = if (bitmap.config != Bitmap.Config.ARGB_8888) {
            bitmap.copy(Bitmap.Config.ARGB_8888, true)
        } else {
            bitmap
        }

        // Allocate a direct ByteBuffer with native byte order
        val byteBuffer = ByteBuffer.allocateDirect(1 * 256 * 256 * 3 * 4)
        byteBuffer.order(ByteOrder.nativeOrder())

        // Resize the bitmap to 256x256 pixels for model input
        val resizedBitmap = Bitmap.createScaledBitmap(softwareBitmap, 256, 256, true)

        // Extract pixel data from the resized bitmap
        val pixels = IntArray(resizedBitmap.width * resizedBitmap.height)
        resizedBitmap.getPixels(pixels, 0, resizedBitmap.width, 0, 0, resizedBitmap.width, resizedBitmap.height)

        // Convert pixel values to normalized floats and put them into the ByteBuffer
        for (pixelValue in pixels) {
            byteBuffer.putFloat(((pixelValue shr 16) and 0xFF) / 255.0f)
            byteBuffer.putFloat(((pixelValue shr 8) and 0xFF) / 255.0f)
            byteBuffer.putFloat((pixelValue and 0xFF) / 255.0f)
        }

        return byteBuffer
    }

    /**
     * Function to load the TensorFlow Lite model file from assets.
     *
     * @param context The application context.
     * @param modelPath The path to the TensorFlow Lite model file in assets.
     * @return ByteBuffer containing the loaded model file.
     */
    private fun loadModelFile(context: Context, modelPath: String): ByteBuffer {
        // Open the model file from assets using FileInputStream and FileChannel
        val assetFileDescriptor = context.assets.openFd(modelPath)
        val fileInputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
        val fileChannel = fileInputStream.channel

        // Map the model file to a ByteBuffer for TensorFlow Lite interpreter
        val startOffset = assetFileDescriptor.startOffset
        val declaredLength = assetFileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }
}
