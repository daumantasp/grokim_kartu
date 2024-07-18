package com.dauma.grokimkartu.models.forms

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import com.dauma.grokimkartu.BR
import com.dauma.grokimkartu.repositories.thomanns.entities.ThomannCity

class ThomannEditForm: BaseObservable() {
    private var initialCity: ThomannCity = ThomannCity()
    private var initialValidUntil: String = ""
    private var formFields: MutableLiveData<List<String>> = MutableLiveData()

    @get:Bindable
    var cityMaxLength: Int = 30
        get() {
            return field
        }

    @get:Bindable
    var city: ThomannCity = ThomannCity()
        set(value) {
            field = value
            notifyPropertyChanged(BR.city)
            notifyPropertyChanged(BR.changed)
        }
    var pickableCities: List<ThomannCity> = listOf()
    var filteredPickableCities: List<ThomannCity> = listOf()

    @get:Bindable
    var validUntil: String = ""
    set(value) {
        field = value
        notifyPropertyChanged(BR.validUntil)
        notifyPropertyChanged(BR.changed)
    }

    fun setInitialValues(
        city: ThomannCity?,
        validUntil: String?
    ) {
        this.initialCity = city ?: ThomannCity()
        this.initialValidUntil = validUntil ?: ""
        this.city = city ?: ThomannCity()
        this.validUntil = validUntil ?: ""
    }

    @Bindable
    fun isChanged(): Boolean {
        return initialCity.id != city.id || initialValidUntil != validUntil
    }
}