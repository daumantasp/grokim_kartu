package com.dauma.grokimkartu.viewmodels.players

import androidx.lifecycle.LiveData
import com.dauma.grokimkartu.models.Player

interface PlayersViewModel {
    fun getPlayers() : LiveData<List<Player>>
}