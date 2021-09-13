package com.dauma.grokimkartu.globals

import com.dauma.grokimkartu.data.players.FakePlayersDaoImpl
import com.dauma.grokimkartu.repositories.players.PlayersRepository
import com.dauma.grokimkartu.repositories.players.PlayersRepositoryImpl

object ComponentProvider {
    @Volatile private var playersRepository: PlayersRepository? = null
    val ioCContainer : IoCContainer = IoCContainerImpl()

    fun playersRepository() : PlayersRepository {
        if (playersRepository == null) {
            synchronized(this) {
                if (playersRepository == null) {
                    playersRepository = PlayersRepositoryImpl(FakePlayersDaoImpl())
                }
            }
        }
        return playersRepository!!
    }
}