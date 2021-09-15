package com.dauma.grokimkartu.globals

import com.dauma.grokimkartu.viewmodels.players.PlayersViewModelFactory
import com.dauma.grokimkartu.viewmodels.registration.RegistrationViewModelFactory

interface IoCContainer {
    fun playersViewModelFactory(): PlayersViewModelFactory
    fun registrationViewModelFactory(): RegistrationViewModelFactory
}