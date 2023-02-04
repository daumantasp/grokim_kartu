package com.dauma.grokimkartu.viewmodels.main

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.general.event.Event
import com.dauma.grokimkartu.general.navigationcommand.NavigationCommand
import com.dauma.grokimkartu.general.utils.locale.Language
import com.dauma.grokimkartu.general.utils.locale.LocaleUtils
import com.dauma.grokimkartu.general.utils.sharedstorage.SharedStorageUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LanguagesViewModel @Inject constructor(
    private val localeUtils: LocaleUtils,
    private val sharedStorageUtils: SharedStorageUtils
) : ViewModel() {
    private val _navigation = MutableLiveData<Event<NavigationCommand>>()
    private val _language = MutableLiveData<Event<Language>>()
    val navigation: LiveData<Event<NavigationCommand>> = _navigation
    val language: LiveData<Event<Language>> = _language

    companion object {
        private val TAG = "LanguagesViewModel"
        const val CURRENT_LANGUAGE_KEY = "CURRENT_LANGUAGE_KEY"
    }

    fun viewIsReady(context: Context) {
        selectLanguage(context)
    }

    fun backClicked() {
        _navigation.value = Event(NavigationCommand.Back)
    }

    fun languageClicked(context: Context, language: Language) {
        localeUtils.setLanguage(context, language)
        sharedStorageUtils.save(CURRENT_LANGUAGE_KEY, language.toString())
        selectLanguage(context)
        backClicked()
    }

    private fun selectLanguage(context: Context) {
        val currentLanguage = localeUtils.getCurrentLanguage(context)
        _language.value = Event(currentLanguage)
    }
}