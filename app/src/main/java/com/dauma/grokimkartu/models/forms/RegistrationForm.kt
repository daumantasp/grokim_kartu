package com.dauma.grokimkartu.models.forms

import android.util.Patterns
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import com.dauma.grokimkartu.BR

class RegistrationForm: BaseObservable() {
    private var name: String = ""
    private var email: String = ""
    private var password: String = ""
    private var passwordRepeat: String = ""
    private var formFields: MutableLiveData<List<String>> = MutableLiveData()

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
        return isEmailValid() && isNameValid() && isPasswordValid() && isPasswordRepeatValid()
    }

    private fun isEmailValid() : Boolean {
        if (Patterns.EMAIL_ADDRESS.matcher(email).matches() == false) {
            return false
        }
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isNameValid() : Boolean {
        if (name.isEmpty() || name.length > 20) {
            return false
        }
        return true
    }

    private fun isPasswordValid() : Boolean {
        if (password.length < 6 || password.length > 35) {
            return false
        }
        return true
    }

    private fun isPasswordRepeatValid() : Boolean {
        return password == passwordRepeat
    }
}