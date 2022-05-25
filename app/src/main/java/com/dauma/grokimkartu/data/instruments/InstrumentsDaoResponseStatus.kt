package com.dauma.grokimkartu.data.instruments

class InstrumentsDaoResponseStatus (
    val isSuccessful: Boolean,
    val error: Errors?
) {
    enum class Errors {
        UNKNOWN
    }
}