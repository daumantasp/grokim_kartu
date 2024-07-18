package com.dauma.grokimkartu.models.forms

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.dauma.grokimkartu.BR
import com.dauma.grokimkartu.models.validators.email.EmailValidator
import com.dauma.grokimkartu.models.validators.password.PasswordValidator

class LoginForm(
    private val emailValidator: EmailValidator,
    private val passwordValidator: PasswordValidator
): BaseObservable() {

    private var email: String = ""
    private var password: String = ""

    fun getEmail(): String {
        return email
    }

    fun setEmail(email: String) {
        this.email = email
        notifyPropertyChanged(BR.valid)
    }

    fun getPassword(): String {
        return password
    }

    fun setPassword(password: String) {
        this.password = password
        notifyPropertyChanged(BR.valid)
    }

    @Bindable
    fun isValid() : Boolean {
        return emailValidator.isValid(email) && passwordValidator.isValid(password)
    }
}