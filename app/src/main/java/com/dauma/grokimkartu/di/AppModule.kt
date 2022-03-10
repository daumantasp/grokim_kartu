package com.dauma.grokimkartu.di

import FirebaseStorageImpl
import com.dauma.grokimkartu.data.auth.AuthDao
import com.dauma.grokimkartu.data.auth.AuthDaoImpl
import com.dauma.grokimkartu.data.firestore.Firestore
import com.dauma.grokimkartu.data.firestore.FirestoreImpl
import com.dauma.grokimkartu.data.players.PlayersDao
import com.dauma.grokimkartu.data.players.PlayersDaoImpl
import com.dauma.grokimkartu.data.users.UsersDao
import com.dauma.grokimkartu.data.users.UsersDaoImpl
import com.dauma.grokimkartu.models.forms.*
import com.dauma.grokimkartu.repositories.players.PlayersRepository
import com.dauma.grokimkartu.repositories.players.PlayersRepositoryImpl
import com.dauma.grokimkartu.repositories.users.UsersRepository
import com.dauma.grokimkartu.repositories.users.UsersRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.dauma.grokimkartu.data.firestore.FirebaseStorage
import com.dauma.grokimkartu.data.thomanns.ThomannsDao
import com.dauma.grokimkartu.data.thomanns.ThomannsDaoImpl
import com.dauma.grokimkartu.general.utils.Utils
import com.dauma.grokimkartu.general.utils.UtilsImpl
import com.dauma.grokimkartu.general.utils.image.ImageUtils
import com.dauma.grokimkartu.general.utils.image.ImageUtilsImpl
import com.dauma.grokimkartu.general.utils.keyboard.KeyboardUtils
import com.dauma.grokimkartu.general.utils.keyboard.KeyboardUtilsImpl
import com.dauma.grokimkartu.general.utils.string.StringUtils
import com.dauma.grokimkartu.general.utils.string.StringUtilsImpl
import com.dauma.grokimkartu.repositories.thomanns.ThomannsRepository
import com.dauma.grokimkartu.repositories.thomanns.ThomannsRepositoryImpl
import com.google.firebase.storage.FirebaseStorage as GoogleFirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    fun providesStringUtils() : StringUtils {
        return StringUtilsImpl()
    }

    @Provides
    fun providesImageUtils() : ImageUtils {
        return ImageUtilsImpl()
    }

    @Provides
    fun keyboardUtils() : KeyboardUtils {
        return KeyboardUtilsImpl()
    }

    @Provides
    fun providesUtils(
        imageUtils: ImageUtils,
        stringUtils: StringUtils,
        keyboardUtils: KeyboardUtils,
    ) : Utils {
        return UtilsImpl(
            imageUtils,
            stringUtils,
            keyboardUtils
        )
    }

    @Provides
    @Singleton
    fun providesFirestore() : Firestore {
        return FirestoreImpl(FirebaseFirestore.getInstance())
    }

    @Provides
    @Singleton
    fun providesFirebaseStorage(imageUtils: ImageUtils) : FirebaseStorage {
        return FirebaseStorageImpl(GoogleFirebaseStorage.getInstance(), imageUtils)
    }

    @Provides
    fun providePlayersDao(firestore: Firestore, firebaseStorage: FirebaseStorage) : PlayersDao {
        return PlayersDaoImpl(firestore, firebaseStorage)
    }

    @Provides
    @Singleton
    fun providePlayersRepository(playersDao: PlayersDao) : PlayersRepository {
        return PlayersRepositoryImpl(playersDao)
    }

    @Provides
    fun provideUsersDao(firestore: Firestore, firebaseStorage: FirebaseStorage) : UsersDao {
        return UsersDaoImpl(firestore, firebaseStorage)
    }

    @Provides
    fun providesAuthDao() : AuthDao {
        return AuthDaoImpl(FirebaseAuth.getInstance())
    }

    @Provides
    @Singleton
    fun providesUsersRepository(authDao: AuthDao, usersDao: UsersDao) : UsersRepository {
        return UsersRepositoryImpl(authDao, usersDao)
    }

    @Provides
    fun providesThomannsDao(firestore: Firestore) : ThomannsDao {
        return ThomannsDaoImpl(firestore)
    }

    @Provides
    @Singleton
    fun providesThomannsRepository(authDao: AuthDao, thomannsDao: ThomannsDao) : ThomannsRepository {
        return ThomannsRepositoryImpl(authDao, thomannsDao)
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

    @Provides
    fun providesPlayerDetailsForm() : PlayerDetailsForm {
        return PlayerDetailsForm()
    }

    @Provides
    fun deleteUserForm() : DeleteUserForm {
        return DeleteUserForm()
    }

    @Provides
    fun thomannEditForm() : ThomannEditForm {
        return ThomannEditForm()
    }
}