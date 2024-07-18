package com.dauma.grokimkartu.models.forms

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.dauma.grokimkartu.BR
import com.dauma.grokimkartu.models.validators.email.EmailValidator
import com.dauma.grokimkartu.models.validators.name.NameValidator
import com.dauma.grokimkartu.models.validators.password.PasswordValidator

class RegistrationForm(
    private val nameValidator: NameValidator,
    private val passwordValidator: PasswordValidator,
    private val emailValidator: EmailValidator
): BaseObservable() {
    private var name: String = ""
    private var email: String = ""
    private var password: String = ""
    private var passwordRepeat: String = ""

    fun getName(): String {
        return name
    }

    fun setName(name: String) {
        this.name = name
        notifyPropertyChanged(BR.valid)
    }

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

    fun getPasswordRepeat(): String {
        return password
    }

    fun setPasswordRepeat(password: String) {
        this.passwordRepeat = password
        notifyPropertyChanged(BR.valid)
    }

    @Bindable
    fun isValid() : Boolean {
        return emailValidator.isValid(email)
                && nameValidator.isValid(name)
                && passwordValidator.isValid(password)
                && password == passwordRepeat
    }
}