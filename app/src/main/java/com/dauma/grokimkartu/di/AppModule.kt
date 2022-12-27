package com.dauma.grokimkartu.di

import android.content.Context
import com.dauma.grokimkartu.data.auth.AuthDao
import com.dauma.grokimkartu.data.auth.AuthDaoImpl
import com.dauma.grokimkartu.data.cities.CitiesDao
import com.dauma.grokimkartu.data.cities.CitiesDaoImpl
import com.dauma.grokimkartu.data.conversations.PrivateConversationsDao
import com.dauma.grokimkartu.data.conversations.PrivateConversationsDaoImpl
import com.dauma.grokimkartu.data.conversations.ThomannConversationsDao
import com.dauma.grokimkartu.data.conversations.ThomannConversationsDaoImpl
import com.dauma.grokimkartu.data.firestore.storage.FirebaseStorage
import com.dauma.grokimkartu.data.firestore.storage.FirebaseStorageImpl
import com.dauma.grokimkartu.data.instruments.InstrumentsDao
import com.dauma.grokimkartu.data.instruments.InstrumentsDaoImpl
import com.dauma.grokimkartu.data.notifications.NotificationsDao
import com.dauma.grokimkartu.data.notifications.NotificationsDaoImpl
import com.dauma.grokimkartu.data.players.PlayersDao
import com.dauma.grokimkartu.data.players.PlayersDaoImpl
import com.dauma.grokimkartu.data.profile.ProfileDao
import com.dauma.grokimkartu.data.profile.ProfileDaoImpl
import com.dauma.grokimkartu.data.settings.SettingsDao
import com.dauma.grokimkartu.data.settings.SettingsDaoImpl
import com.dauma.grokimkartu.data.thomanns.ThomannsDao
import com.dauma.grokimkartu.data.thomanns.ThomannsDaoImpl
import com.dauma.grokimkartu.general.networkchangereceiver.NetworkChangeReceiver
import com.dauma.grokimkartu.general.networkchangereceiver.NetworkChangeReceiverImpl
import com.dauma.grokimkartu.general.thememodemanager.ThemeModeManager
import com.dauma.grokimkartu.general.thememodemanager.ThemeModeManagerImpl
import com.dauma.grokimkartu.general.user.User
import com.dauma.grokimkartu.general.utils.Utils
import com.dauma.grokimkartu.general.utils.UtilsImpl
import com.dauma.grokimkartu.general.utils.dispatcher.DispatcherUtils
import com.dauma.grokimkartu.general.utils.dispatcher.DispatcherUtilsImpl
import com.dauma.grokimkartu.general.utils.image.ImageUtils
import com.dauma.grokimkartu.general.utils.image.ImageUtilsImpl
import com.dauma.grokimkartu.general.utils.keyboard.KeyboardUtils
import com.dauma.grokimkartu.general.utils.keyboard.KeyboardUtilsImpl
import com.dauma.grokimkartu.general.utils.locale.LocaleUtils
import com.dauma.grokimkartu.general.utils.locale.LocaleUtilsImpl
import com.dauma.grokimkartu.general.utils.other.OtherUtils
import com.dauma.grokimkartu.general.utils.other.OtherUtilsImpl
import com.dauma.grokimkartu.general.utils.sharedstorage.SharedStorageUtils
import com.dauma.grokimkartu.general.utils.sharedstorage.SharedStorageUtilsImpl
import com.dauma.grokimkartu.general.utils.string.StringUtils
import com.dauma.grokimkartu.general.utils.string.StringUtilsImpl
import com.dauma.grokimkartu.general.utils.time.TimeUtils
import com.dauma.grokimkartu.general.utils.time.TimeUtilsImpl
import com.dauma.grokimkartu.models.forms.*
import com.dauma.grokimkartu.repositories.auth.AuthRepository
import com.dauma.grokimkartu.repositories.conversations.PrivateConversationsRepository
import com.dauma.grokimkartu.repositories.conversations.PrivateConversationsRepositoryImpl
import com.dauma.grokimkartu.repositories.conversations.ThomannConversationsRepository
import com.dauma.grokimkartu.repositories.conversations.ThomannConversationsRepositoryImpl
import com.dauma.grokimkartu.repositories.conversations.paginator.PrivateConversationsPaginator
import com.dauma.grokimkartu.repositories.conversations.paginator.PrivateConversationsPaginatorImpl
import com.dauma.grokimkartu.repositories.conversations.paginator.ThomannConversationsPaginator
import com.dauma.grokimkartu.repositories.conversations.paginator.ThomannConversationsPaginatorImpl
import com.dauma.grokimkartu.repositories.notifications.NotificationsRepository
import com.dauma.grokimkartu.repositories.notifications.NotificationsRepositoryImpl
import com.dauma.grokimkartu.repositories.notifications.paginator.NotificationsPaginator
import com.dauma.grokimkartu.repositories.notifications.paginator.NotificationsPaginatorImpl
import com.dauma.grokimkartu.repositories.players.PlayersRepository
import com.dauma.grokimkartu.repositories.players.PlayersRepositoryImpl
import com.dauma.grokimkartu.repositories.players.paginator.PlayersPaginator
import com.dauma.grokimkartu.repositories.players.paginator.PlayersPaginatorImpl
import com.dauma.grokimkartu.repositories.profile.ProfileRepository
import com.dauma.grokimkartu.repositories.profile.ProfileRepositoryImpl
import com.dauma.grokimkartu.repositories.settings.SettingsRepository
import com.dauma.grokimkartu.repositories.settings.SettingsRepositoryImpl
import com.dauma.grokimkartu.repositories.thomanns.MyThomannsRepository
import com.dauma.grokimkartu.repositories.thomanns.MyThomannsRepositoryImpl
import com.dauma.grokimkartu.repositories.thomanns.ThomannsRepository
import com.dauma.grokimkartu.repositories.thomanns.ThomannsRepositoryImpl
import com.dauma.grokimkartu.repositories.thomanns.paginator.MyThomannsPaginatorImpl
import com.dauma.grokimkartu.repositories.thomanns.paginator.ThomannsPaginatorImpl
import com.dauma.grokimkartu.repositories.users.AuthRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
import com.google.firebase.storage.FirebaseStorage as GoogleFirebaseStorage

