package com.dauma.grokimkartu.repositories.profile

import android.graphics.Bitmap
import com.dauma.grokimkartu.data.cities.CitiesDao
import com.dauma.grokimkartu.data.cities.entities.CityResponse
import com.dauma.grokimkartu.data.profile.ProfileDao
import com.dauma.grokimkartu.data.profile.ProfileDaoResponseStatus
import com.dauma.grokimkartu.data.profile.entities.ProfileResponse
import com.dauma.grokimkartu.data.profile.entities.UpdateProfileRequest
import com.dauma.grokimkartu.general.user.User
import com.dauma.grokimkartu.repositories.profile.entities.Profile
import com.dauma.grokimkartu.repositories.profile.entities.ProfileCity
import com.dauma.grokimkartu.repositories.profile.entities.UpdateProfile

class ProfileRepositoryImpl(
    private val profileDao: ProfileDao,
    private val citiesDao: CitiesDao,
    private val user: User
) : ProfileRepository {
    override fun profile(onComplete: (Profile?, ProfileErrors?) -> Unit) {
        if (user.isUserLoggedIn()) {
            profileDao.profile(user.getBearerAccessToken()!!) { profileResponse, profileDaoResponseStatus ->
                if (profileDaoResponseStatus.isSuccessful && profileResponse != null) {
                    val profile = toProfile(profileResponse)
                    onComplete(profile, null)
                } else {
                    onComplete(null, ProfileErrors.UNKNOWN)
                }
            }
        } else {
            throw ProfileException(ProfileErrors.USER_NOT_LOGGED_IN)
        }
    }

    override fun cities(onComplete: (List<ProfileCity>?, ProfileErrors?) -> Unit) {
        if (user.isUserLoggedIn()) {
            citiesDao.cities(user.getBearerAccessToken()!!) { citiesResponse, citiesDaoResponseStatus ->
                if (citiesDaoResponseStatus.isSuccessful && citiesResponse != null) {
                    val profileCities = citiesResponse.map { cr -> toProfileCity(cr) }
                    onComplete(profileCities, null)
                } else {
                    onComplete(null, ProfileErrors.UNKNOWN)
                }
            }
        } else {
            throw ProfileException(ProfileErrors.USER_NOT_LOGGED_IN)
        }
    }

    override fun searchCity(
        value: String,
        onComplete: (List<ProfileCity>?, ProfileErrors?) -> Unit
    ) {
        if (user.isUserLoggedIn()) {
            citiesDao.search(value, user.getBearerAccessToken()!!) { citiesResponse, citiesDaoResponseStatus ->
                if (citiesDaoResponseStatus.isSuccessful && citiesResponse != null) {
                    val profileCities = citiesResponse.map { cr -> toProfileCity(cr) }
                    onComplete(profileCities, null)
                } else {
                    onComplete(null, ProfileErrors.UNKNOWN)
                }
            }
        } else {
            throw ProfileException(ProfileErrors.USER_NOT_LOGGED_IN)
        }
    }

    override fun update(updateProfile: UpdateProfile, onComplete: (Profile?, ProfileErrors?) -> Unit) {
        if (user.isUserLoggedIn()) {
            val updateProfileRequest = UpdateProfileRequest(
                description = updateProfile.description,
                cityId = updateProfile.cityId,
                instrumentId = updateProfile.instrumentId
            )
            profileDao.update(updateProfileRequest, user.getBearerAccessToken()!!) { profileResponse, profileDaoResponseStatus ->
                if (profileDaoResponseStatus.isSuccessful && profileResponse != null) {
                    val updatedProfile = toProfile(profileResponse)
                    onComplete(updatedProfile, null)
                } else {
                    onComplete(null, ProfileErrors.UNKNOWN)
                }
            }
        } else {
            throw ProfileException(ProfileErrors.USER_NOT_LOGGED_IN)
        }
    }

    override fun icon(onComplete: (Bitmap?, ProfileErrors?) -> Unit) {
        if (user.isUserLoggedIn()) {
            profileDao.icon(user.getBearerAccessToken()!!) { icon, profileDaoResponseStatus ->
                if (profileDaoResponseStatus.isSuccessful && icon != null) {
                    onComplete(icon, null)
                } else {
                    when (profileDaoResponseStatus.error) {
                        ProfileDaoResponseStatus.Errors.ICON_NOT_FOUND -> {
                            onComplete(null, ProfileErrors.ICON_NOT_FOUND)
                        }
                        else -> {
                            onComplete(null, ProfileErrors.UNKNOWN)
                        }
                    }
                }
            }
        } else {
            throw ProfileException(ProfileErrors.USER_NOT_LOGGED_IN)
        }
    }

    override fun photo(onComplete: (Bitmap?, ProfileErrors?) -> Unit) {
        if (user.isUserLoggedIn()) {
            profileDao.photo(user.getBearerAccessToken()!!) { photo, profileDaoResponseStatus ->
                if (profileDaoResponseStatus.isSuccessful && photo != null) {
                    onComplete(photo, null)
                } else {
                    when (profileDaoResponseStatus.error) {
                        ProfileDaoResponseStatus.Errors.PHOTO_NOT_FOUND -> {
                            onComplete(null, ProfileErrors.PHOTO_NOT_FOUND)
                        }
                        else -> {
                            onComplete(null, ProfileErrors.UNKNOWN)
                        }
                    }
                }
            }
        } else {
            throw ProfileException(ProfileErrors.USER_NOT_LOGGED_IN)
        }
    }

    override fun updatePhoto(photo: Bitmap, onComplete: (Bitmap?, ProfileErrors?) -> Unit) {
        if (user.isUserLoggedIn()) {
            profileDao.updatePhoto(user.getBearerAccessToken()!!, photo) { updatedPhoto, profileDaoResponseStatus ->
                if (profileDaoResponseStatus.isSuccessful && updatedPhoto != null) {
                    onComplete(updatedPhoto, null)
                } else {
                    when (profileDaoResponseStatus.error) {
                        ProfileDaoResponseStatus.Errors.ATTACHED_PHOTO_IS_INVALID -> {
                            onComplete(null, ProfileErrors.ATTACHED_PHOTO_IS_INVALID)
                        }
                        else -> {
                            onComplete(null, ProfileErrors.UNKNOWN)
                        }
                    }
                }
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
        return Profile(
            userId = profileResponse.user,
            name = profileResponse.name,
            description = profileResponse.description,
            city = profileCity,
            instrument = profileResponse.instrument,
            createdAt = profileResponse.createdAt
        )
    }

    private fun toProfileCity(cityResponse: CityResponse): ProfileCity {
        return ProfileCity(
            id = cityResponse.id,
            name = cityResponse.name
        )
    }
}