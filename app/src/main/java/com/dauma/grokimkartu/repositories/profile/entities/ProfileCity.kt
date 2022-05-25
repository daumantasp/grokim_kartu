package com.dauma.grokimkartu.repositories.profile.entities

import com.dauma.grokimkartu.ui.main.adapters.CodeValue

class ProfileCity(
    var id: Int?,
    var name: String?
) {
    constructor() : this(null, null)

    fun toCodeValue() : CodeValue {
        return CodeValue(
            code = id.toString(),
            value = name.toString()
        )
    }
}