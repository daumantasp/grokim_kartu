package com.dauma.grokimkartu.data.thomanns

import com.dauma.grokimkartu.data.thomanns.entities.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.*

class ThomannsDaoImpl(retrofit: Retrofit) : ThomannsDao {
    private val retrofitThomanns: RetrofitThomanns = retrofit.create(RetrofitThomanns::class.java)

    override fun create(
        createRequest: CreateThomannRequest,
        accessToken: String,
        onComplete: (ThomannDetailsResponse?, ThomannsDaoResponseStatus) -> Unit
    ) {
        retrofitThomanns.createThomann(accessToken, createRequest).enqueue(object : Callback<ThomannDetailsResponse> {
            override fun onResponse(
                call: Call<ThomannDetailsResponse>,
                response: Response<ThomannDetailsResponse>
            ) {
                when (response.code()) {
                    201 -> {
                        val thomannDetailsResponse = response.body()
                        val status = ThomannsDaoResponseStatus(true, null)
                        onComplete(thomannDetailsResponse, status)
                    }
                    400 -> {
                        val status = ThomannsDaoResponseStatus(false, ThomannsDaoResponseStatus.Errors.INVALID_VALID_UNTIL_DATE)
                        onComplete(null, status)
                    }
                    else -> {
                        val status = ThomannsDaoResponseStatus(false, ThomannsDaoResponseStatus.Errors.UNKNOWN)
                        onComplete(null, status)
                    }
                }
            }

            override fun onFailure(call: Call<ThomannDetailsResponse>, t: Throwable) {
                val status = ThomannsDaoResponseStatus(false, ThomannsDaoResponseStatus.Errors.UNKNOWN)
                onComplete(null, status)
            }
        })
    }

    override fun update(
        thomannId: Int,
        updateRequest: UpdateThomannRequest,
        accessToken: String,
        onComplete: (ThomannDetailsResponse?, ThomannsDaoResponseStatus) -> Unit
    ) {

        retrofitThomanns.updateThomann(accessToken, thomannId, updateRequest).enqueue(object : Callback<ThomannDetailsResponse> {
            override fun onResponse(
                call: Call<ThomannDetailsResponse>,
                response: Response<ThomannDetailsResponse>
            ) {
                when (response.code()) {
                    200 -> {
                        val thomannDetailsResponse = response.body()
                        val status = ThomannsDaoResponseStatus(true, null)
                        onComplete(thomannDetailsResponse, status)
                    }
                    400 -> {
                        val status = ThomannsDaoResponseStatus(false, ThomannsDaoResponseStatus.Errors.INVALID_VALID_UNTIL_DATE)
                        onComplete(null, status)
                    }
                    403 -> {
                        val status = ThomannsDaoResponseStatus(false, ThomannsDaoResponseStatus.Errors.FORBIDDEN)
                        onComplete(null, status)
                    }
                    404 -> {
                        val status = ThomannsDaoResponseStatus(false, ThomannsDaoResponseStatus.Errors.THOMANN_NOT_FOUND)
                        onComplete(null, status)
                    }
                    else -> {
                        val status = ThomannsDaoResponseStatus(false, ThomannsDaoResponseStatus.Errors.UNKNOWN)
                        onComplete(null, status)
                    }
                }
            }

            override fun onFailure(call: Call<ThomannDetailsResponse>, t: Throwable) {
                val status = ThomannsDaoResponseStatus(false, ThomannsDaoResponseStatus.Errors.UNKNOWN)
                onComplete(null, status)
            }
        })
    }

