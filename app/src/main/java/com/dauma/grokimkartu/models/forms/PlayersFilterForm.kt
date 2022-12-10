package com.dauma.grokimkartu.models.forms

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.dauma.grokimkartu.BR
import com.dauma.grokimkartu.repositories.profile.entities.ProfileCity
import com.dauma.grokimkartu.repositories.profile.entities.ProfileInstrument

class PlayersFilterForm: BaseObservable() {
    private var initialCity: ProfileCity = ProfileCity()
    private var initialInstrument: ProfileInstrument = ProfileInstrument()
    private var initialText: String = ""

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
    var instrumentMaxLength: Int = 30
        get() {
            return field
        }

    @get:Bindable
    var instrument: ProfileInstrument = ProfileInstrument()
        set(value) {
            field = value
            notifyPropertyChanged(BR.instrument)
            notifyPropertyChanged(BR.changed)
        }
    var pickableInstruments: List<ProfileInstrument> = listOf()
    var filteredPickableInstruments: List<ProfileInstrument> = listOf()

    @get:Bindable
    var textMaxLength: Int = 30
        get() {
            return field
        }

    @get:Bindable
    var text: String = ""
        set(value) {
            field = value.take(textMaxLength)
            notifyPropertyChanged(BR.description)
            notifyPropertyChanged(BR.changed)
        }

    fun setInitialValues(
        instrument: ProfileInstrument?,
        text: String?,
        city: ProfileCity?
    ) {
        this.initialInstrument = instrument ?: ProfileInstrument()
        this.initialText = text ?: ""
        this.initialCity = city ?: ProfileCity()
        this.instrument = instrument ?: ProfileInstrument()
        this.text = text ?: ""
        this.city = city ?: ProfileCity()
    }

    @Bindable
    fun isChanged(): Boolean {
        return initialInstrument.id != instrument.id ||
                initialText != text ||
                initialCity.id != city.id
    }

    @Bindable
    fun isInitialEmpty(): Boolean {
        return initialInstrument.id == null &&
                initialCity.id == null &&
                initialText == ""
    }
}