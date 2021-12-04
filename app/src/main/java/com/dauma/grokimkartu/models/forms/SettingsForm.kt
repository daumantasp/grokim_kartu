package com.dauma.grokimkartu.models.forms

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.dauma.grokimkartu.BR

class SettingsForm: BaseObservable() {
    private var initialIsVisible: Boolean = false

    @get:Bindable
    var name: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.name)
        }

    @get:Bindable
    var isVisible: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.visible)
            notifyPropertyChanged(BR.changed)
        }

    @get:Bindable
    var password: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.passwordValid)
        }

    fun setInitialValues(name: String, isVisible: Boolean) {
        this.name = name
        this.isVisible = isVisible
        this.initialIsVisible = isVisible
    }

    // TODO: refactor, Duplicating in registrationForm
    @Bindable
    fun isPasswordValid(): Boolean {
        if (password.length < 6 || password.length > 35) {
            return false
        }
        return true
    }

    @Bindable
    fun isChanged(): Boolean {
        return initialIsVisible != isVisible
    }
}