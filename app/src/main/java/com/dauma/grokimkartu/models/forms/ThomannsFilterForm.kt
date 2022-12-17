package com.dauma.grokimkartu.models.forms

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.dauma.grokimkartu.BR
import com.dauma.grokimkartu.repositories.thomanns.entities.ThomannCity

class ThomannsFilterForm: BaseObservable() {
    private var initialCity: ThomannCity = ThomannCity()
    private var initialValidUntil: String = ""
    private var initialShowOnlyUnlocked: Boolean = false

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
            notifyPropertyChanged(BR.initialEmpty)
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

    @Bindable
    var showOnlyUnlocked: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.showOnlyUnlocked)
            notifyPropertyChanged(BR.changed)
        }

    fun setInitialValues(
        city: ThomannCity?,
        validUntil: String?,
        showOnlyUnlocked: Boolean
    ) {
        this.initialCity = city ?: ThomannCity()
        this.initialValidUntil = validUntil ?: ""
        this.initialShowOnlyUnlocked = showOnlyUnlocked
        this.city = city ?: ThomannCity()
        this.validUntil = validUntil ?: ""
        this.showOnlyUnlocked = showOnlyUnlocked
    }

    @Bindable
    fun isChanged(): Boolean {
        return initialCity.id != city.id ||
                initialValidUntil != validUntil ||
                initialShowOnlyUnlocked != showOnlyUnlocked
    }

    @Bindable
    fun isInitialEmpty(): Boolean {
        return initialCity.id == null &&
                initialValidUntil == "" &&
                initialShowOnlyUnlocked == false
    }
}