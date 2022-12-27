package com.dauma.grokimkartu.general.thememodemanager

class ThemeModeException(error: ThemeModeErrors)
    : Exception(error.toString()) {}


enum class ThemeModeErrors {
    THEME_MANAGER_IS_NOT_SET
}