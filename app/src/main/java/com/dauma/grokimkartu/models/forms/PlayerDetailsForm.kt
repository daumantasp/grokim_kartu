package com.dauma.grokimkartu.models.forms

import android.graphics.Bitmap
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.dauma.grokimkartu.BR

class PlayerDetailsForm: BaseObservable() {
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
    var instrument: String = ""
        private set(value) {
            field = value
            notifyPropertyChanged(BR.instrument)
        }

    @get:Bindable
    var photo: Bitmap? = null
        private set(value) {
            field = value
            notifyPropertyChanged(BR.photo)
        }

    fun setInitialValues(userId: String, name: String, instrument: String) {
        this.userId = userId
        this.name = name
        this.instrument = instrument
    }

    fun setInitialPhoto(photo: Bitmap) {
        this.photo = photo
    }
}