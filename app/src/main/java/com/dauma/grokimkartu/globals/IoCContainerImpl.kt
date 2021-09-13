package com.dauma.grokimkartu.globals

import com.dauma.grokimkartu.viewmodels.players.PlayersViewModelFactory

class IoCContainerImpl : IoCContainer {
    override fun playersViewModelFactory(): PlayersViewModelFactory {
        return PlayersViewModelFactory(ComponentProvider.playersRepository())
    }
}