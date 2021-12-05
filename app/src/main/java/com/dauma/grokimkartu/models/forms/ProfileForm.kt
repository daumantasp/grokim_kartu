package com.dauma.grokimkartu.models.forms

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dauma.grokimkartu.BR

class ProfileForm: BaseObservable() {
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
}