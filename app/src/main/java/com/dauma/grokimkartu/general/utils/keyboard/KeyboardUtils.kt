package com.dauma.grokimkartu.general.utils.keyboard

import android.view.View

interface KeyboardUtils {
    fun registerListener(id: String, decorView: View, listener: (Boolean, Int) -> Unit)
    fun unregisterListener(id: String)
    fun hideKeyboard(view: View)
    fun showKeyboard(view: View)
}