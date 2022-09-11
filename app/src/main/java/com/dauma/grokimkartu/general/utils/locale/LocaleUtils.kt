package com.dauma.grokimkartu.general.utils.locale

import android.content.Context

interface LocaleUtils {
    fun getCurrentLanguage(context: Context) : Language
    fun setLanguage(context: Context, language: Language)
}