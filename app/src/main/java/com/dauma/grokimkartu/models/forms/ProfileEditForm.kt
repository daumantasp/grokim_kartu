package com.dauma.grokimkartu.models.forms

import android.graphics.Bitmap
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.dauma.grokimkartu.BR
import com.dauma.grokimkartu.repositories.profile.entities.ProfileCity
import com.dauma.grokimkartu.ui.main.adapters.CodeValue

class ProfileEditForm: BaseObservable() {
    private var initialInstrument: String = ""
    private var initialDescription: String = ""
    private var initialCity: ProfileCity = ProfileCity()
    private var initialPhoto: Bitmap? = null

    @get:Bindable
    var nameMaxLength: Int = 30
        get() {
            return field
        }

    @get:Bindable
    var name: String = ""
        private set(value) {
            field = value.take(nameMaxLength)
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
            field = value.take(instrumentMaxLength)
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
            field = value.take(descriptionMaxLength)
            notifyPropertyChanged(BR.description)
            notifyPropertyChanged(BR.changed)
        }

    @get:Bindable
    var cityMaxLength: Int = 30
        get() {
            return field
        }

    @get:Bindable
    var city: ProfileCity = ProfileCity()
        set(value) {
            field = value
            notifyPropertyChanged(BR.city)
            notifyPropertyChanged(BR.changed)
        }
    var pickableCities: List<ProfileCity> = listOf()
    var filteredPickableCities: List<ProfileCity> = listOf()

    @get:Bindable
    var photo: Bitmap? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.photo)
            notifyPropertyChanged(BR.changed)
        }

    fun setInitialValues(
        name: String?,
        instrument: String?,
        description: String?,
        city: ProfileCity?
    ) {
        this.initialInstrument = instrument ?: ""
        this.initialDescription = description ?: ""
        this.initialCity = city ?: ProfileCity()
        this.name = name ?: ""
        this.instrument = instrument ?: ""
        this.description = description ?: ""
        this.city = city ?: ProfileCity()
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
                initialCity.id != city.id
    }

    fun isPhotoChanged(): Boolean {
        return initialPhoto != photo
    }
}