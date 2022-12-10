package com.dauma.grokimkartu.repositories.players

import android.graphics.Bitmap
import com.dauma.grokimkartu.repositories.players.entities.PlayerCity
import com.dauma.grokimkartu.repositories.players.entities.PlayerDetails
import com.dauma.grokimkartu.repositories.players.entities.PlayerInstrument
import com.dauma.grokimkartu.repositories.players.entities.PlayersPage

interface PlayersRepository {
    val pages: List<PlayersPage>
    var filter: PlayersFilter
    val isFilterApplied: Boolean
    fun loadNextPage(onComplete: (PlayersPage?, PlayersErrors?) -> Unit)
    fun playerDetails(userId: Int, onComplete: (PlayerDetails?, PlayersErrors?) -> Unit)
    fun playerPhoto(userId: Int, onComplete: (Bitmap?, PlayersErrors?) -> Unit)
    fun playerIcon(userId: Int, onComplete: (Bitmap?, PlayersErrors?) -> Unit)
    fun reload(onComplete: (PlayersPage?, PlayersErrors?) -> Unit)
    fun cities(onComplete: (List<PlayerCity>?, PlayersErrors?) -> Unit)
    fun searchCity(value: String, onComplete: (List<PlayerCity>?, PlayersErrors?) -> Unit)
    fun instruments(onComplete: (List<PlayerInstrument>?, PlayersErrors?) -> Unit)
    fun searchInstrument(value: String, onComplete: (List<PlayerInstrument>?, PlayersErrors?) -> Unit)
}