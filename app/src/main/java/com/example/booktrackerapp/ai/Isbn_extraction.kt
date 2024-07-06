package com.example.booktrackerapp.ai

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

fun extractISBNFromBitmap(context: Context, bitmap: Bitmap, callback: (String?) -> Unit) {
    val image = InputImage.fromBitmap(bitmap, 0)
    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

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
