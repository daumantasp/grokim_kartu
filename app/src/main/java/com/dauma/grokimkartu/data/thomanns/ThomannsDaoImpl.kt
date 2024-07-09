package com.dauma.grokimkartu.data.thomanns

import com.dauma.grokimkartu.data.DaoResult
import com.dauma.grokimkartu.data.thomanns.entities.*
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.*

class ThomannsDaoImpl(retrofit: Retrofit) : ThomannsDao {
    private val retrofitThomanns: RetrofitThomanns = retrofit.create(RetrofitThomanns::class.java)

    override suspend fun create(
        createRequest: CreateThomannRequest,
        accessToken: String
    ): DaoResult<ThomannDetailsResponse?, ThomannsDaoResponseStatus> {
        val response = retrofitThomanns.createThomann(accessToken, createRequest)

        if (response.isSuccessful) {
            when (response.code()) {
                201 -> {
                    val thomannDetailsResponse = response.body()
                    val status = ThomannsDaoResponseStatus(true, null)
                    return DaoResult(thomannDetailsResponse, status)
                }
                400 -> {
                    val status = ThomannsDaoResponseStatus(false, ThomannsDaoResponseStatus.Errors.INVALID_VALID_UNTIL_DATE)
                    return DaoResult(null, status)
                }
                else -> {
                    val status = ThomannsDaoResponseStatus(false, ThomannsDaoResponseStatus.Errors.UNKNOWN)
                    return DaoResult(null, status)
                }
            }
        } else {
            val status = ThomannsDaoResponseStatus(false, ThomannsDaoResponseStatus.Errors.UNKNOWN)
            return DaoResult(null, status)
        }
    }

    override suspend fun update(
        thomannId: Int,
        updateRequest: UpdateThomannRequest,
        accessToken: String
    ): DaoResult<ThomannDetailsResponse?, ThomannsDaoResponseStatus> {
        val response = retrofitThomanns.updateThomann(accessToken, thomannId, updateRequest)

        if (response.isSuccessful) {
            when (response.code()) {
                200 -> {
                    val thomannDetailsResponse = response.body()
                    val status = ThomannsDaoResponseStatus(true, null)
                    return DaoResult(thomannDetailsResponse, status)
                }
                400 -> {
                    val status = ThomannsDaoResponseStatus(false, ThomannsDaoResponseStatus.Errors.INVALID_VALID_UNTIL_DATE)
                    return DaoResult(null, status)
                }
                403 -> {
                    val status = ThomannsDaoResponseStatus(false, ThomannsDaoResponseStatus.Errors.FORBIDDEN)
                    return DaoResult(null, status)
                }
                404 -> {
                    val status = ThomannsDaoResponseStatus(false, ThomannsDaoResponseStatus.Errors.THOMANN_NOT_FOUND)
                    return DaoResult(null, status)
                }
                else -> {
                    val status = ThomannsDaoResponseStatus(false, ThomannsDaoResponseStatus.Errors.UNKNOWN)
                    return DaoResult(null, status)
                }
            }
        } else {
            val status = ThomannsDaoResponseStatus(false, ThomannsDaoResponseStatus.Errors.UNKNOWN)
            return DaoResult(null, status)
        }
    }

    override suspend fun delete(
        thomannId: Int,
        accessToken: String
    ): DaoResult<Nothing?, ThomannsDaoResponseStatus> {
        val response = retrofitThomanns.delete(accessToken, thomannId)

        if (response.isSuccessful) {
            when (response.code()) {
                200 -> {
                    val status = ThomannsDaoResponseStatus(true, null)
                    return DaoResult(null, status)
                }
                403 -> {
                    val status = ThomannsDaoResponseStatus(false, ThomannsDaoResponseStatus.Errors.FORBIDDEN)
                    return DaoResult(null, status)
                }
                404 -> {
                    val status = ThomannsDaoResponseStatus(false, ThomannsDaoResponseStatus.Errors.THOMANN_NOT_FOUND)
                    return DaoResult(null, status)
                }
                else -> {
                    val status = ThomannsDaoResponseStatus(false, ThomannsDaoResponseStatus.Errors.UNKNOWN)
                    return DaoResult(null, status)
                }
            }
        } else {
            val status = ThomannsDaoResponseStatus(false, ThomannsDaoResponseStatus.Errors.UNKNOWN)
            return DaoResult(null, status)
        }
    }

