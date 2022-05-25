package com.dauma.grokimkartu.di

import com.dauma.grokimkartu.data.auth.AuthDao
import com.dauma.grokimkartu.data.auth.AuthDaoImpl
import com.dauma.grokimkartu.data.cities.CitiesDao
import com.dauma.grokimkartu.data.cities.CitiesDaoImpl
import com.dauma.grokimkartu.data.firestore.storage.FirebaseStorage
import com.dauma.grokimkartu.data.firestore.storage.FirebaseStorageImpl
import com.dauma.grokimkartu.data.instruments.InstrumentsDao
import com.dauma.grokimkartu.data.instruments.InstrumentsDaoImpl
import com.dauma.grokimkartu.data.players.PlayersDao
import com.dauma.grokimkartu.data.players.PlayersDaoImpl
import com.dauma.grokimkartu.data.profile.ProfileDao
import com.dauma.grokimkartu.data.profile.ProfileDaoImpl
import com.dauma.grokimkartu.data.settings.SettingsDao
import com.dauma.grokimkartu.data.settings.SettingsDaoImpl
import com.dauma.grokimkartu.data.thomanns.ThomannsDao
import com.dauma.grokimkartu.data.thomanns.ThomannsDaoImpl
import com.dauma.grokimkartu.general.user.User
import com.dauma.grokimkartu.general.utils.Utils
import com.dauma.grokimkartu.general.utils.UtilsImpl
import com.dauma.grokimkartu.general.utils.image.ImageUtils
import com.dauma.grokimkartu.general.utils.image.ImageUtilsImpl
import com.dauma.grokimkartu.general.utils.keyboard.KeyboardUtils
import com.dauma.grokimkartu.general.utils.keyboard.KeyboardUtilsImpl
import com.dauma.grokimkartu.general.utils.string.StringUtils
import com.dauma.grokimkartu.general.utils.string.StringUtilsImpl
import com.dauma.grokimkartu.general.utils.time.TimeUtils
import com.dauma.grokimkartu.general.utils.time.TimeUtilsImpl
import com.dauma.grokimkartu.models.forms.*
import com.dauma.grokimkartu.repositories.auth.AuthRepository
import com.dauma.grokimkartu.repositories.players.PlayersRepository
import com.dauma.grokimkartu.repositories.players.PlayersRepositoryImpl
import com.dauma.grokimkartu.repositories.profile.ProfileRepository
import com.dauma.grokimkartu.repositories.profile.ProfileRepositoryImpl
import com.dauma.grokimkartu.repositories.settings.SettingsRepository
import com.dauma.grokimkartu.repositories.settings.SettingsRepositoryImpl
import com.dauma.grokimkartu.repositories.thomanns.ThomannsRepository
import com.dauma.grokimkartu.repositories.thomanns.ThomannsRepositoryImpl
import com.dauma.grokimkartu.repositories.users.AuthRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
import com.google.firebase.storage.FirebaseStorage as GoogleFirebaseStorage

val BASE_URL = "http://192.168.0.104:8000/api/"
//val BASE_URL = "http://127.0.0.1:8000/api/"
//val BASE_URL = "http://10.0.2.2:8000/api/"

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
    fun timeUtils() : TimeUtils {
        return TimeUtilsImpl()
    }

    @Provides
    fun providesUtils(
        imageUtils: ImageUtils,
        stringUtils: StringUtils,
        keyboardUtils: KeyboardUtils,
        timeUtils: TimeUtils
    ) : Utils {
        return UtilsImpl(
            imageUtils,
            stringUtils,
            keyboardUtils,
            timeUtils
        )
    }

    @Provides
    @Singleton
    fun providesFirebaseStorage(imageUtils: ImageUtils) : FirebaseStorage {
        return FirebaseStorageImpl(GoogleFirebaseStorage.getInstance(), imageUtils)
    }

    @Provides
    @Singleton
    fun providesRetrofit() : Retrofit {
        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(object : Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                val original = chain.request()

                val request = original.newBuilder()
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .method(original.method(), original.body())
                    .build()

                return chain.proceed(request)
            }
        })

        val client = httpClient.build();

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    @Provides
    @Singleton
    fun providesUser() : User {
        return User()
    }

    @Provides
    fun providesAuthDao(retrofit: Retrofit) : AuthDao {
        return AuthDaoImpl(retrofit)
    }

    @Provides
    @Singleton
    fun providesAuthRepository(authDao: AuthDao, user: User) : AuthRepository {
        return AuthRepositoryImpl(authDao, user)
    }

    @Provides
    fun providesSettingsDao(retrofit: Retrofit): SettingsDao {
        return SettingsDaoImpl(retrofit)
    }

    @Provides
    @Singleton
    fun providesSettingsRepository(settingsDao: SettingsDao, user: User): SettingsRepository {
        return SettingsRepositoryImpl(settingsDao, user)
    }

    @Provides
    fun providePlayersDao(retrofit: Retrofit) : PlayersDao {
        return PlayersDaoImpl(retrofit)
    }

    @Provides
    @Singleton
    fun providePlayersRepository(playersDao: PlayersDao, user: User) : PlayersRepository {
        return PlayersRepositoryImpl(playersDao, user)
    }

    @Provides
    fun providesProfileDao(retrofit: Retrofit, imageUtils: ImageUtils) : ProfileDao {
        return ProfileDaoImpl(retrofit, imageUtils)
    }

    @Provides
    @Singleton
    fun provideProfileRepository(profileDao: ProfileDao, citiesDao: CitiesDao, user: User) : ProfileRepository {
        return ProfileRepositoryImpl(profileDao, citiesDao, user)
    }

    @Provides
    fun providesThomannsDao(retrofit: Retrofit) : ThomannsDao {
        return ThomannsDaoImpl(retrofit)
    }

    @Provides
    @Singleton
    fun providesThomannsRepository(thomannsDao: ThomannsDao, playersDao: PlayersDao, user: User) : ThomannsRepository {
        return ThomannsRepositoryImpl(thomannsDao, playersDao, user)
    }

    @Provides
    fun providesCitiesDao(retrofit: Retrofit) : CitiesDao {
        return CitiesDaoImpl(retrofit)
    }

    @Provides
    fun providesInstrumentsDao(retrofit: Retrofit) : InstrumentsDao {
        return InstrumentsDaoImpl(retrofit)
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

    @Provides
    fun providesProfileEditForm() : ProfileEditForm {
        return ProfileEditForm()
    }
}