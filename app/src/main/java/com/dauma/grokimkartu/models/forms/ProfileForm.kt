package com.dauma.grokimkartu.models.forms

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dauma.grokimkartu.BR

class ProfileForm: BaseObservable() {
    private var initialInstrument: String = ""
    private var initialDescription: String = ""

    @get:Bindable
    var instrument: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.instrument)
            notifyPropertyChanged(BR.changed)
        }

    @get:Bindable
    var description: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.description)
            notifyPropertyChanged(BR.changed)
        }

    fun setInitialValues(instrument: String, description: String) {
        this.instrument = instrument
        this.description = description
        this.initialInstrument = instrument
        this.initialDescription = description
    }

    @Bindable
    fun isChanged(): Boolean {
        return initialInstrument != instrument || initialDescription != description
    }
}