package com.dauma.grokimkartu.data.players

import android.graphics.Bitmap
import com.dauma.grokimkartu.data.players.entities.PlayerDao

interface PlayersDao {
    fun getPlayers(onComplete: (Boolean, List<PlayerDao>?, Exception?) -> Unit)
    fun getPlayerIcon(userId: String, onComplete: (Bitmap?, Exception?) -> Unit)
//    fun getPlayerDetails(userId: String, onComplete: (Boolean, PlayerDetails?, Exception?) -> Unit)
}