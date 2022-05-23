package com.dauma.grokimkartu.repositories.players

import android.graphics.Bitmap
import com.dauma.grokimkartu.repositories.players.entities.Player
import com.dauma.grokimkartu.repositories.players.entities.PlayerDetails

interface PlayersRepository {
    fun players(onComplete: (List<Player>?, PlayersErrors?) -> Unit)
    fun playerDetails(userId: Int, onComplete: (PlayerDetails?, PlayersErrors?) -> Unit)
    fun playerPhoto(userId: Int, onComplete: (Bitmap?, PlayersErrors?) -> Unit)
    fun playerIcon(userId: Int, onComplete: (Bitmap?, PlayersErrors?) -> Unit)
}