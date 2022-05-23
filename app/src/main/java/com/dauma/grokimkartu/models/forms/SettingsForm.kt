package com.dauma.grokimkartu.models.forms

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.dauma.grokimkartu.BR

class SettingsForm: BaseObservable() {
    private var initialIsVisible: Boolean = false

    @get:Bindable
    var email: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.email)
        }

    @get:Bindable
    var isVisible: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.visible)
            notifyPropertyChanged(BR.changed)
        }

    fun setInitialValues(email: String?, isVisible: Boolean?) {
        this.email = email ?: ""
        this.isVisible = isVisible ?: false
        this.initialIsVisible = isVisible ?: false
    }

    @Bindable
    fun isChanged(): Boolean {
        return initialIsVisible != isVisible
    }
}