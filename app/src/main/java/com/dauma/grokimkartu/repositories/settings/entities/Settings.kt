package com.dauma.grokimkartu.repositories.settings.entities

import java.sql.Timestamp

class Settings(
    var name: String?,
    var email: String?,
    var createdAt: Timestamp?,
    var isVisible: Boolean?
)