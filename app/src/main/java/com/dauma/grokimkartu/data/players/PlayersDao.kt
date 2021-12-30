package com.dauma.grokimkartu.data.players

import android.graphics.Bitmap
import com.dauma.grokimkartu.data.players.entities.PlayerDao
import com.dauma.grokimkartu.data.players.entities.PlayerDetailsDao

interface PlayersDao {
    fun getPlayers(onComplete: (Boolean, List<PlayerDao>?, Exception?) -> Unit)
    fun getPlayerIcon(userId: String, onComplete: (Bitmap?, Exception?) -> Unit)
    fun getPlayerDetails(userId: String, onComplete: (PlayerDetailsDao?, Exception?) -> Unit)
}