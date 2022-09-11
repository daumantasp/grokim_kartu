package com.dauma.grokimkartu.general.utils.locale

import android.content.Context
import java.util.*

class LocaleUtilsImpl() : LocaleUtils {
    override fun getCurrentLanguage(context: Context) : Language {
        val locale = context.resources.configuration.locale
        val language = getLanguage(locale.language)
        return language
    }

    override fun setLanguage(context: Context, language: Language) {
        val languageCode = getLanguageCode(language)
        val locale = Locale(languageCode)
        val resources = context.resources
        val configuration = resources.configuration
        configuration.locale = locale
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }

    private fun getLanguage(languageCode: String): Language {
        return when (languageCode) {
            "lt" -> Language.LT
            else -> Language.EN
        }
    }

    private fun getLanguageCode(language: Language): String {
        return when (language) {
            Language.LT -> "lt"
            Language.EN -> "en"
        }
    }
}