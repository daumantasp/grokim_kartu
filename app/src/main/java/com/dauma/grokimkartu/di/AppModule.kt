package com.dauma.grokimkartu.di

import com.dauma.grokimkartu.data.players.FakePlayersDaoImpl
import com.dauma.grokimkartu.data.players.PlayersDao
import com.dauma.grokimkartu.repositories.players.PlayersRepository
import com.dauma.grokimkartu.repositories.players.PlayersRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    fun providePlayersDao() : PlayersDao {
        return FakePlayersDaoImpl()
    }

    @Provides
    @Singleton
    fun providePlayersRepository(playersDao: PlayersDao) : PlayersRepository {
        return PlayersRepositoryImpl(playersDao)
    }
}