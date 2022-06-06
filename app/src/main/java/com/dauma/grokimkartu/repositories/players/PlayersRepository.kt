package com.dauma.grokimkartu.repositories.players

import android.graphics.Bitmap
import com.dauma.grokimkartu.repositories.players.entities.PlayerDetails
import com.dauma.grokimkartu.repositories.players.entities.PlayersPage

interface PlayersRepository {
    val playersPages: List<PlayersPage>
    fun loadNextPage(onComplete: (PlayersPage?, PlayersErrors?) -> Unit)
    fun playerDetails(userId: Int, onComplete: (PlayerDetails?, PlayersErrors?) -> Unit)
    fun playerPhoto(userId: Int, onComplete: (Bitmap?, PlayersErrors?) -> Unit)
    fun playerIcon(userId: Int, onComplete: (Bitmap?, PlayersErrors?) -> Unit)
    fun clear()
}