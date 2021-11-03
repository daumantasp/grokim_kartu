package com.dauma.grokimkartu.general

import androidx.lifecycle.Observer
import com.dauma.grokimkartu.models.Event

class EventObserver<T>(private val onEventUnhandledContent: (T) -> Unit) : Observer<Event<T>> {
    override fun onChanged(event: Event<T>?) {
        event?.getContentIfNotHandled()?.let {
            onEventUnhandledContent(it)
        }
    }
}