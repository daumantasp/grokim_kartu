package com.dauma.grokimkartu.viewmodels.main

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.general.event.Event
import com.dauma.grokimkartu.general.utils.locale.Language
import com.dauma.grokimkartu.general.utils.locale.LocaleUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LanguagesViewModel @Inject constructor(
    private val localeUtils: LocaleUtils
) : ViewModel() {
    private val _navigateBack = MutableLiveData<Event<String>>()
    private val _language = MutableLiveData<Event<Language>>()
    val navigateBack: LiveData<Event<String>> = _navigateBack
    val language: LiveData<Event<Language>> = _language

    companion object {
        private val TAG = "LanguagesViewModel"
    }

    fun viewIsReady(context: Context) {
        selectLanguage(context)
    }

    fun backClicked() {
        _navigateBack.value = Event("")
    }

    fun languageClicked(context: Context, language: Language) {
        localeUtils.setLanguage(context, language)
        selectLanguage(context)
        backClicked()
    }

    private fun selectLanguage(context: Context) {
        val currentLanguage = localeUtils.getCurrentLanguage(context)
        _language.value = Event(currentLanguage)
    }
}