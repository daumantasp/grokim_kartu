package com.dauma.grokimkartu.models.forms

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.dauma.grokimkartu.BR
import com.dauma.grokimkartu.models.validators.newpassword.NewPasswordValidator

class PasswordChangeForm(
    private val newPasswordValidator: NewPasswordValidator
): BaseObservable() {
    private var oldPassword: String = ""
    private var newPassword: String = ""
    private var repeatPassword: String = ""

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

    @Bindable
    fun isValid() : Boolean = newPasswordValidator.isValid(oldPassword, repeatPassword, newPassword)

    fun isOldAndNewPasswordSame() : Boolean = newPassword == oldPassword

    fun isNewAndRepeatPasswordSame() : Boolean = newPassword == repeatPassword
}