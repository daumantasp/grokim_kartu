package com.dauma.grokimkartu.data.profile

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.dauma.grokimkartu.data.DaoResult
import com.dauma.grokimkartu.data.profile.entities.ProfileResponse
import com.dauma.grokimkartu.data.profile.entities.ProfileUnreadCountResponse
import com.dauma.grokimkartu.data.profile.entities.UpdateProfileRequest
import com.dauma.grokimkartu.general.utils.image.ImageUtils
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.*

class ProfileDaoImpl(
    retrofit: Retrofit,
    private val imageUtils: ImageUtils
): ProfileDao {
    private val retrofitProfile: RetrofitProfile = retrofit.create(RetrofitProfile::class.java)

    override suspend fun profile(accessToken: String): DaoResult<ProfileResponse?, ProfileDaoResponseStatus> {
        val response = retrofitProfile.profile(accessToken)

        if (response.isSuccessful) {
            when (response.code()) {
                200 -> {
                    val profileResponseData = response.body()
                    val status = ProfileDaoResponseStatus(true, null)
                    return DaoResult(profileResponseData, status)
                }
                500 -> {
                    val status = ProfileDaoResponseStatus(false, ProfileDaoResponseStatus.Errors.PROFILE_NOT_FOUND)
                    return DaoResult(null, status)
                }
                else -> {
                    val status = ProfileDaoResponseStatus(false, ProfileDaoResponseStatus.Errors.UNKNOWN)
                    return DaoResult(null, status)
                }
            }
        } else {
            val status = ProfileDaoResponseStatus(false, ProfileDaoResponseStatus.Errors.UNKNOWN)
            return DaoResult(null, status)
        }
    }

    override suspend fun update(
        updateProfileRequest: UpdateProfileRequest,
        accessToken: String
    ): DaoResult<ProfileResponse?, ProfileDaoResponseStatus> {
        val response = retrofitProfile.update(accessToken, updateProfileRequest)

        if (response.isSuccessful) {
            when (response.code()) {
                200 -> {
                    val profileResponseData = response.body()
                    val status = ProfileDaoResponseStatus(true, null)
                    return DaoResult(profileResponseData, status)
                }
                500 -> {
                    val status = ProfileDaoResponseStatus(false, ProfileDaoResponseStatus.Errors.PROFILE_NOT_FOUND)
                    return DaoResult(null, status)
                }
                else -> {
                    val status = ProfileDaoResponseStatus(false, ProfileDaoResponseStatus.Errors.UNKNOWN)
                    return DaoResult(null, status)
                }
            }
        } else {
            val status = ProfileDaoResponseStatus(false, ProfileDaoResponseStatus.Errors.UNKNOWN)
            return DaoResult(null, status)
        }
    }

    override suspend fun icon(accessToken: String): DaoResult<Bitmap?, ProfileDaoResponseStatus> {
        val response = retrofitProfile.icon(accessToken)

        if (response.isSuccessful) {
            when (response.code()) {
                200 -> {
                    val profileResponseData = response.body()
                    val stream = profileResponseData?.byteStream()
                    if (stream != null) {
                        val bitmap = BitmapFactory.decodeStream(stream)
                        val status = ProfileDaoResponseStatus(true, null)
                        return DaoResult(bitmap, status)
                    } else {
                        val status = ProfileDaoResponseStatus(false, ProfileDaoResponseStatus.Errors.UNKNOWN)
                        return DaoResult(null, status)
                    }
                }
                404 -> {
                    val status = ProfileDaoResponseStatus(false, ProfileDaoResponseStatus.Errors.ICON_NOT_FOUND)
                    return DaoResult(null, status)
                }
                else -> {
                    val status = ProfileDaoResponseStatus(false, ProfileDaoResponseStatus.Errors.UNKNOWN)
                    return DaoResult(null, status)
                }
            }
        } else {
            val status = ProfileDaoResponseStatus(false, ProfileDaoResponseStatus.Errors.UNKNOWN)
            return DaoResult(null, status)
        }
    }

    override suspend fun photo(accessToken: String): DaoResult<Bitmap?, ProfileDaoResponseStatus> {
        val response = retrofitProfile.photo(accessToken)

        if (response.isSuccessful) {
            when (response.code()) {
                200 -> {
                    val profileResponseData = response.body()
                    val stream = profileResponseData?.byteStream()
                    if (stream != null) {
                        val bitmap = BitmapFactory.decodeStream(stream)
                        val status = ProfileDaoResponseStatus(true, null)
                        return DaoResult(bitmap, status)
                    } else {
                        val status = ProfileDaoResponseStatus(false, ProfileDaoResponseStatus.Errors.UNKNOWN)
                        return DaoResult(null, status)
                    }
                }
                404 -> {
                    val status = ProfileDaoResponseStatus(false, ProfileDaoResponseStatus.Errors.PHOTO_NOT_FOUND)
                    return DaoResult(null, status)
                }
                else -> {
                    val status = ProfileDaoResponseStatus(false, ProfileDaoResponseStatus.Errors.UNKNOWN)
                    return DaoResult(null, status)
                }
            }
        } else {
            val status = ProfileDaoResponseStatus(false, ProfileDaoResponseStatus.Errors.UNKNOWN)
            return DaoResult(null, status)
        }
    }

    override suspend fun updatePhoto(
        accessToken: String,
        photo: Bitmap
    ): DaoResult<Bitmap?, ProfileDaoResponseStatus> {
        val photoByteArray = imageUtils.convertBitmapToByteArray(photo)
        val photoRequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), photoByteArray);
        val body = MultipartBody.Part.createFormData("image", "image.jpg", photoRequestBody)

        val response = retrofitProfile.updatePhoto(accessToken, body)

        if (response.isSuccessful) {
            when (response.code()) {
                200 -> {
                    val profileResponseData = response.body()
                    val stream = profileResponseData?.byteStream()
                    if (stream != null) {
                        val bitmap = BitmapFactory.decodeStream(stream)
                        val status = ProfileDaoResponseStatus(true, null)
                        return DaoResult(bitmap, status)
                    } else {
                        val status = ProfileDaoResponseStatus(false, ProfileDaoResponseStatus.Errors.UNKNOWN)
                        return DaoResult(null, status)
                    }
                }
                400 -> {
                    val errorBody = response.errorBody()?.string() ?: ""
                    if (errorBody.contains(ProfileDaoResponseStatus.Errors.PHOTO_NOT_ATTACHED.toString(), true)) {
                        val status = ProfileDaoResponseStatus(false, ProfileDaoResponseStatus.Errors.PHOTO_NOT_ATTACHED)
                        return DaoResult(null, status)
                    } else if (errorBody.contains(ProfileDaoResponseStatus.Errors.ATTACHED_PHOTO_IS_INVALID.toString(), true)) {
                        val status = ProfileDaoResponseStatus(false, ProfileDaoResponseStatus.Errors.ATTACHED_PHOTO_IS_INVALID)
                        return DaoResult(null, status)
                    } else if (errorBody.contains(ProfileDaoResponseStatus.Errors.INCORRECT_PHOTO_IMAGE_FORMAT.toString(), true)) {
                        val status = ProfileDaoResponseStatus(false, ProfileDaoResponseStatus.Errors.INCORRECT_PHOTO_IMAGE_FORMAT)
                        return DaoResult(null, status)
                    } else {
                        val status = ProfileDaoResponseStatus(false, ProfileDaoResponseStatus.Errors.UNKNOWN)
                        return DaoResult(null, status)
                    }
                }
                else -> {
                    val status = ProfileDaoResponseStatus(false, ProfileDaoResponseStatus.Errors.UNKNOWN)
                    return DaoResult(null, status)
                }
            }
        } else {
            val status = ProfileDaoResponseStatus(false, ProfileDaoResponseStatus.Errors.UNKNOWN)
            return DaoResult(null, status)
        }
    }

    override suspend fun unreadCount(accessToken: String): DaoResult<ProfileUnreadCountResponse?, ProfileDaoResponseStatus> {
        val response = retrofitProfile.unreadCount(accessToken)

        if (response.isSuccessful) {
            when (response.code()) {
                200 -> {
                    val profileUnreadCountResponse = response.body()
                    val status = ProfileDaoResponseStatus(true, null)
                    return DaoResult(profileUnreadCountResponse, status)
                }
                else -> {
                    val status = ProfileDaoResponseStatus(false, ProfileDaoResponseStatus.Errors.UNKNOWN)
                    return DaoResult(null, status)
                }
            }
        } else {
            val status = ProfileDaoResponseStatus(false, ProfileDaoResponseStatus.Errors.UNKNOWN)
            return DaoResult(null, status)
        }
    }

    private interface RetrofitProfile {
        @GET("profile") suspend fun profile(@Header("Authorization") accessToken: String): Response<ProfileResponse>
        @PUT("profile") suspend fun update(@Header("Authorization") accessToken: String, @Body updateRequest: UpdateProfileRequest): Response<ProfileResponse>
        @GET("profile/icon") suspend fun icon(@Header("Authorization") accessToken: String): Response<ResponseBody>
        @GET("profile/photo") suspend fun photo(@Header("Authorization") accessToken: String): Response<ResponseBody>
        @Multipart @POST("profile/photo") suspend fun updatePhoto(@Header("Authorization") accessToken: String, @Part image: MultipartBody.Part): Response<ResponseBody>
        @GET("profile/unreadCount") suspend fun unreadCount(@Header("Authorization") accessToken: String): Response<ProfileUnreadCountResponse>
    }
}