//val BASE_URL = "http://192.168.0.105:8000/api/"
//val BASE_URL = "http://127.0.0.1:8000/api/"
val BASE_URL = "http://10.0.2.2:8000/api/"

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    fun providesStringUtils() : StringUtils {
        return StringUtilsImpl()
    }

    @Provides
    fun providesImageUtils(@ApplicationContext appContext: Context) : ImageUtils {
        return ImageUtilsImpl(appContext)
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
    fun sharedStorageUtils(@ApplicationContext appContext: Context) : SharedStorageUtils {
        return SharedStorageUtilsImpl(appContext)
    }

    @Provides
    fun dispatcherUtils() : DispatcherUtils {
        return DispatcherUtilsImpl()
    }

    @Provides
    fun localeUtils() : LocaleUtils {
        return LocaleUtilsImpl()
    }

    @Provides
    fun otherUtils() : OtherUtils {
        return OtherUtilsImpl()
    }

    @Provides
    fun providesUtils(
        imageUtils: ImageUtils,
        stringUtils: StringUtils,
        keyboardUtils: KeyboardUtils,
        timeUtils: TimeUtils,
        sharedStorageUtils: SharedStorageUtils,
        dispatcherUtils: DispatcherUtils,
        localeUtils: LocaleUtils,
        otherUtils: OtherUtils
    ) : Utils {
        return UtilsImpl(
            imageUtils,
            stringUtils,
            keyboardUtils,
            timeUtils,
            sharedStorageUtils,
            dispatcherUtils,
            localeUtils,
            otherUtils
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
    @Singleton
    fun providesNetworkChangeReceiver(@ApplicationContext appContext: Context) : NetworkChangeReceiver {
        return NetworkChangeReceiverImpl(appContext)
    }

    @Provides
    fun providesAuthDao(retrofit: Retrofit) : AuthDao {
        return AuthDaoImpl(retrofit)
    }

    @Provides
    @Singleton
    fun providesAuthRepository(authDao: AuthDao, user: User, utils: Utils) : AuthRepository {
        return AuthRepositoryImpl(authDao, user, utils)
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
    fun providePlayersPaginator(playersDao: PlayersDao) : PlayersPaginator {
        return PlayersPaginatorImpl(playersDao)
    }

    @Provides
    @Singleton
    fun providePlayersRepository(
        playersDao: PlayersDao,
        paginator: PlayersPaginator,
        citiesDao: CitiesDao,
        instrumentsDao: InstrumentsDao,
        user: User,
        authRepository: AuthRepository
    ) : PlayersRepository {
        val playersRepository = PlayersRepositoryImpl(playersDao, paginator, citiesDao, instrumentsDao, user)
        authRepository.registerLoginListener("PLAYERS_REPOSITORY_LOGIN_LISTENER_ID", playersRepository)
        return playersRepository
    }

    @Provides
    fun providesProfileDao(retrofit: Retrofit, imageUtils: ImageUtils) : ProfileDao {
        return ProfileDaoImpl(retrofit, imageUtils)
    }

    @Provides
    @Singleton
    fun provideProfileRepository(
        profileDao: ProfileDao,
        citiesDao: CitiesDao,
        instrumentsDao: InstrumentsDao,
        user: User
    ) : ProfileRepository {
        return ProfileRepositoryImpl(profileDao, citiesDao, instrumentsDao, user)
    }

    @Provides
    fun providesThomannsDao(retrofit: Retrofit) : ThomannsDao {
        return ThomannsDaoImpl(retrofit)
    }

    @Provides
    @Singleton
    fun providesThomannsRepository(
        thomannsDao: ThomannsDao,
        playersDao: PlayersDao,
        citiesDao: CitiesDao,
        user: User,
        authRepository: AuthRepository
    ) : ThomannsRepository {
        val thomannsPaginator = ThomannsPaginatorImpl(thomannsDao)
        val thomannsRepository = ThomannsRepositoryImpl(thomannsDao, playersDao, citiesDao, thomannsPaginator, user)
        authRepository.registerLoginListener("THOMANNS_REPOSITORY_LOGIN_LISTENER_ID", thomannsRepository)
        return thomannsRepository
    }

    @Provides
    @Singleton
    fun providesMyThomannsRepository(
        thomannsDao: ThomannsDao,
        playersDao: PlayersDao,
        user: User,
        authRepository: AuthRepository
    ) : MyThomannsRepository {
        val myThommansPaginator = MyThomannsPaginatorImpl(thomannsDao)
        val myThomannsRepository = MyThomannsRepositoryImpl(thomannsDao, playersDao, myThommansPaginator, user)
        authRepository.registerLoginListener("THOMANNS_MY_REPOSITORY_LOGIN_LISTENER_ID", myThomannsRepository)
        return myThomannsRepository
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
    fun providesNotificationsDao(retrofit: Retrofit) : NotificationsDao {
        return NotificationsDaoImpl(retrofit)
    }

    @Provides
    fun providesNotificationsPaginator(notificationsDao: NotificationsDao) : NotificationsPaginator {
        return NotificationsPaginatorImpl(notificationsDao)
    }

    @Provides
    @Singleton
    fun providesNotificationsRepository(
        notificationsDao: NotificationsDao,
        paginator: NotificationsPaginator,
        user: User,
        utils: Utils,
        authRepository: AuthRepository
    ) : NotificationsRepository {
        val notificationsRepository = NotificationsRepositoryImpl(notificationsDao, paginator, user, utils)
        authRepository.registerLoginListener("NOTIFICATIONS_REPOSITORY_LOGIN_LISTENER", notificationsRepository)
        return notificationsRepository
    }

    @Provides
    fun providesPrivateConversationsDao(retrofit: Retrofit) : PrivateConversationsDao {
        return PrivateConversationsDaoImpl(retrofit)
    }

    @Provides
    fun providesThomannConversationsDao(retrofit: Retrofit) : ThomannConversationsDao {
        return ThomannConversationsDaoImpl(retrofit)
    }

    @Provides
    fun providesPrivateConversationsPaginator(privateConversationsDao: PrivateConversationsDao)
        : PrivateConversationsPaginator {
        return PrivateConversationsPaginatorImpl(privateConversationsDao)
    }

    @Provides
    fun providesThomannConversationsPaginator(thomannConversationsDao: ThomannConversationsDao)
            : ThomannConversationsPaginator {
        return ThomannConversationsPaginatorImpl(thomannConversationsDao)
    }

    @Provides
    @Singleton
    fun providesPrivateConversationsRepository(
        privateConversationsDao: PrivateConversationsDao,
        playersDao: PlayersDao,
        paginator: PrivateConversationsPaginator,
        user: User,
        utils: Utils
    ) : PrivateConversationsRepository {
        return PrivateConversationsRepositoryImpl(privateConversationsDao, playersDao, paginator, user, utils)
    }

    @Provides
    @Singleton
    fun providesThomannConversationsRepository(
        thomannConversationsDao: ThomannConversationsDao,
        playersDao: PlayersDao,
        paginator: ThomannConversationsPaginator,
        user: User,
        utils: Utils
    ) : ThomannConversationsRepository {
        return ThomannConversationsRepositoryImpl(thomannConversationsDao, playersDao, paginator, user, utils)
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

    @Provides
    fun providesPlayersFilterForm() : PlayersFilterForm {
        return PlayersFilterForm()
    }

    @Provides
    fun providesThomannsFilterForm() : ThomannsFilterForm {
        return ThomannsFilterForm()
    }

    @Provides
    @Singleton
    fun providesThemeModeManager(utils: Utils) : ThemeModeManager {
        return ThemeModeManagerImpl(utils)
    }
}