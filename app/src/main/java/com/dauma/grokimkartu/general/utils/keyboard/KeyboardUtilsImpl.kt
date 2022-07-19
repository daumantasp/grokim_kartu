package com.dauma.grokimkartu.general.utils.keyboard

import android.app.Activity
import android.graphics.Rect
import android.os.Build
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager

class KeyboardUtilsImpl : KeyboardUtils {
    private var listeners: MutableMap<String, DecorViewToListener> = mutableMapOf()
    val SOFT_KEYBOARD_HEIGHT_PX = 100 // value retrieved from experimentation

    override fun registerListener(id: String, decorView: View, listener: (Boolean, Int) -> Unit) {
        listeners[id]?.let {
            unregisterListener(id)
        }

        val rootView = decorView.findViewById<View>(android.R.id.content)
        val layoutListener = object : ViewTreeObserver.OnGlobalLayoutListener {
            private var isOpened = false
            private var keyboardHeight: Int = -1

            override fun onGlobalLayout() {
                val rect = Rect()
                rootView.getWindowVisibleDisplayFrame(rect)
                val displayMetrics = rootView.resources.displayMetrics
                var screenHeightIncludingStatusBar = displayMetrics.heightPixels
                val visibleRootViewHeightIncludingStatusBar = rect.bottom
                if (Build.VERSION.SDK_INT >= 28) {
                    //For some reason id device has a cutout, displayMetrics.heightPixels returns height without top inset
                    //but if the device does not have a cutout, the returned size includes top inset -- might be android bug
                    //As rect.bottom includes top inset, we need to add it here
                    decorView.rootWindowInsets.displayCutout?.let {
                        screenHeightIncludingStatusBar += it.safeInsetTop
                    }
                }
                val heightDiff = screenHeightIncludingStatusBar - visibleRootViewHeightIncludingStatusBar

                if (heightDiff > SOFT_KEYBOARD_HEIGHT_PX) {
                    if (isOpened == false || keyboardHeight != heightDiff) {
                        keyboardHeight = heightDiff
                        isOpened = true
                        listener(isOpened, keyboardHeight)
                    }
                } else if (isOpened == true) {
                    isOpened = false
                    listener(isOpened, keyboardHeight)
                }
            }
        }
        rootView.viewTreeObserver.addOnGlobalLayoutListener(layoutListener)
        listeners.put(id, DecorViewToListener(rootView, layoutListener))
    }

    override fun unregisterListener(id: String) {
        listeners.remove(id)?.let {
            it.decorView.viewTreeObserver.removeOnGlobalLayoutListener(it.listener)
        }
    }

    // Read more https://stackoverflow.com/questions/1109022/how-do-you-close-hide-the-android-soft-keyboard-programmatically
    override fun hideKeyboard(view: View) {
        val inputMethodManager = view.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun showKeyboard(view: View) {
        val inputMethodManager = view.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    private data class DecorViewToListener(val decorView: View, val listener: ViewTreeObserver.OnGlobalLayoutListener)
}