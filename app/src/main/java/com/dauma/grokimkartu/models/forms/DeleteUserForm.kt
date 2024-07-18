package com.dauma.grokimkartu.models.forms

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.dauma.grokimkartu.BR
import com.dauma.grokimkartu.models.validators.password.PasswordValidator

class DeleteUserForm(
    private val passwordValidator: PasswordValidator
): BaseObservable() {
    @get:Bindable
    var password: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.passwordValid)
        }

    @Bindable
    fun isPasswordValid(): Boolean = passwordValidator.isValid(password)
}