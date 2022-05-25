package com.dauma.grokimkartu.data.cities

class CityDaoResponseStatus (
    val isSuccessful: Boolean,
    val error: Errors?
) {
    enum class Errors {
        UNKNOWN
    }
}