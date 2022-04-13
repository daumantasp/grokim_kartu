package com.dauma.grokimkartu.models.forms

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dauma.grokimkartu.BR

class ThomannEditForm: BaseObservable() {
    private var initialCity: String = ""
    private var initialValidUntil: String = ""
    private var formFields: MutableLiveData<List<String>> = MutableLiveData()

    @get:Bindable
    var cityMaxLength: Int = 30
        get() {
            return field
        }

    @get:Bindable
    var city: String = ""
        set(value) {
            field = value.take(cityMaxLength)
            notifyPropertyChanged(BR.city)
            notifyPropertyChanged(BR.changed)
        }

    @get:Bindable
    var validUntil: String = ""
    set(value) {
        field = value
        notifyPropertyChanged(BR.validUntil)
        notifyPropertyChanged(BR.changed)
    }

    fun setInitialValues(
        city: String,
        validUntil: String
    ) {
        this.initialCity = city
        this.initialValidUntil = validUntil
        this.city = city
        this.validUntil = validUntil
    }

    fun getFormFields(): LiveData<List<String>> {
        return formFields
    }

    fun onClick() {
        if (isChanged()) {
            formFields.value = listOf(city, validUntil)
        }
    }

    @Bindable
    fun isChanged(): Boolean {
        return initialCity != city || initialValidUntil != validUntil
    }
}