    override suspend fun thomanns(
        thomannsRequest: ThomannsRequest,
        accessToken: String
    ): DaoResult<ThomannsResponse?, ThomannsDaoResponseStatus> {
        val response = retrofitThomanns.thomanns(
            page = thomannsRequest.page,
            pageSize = thomannsRequest.pageSize,
            cityId = thomannsRequest.cityId,
            validUntil = thomannsRequest.validUntil,
            isLocked = thomannsRequest.isLocked,
            accessToken = accessToken
        )

        if (response.isSuccessful) {
            when (response.code()) {
                200 -> {
                    val thomannsResponse = response.body()
                    val status = ThomannsDaoResponseStatus(true, null)
                    return DaoResult(thomannsResponse, status)
                }
                else -> {
                    val status = ThomannsDaoResponseStatus(false, ThomannsDaoResponseStatus.Errors.UNKNOWN)
                    return DaoResult(null, status)
                }
            }
        } else {
            val status = ThomannsDaoResponseStatus(false, ThomannsDaoResponseStatus.Errors.UNKNOWN)
            return DaoResult(null, status)
        }
    }

    override suspend fun myThomanns(
        page: Int,
        pageSize: Int,
        accessToken: String
    ): DaoResult<ThomannsResponse?, ThomannsDaoResponseStatus> {
        val response = retrofitThomanns.myThomanns(page, pageSize, accessToken)

        if (response.isSuccessful) {
            when (response.code()) {
                200 -> {
                    val thomannsResponse = response.body()
                    val status = ThomannsDaoResponseStatus(true, null)
                    return DaoResult(thomannsResponse, status)
                }
                else -> {
                    val status = ThomannsDaoResponseStatus(false, ThomannsDaoResponseStatus.Errors.UNKNOWN)
                    return DaoResult(null, status)
                }
            }
        } else {
            val status = ThomannsDaoResponseStatus(false, ThomannsDaoResponseStatus.Errors.UNKNOWN)
            return DaoResult(null, status)
        }
    }

    override suspend fun thomannDetails(
        thomannId: Int,
        accessToken: String
    ): DaoResult<ThomannDetailsResponse?, ThomannsDaoResponseStatus> {
        val response = retrofitThomanns.thomannDetails(accessToken, thomannId)

        if (response.isSuccessful) {
            when (response.code()) {
                200 -> {
                    val thomannDetailsResponse = response.body()
                    val status = ThomannsDaoResponseStatus(true, null)
                    return DaoResult(thomannDetailsResponse, status)
                }
                403 -> {
                    val status = ThomannsDaoResponseStatus(false, ThomannsDaoResponseStatus.Errors.FORBIDDEN)
                    return DaoResult(null, status)
                }
                404 -> {
                    val status = ThomannsDaoResponseStatus(false, ThomannsDaoResponseStatus.Errors.THOMANN_NOT_FOUND)
                    return DaoResult(null, status)
                }
                else -> {
                    val status = ThomannsDaoResponseStatus(false, ThomannsDaoResponseStatus.Errors.UNKNOWN)
                    return DaoResult(null, status)
                }
            }
        } else {
            val status = ThomannsDaoResponseStatus(false, ThomannsDaoResponseStatus.Errors.UNKNOWN)
            return DaoResult(null, status)
        }
    }

