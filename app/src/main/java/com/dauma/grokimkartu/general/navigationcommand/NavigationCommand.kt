package com.dauma.grokimkartu.general.navigationcommand

import androidx.navigation.NavDirections

sealed class NavigationCommand {
    data class ToDirection(val directions: NavDirections) : NavigationCommand()
    object Back : NavigationCommand()
    object CloseApp : NavigationCommand()
}

