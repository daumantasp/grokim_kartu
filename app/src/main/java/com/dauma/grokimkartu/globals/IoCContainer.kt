package com.dauma.grokimkartu.globals

import com.dauma.grokimkartu.viewmodels.players.PlayersViewModelFactory

interface IoCContainer {
    fun playersViewModelFactory(): PlayersViewModelFactory
}