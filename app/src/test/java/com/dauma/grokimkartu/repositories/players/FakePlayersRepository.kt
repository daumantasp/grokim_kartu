package com.dauma.grokimkartu.repositories.players

import android.graphics.Bitmap
import com.dauma.grokimkartu.repositories.Result
import com.dauma.grokimkartu.repositories.players.entities.PlayerCity
import com.dauma.grokimkartu.repositories.players.entities.PlayerDetails
import com.dauma.grokimkartu.repositories.players.entities.PlayerInstrument
import com.dauma.grokimkartu.repositories.players.entities.PlayersPage
import com.dauma.grokimkartu.repositories.players.paginator.PlayersPaginator

class FakePlayersRepository(
    override val paginator: PlayersPaginator
) : PlayersRepository {
    override suspend fun playerDetails(userId: Int): Result<PlayerDetails?, PlayersErrors?> {
        TODO("Not yet implemented")
    }

    override suspend fun playerPhoto(userId: Int): Result<Bitmap?, PlayersErrors?> {
        TODO("Not yet implemented")
    }

    override suspend fun reload(): Result<PlayersPage?, PlayersErrors?> {
        TODO("Not yet implemented")
    }

    override suspend fun cities(): Result<List<PlayerCity>?, PlayersErrors?> {
        TODO("Not yet implemented")
    }

    override suspend fun searchCity(value: String): Result<List<PlayerCity>?, PlayersErrors?> {
        TODO("Not yet implemented")
    }

    override suspend fun instruments(): Result<List<PlayerInstrument>?, PlayersErrors?> {
        TODO("Not yet implemented")
    }

    override suspend fun searchInstrument(value: String): Result<List<PlayerInstrument>?, PlayersErrors?> {
        TODO("Not yet implemented")
    }

    override fun loginCompleted(isSuccessful: Boolean) {
        TODO("Not yet implemented")
    }
}