package com.dauma.grokimkartu.general.utils.image

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import java.io.File

interface ImageUtils {
    fun getImageWithAuthority(context: Context, uri: Uri, width: Int, height: Int): Bitmap?
    fun getImageWithAuthority(filePath: String, width: Int, height: Int): Bitmap
    fun scaleImage(bitmap: Bitmap, width: Int, height: Int): Bitmap
    fun getSquaredBitmap(bitmap: Bitmap): Bitmap
    fun getRoundedCornerBitmap(bitmap: Bitmap, radius: Float): Bitmap
    fun getOvalBitmap(bitmap: Bitmap): Bitmap
    fun convertBitmapToByteArray(bitmap: Bitmap): ByteArray
    fun createUniqueImageFile(context: Context): File
}