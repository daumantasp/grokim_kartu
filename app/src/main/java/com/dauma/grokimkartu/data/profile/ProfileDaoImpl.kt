package com.dauma.grokimkartu.data.profile

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.dauma.grokimkartu.data.profile.entities.ProfileResponse
import com.dauma.grokimkartu.data.profile.entities.ProfileUnreadCountResponse
import com.dauma.grokimkartu.data.profile.entities.UpdateProfileRequest
import com.dauma.grokimkartu.general.utils.image.ImageUtils
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.*

class ProfileDaoImpl(
    retrofit: Retrofit,
    private val imageUtils: ImageUtils
): ProfileDao {
    private val retrofitProfile: RetrofitProfile = retrofit.create(RetrofitProfile::class.java)

    override fun profile(
        accessToken: String,
        onComplete: (ProfileResponse?, ProfileDaoResponseStatus) -> Unit
    ) {
        retrofitProfile.profile(accessToken).enqueue(object : Callback<ProfileResponse> {
            override fun onResponse(
                call: Call<ProfileResponse>,
                response: Response<ProfileResponse>
            ) {
                when (response.code()) {
                    200 -> {
                        val profileResponseData = response.body()
                        val status = ProfileDaoResponseStatus(true, null)
                        onComplete(profileResponseData, status)
                    }
                    500 -> {
                        val status = ProfileDaoResponseStatus(false, ProfileDaoResponseStatus.Errors.PROFILE_NOT_FOUND)
                        onComplete(null, status)
                    }
                    else -> {
                        val status = ProfileDaoResponseStatus(false, ProfileDaoResponseStatus.Errors.UNKNOWN)
                        onComplete(null, status)
                    }
                }
            }

            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                val status = ProfileDaoResponseStatus(false, ProfileDaoResponseStatus.Errors.UNKNOWN)
                onComplete(null, status)
            }
        })
    }

    override fun update(
        updateProfileRequest: UpdateProfileRequest,
        accessToken: String,
        onComplete: (ProfileResponse?, ProfileDaoResponseStatus) -> Unit
    ) {
        retrofitProfile.update(accessToken, updateProfileRequest).enqueue(object : Callback<ProfileResponse> {
            override fun onResponse(
                call: Call<ProfileResponse>,
                response: Response<ProfileResponse>
            ) {
                when (response.code()) {
                    200 -> {
                        val profileResponseData = response.body()
                        val status = ProfileDaoResponseStatus(true, null)
                        onComplete(profileResponseData, status)
                    }
                    500 -> {
                        val status = ProfileDaoResponseStatus(false, ProfileDaoResponseStatus.Errors.PROFILE_NOT_FOUND)
                        onComplete(null, status)
                    }
                    else -> {
                        val status = ProfileDaoResponseStatus(false, ProfileDaoResponseStatus.Errors.UNKNOWN)
                        onComplete(null, status)
                    }
                }
            }

            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                val status = ProfileDaoResponseStatus(false, ProfileDaoResponseStatus.Errors.UNKNOWN)
                onComplete(null, status)
            }
        })
    }

    override fun icon(
        accessToken: String,
        onComplete: (Bitmap?, ProfileDaoResponseStatus) -> Unit
    ) {
        retrofitProfile.icon(accessToken).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                when (response.code()) {
                    200 -> {
                        val profileResponseData = response.body()
                        val stream = profileResponseData?.byteStream()
                        if (stream != null) {
                            val bitmap = BitmapFactory.decodeStream(stream)
                            val status = ProfileDaoResponseStatus(true, null)
                            onComplete(bitmap, status)
                        } else {
                            val status = ProfileDaoResponseStatus(false, ProfileDaoResponseStatus.Errors.UNKNOWN)
                            onComplete(null, status)
                        }
                    }
                    404 -> {
                        val status = ProfileDaoResponseStatus(false, ProfileDaoResponseStatus.Errors.ICON_NOT_FOUND)
                        onComplete(null, status)
                    }
                    else -> {
                        val status = ProfileDaoResponseStatus(false, ProfileDaoResponseStatus.Errors.UNKNOWN)
                        onComplete(null, status)
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                val status = ProfileDaoResponseStatus(false, ProfileDaoResponseStatus.Errors.UNKNOWN)
                onComplete(null, status)
            }
        })
    }

    override fun photo(
        accessToken: String,
        onComplete: (Bitmap?, ProfileDaoResponseStatus) -> Unit
    ) {
        retrofitProfile.photo(accessToken).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                when (response.code()) {
                    200 -> {
                        val profileResponseData = response.body()
                        val stream = profileResponseData?.byteStream()
                        if (stream != null) {
                            val bitmap = BitmapFactory.decodeStream(stream)
                            val status = ProfileDaoResponseStatus(true, null)
                            onComplete(bitmap, status)
                        } else {
                            val status = ProfileDaoResponseStatus(false, ProfileDaoResponseStatus.Errors.UNKNOWN)
                            onComplete(null, status)
                        }
                    }
                    404 -> {
                        val status = ProfileDaoResponseStatus(false, ProfileDaoResponseStatus.Errors.PHOTO_NOT_FOUND)
                        onComplete(null, status)
                    }
                    else -> {
                        val status = ProfileDaoResponseStatus(false, ProfileDaoResponseStatus.Errors.UNKNOWN)
                        onComplete(null, status)
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                val status = ProfileDaoResponseStatus(false, ProfileDaoResponseStatus.Errors.UNKNOWN)
                onComplete(null, status)
            }
        })
    }

    override fun updatePhoto(
        accessToken: String,
        photo: Bitmap,
        onComplete: (Bitmap?, ProfileDaoResponseStatus) -> Unit
    ) {
        val photoByteArray = imageUtils.convertBitmapToByteArray(photo)
        val photoRequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), photoByteArray);
        val body = MultipartBody.Part.createFormData("image", "image.jpg", photoRequestBody)

        retrofitProfile.updatePhoto(accessToken, body).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                when (response.code()) {
                    200 -> {
                        val profileResponseData = response.body()
                        val stream = profileResponseData?.byteStream()
                        if (stream != null) {
                            val bitmap = BitmapFactory.decodeStream(stream)
                            val status = ProfileDaoResponseStatus(true, null)
                            onComplete(bitmap, status)
                        } else {
                            val status = ProfileDaoResponseStatus(false, ProfileDaoResponseStatus.Errors.UNKNOWN)
                            onComplete(null, status)
                        }
                    }
                    400 -> {
                        val errorBody = response.errorBody()?.string() ?: ""
                        if (errorBody.contains(ProfileDaoResponseStatus.Errors.PHOTO_NOT_ATTACHED.toString(), true)) {
                            val status = ProfileDaoResponseStatus(false, ProfileDaoResponseStatus.Errors.PHOTO_NOT_ATTACHED)
                            onComplete(null, status)
                        } else if (errorBody.contains(ProfileDaoResponseStatus.Errors.ATTACHED_PHOTO_IS_INVALID.toString(), true)) {
                            val status = ProfileDaoResponseStatus(false, ProfileDaoResponseStatus.Errors.ATTACHED_PHOTO_IS_INVALID)
                            onComplete(null, status)
                        } else if (errorBody.contains(ProfileDaoResponseStatus.Errors.INCORRECT_PHOTO_IMAGE_FORMAT.toString(), true)) {
                            val status = ProfileDaoResponseStatus(false, ProfileDaoResponseStatus.Errors.INCORRECT_PHOTO_IMAGE_FORMAT)
                            onComplete(null, status)
                        }
                    }
                    else -> {
                        val status = ProfileDaoResponseStatus(false, ProfileDaoResponseStatus.Errors.UNKNOWN)
                        onComplete(null, status)
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                val status = ProfileDaoResponseStatus(false, ProfileDaoResponseStatus.Errors.UNKNOWN)
                onComplete(null, status)
            }
        })
    }

    override fun unreadCount(
        accessToken: String,
        onComplete: (ProfileUnreadCountResponse?, ProfileDaoResponseStatus) -> Unit
    ) {
        retrofitProfile.unreadCount(accessToken).enqueue(object : Callback<ProfileUnreadCountResponse> {
            override fun onResponse(
                call: Call<ProfileUnreadCountResponse>,
                response: Response<ProfileUnreadCountResponse>
            ) {
                when (response.code()) {
                    200 -> {
                        val profileUnreadCountResponse = response.body()
                        val status = ProfileDaoResponseStatus(true, null)
                        onComplete(profileUnreadCountResponse, status)
                    }
                    else -> {
                        val status = ProfileDaoResponseStatus(false, ProfileDaoResponseStatus.Errors.UNKNOWN)
                        onComplete(null, status)
                    }
                }
            }

            override fun onFailure(call: Call<ProfileUnreadCountResponse>, t: Throwable) {
                val status = ProfileDaoResponseStatus(false, ProfileDaoResponseStatus.Errors.UNKNOWN)
                onComplete(null, status)
            }
        })
    }

    private interface RetrofitProfile {
        @GET("profile") fun profile(@Header("Authorization") accessToken: String): Call<ProfileResponse>
        @PUT("profile") fun update(@Header("Authorization") accessToken: String, @Body updateRequest: UpdateProfileRequest): Call<ProfileResponse>
        @GET("profile/icon") fun icon(@Header("Authorization") accessToken: String): Call<ResponseBody>
        @GET("profile/photo") fun photo(@Header("Authorization") accessToken: String): Call<ResponseBody>
        @Multipart @POST("profile/photo") fun updatePhoto(@Header("Authorization") accessToken: String, @Part image: MultipartBody.Part): Call<ResponseBody>
        @GET("profile/unreadCount") fun unreadCount(@Header("Authorization") accessToken: String): Call<ProfileUnreadCountResponse>
    }
}