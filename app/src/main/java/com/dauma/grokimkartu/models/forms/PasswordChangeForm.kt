package com.dauma.grokimkartu.models.forms

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dauma.grokimkartu.BR

class PasswordChangeForm: BaseObservable() {
    private var oldPassword: String = ""
    private var newPassword: String = ""
    private var repeatPassword: String = ""
    private var formFields: MutableLiveData<List<String>> = MutableLiveData()

    fun getOldPassword(): String {
        return oldPassword
    }

    fun setOldPassword(password: String) {
        this.oldPassword = password
        notifyPropertyChanged(BR.valid)
    }

    fun getNewPassword(): String {
        return newPassword
    }

    fun setNewPassword(password: String) {
        this.newPassword = password
        notifyPropertyChanged(BR.passwordValid)
    }

    fun getRepeatPassword(): String {
        return repeatPassword
    }

    fun setRepeatPassword(password: String) {
        this.repeatPassword = password
        notifyPropertyChanged(BR.valid)
    }

    fun getFormFields(): LiveData<List<String>> {
        return formFields
    }

    fun onClick() {
        if (isValid()) {
            formFields.value = listOf(oldPassword, newPassword, repeatPassword)
        }
    }

    // TODO: refactor, Duplicating in registrationForm
    @Bindable
    fun isValid() : Boolean {
        return isNewPasswordValid() &&
                oldPassword.isEmpty() == false &&
                newPassword.isEmpty() == false
    }

    private fun isNewPasswordValid() : Boolean {
        if (newPassword.length < 6 || newPassword.length > 35) {
            return false
        }
        return true
    }

    fun isOldAndNewPasswordSame() : Boolean {
        return newPassword == oldPassword
    }

    fun isNewAndRepeatPasswordSame() : Boolean {
        return newPassword == repeatPassword
    }
}