    override fun delete(
        thomannId: Int,
        accessToken: String,
        onComplete: (ThomannsDaoResponseStatus) -> Unit
    ) {
        retrofitThomanns.delete(accessToken, thomannId).enqueue(object : Callback<Array<String>> {
            override fun onResponse(call: Call<Array<String>>, response: Response<Array<String>>) {
                when (response.code()) {
                    200 -> {
                        val status = ThomannsDaoResponseStatus(true, null)
                        onComplete(status)
                    }
                    403 -> {
                        val status = ThomannsDaoResponseStatus(false, ThomannsDaoResponseStatus.Errors.FORBIDDEN)
                        onComplete(status)
                    }
                    404 -> {
                        val status = ThomannsDaoResponseStatus(false, ThomannsDaoResponseStatus.Errors.THOMANN_NOT_FOUND)
                        onComplete(status)
                    }
                    else -> {
                        val status = ThomannsDaoResponseStatus(false, ThomannsDaoResponseStatus.Errors.UNKNOWN)
                        onComplete(status)
                    }
                }
            }

            override fun onFailure(call: Call<Array<String>>, t: Throwable) {
                val status = ThomannsDaoResponseStatus(false, ThomannsDaoResponseStatus.Errors.UNKNOWN)
                onComplete(status)
            }
        })
    }

    override fun thomanns(
        page: Int,
        pageSize: Int,
        accessToken: String,
        onComplete: (ThomannsResponse?, ThomannsDaoResponseStatus) -> Unit
    ) {
        retrofitThomanns.thomanns(page, pageSize, accessToken).enqueue(object : Callback<ThomannsResponse> {
            override fun onResponse(
                call: Call<ThomannsResponse>,
                response: Response<ThomannsResponse>
            ) {
                when (response.code()) {
                    200 -> {
                        val thomannsResponse = response.body()
                        val status = ThomannsDaoResponseStatus(true, null)
                        onComplete(thomannsResponse, status)
                    }
                    else -> {
                        val status = ThomannsDaoResponseStatus(false, ThomannsDaoResponseStatus.Errors.UNKNOWN)
                        onComplete(null, status)
                    }
                }
            }

            override fun onFailure(call: Call<ThomannsResponse>, t: Throwable) {
                val status = ThomannsDaoResponseStatus(false, ThomannsDaoResponseStatus.Errors.UNKNOWN)
                onComplete(null, status)
            }
        })
    }

    override fun thomannDetails(
        thomannId: Int,
        accessToken: String,
        onComplete: (ThomannDetailsResponse?, ThomannsDaoResponseStatus) -> Unit
    ) {
        retrofitThomanns.thomannDetails(accessToken, thomannId).enqueue(object : Callback<ThomannDetailsResponse> {
            override fun onResponse(
                call: Call<ThomannDetailsResponse>,
                response: Response<ThomannDetailsResponse>
            ) {
                when (response.code()) {
                    200 -> {
                        val thomannDetailsResponse = response.body()
                        val status = ThomannsDaoResponseStatus(true, null)
                        onComplete(thomannDetailsResponse, status)
                    }
                    403 -> {
                        val status = ThomannsDaoResponseStatus(false, ThomannsDaoResponseStatus.Errors.FORBIDDEN)
                        onComplete(null, status)
                    }
                    404 -> {
                        val status = ThomannsDaoResponseStatus(false, ThomannsDaoResponseStatus.Errors.THOMANN_NOT_FOUND)
                        onComplete(null, status)
                    }
                    else -> {
                        val status = ThomannsDaoResponseStatus(false, ThomannsDaoResponseStatus.Errors.UNKNOWN)
                        onComplete(null, status)
                    }
                }
            }

            override fun onFailure(call: Call<ThomannDetailsResponse>, t: Throwable) {
                val status = ThomannsDaoResponseStatus(false, ThomannsDaoResponseStatus.Errors.UNKNOWN)
                onComplete(null, status)
            }
        })
    }

