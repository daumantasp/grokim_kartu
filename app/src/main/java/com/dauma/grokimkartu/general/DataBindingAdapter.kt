package com.dauma.grokimkartu.general

import android.graphics.Bitmap
import android.util.Log
import android.widget.ImageView
import androidx.databinding.BindingAdapter

// READ https://medium.com/@hkhcheung/defining-android-binding-adapter-in-kotlin-b08e82116704
// https://stackoverflow.com/questions/35304185/databinding-an-in-memory-bitmap-to-an-imageview
object DataBindingAdapter {
    @BindingAdapter("android:src")
    @JvmStatic
    fun bindSrc(view: ImageView, bitmap: Bitmap?) {
        Log.d("DataBindingAdapter", "Binding bitmap not null = ${bitmap != null}")
        view.setImageBitmap(bitmap)
    }
}