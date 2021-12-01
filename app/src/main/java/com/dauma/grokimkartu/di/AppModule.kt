package com.dauma.grokimkartu.di

import com.dauma.grokimkartu.data.auth.AuthDao
import com.dauma.grokimkartu.data.auth.AuthDaoImpl
import com.dauma.grokimkartu.data.players.FakePlayersDaoImpl
import com.dauma.grokimkartu.data.players.PlayersDao
import com.dauma.grokimkartu.data.users.UsersDao
import com.dauma.grokimkartu.data.users.UsersDaoImpl
import com.dauma.grokimkartu.models.forms.*
import com.dauma.grokimkartu.repositories.players.PlayersRepository
import com.dauma.grokimkartu.repositories.players.PlayersRepositoryImpl
import com.dauma.grokimkartu.repositories.users.UsersRepository
import com.dauma.grokimkartu.repositories.users.UsersRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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

    @Provides
    fun provideUsersDao() : UsersDao {
        return UsersDaoImpl(FirebaseFirestore.getInstance())
    }

    @Provides
    fun providesAuthDao() : AuthDao {
        return AuthDaoImpl(FirebaseAuth.getInstance())
    }

    @Provides
    @Singleton
    fun providesUsersRepository(usersDao: UsersDao) : UsersRepository {
        return UsersRepositoryImpl(usersDao)
    }

    @Provides
    fun providesRegistrationForm() : RegistrationForm {
        return RegistrationForm()
    }

    @Provides
    fun providesLoginForm() : LoginForm {
        return LoginForm()
    }

    @Provides
    fun providesForgotPasswordForm() : ForgotPasswordForm {
        return ForgotPasswordForm()
    }

    @Provides
    fun providesProfileForm() : ProfileForm {
        return ProfileForm()
    }

    @Provides
    fun providesPasswordChangeForm() : PasswordChangeForm {
        return PasswordChangeForm()
    }

    @Provides
    fun providesSettingsForm() : SettingsForm {
        return SettingsForm()
    }
}