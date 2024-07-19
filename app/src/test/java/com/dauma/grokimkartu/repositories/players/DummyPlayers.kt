package com.dauma.grokimkartu.repositories.players

import com.dauma.grokimkartu.general.IconLoader
import com.dauma.grokimkartu.repositories.players.entities.Player

class DummyPlayers {
    companion object {
        fun players(): List<Player> {
            return listOf(
                Player(
                    userId = 1,
                    name = "Jonas",
                    instrument = "Elektrinė gitara",
                    description = "Mėgstu elektrinę gitarą",
                    iconLoader = IconLoader { },
                    city = "Vilnius"
                ),
                Player(
                    userId = 2,
                    name = "Petras",
                    instrument = "Bosinė gitara",
                    description = "Mėgstu bosinę gitarą",
                    iconLoader = IconLoader { },
                    city = "Kaunas"
                ),
                Player(
                    userId = 2,
                    name = "Kazys",
                    instrument = "Akustinė gitara",
                    description = "Mėgstu akustinę gitarą",
                    iconLoader = IconLoader { },
                    city = "Klaipėda"
                )
            )
        }
    }
}