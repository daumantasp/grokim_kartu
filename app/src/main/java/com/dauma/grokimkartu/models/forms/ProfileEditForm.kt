package com.dauma.grokimkartu.models.forms

import android.graphics.Bitmap
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.dauma.grokimkartu.BR

class ProfileEditForm: BaseObservable() {
    private var initialInstrument: String = ""
    private var initialDescription: String = ""
    private var initialCity: String = ""
    private var initialPhoto: Bitmap? = null

    @get:Bindable
    var nameMaxLength: Int = 30
        get() {
            return field
        }

    @get:Bindable
    var name: String = ""
        private set(value) {
            field = value
            notifyPropertyChanged(BR.name)
        }

    @get:Bindable
    var instrumentMaxLength: Int = 30
        get() {
            return field
        }

    @get:Bindable
    var instrument: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.instrument)
            notifyPropertyChanged(BR.changed)
        }

    @get:Bindable
    var descriptionMaxLength: Int = 300
        get() {
            return field
        }

    @get:Bindable
    var description: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.description)
            notifyPropertyChanged(BR.changed)
        }

    @get:Bindable
    var cityMaxLength: Int = 30
        get() {
            return field
        }

    @get:Bindable
    var city: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.city)
            notifyPropertyChanged(BR.changed)
        }

    @get:Bindable
    var photo: Bitmap? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.photo)
            notifyPropertyChanged(BR.changed)
        }

    fun setInitialValues(
        name: String,
        instrument: String,
        description: String,
        city: String
    ) {
        this.initialInstrument = instrument
        this.initialDescription = description
        this.initialCity = city
        this.name = name
        this.instrument = instrument
        this.description = description
        this.city = city
    }

    fun setInitialPhoto(photo: Bitmap?) {
        this.initialPhoto = photo
        this.photo = photo
    }

    @Bindable
    fun isChanged(): Boolean {
        return areValuesChanged() || isPhotoChanged()
    }

    fun areValuesChanged(): Boolean {
        return initialInstrument != instrument ||
                initialDescription != description ||
                initialCity != city
    }

    fun isPhotoChanged(): Boolean {
        return initialPhoto != photo
    }
}