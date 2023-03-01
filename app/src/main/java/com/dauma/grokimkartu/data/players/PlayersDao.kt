package com.dauma.grokimkartu.data.players

import android.graphics.Bitmap
import com.dauma.grokimkartu.data.DaoResult
import com.dauma.grokimkartu.data.players.entities.PlayerDetailsResponse
import com.dauma.grokimkartu.data.players.entities.PlayersRequest
import com.dauma.grokimkartu.data.players.entities.PlayersResponse

interface PlayersDao {
    suspend fun players(playersRequest: PlayersRequest, accessToken: String): DaoResult<PlayersResponse?, PlayersDaoResponseStatus>
    suspend fun playerDetails(userId: Int, accessToken: String): DaoResult<PlayerDetailsResponse?, PlayersDaoResponseStatus>
    suspend fun playerPhoto(userId: Int, accessToken: String): DaoResult<Bitmap?, PlayersDaoResponseStatus>
    suspend fun playerIcon(userId: Int, accessToken: String): DaoResult<Bitmap?, PlayersDaoResponseStatus>
}