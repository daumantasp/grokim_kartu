package com.dauma.grokimkartu.models.forms

import android.graphics.Bitmap
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.dauma.grokimkartu.BR

//READ https://developer.android.com/topic/libraries/data-binding/two-way
class ProfileForm(): BaseObservable() {
    @get:Bindable
    var name: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.name)
        }

    @get:Bindable
    var instrument: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.instrument)
        }
    @get:Bindable
    var description: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.description)
        }

    @get:Bindable
    var city: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.city)
        }

    @get:Bindable
    var photo: Bitmap? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.photo)
        }

    fun setValues(
        name: String?,
        instrument: String?,
        description: String?,
        city: String?
    ) {
        this.name = name ?: ""
        this.instrument = instrument ?: ""
        this.description = description ?: ""
        this.city = city ?: ""
    }
}