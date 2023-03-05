package com.dauma.grokimkartu.repositories.players

import android.graphics.Bitmap
import com.dauma.grokimkartu.repositories.Result
import com.dauma.grokimkartu.repositories.players.entities.PlayerCity
import com.dauma.grokimkartu.repositories.players.entities.PlayerDetails
import com.dauma.grokimkartu.repositories.players.entities.PlayerInstrument
import com.dauma.grokimkartu.repositories.players.entities.PlayersPage
import com.dauma.grokimkartu.repositories.players.paginator.PlayersPaginator

interface PlayersRepository {
    val paginator: PlayersPaginator
    suspend fun playerDetails(userId: Int): Result<PlayerDetails?, PlayersErrors?>
    suspend fun playerPhoto(userId: Int): Result<Bitmap?, PlayersErrors?>
    suspend fun reload(): Result<PlayersPage?, PlayersErrors?>
    suspend fun cities(): Result<List<PlayerCity>?, PlayersErrors?>
    suspend fun searchCity(value: String): Result<List<PlayerCity>?, PlayersErrors?>
    suspend fun instruments(): Result<List<PlayerInstrument>?, PlayersErrors?>
    suspend fun searchInstrument(value: String): Result<List<PlayerInstrument>?, PlayersErrors?>
}