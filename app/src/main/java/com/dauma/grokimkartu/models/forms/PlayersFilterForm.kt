package com.dauma.grokimkartu.models.forms

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.dauma.grokimkartu.BR
import com.dauma.grokimkartu.repositories.players.entities.PlayerCity
import com.dauma.grokimkartu.repositories.players.entities.PlayerInstrument

class PlayersFilterForm: BaseObservable() {
    private var initialCity: PlayerCity = PlayerCity()
    private var initialInstrument: PlayerInstrument = PlayerInstrument()
    private var initialText: String = ""

    @get:Bindable
    var cityMaxLength: Int = 30
        get() {
            return field
        }

    @get:Bindable
    var city: PlayerCity = PlayerCity()
        set(value) {
            field = value
            notifyPropertyChanged(BR.city)
            notifyPropertyChanged(BR.changed)
            notifyPropertyChanged(BR.initialEmpty)
        }
    var pickableCities: List<PlayerCity> = listOf()
    var filteredPickableCities: List<PlayerCity> = listOf()

    @get:Bindable
    var instrumentMaxLength: Int = 30
        get() {
            return field
        }

    @get:Bindable
    var instrument: PlayerInstrument = PlayerInstrument()
        set(value) {
            field = value
            notifyPropertyChanged(BR.instrument)
            notifyPropertyChanged(BR.changed)
            notifyPropertyChanged(BR.initialEmpty)
        }
    var pickableInstruments: List<PlayerInstrument> = listOf()
    var filteredPickableInstruments: List<PlayerInstrument> = listOf()

    @get:Bindable
    var textMaxLength: Int = 30
        get() {
            return field
        }

    @get:Bindable
    var text: String = ""
        set(value) {
            field = value.take(textMaxLength)
            notifyPropertyChanged(BR.text)
            notifyPropertyChanged(BR.changed)
            notifyPropertyChanged(BR.initialEmpty)
        }

    fun setInitialValues(
        city: PlayerCity?,
        instrument: PlayerInstrument?,
        text: String?
    ) {
        this.initialCity = city ?: PlayerCity()
        this.initialInstrument = instrument ?: PlayerInstrument()
        this.initialText = text ?: ""
        this.instrument = instrument ?: PlayerInstrument()
        this.city = city ?: PlayerCity()
        this.text = text ?: ""
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