package com.dauma.grokimkartu.models.forms

import android.util.Patterns
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dauma.grokimkartu.BR

class ForgotPasswordForm: BaseObservable() {
    private var email: String = ""
    private var formFields: MutableLiveData<List<String>> = MutableLiveData()

    fun getEmail(): String {
        return email
    }

    fun setEmail(email: String) {
        this.email = email
        notifyPropertyChanged(BR.emailValid)
    }

    fun getFormFields(): LiveData<List<String>> {
        return formFields
    }

    fun onClick() {
        if (isEmailValid()) {
            formFields.value = listOf(email)
        }
    }

    // TODO: refactor, Duplicating in registrationForm
    @Bindable
    fun isEmailValid() : Boolean {
        if (Patterns.EMAIL_ADDRESS.matcher(email).matches() == false) {
            return false
        }
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}