    override fun join(
        thomannId: Int,
        joinRequest: JoinThomannRequest,
        accessToken: String,
        onComplete: (ThomannDetailsResponse?, ThomannsDaoResponseStatus) -> Unit
    ) {
        retrofitThomanns.joinThomann(accessToken, thomannId, joinRequest).enqueue(object : Callback<ThomannDetailsResponse> {
            override fun onResponse(
                call: Call<ThomannDetailsResponse>,
                response: Response<ThomannDetailsResponse>
            ) {
                when (response.code()) {
                    200 -> {
                        val thomannDetailsResponse = response.body()
                        val status = ThomannsDaoResponseStatus(true, null)
                        onComplete(thomannDetailsResponse, status)
                    }
                    400 -> {
                        val errorBody = response.errorBody()?.string() ?: ""
                        if (errorBody.contains(ThomannsDaoResponseStatus.Errors.NOT_JOINABLE_FOR_OWNER.toString(), true)) {
                            val status = ThomannsDaoResponseStatus(false, ThomannsDaoResponseStatus.Errors.NOT_JOINABLE_FOR_OWNER)
                            onComplete(null, status)
                        } else if (errorBody.contains(ThomannsDaoResponseStatus.Errors.ALREADY_JOINED.toString(), true)) {
                            val status = ThomannsDaoResponseStatus(false, ThomannsDaoResponseStatus.Errors.ALREADY_JOINED)
                            onComplete(null, status)
                        } else if (errorBody.contains(ThomannsDaoResponseStatus.Errors.INVALID_VALID_UNTIL_DATE.toString(), true)) {
                            val status = ThomannsDaoResponseStatus(false, ThomannsDaoResponseStatus.Errors.INVALID_VALID_UNTIL_DATE)
                            onComplete(null, status)
                        } else if (errorBody.contains(ThomannsDaoResponseStatus.Errors.INVALID_AMOUNT.toString(), true)) {
                            val status = ThomannsDaoResponseStatus(false, ThomannsDaoResponseStatus.Errors.INVALID_AMOUNT)
                            onComplete(null, status)
                        }
                    }
                    403 -> {
                        val status = ThomannsDaoResponseStatus(false, ThomannsDaoResponseStatus.Errors.FORBIDDEN)
                        onComplete(null, status)
                    }
                    404 -> {
                        val status = ThomannsDaoResponseStatus(false, ThomannsDaoResponseStatus.Errors.THOMANN_NOT_FOUND)
                        onComplete(null, status)
                    }
                    else -> {
                        val status = ThomannsDaoResponseStatus(false, ThomannsDaoResponseStatus.Errors.UNKNOWN)
                        onComplete(null, status)
                    }
                }
            }

            override fun onFailure(call: Call<ThomannDetailsResponse>, t: Throwable) {
                val status = ThomannsDaoResponseStatus(false, ThomannsDaoResponseStatus.Errors.UNKNOWN)
                onComplete(null, status)
            }
        })
    }

    override fun quit(
        thomannId: Int,
        accessToken: String,
        onComplete: (ThomannDetailsResponse?, ThomannsDaoResponseStatus) -> Unit
    ) {
        retrofitThomanns.quitThomann(accessToken, thomannId).enqueue(object : Callback<ThomannDetailsResponse> {
            override fun onResponse(
                call: Call<ThomannDetailsResponse>,
                response: Response<ThomannDetailsResponse>
            ) {
                when (response.code()) {
                    200 -> {
                        val thomannDetailsResponse = response.body()
                        val status = ThomannsDaoResponseStatus(true, null)
                        onComplete(thomannDetailsResponse, status)
                    }
                    400 -> {
                        val errorBody = response.errorBody()?.string() ?: ""
                        if (errorBody.contains(ThomannsDaoResponseStatus.Errors.NOT_QUITABLE_FOR_OWNER.toString(), true)) {
                            val status = ThomannsDaoResponseStatus(false, ThomannsDaoResponseStatus.Errors.NOT_QUITABLE_FOR_OWNER)
                            onComplete(null, status)
                        } else if (errorBody.contains(ThomannsDaoResponseStatus.Errors.NOT_A_THOMANN_USER.toString(), true)) {
                            val status = ThomannsDaoResponseStatus(false, ThomannsDaoResponseStatus.Errors.NOT_A_THOMANN_USER)
                            onComplete(null, status)
                        }
                    }
                    403 -> {
                        val status = ThomannsDaoResponseStatus(false, ThomannsDaoResponseStatus.Errors.FORBIDDEN)
                        onComplete(null, status)
                    }
                    404 -> {
                        val status = ThomannsDaoResponseStatus(false, ThomannsDaoResponseStatus.Errors.THOMANN_NOT_FOUND)
                        onComplete(null, status)
                    }
                    else -> {
                        val status = ThomannsDaoResponseStatus(false, ThomannsDaoResponseStatus.Errors.UNKNOWN)
                        onComplete(null, status)
                    }
                }
            }

            override fun onFailure(call: Call<ThomannDetailsResponse>, t: Throwable) {
                val status = ThomannsDaoResponseStatus(false, ThomannsDaoResponseStatus.Errors.UNKNOWN)
                onComplete(null, status)
            }
        })
    }

