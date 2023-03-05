package com.dauma.grokimkartu.repositories.profile

import android.graphics.Bitmap
import com.dauma.grokimkartu.data.cities.CitiesDao
import com.dauma.grokimkartu.data.cities.entities.CityResponse
import com.dauma.grokimkartu.data.instruments.InstrumentsDao
import com.dauma.grokimkartu.data.instruments.entities.InstrumentResponse
import com.dauma.grokimkartu.data.profile.ProfileDao
import com.dauma.grokimkartu.data.profile.ProfileDaoResponseStatus
import com.dauma.grokimkartu.data.profile.entities.ProfileResponse
import com.dauma.grokimkartu.data.profile.entities.ProfileUnreadCountResponse
import com.dauma.grokimkartu.data.profile.entities.UpdateProfileRequest
import com.dauma.grokimkartu.general.user.User
import com.dauma.grokimkartu.general.utils.Utils
import com.dauma.grokimkartu.repositories.auth.LoginListener
import com.dauma.grokimkartu.repositories.auth.LogoutListener
import com.dauma.grokimkartu.repositories.profile.entities.*
import com.dauma.grokimkartu.repositories.users.AuthenticationErrors
import com.dauma.grokimkartu.repositories.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileRepositoryImpl(
    private val profileDao: ProfileDao,
    private val citiesDao: CitiesDao,
    private val instrumentsDao: InstrumentsDao,
    private val user: User,
    private val utils: Utils
) : ProfileRepository, LoginListener, LogoutListener {
    private val coroutineIOScope = CoroutineScope(Dispatchers.IO)

    private var _unreadCount: MutableStateFlow<ProfileUnreadCount?> = MutableStateFlow(null)
    override val unreadCount: StateFlow<ProfileUnreadCount?> = _unreadCount.asStateFlow()

    companion object {
        private const val PROFILE_UNREAD_COUNT_PERIODIC_RELOAD = "PROFILE_UNREAD_COUNT_PERIODIC_RELOAD"
    }

    override suspend fun profile(): Result<Profile?, ProfileErrors?> {
        if (user.isUserLoggedIn()) {
            val response = profileDao.profile(user.getBearerAccessToken()!!)
            val status = response.status
            val profileResponse = response.data
            if (status.isSuccessful && profileResponse != null) {
                val profile = toProfile(profileResponse)
                return Result(profile, null)
            } else {
                return Result(null, ProfileErrors.UNKNOWN)
            }
        } else {
            throw ProfileException(ProfileErrors.USER_NOT_LOGGED_IN)
        }
    }

    override suspend fun cities(): Result<List<ProfileCity>?, ProfileErrors?> {
        if (user.isUserLoggedIn()) {
            val response = citiesDao.cities(user.getBearerAccessToken()!!)
            val status = response.status
            val citiesResponse = response.data
            if (status.isSuccessful && citiesResponse != null) {
                val profileCities = citiesResponse.map { cr -> toProfileCity(cr) }
                return Result(profileCities, null)
            } else {
                return Result(null, ProfileErrors.UNKNOWN)
            }
        } else {
            throw ProfileException(ProfileErrors.USER_NOT_LOGGED_IN)
        }
    }

    override suspend fun searchCity(value: String): Result<List<ProfileCity>?, ProfileErrors?> {
        if (user.isUserLoggedIn()) {
            val response = citiesDao.search(value, user.getBearerAccessToken()!!)
            val status = response.status
            val citiesResponse = response.data
            if (status.isSuccessful && citiesResponse != null) {
                val profileCities = citiesResponse.map { cr -> toProfileCity(cr) }
                return Result(profileCities, null)
            } else {
                return Result(null, ProfileErrors.UNKNOWN)
            }
        } else {
            throw ProfileException(ProfileErrors.USER_NOT_LOGGED_IN)
        }
    }

    override suspend fun instruments(): Result<List<ProfileInstrument>?, ProfileErrors?> {
        if (user.isUserLoggedIn()) {
            val response = instrumentsDao.instruments(user.getBearerAccessToken()!!)
            val status = response.status
            val instrumentsResponse = response.data
            if (status.isSuccessful && instrumentsResponse != null) {
                val profileInstruments = instrumentsResponse.map { ir -> toProfileInstrument(ir) }
                return Result(profileInstruments, null)
            } else {
                return Result(null, ProfileErrors.UNKNOWN)
            }
        } else {
            throw ProfileException(ProfileErrors.USER_NOT_LOGGED_IN)
        }
    }

    override suspend fun searchInstrument(value: String): Result<List<ProfileInstrument>?, ProfileErrors?> {
        if (user.isUserLoggedIn()) {
            val response = instrumentsDao.search(value, user.getBearerAccessToken()!!)
            val status = response.status
            val instrumentsResponse = response.data
            if (status.isSuccessful && instrumentsResponse != null) {
                val profileInstruments = instrumentsResponse.map { ir -> toProfileInstrument(ir) }
                return Result(profileInstruments, null)
            } else {
                return Result(null, ProfileErrors.UNKNOWN)
            }
        } else {
            throw ProfileException(ProfileErrors.USER_NOT_LOGGED_IN)
        }
    }

    override suspend fun update(updateProfile: UpdateProfile): Result<Profile?, ProfileErrors?> {
        if (user.isUserLoggedIn()) {
            val updateProfileRequest = UpdateProfileRequest(
                description = updateProfile.description,
                cityId = updateProfile.cityId,
                instrumentId = updateProfile.instrumentId
            )
            val response = profileDao.update(updateProfileRequest, user.getBearerAccessToken()!!)
            val status = response.status
            val profileResponse = response.data
            if (status.isSuccessful && profileResponse != null) {
                val updatedProfile = toProfile(profileResponse)
                return Result(updatedProfile, null)
            } else {
                return Result(null, ProfileErrors.UNKNOWN)
            }
        } else {
            throw ProfileException(ProfileErrors.USER_NOT_LOGGED_IN)
        }
    }

    override suspend fun icon(): Result<Bitmap?, ProfileErrors?> {
        if (user.isUserLoggedIn()) {
            val response = profileDao.icon(user.getBearerAccessToken()!!)
            val status = response.status
            val iconResponse = response.data
            if (status.isSuccessful && iconResponse != null) {
                return Result(iconResponse, null)
            } else {
                when (status.error) {
                    ProfileDaoResponseStatus.Errors.ICON_NOT_FOUND -> {
                        return Result(null, ProfileErrors.ICON_NOT_FOUND)
                    }
                    else -> {
                        return Result(null, ProfileErrors.UNKNOWN)
                    }
                }
            }
        } else {
            throw ProfileException(ProfileErrors.USER_NOT_LOGGED_IN)
        }
    }

    override suspend fun photo(): Result<Bitmap?, ProfileErrors?> {
        if (user.isUserLoggedIn()) {
            val response = profileDao.photo(user.getBearerAccessToken()!!)
            val status = response.status
            val photoResponse = response.data
            if (status.isSuccessful && photoResponse != null) {
                return Result(photoResponse, null)
            } else {
                when (status.error) {
                    ProfileDaoResponseStatus.Errors.PHOTO_NOT_FOUND -> {
                        return Result(null, ProfileErrors.PHOTO_NOT_FOUND)
                    }
                    else -> {
                        return Result(null, ProfileErrors.UNKNOWN)
                    }
                }
            }
        } else {
            throw ProfileException(ProfileErrors.USER_NOT_LOGGED_IN)
        }
    }

    override suspend fun updatePhoto(photo: Bitmap): Result<Bitmap?, ProfileErrors?> {
        if (user.isUserLoggedIn()) {
            val response = profileDao.updatePhoto(user.getBearerAccessToken()!!, photo)
            val status = response.status
            val photoResponse = response.data
            if (status.isSuccessful && photoResponse != null) {
                return Result(photoResponse, null)
            } else {
                when (status.error) {
                    ProfileDaoResponseStatus.Errors.ATTACHED_PHOTO_IS_INVALID -> {
                        return Result(null, ProfileErrors.ATTACHED_PHOTO_IS_INVALID)
                    }
                    else -> {
                        return Result(null, ProfileErrors.UNKNOWN)
                    }
                }
            }
        } else {
            throw ProfileException(ProfileErrors.USER_NOT_LOGGED_IN)
        }
    }

    override suspend fun reloadUnreadCount() {
        if (user.isUserLoggedIn()) {
            val response = profileDao.unreadCount(user.getBearerAccessToken()!!)
            val status = response.status
            val unreadCountResponse = response.data
            if (status.isSuccessful && unreadCountResponse != null) {
                this._unreadCount.value = toProfileUnreadCount(unreadCountResponse)
            }
        } else {
            throw ProfileException(ProfileErrors.USER_NOT_LOGGED_IN)
        }
    }

    private fun toProfile(profileResponse: ProfileResponse): Profile {
        val profileCity = ProfileCity(
            profileResponse.city?.id,
            profileResponse.city?.name
        )
        val profileInstrument = ProfileInstrument(
            profileResponse.instrument?.id,
            profileResponse.instrument?.name
        )
        return Profile(
            userId = profileResponse.userId,
            name = profileResponse.name,
            description = profileResponse.description,
            city = profileCity,
            instrument = profileInstrument,
            createdAt = profileResponse.createdAt
        )
    }

    private fun toProfileCity(cityResponse: CityResponse): ProfileCity {
        return ProfileCity(
            id = cityResponse.id,
            name = cityResponse.name
        )
    }

    private fun toProfileInstrument(instrumentResponse: InstrumentResponse): ProfileInstrument {
        return ProfileInstrument(
            id = instrumentResponse.id,
            name = instrumentResponse.name
        )
    }

    private fun toProfileUnreadCount(profileUnreadCountResponse: ProfileUnreadCountResponse): ProfileUnreadCount {
        return ProfileUnreadCount(
            unreadNotificationsCount = profileUnreadCountResponse.unreadNotificationsCount,
            unreadPrivateConversationsCount = profileUnreadCountResponse.unreadPrivateConversationsCount,
            unreadThomannConversationsCount = profileUnreadCountResponse.unreadThomannConversationsCount
        )
    }

    override fun loginCompleted(isSuccessful: Boolean, errors: AuthenticationErrors?) {
        if (isSuccessful) {
            _unreadCount.value = null
            utils.dispatcherUtils.main.cancelPeriodic(PROFILE_UNREAD_COUNT_PERIODIC_RELOAD)
            utils.dispatcherUtils.main.periodic(
                operationKey = PROFILE_UNREAD_COUNT_PERIODIC_RELOAD,
                period = 60.0,
                startImmediately = true,
                repeats = true
            ) {
                coroutineIOScope.launch {
                    reloadUnreadCount()
                }
            }
        }
    }

    override fun logoutCompleted(isSuccessful: Boolean, errors: AuthenticationErrors?) {
        if (isSuccessful) {
            utils.dispatcherUtils.main.cancelPeriodic(PROFILE_UNREAD_COUNT_PERIODIC_RELOAD)
        }
    }
}