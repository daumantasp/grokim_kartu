package com.dauma.grokimkartu.general.thememodemanager

import androidx.annotation.StyleRes

interface ThemeManager {
    val uiMode: Int
    fun setTheme(@StyleRes resId: Int)
}