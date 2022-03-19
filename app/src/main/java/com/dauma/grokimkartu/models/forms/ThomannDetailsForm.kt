package com.dauma.grokimkartu.models.forms

import android.graphics.Bitmap
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.dauma.grokimkartu.BR

class ThomannDetailsForm: BaseObservable() {
    private var _isLocked: Boolean = false

    @get:Bindable
    var userId: String = ""
        private set(value) {
            field = value
            notifyPropertyChanged(BR.userId)
        }

    @get:Bindable
    var name: String = ""
        private set(value) {
            field = value
            notifyPropertyChanged(BR.name)
        }

    @get:Bindable
    var city: String = ""
        private set(value) {
            field = value
            notifyPropertyChanged(BR.city)
        }

    fun isLocked() : String {
        return if (_isLocked == true) "*Užrakinta*" else "*Atrakinta*"
    }

    @get:Bindable
    var creationDate: String = ""
        private set(value) {
            field = value
            notifyPropertyChanged(BR.creationDate)
        }

    @get:Bindable
    var validUntil: String = ""
        private set(value) {
            field = value
            notifyPropertyChanged(BR.validUntil)
        }

    @get:Bindable
    var isJoinPossible: Boolean = false
        private set(value) {
            field = value
            notifyPropertyChanged(BR.joinPossible)
        }

    @get:Bindable
    var photo: Bitmap? = null
        private set(value) {
            field = value
            notifyPropertyChanged(BR.photo)
        }

    fun setInitialValues(
        userId: String,
        name: String,
        city: String,
        isLocked: Boolean,
        creationDate: String,
        validUntil: String,
        isJoinPossible: Boolean
    ) {
        this.userId = userId
        this.name = name
        this.city = city
        this._isLocked = isLocked
        this.creationDate = creationDate
        this.validUntil = validUntil
        this.isJoinPossible = isJoinPossible
    }

    fun setInitialPhoto(photo: Bitmap) {
        this.photo = photo
    }
}