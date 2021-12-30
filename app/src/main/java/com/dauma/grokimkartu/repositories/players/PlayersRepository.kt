package com.dauma.grokimkartu.repositories.players

import android.graphics.Bitmap
import com.dauma.grokimkartu.repositories.players.entities.Player
import com.dauma.grokimkartu.repositories.players.entities.PlayerDetails

interface PlayersRepository {
    fun getPlayers(onComplete: (Boolean, List<Player>?, PlayersError?) -> Unit)
    fun getPlayerPhoto(userId: String, onComplete: (Bitmap?, PlayersError?) -> Unit)
    fun getPlayerIcon(userId: String, onComplete: (Bitmap?, PlayersError?) -> Unit)
    fun getPlayerDetails(userId: String, onComplete: (PlayerDetails?, PlayersError?) -> Unit)
}