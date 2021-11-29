package com.dauma.grokimkartu.models.forms

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dauma.grokimkartu.BR

class SettingsForm: BaseObservable() {
    private var password: String = ""
    private var formFields: MutableLiveData<List<String>> = MutableLiveData()

    fun getPassword(): String {
        return password
    }

    fun setPassword(password: String) {
        this.password = password
        notifyPropertyChanged(BR.passwordValid)
    }

    fun getFormFields(): LiveData<List<String>> {
        return formFields
    }

    fun onClick() {
        if (isPasswordValid()) {
            formFields.value = listOf(password)
        }
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