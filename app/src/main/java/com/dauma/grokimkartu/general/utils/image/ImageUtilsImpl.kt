package com.dauma.grokimkartu.general.utils.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.InputStream

class ImageUtilsImpl : ImageUtils {
    override fun getImageWithAuthority(context: Context, uri: Uri, width: Int, height: Int): Bitmap? {
        return decodeUriStreamToSize(context, uri, width, height)
    }

    override fun scaleImage(bitmap: Bitmap, width: Int, height: Int): Bitmap {
        val aspectRatio = bitmap.width.toFloat() / bitmap.height.toFloat()
        val ratioHeight = Math.round(width / aspectRatio)
        return Bitmap.createScaledBitmap(bitmap, width, ratioHeight, false)
    }

    private fun decodeUriStreamToSize(context: Context, uri: Uri, width: Int, height: Int): Bitmap? {
        var inputStream: InputStream? = null
        try {
            val options: BitmapFactory.Options
            inputStream = context.contentResolver.openInputStream(uri)
            if (inputStream != null) {
                options = BitmapFactory.Options()
                options.inJustDecodeBounds = true
                BitmapFactory.decodeStream(inputStream, null, options)
                inputStream.close()
                inputStream = context.contentResolver.openInputStream(uri)
                if (inputStream != null) {
                    options.inSampleSize = calculateInSampleSize(options.outWidth, options.outHeight, width, height)
                    options.inJustDecodeBounds = false
                    val bitmap = BitmapFactory.decodeStream(
                        inputStream, null, options)
                    inputStream.close()
                    return bitmap
                } }
            return null
        } catch (e: Exception) {
            return null
        } finally {
            inputStream?.close()
        }
    }

    private fun calculateInSampleSize(width: Int, height: Int, reqWidth: Int, reqHeight: Int): Int {
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2
            while (halfHeight / inSampleSize >= reqHeight &&
                halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            } }
        return inSampleSize
    }
}