package com.dauma.grokimkartu.models.forms

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.dauma.grokimkartu.BR

class DeleteUserForm: BaseObservable() {
    @get:Bindable
    var password: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.passwordValid)
        }

    // TODO: refactor, Duplicating in registrationForm
    @Bindable
    fun isPasswordValid(): Boolean {
        if (password.length < 6 || password.length > 35) {
            return false
        }
        return true
    }
}