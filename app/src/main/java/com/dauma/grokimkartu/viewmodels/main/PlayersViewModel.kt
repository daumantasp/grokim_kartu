package com.dauma.grokimkartu.viewmodels.main

import androidx.lifecycle.ViewModel
import com.dauma.grokimkartu.data.players.entities.Player
import com.dauma.grokimkartu.repositories.players.PlayersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PlayersViewModel @Inject constructor(
    private val playersRepository: PlayersRepository
) : ViewModel() {
    fun getPlayers() = playersRepository.getPlayers()
    fun addPlayer(player: Player) = playersRepository.addPlayer(player)

    fun backClicked() {
        // TODO
    }
}