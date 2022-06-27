package com.dauma.grokimkartu.general.utils.image

import android.content.Context
import android.graphics.*
import android.net.Uri
import android.os.Environment
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*


class ImageUtilsImpl : ImageUtils {
    override fun getImageWithAuthority(context: Context, uri: Uri, width: Int, height: Int): Bitmap? {
        return decodeUriStreamToSize(context, uri, width, height)
    }

    override fun getImageWithAuthority(filePath: String, width: Int, height: Int
    ): Bitmap {
        return decodeFileToSize(filePath, width, height)
    }

    override fun scaleImage(bitmap: Bitmap, width: Int, height: Int): Bitmap {
        val aspectRatio = bitmap.width.toFloat() / bitmap.height.toFloat()
        val ratioHeight = Math.round(width / aspectRatio)
        return Bitmap.createScaledBitmap(bitmap, width, ratioHeight, false)
    }

    override fun getSquaredBitmap(bitmap: Bitmap): Bitmap {
        val isWidthLarger = bitmap.width > bitmap.height
        val startX: Int
        val startY: Int
        val length: Int
        if (isWidthLarger == true) {
            startX = (bitmap.width - bitmap.height) / 2
            startY = 0
            length = bitmap.height
        } else {
            startX = 0
            startY = (bitmap.height - bitmap.width) / 2
            length = bitmap.width
        }

        return Bitmap.createBitmap(bitmap, startX, startY, length, length)
    }

    override fun getRoundedCornerBitmap(bitmap: Bitmap, radius: Float): Bitmap {
        val output = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)

        val color = -0xbdbdbe
        val paint = Paint()
        val rect = Rect(0, 0, bitmap.width, bitmap.height)
        val rectF = RectF(rect)
        val roundPx = radius

        paint.setAntiAlias(true)
        canvas.drawARGB(0, 0, 0, 0)
        paint.setColor(color)
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint)

        paint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.SRC_IN))
        canvas.drawBitmap(bitmap, rect, rect, paint)

        return output
    }

    override fun getOvalBitmap(bitmap: Bitmap): Bitmap {
        val squaredBitmap = getSquaredBitmap(bitmap)
        val radius = squaredBitmap.width / 2.0f
        val circularBitmap = getRoundedCornerBitmap(squaredBitmap, radius)
        return circularBitmap
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

    private fun decodeFileToSize(filePath: String, width: Int, height: Int): Bitmap {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(filePath, options)
        options.inSampleSize = calculateInSampleSize(options.outWidth, options.outHeight, width, height)
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeFile(filePath, options)
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

    override fun convertBitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val byteArray = stream.toByteArray()
        return byteArray
    }

    override fun createUniqueImageFile(context: Context): File {
        val timeStamp = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
        val filename = "Grokim_${timeStamp}_"
        val filesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(filename, ".jpg", filesDir)
    }
}