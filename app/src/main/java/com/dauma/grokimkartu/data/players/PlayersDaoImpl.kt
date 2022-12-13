package com.dauma.grokimkartu.data.players

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.dauma.grokimkartu.data.players.entities.PlayerDetailsResponse
import com.dauma.grokimkartu.data.players.entities.PlayersRequest
import com.dauma.grokimkartu.data.players.entities.PlayersResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.*

class PlayersDaoImpl(retrofit: Retrofit) : PlayersDao {
    private val retrofitPlayers: RetrofitPlayers = retrofit.create(RetrofitPlayers::class.java)

    override fun players(playersRequest: PlayersRequest, accessToken: String, onComplete: (PlayersResponse?, PlayersDaoResponseStatus) -> Unit) {
        retrofitPlayers.players(
            page = playersRequest.page,
            pageSize = playersRequest.pageSize,
            cityId = playersRequest.cityId,
            instrumentId = playersRequest.instrumentId,
            text = playersRequest.text,
            accessToken = accessToken
        ).enqueue(object : Callback<PlayersResponse> {
            override fun onResponse(
                call: Call<PlayersResponse>,
                response: Response<PlayersResponse>
            ) {
                when (response.code()) {
                    200 -> {
                        val playersResponse = response.body()
                        val status = PlayersDaoResponseStatus(true, null)
                        onComplete(playersResponse, status)
                    }
                    else -> {
                        val status = PlayersDaoResponseStatus(false, PlayersDaoResponseStatus.Errors.UNKNOWN)
                        onComplete(null, status)
                    }
                }
            }

            override fun onFailure(call: Call<PlayersResponse>, t: Throwable) {
                val status = PlayersDaoResponseStatus(false, PlayersDaoResponseStatus.Errors.UNKNOWN)
                onComplete(null, status)
            }
        })
    }

    override fun playerDetails(
        userId: Int,
        accessToken: String,
        onComplete: (PlayerDetailsResponse?, PlayersDaoResponseStatus) -> Unit
    ) {
        retrofitPlayers.playerDetails(accessToken, userId).enqueue(object : Callback<PlayerDetailsResponse> {
            override fun onResponse(
                call: Call<PlayerDetailsResponse>,
                response: Response<PlayerDetailsResponse>
            ) {
                when (response.code()) {
                    200 -> {
                        val playerDetailsResponse = response.body()
                        val status = PlayersDaoResponseStatus(true, null)
                        onComplete(playerDetailsResponse, status)
                    }
                    403 -> {
                        val status = PlayersDaoResponseStatus(false, PlayersDaoResponseStatus.Errors.FORBIDDEN)
                        onComplete(null, status)
                    }
                    404 -> {
                        val status = PlayersDaoResponseStatus(false, PlayersDaoResponseStatus.Errors.PLAYER_NOT_FOUND)
                        onComplete(null, status)
                    }
                    else -> {
                        val status = PlayersDaoResponseStatus(false, PlayersDaoResponseStatus.Errors.UNKNOWN)
                        onComplete(null, status)
                    }
                }
            }

            override fun onFailure(call: Call<PlayerDetailsResponse>, t: Throwable) {
                val status = PlayersDaoResponseStatus(false, PlayersDaoResponseStatus.Errors.UNKNOWN)
                onComplete(null, status)
            }
        })
    }

