package com.dauma.grokimkartu.models.forms

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.dauma.grokimkartu.BR
import com.dauma.grokimkartu.models.validators.email.EmailValidator

class ForgotPasswordForm(
    private val emailValidator: EmailValidator
): BaseObservable() {
    private var email: String = ""

    fun getEmail(): String {
        return email
    }

    fun setEmail(email: String) {
        this.email = email
        notifyPropertyChanged(BR.emailValid)
    }

    @Bindable
    fun isEmailValid() : Boolean = emailValidator.isValid(email)
}