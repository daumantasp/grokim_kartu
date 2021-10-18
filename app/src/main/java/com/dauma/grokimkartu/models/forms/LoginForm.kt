package com.dauma.grokimkartu.models.forms

import android.util.Patterns
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dauma.grokimkartu.BR

class LoginForm: BaseObservable() {
    private var email: String = ""
    private var password: String = ""
    private var formFields: MutableLiveData<List<String>> = MutableLiveData()

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

    fun getFormFields(): LiveData<List<String>> {
        return formFields
    }

    fun onClick() {
        if (isValid()) {
            formFields.value = listOf(email, password)
        }
    }

    @Bindable
    fun isValid() : Boolean {
        return isEmailValid() && isPasswordValid()
    }

    // TODO: refactor, Duplicating in registrationForm
    private fun isEmailValid() : Boolean {
        if (Patterns.EMAIL_ADDRESS.matcher(email).matches() == false) {
            return false
        }
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // TODO: refactor, Duplicating in registrationForm
    private fun isPasswordValid() : Boolean {
        if (password.length < 6 || password.length > 35) {
            return false
        }
        return true
    }
}