    override fun playerPhoto(
        userId: Int,
        accessToken: String,
        onComplete: (Bitmap?, PlayersDaoResponseStatus) -> Unit
    ) {
        retrofitPlayers.photo(accessToken, userId).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                when (response.code()) {
                    200 -> {
                        val playerPhotoResponseData = response.body()
                        val stream = playerPhotoResponseData?.byteStream()
                        if (stream != null) {
                            val bitmap = BitmapFactory.decodeStream(stream)
                            val status = PlayersDaoResponseStatus(true, null)
                            onComplete(bitmap, status)
                        } else {
                            val status = PlayersDaoResponseStatus(false, PlayersDaoResponseStatus.Errors.UNKNOWN)
                            onComplete(null, status)
                        }
                    }
                    404 -> {
                        val errorBody = response.errorBody()?.string() ?: ""
                        if (errorBody.contains(PlayersDaoResponseStatus.Errors.PLAYER_NOT_FOUND.toString(), true)) {
                            val status = PlayersDaoResponseStatus(false, PlayersDaoResponseStatus.Errors.PLAYER_NOT_FOUND)
                            onComplete(null,status)
                        } else if (errorBody.contains(PlayersDaoResponseStatus.Errors.PHOTO_NOT_FOUND.toString(), true)) {
                            val status = PlayersDaoResponseStatus(false, PlayersDaoResponseStatus.Errors.PHOTO_NOT_FOUND)
                            onComplete(null, status)
                        }
                    }
                    else -> {
                        val status = PlayersDaoResponseStatus(false, PlayersDaoResponseStatus.Errors.UNKNOWN)
                        onComplete(null, status)
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                val status = PlayersDaoResponseStatus(false, PlayersDaoResponseStatus.Errors.UNKNOWN)
                onComplete(null, status)
            }
        })
    }

    override fun playerIcon(
        userId: Int,
        accessToken: String,
        onComplete: (Bitmap?, PlayersDaoResponseStatus) -> Unit
    ) {
        retrofitPlayers.icon(accessToken, userId).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                when (response.code()) {
                    200 -> {
                        val playerPhotoResponseData = response.body()
                        val stream = playerPhotoResponseData?.byteStream()
                        if (stream != null) {
                            val bitmap = BitmapFactory.decodeStream(stream)
                            val status = PlayersDaoResponseStatus(true, null)
                            onComplete(bitmap, status)
                        } else {
                            val status = PlayersDaoResponseStatus(false, PlayersDaoResponseStatus.Errors.UNKNOWN)
                            onComplete(null, status)
                        }
                    }
                    404 -> {
                        val errorBody = response.errorBody()?.string() ?: ""
                        if (errorBody.contains(PlayersDaoResponseStatus.Errors.PLAYER_NOT_FOUND.toString(), true)) {
                            val status = PlayersDaoResponseStatus(false, PlayersDaoResponseStatus.Errors.PLAYER_NOT_FOUND)
                            onComplete(null,status)
                        } else if (errorBody.contains(PlayersDaoResponseStatus.Errors.ICON_NOT_FOUND.toString(), true)) {
                            val status = PlayersDaoResponseStatus(false, PlayersDaoResponseStatus.Errors.ICON_NOT_FOUND)
                            onComplete(null, status)
                        }
                    }
                    else -> {
                        val status = PlayersDaoResponseStatus(false, PlayersDaoResponseStatus.Errors.UNKNOWN)
                        onComplete(null, status)
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                val status = PlayersDaoResponseStatus(false, PlayersDaoResponseStatus.Errors.UNKNOWN)
                onComplete(null, status)
            }
        })
    }

    private interface RetrofitPlayers {
        @GET("players")
        fun players(
            @Query("page") page: Int,
            @Query("page_size") pageSize: Int,
            @Query("city_id") cityId: Int?,
            @Query("instrument_id") instrumentId: Int?,
            @Query("text") text: String?,
            @Header("Authorization") accessToken: String
        ): Call<PlayersResponse>

        @GET("players/details") fun playerDetails(@Header("Authorization") accessToken: String, @Query("id") id: Int) : Call<PlayerDetailsResponse>
        @GET("players/icon") fun icon(@Header("Authorization") accessToken: String, @Query("id") id: Int): Call<ResponseBody>

        @Headers("Connection: close")
        @GET("players/photo")
        fun photo(@Header("Authorization") accessToken: String, @Query("id") id: Int): Call<ResponseBody>
    }
}