package com.dauma.grokimkartu.viewmodels.main

import com.dauma.grokimkartu.MainCoroutineRule
import com.dauma.grokimkartu.repositories.players.DummyPlayers
import com.dauma.grokimkartu.repositories.players.FakePlayersRepository
import com.dauma.grokimkartu.repositories.players.paginator.FakePlayersPaginator
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PlayersViewModelTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: PlayersViewModel

    @Before
    fun setup() {
        val paginator = FakePlayersPaginator(DummyPlayers.players())
        val playersRepository = FakePlayersRepository(paginator)
        viewModel = PlayersViewModel(playersRepository)
    }

    @Test
    fun `players loaded on init`() = runBlocking {
        var playersCount = 0
        for (page in viewModel.uiState.value.playersPages)
            playersCount += page.players?.size ?: 0
        assertThat(playersCount).isEqualTo(DummyPlayers.players().size)
    }
}