package com.dauma.grokimkartu.models.forms

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

    fun setInitialValues(userId: String, name: String, instrument: String) {
        this.userId = userId
        this.name = name
        this.instrument = instrument
    }
}