    override suspend fun join(
        thomannId: Int,
        joinRequest: JoinThomannRequest,
        accessToken: String
    ): DaoResult<ThomannDetailsResponse?, ThomannsDaoResponseStatus> {
        val response = retrofitThomanns.joinThomann(accessToken, thomannId, joinRequest)

        if (response.isSuccessful) {
            when (response.code()) {
                200 -> {
                    val thomannDetailsResponse = response.body()
                    val status = ThomannsDaoResponseStatus(true, null)
                    return DaoResult(thomannDetailsResponse, status)
                }
                400 -> {
                    val errorBody = response.errorBody()?.string() ?: ""
                    if (errorBody.contains(ThomannsDaoResponseStatus.Errors.NOT_JOINABLE_FOR_OWNER.toString(), true)) {
                        val status = ThomannsDaoResponseStatus(false, ThomannsDaoResponseStatus.Errors.NOT_JOINABLE_FOR_OWNER)
                        return DaoResult(null, status)
                    } else if (errorBody.contains(ThomannsDaoResponseStatus.Errors.ALREADY_JOINED.toString(), true)) {
                        val status = ThomannsDaoResponseStatus(false, ThomannsDaoResponseStatus.Errors.ALREADY_JOINED)
                        return DaoResult(null, status)
                    } else if (errorBody.contains(ThomannsDaoResponseStatus.Errors.INVALID_VALID_UNTIL_DATE.toString(), true)) {
                        val status = ThomannsDaoResponseStatus(false, ThomannsDaoResponseStatus.Errors.INVALID_VALID_UNTIL_DATE)
                        return DaoResult(null, status)
                    } else if (errorBody.contains(ThomannsDaoResponseStatus.Errors.INVALID_AMOUNT.toString(), true)) {
                        val status = ThomannsDaoResponseStatus(false, ThomannsDaoResponseStatus.Errors.INVALID_AMOUNT)
                        return DaoResult(null, status)
                    } else {
                        val status = ThomannsDaoResponseStatus(false, ThomannsDaoResponseStatus.Errors.FORBIDDEN)
                        return DaoResult(null, status)
                    }
                }
                403 -> {
                    val status = ThomannsDaoResponseStatus(false, ThomannsDaoResponseStatus.Errors.FORBIDDEN)
                    return DaoResult(null, status)
                }
                404 -> {
                    val status = ThomannsDaoResponseStatus(false, ThomannsDaoResponseStatus.Errors.THOMANN_NOT_FOUND)
                    return DaoResult(null, status)
                }
                else -> {
                    val status = ThomannsDaoResponseStatus(false, ThomannsDaoResponseStatus.Errors.UNKNOWN)
                    return DaoResult(null, status)
                }
            }
        } else {
            val status = ThomannsDaoResponseStatus(false, ThomannsDaoResponseStatus.Errors.UNKNOWN)
            return DaoResult(null, status)
        }
    }

    override suspend fun quit(
        thomannId: Int,
        accessToken: String
    ): DaoResult<ThomannDetailsResponse?, ThomannsDaoResponseStatus> {
        val response = retrofitThomanns.quitThomann(accessToken, thomannId)

        if (response.isSuccessful) {
            when (response.code()) {
                200 -> {
                    val thomannDetailsResponse = response.body()
                    val status = ThomannsDaoResponseStatus(true, null)
                    return DaoResult(thomannDetailsResponse, status)
                }
                400 -> {
                    val errorBody = response.errorBody()?.string() ?: ""
                    if (errorBody.contains(ThomannsDaoResponseStatus.Errors.NOT_QUITABLE_FOR_OWNER.toString(), true)) {
                        val status = ThomannsDaoResponseStatus(false, ThomannsDaoResponseStatus.Errors.NOT_QUITABLE_FOR_OWNER)
                        return DaoResult(null, status)
                    } else if (errorBody.contains(ThomannsDaoResponseStatus.Errors.NOT_A_THOMANN_USER.toString(), true)) {
                        val status = ThomannsDaoResponseStatus(false, ThomannsDaoResponseStatus.Errors.NOT_A_THOMANN_USER)
                        return DaoResult(null, status)
                    } else {
                        val status = ThomannsDaoResponseStatus(false, ThomannsDaoResponseStatus.Errors.FORBIDDEN)
                        return DaoResult(null, status)
                    }
                }
                403 -> {
                    val status = ThomannsDaoResponseStatus(false, ThomannsDaoResponseStatus.Errors.FORBIDDEN)
                    return DaoResult(null, status)
                }
                404 -> {
                    val status = ThomannsDaoResponseStatus(false, ThomannsDaoResponseStatus.Errors.THOMANN_NOT_FOUND)
                    return DaoResult(null, status)
                }
                else -> {
                    val status = ThomannsDaoResponseStatus(false, ThomannsDaoResponseStatus.Errors.UNKNOWN)
                    return DaoResult(null, status)
                }
            }
        } else {
            val status = ThomannsDaoResponseStatus(false, ThomannsDaoResponseStatus.Errors.UNKNOWN)
            return DaoResult(null, status)
        }
    }

