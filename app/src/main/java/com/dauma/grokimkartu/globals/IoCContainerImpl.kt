package com.dauma.grokimkartu.globals

import com.dauma.grokimkartu.viewmodels.players.PlayersViewModelFactory
import com.dauma.grokimkartu.viewmodels.registration.RegistrationViewModelFactory

class IoCContainerImpl : IoCContainer {
    override fun playersViewModelFactory(): PlayersViewModelFactory {
        return PlayersViewModelFactory(ComponentProvider.playersRepository())
    }

    override fun registrationViewModelFactory(): RegistrationViewModelFactory {
        return RegistrationViewModelFactory()
    }
}