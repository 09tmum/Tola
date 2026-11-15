package com.example.tola.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import java.io.ByteArrayOutputStream

object Base64ImageUtils {

    fun compressAndEncodeUri(
        context: Context,
        imageUri: Uri,
        maxWidth: Int = 400,
        maxHeight: Int = 400,
        quality: Int = 70
    ): String {
        return try {
            val inputStream = context.contentResolver.openInputStream(imageUri) ?: return ""
            val originalBitmap = BitmapFactory.decodeStream(inputStream) ?: return ""
            inputStream.close()

            val scaledBitmap = scaleBitmap(originalBitmap, maxWidth, maxHeight)
            val byteArrayOutputStream = ByteArrayOutputStream()
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()

            val base64String = Base64.encodeToString(byteArray, Base64.NO_WRAP)
            "data:image/jpeg;base64,$base64String"
        } catch (_: Exception) {
            ""
        }
    }

    fun compressAndEncodeBitmap(
        bitmap: Bitmap,
        maxWidth: Int = 400,
        maxHeight: Int = 400,
        quality: Int = 70
    ): String {
        return try {
            val scaledBitmap = scaleBitmap(bitmap, maxWidth, maxHeight)
            val byteArrayOutputStream = ByteArrayOutputStream()
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()

            val base64String = Base64.encodeToString(byteArray, Base64.NO_WRAP)
            "data:image/jpeg;base64,$base64String"
        } catch (_: Exception) {
            ""
        }
    }

    fun decodeBase64ToBitmap(base64String: String): Bitmap? {
        return try {
            val cleanBase64 = if (base64String.contains(",")) {
                base64String.substringAfter(",")
            } else {
                base64String
            }
            val bytes = Base64.decode(cleanBase64, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } catch (_: Exception) {
            null
        }
    }

    private fun scaleBitmap(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        if (width <= maxWidth && height <= maxHeight) return bitmap

        val aspectRatio = width.toFloat() / height.toFloat()
        var newWidth = maxWidth
        var newHeight = (newWidth / aspectRatio).toInt()

        if (newHeight > maxHeight) {
            newHeight = maxHeight
            newWidth = (newHeight * aspectRatio).toInt()
        }

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }
}
