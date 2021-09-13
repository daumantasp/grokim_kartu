package com.dauma.grokimkartu.viewmodels.players

import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.models.Player
import com.dauma.grokimkartu.repositories.players.PlayersRepository

class PlayersViewModelImpl(private val playersRepository: PlayersRepository) : ViewModel(), PlayersViewModel {
    override fun getPlayers() = playersRepository.getPlayers()
    fun addPlayer(player: Player) = playersRepository.addPlayer(player)
}