package com.dauma.grokimkartu.repositories.thomanns.entities

import com.dauma.grokimkartu.general.CodeValue

class ThomannCity(
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