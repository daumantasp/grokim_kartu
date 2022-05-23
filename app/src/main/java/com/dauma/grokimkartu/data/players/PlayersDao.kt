package com.dauma.grokimkartu.data.players

import android.graphics.Bitmap
import com.dauma.grokimkartu.data.players.entities.PlayerDetailsResponse
import com.dauma.grokimkartu.data.players.entities.PlayerResponse

interface PlayersDao {
    fun players(accessToken: String, onComplete: (List<PlayerResponse>?, PlayersDaoResponseStatus) -> Unit)
    fun playerDetails(userId: Int, accessToken: String, onComplete: (PlayerDetailsResponse?, PlayersDaoResponseStatus) -> Unit)
    fun playerPhoto(userId: Int, accessToken: String, onComplete: (Bitmap?, PlayersDaoResponseStatus) -> Unit)
    fun playerIcon(userId: Int, accessToken: String, onComplete: (Bitmap?, PlayersDaoResponseStatus) -> Unit)
}