    override fun kick(
        thomannId: Int,
        kickRequest: KickThomannRequest,
        accessToken: String,
        onComplete: (ThomannDetailsResponse?, ThomannsDaoResponseStatus) -> Unit
    ) {
        retrofitThomanns.kickThomann(accessToken, thomannId, kickRequest).enqueue(object : Callback<ThomannDetailsResponse> {
            override fun onResponse(
                call: Call<ThomannDetailsResponse>,
                response: Response<ThomannDetailsResponse>
            ) {
                when (response.code()) {
                    200 -> {
                        val thomannDetailsResponse = response.body()
                        val status = ThomannsDaoResponseStatus(true, null)
                        onComplete(thomannDetailsResponse, status)
                    }
                    400 -> {
                        val errorBody = response.errorBody()?.string() ?: ""
                        if (errorBody.contains(ThomannsDaoResponseStatus.Errors.USER_ID_NOT_PROVIDED.toString(), true)) {
                            val status = ThomannsDaoResponseStatus(false, ThomannsDaoResponseStatus.Errors.USER_ID_NOT_PROVIDED)
                            onComplete(null, status)
                        } else if (errorBody.contains(ThomannsDaoResponseStatus.Errors.NOT_A_THOMANN_USER.toString(), true)) {
                            val status = ThomannsDaoResponseStatus(false, ThomannsDaoResponseStatus.Errors.NOT_A_THOMANN_USER)
                            onComplete(null, status)
                        }
                    }
                    403 -> {
                        val status = ThomannsDaoResponseStatus(false, ThomannsDaoResponseStatus.Errors.FORBIDDEN)
                        onComplete(null, status)
                    }
                    404 -> {
                        val status = ThomannsDaoResponseStatus(false, ThomannsDaoResponseStatus.Errors.THOMANN_NOT_FOUND)
                        onComplete(null, status)
                    }
                    else -> {
                        val status = ThomannsDaoResponseStatus(false, ThomannsDaoResponseStatus.Errors.UNKNOWN)
                        onComplete(null, status)
                    }
                }
            }

            override fun onFailure(call: Call<ThomannDetailsResponse>, t: Throwable) {
                val status = ThomannsDaoResponseStatus(false, ThomannsDaoResponseStatus.Errors.UNKNOWN)
                onComplete(null, status)
            }
        })
    }

    private interface RetrofitThomanns {
        @POST("thomann") fun createThomann(@Header("Authorization") accessToken: String, @Body createRequest: CreateThomannRequest): Call<ThomannDetailsResponse>

        @GET("thomanns")
        fun thomanns(
            @Query("page") page: Int,
            @Query("page_size") pageSize: Int,
            @Header("Authorization") accessToken: String
        ): Call<ThomannsResponse>

        @GET("thomann/details/{id}") fun thomannDetails(@Header("Authorization") accessToken: String, @Path("id") id: Int): Call<ThomannDetailsResponse>
        @PUT("thomann/{id}") fun updateThomann(@Header("Authorization") accessToken: String, @Path("id") id: Int, @Body updateRequest: UpdateThomannRequest): Call<ThomannDetailsResponse>
        @DELETE("thomann/{id}") fun delete(@Header("Authorization") accessToken: String, @Path("id") id: Int): Call<Array<String>>
        @POST("thomann/join/{id}") fun joinThomann(@Header("Authorization") accessToken: String, @Path("id") id: Int, @Body joinRequest: JoinThomannRequest): Call<ThomannDetailsResponse>
        @POST("thomann/quit/{id}") fun quitThomann(@Header("Authorization") accessToken: String, @Path("id") id: Int): Call<ThomannDetailsResponse>
        @POST("thomann/kick/{id}") fun kickThomann(@Header("Authorization") accessToken: String, @Path("id") id: Int, @Body joinRequest: KickThomannRequest): Call<ThomannDetailsResponse>
    }
}