    override suspend fun kick(
        thomannId: Int,
        kickRequest: KickThomannRequest,
        accessToken: String
    ): DaoResult<ThomannDetailsResponse?, ThomannsDaoResponseStatus> {
        val response = retrofitThomanns.kickThomann(accessToken, thomannId, kickRequest)

        if (response.isSuccessful) {
            when (response.code()) {
                200 -> {
                    val thomannDetailsResponse = response.body()
                    val status = ThomannsDaoResponseStatus(true, null)
                    return DaoResult(thomannDetailsResponse, status)
                }
                400 -> {
                    val errorBody = response.errorBody()?.string() ?: ""
                    if (errorBody.contains(ThomannsDaoResponseStatus.Errors.USER_ID_NOT_PROVIDED.toString(), true)) {
                        val status = ThomannsDaoResponseStatus(false, ThomannsDaoResponseStatus.Errors.USER_ID_NOT_PROVIDED)
                        return DaoResult(null, status)
                    } else if (errorBody.contains(ThomannsDaoResponseStatus.Errors.NOT_A_THOMANN_USER.toString(), true)) {
                        val status = ThomannsDaoResponseStatus(false, ThomannsDaoResponseStatus.Errors.NOT_A_THOMANN_USER)
                        return DaoResult(null, status)
                    } else {
                        val status = ThomannsDaoResponseStatus(false, ThomannsDaoResponseStatus.Errors.UNKNOWN)
                        return DaoResult(null, status)
                    }
                }
                403 -> {
                    val status = ThomannsDaoResponseStatus(false, ThomannsDaoResponseStatus.Errors.FORBIDDEN)
                    return DaoResult(null, status)
                }
                404 -> {
                    val status = ThomannsDaoResponseStatus(false, ThomannsDaoResponseStatus.Errors.THOMANN_NOT_FOUND)
                    return DaoResult(null, status)
                }
                else -> {
                    val status = ThomannsDaoResponseStatus(false, ThomannsDaoResponseStatus.Errors.UNKNOWN)
                    return DaoResult(null, status)
                }
            }
        } else {
            val status = ThomannsDaoResponseStatus(false, ThomannsDaoResponseStatus.Errors.UNKNOWN)
            return DaoResult(null, status)
        }
    }

    private interface RetrofitThomanns {
        @POST("thomanns") suspend fun createThomann(@Header("Authorization") accessToken: String, @Body createRequest: CreateThomannRequest): Response<ThomannDetailsResponse>

        @GET("thomanns")
        suspend fun thomanns(
            @Query("page") page: Int,
            @Query("page_size") pageSize: Int,
            @Query("city_id") cityId: Int?,
            @Query("valid_until") validUntil: String?,
            @Query("is_locked") isLocked: Boolean?,
            @Header("Authorization") accessToken: String
        ): Response<ThomannsResponse>

        @GET("mythomanns")
        suspend fun myThomanns(
            @Query("page") page: Int,
            @Query("page_size") pageSize: Int,
            @Header("Authorization") accessToken: String
        ): Response<ThomannsResponse>

        @GET("thomanns/details") suspend fun thomannDetails(@Header("Authorization") accessToken: String, @Query("id") id: Int): Response<ThomannDetailsResponse>
        @PUT("thomanns") suspend fun updateThomann(@Header("Authorization") accessToken: String, @Query("id") id: Int, @Body updateRequest: UpdateThomannRequest): Response<ThomannDetailsResponse>
        @DELETE("thomanns") suspend fun delete(@Header("Authorization") accessToken: String, @Query("id") id: Int): Response<Array<String>>
        @POST("thomanns/join") suspend fun joinThomann(@Header("Authorization") accessToken: String, @Query("id") id: Int, @Body joinRequest: JoinThomannRequest): Response<ThomannDetailsResponse>
        @POST("thomanns/quit") suspend fun quitThomann(@Header("Authorization") accessToken: String, @Query("id") id: Int): Response<ThomannDetailsResponse>
        @POST("thomanns/kick") suspend fun kickThomann(@Header("Authorization") accessToken: String, @Query("id") id: Int, @Body joinRequest: KickThomannRequest): Response<ThomannDetailsResponse>
    }
}