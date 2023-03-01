package com.dauma.grokimkartu.data.players

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.dauma.grokimkartu.data.DaoResult
import com.dauma.grokimkartu.data.players.entities.PlayerDetailsResponse
import com.dauma.grokimkartu.data.players.entities.PlayersRequest
import com.dauma.grokimkartu.data.players.entities.PlayersResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.*

class PlayersDaoImpl(retrofit: Retrofit) : PlayersDao {
    private val retrofitPlayers: RetrofitPlayers = retrofit.create(RetrofitPlayers::class.java)

    override suspend fun players(
        playersRequest: PlayersRequest,
        accessToken: String
    ): DaoResult<PlayersResponse?, PlayersDaoResponseStatus> {
        val response = retrofitPlayers.players(
            page = playersRequest.page,
            pageSize = playersRequest.pageSize,
            cityId = playersRequest.cityId,
            instrumentId = playersRequest.instrumentId,
            text = playersRequest.text,
            accessToken = accessToken
        )

        if (response.isSuccessful) {
            when (response.code()) {
                200 -> {
                    val playersResponse = response.body()
                    val status = PlayersDaoResponseStatus(true, null)
                    return DaoResult(playersResponse, status)
                }
                else -> {
                    val status = PlayersDaoResponseStatus(false, PlayersDaoResponseStatus.Errors.UNKNOWN)
                    return DaoResult(null, status)
                }
            }
        } else {
            val status = PlayersDaoResponseStatus(false, PlayersDaoResponseStatus.Errors.UNKNOWN)
            return DaoResult(null, status)
        }
    }

    override suspend fun playerDetails(
        userId: Int,
        accessToken: String
    ): DaoResult<PlayerDetailsResponse?, PlayersDaoResponseStatus> {
        val response = retrofitPlayers.playerDetails(accessToken, userId)

        if (response.isSuccessful) {
            when (response.code()) {
                200 -> {
                    val playerDetailsResponse = response.body()
                    val status = PlayersDaoResponseStatus(true, null)
                    return DaoResult(playerDetailsResponse, status)
                }
                403 -> {
                    val status = PlayersDaoResponseStatus(false, PlayersDaoResponseStatus.Errors.FORBIDDEN)
                    return DaoResult(null, status)
                }
                404 -> {
                    val status = PlayersDaoResponseStatus(false, PlayersDaoResponseStatus.Errors.PLAYER_NOT_FOUND)
                    return DaoResult(null, status)
                }
                else -> {
                    val status = PlayersDaoResponseStatus(false, PlayersDaoResponseStatus.Errors.UNKNOWN)
                    return DaoResult(null, status)
                }
            }
        } else {
            val status = PlayersDaoResponseStatus(false, PlayersDaoResponseStatus.Errors.UNKNOWN)
            return DaoResult(null, status)
        }
    }

    override suspend fun playerPhoto(
        userId: Int,
        accessToken: String
    ): DaoResult<Bitmap?, PlayersDaoResponseStatus> {
        val response = retrofitPlayers.photo(accessToken, userId)

        if (response.isSuccessful) {
            when (response.code()) {
                200 -> {
                    val playerPhotoResponseData = response.body()
                    val stream = playerPhotoResponseData?.byteStream()
                    if (stream != null) {
                        val bitmap = BitmapFactory.decodeStream(stream)
                        val status = PlayersDaoResponseStatus(true, null)
                        return DaoResult(bitmap, status)
                    } else {
                        val status = PlayersDaoResponseStatus(false, PlayersDaoResponseStatus.Errors.UNKNOWN)
                        return DaoResult(null, status)
                    }
                }
                404 -> {
                    val errorBody = response.errorBody()?.string() ?: ""
                    if (errorBody.contains(PlayersDaoResponseStatus.Errors.PLAYER_NOT_FOUND.toString(), true)) {
                        val status = PlayersDaoResponseStatus(false, PlayersDaoResponseStatus.Errors.PLAYER_NOT_FOUND)
                        return DaoResult(null, status)
                    } else if (errorBody.contains(PlayersDaoResponseStatus.Errors.PHOTO_NOT_FOUND.toString(), true)) {
                        val status = PlayersDaoResponseStatus(false, PlayersDaoResponseStatus.Errors.PHOTO_NOT_FOUND)
                        return DaoResult(null, status)
                    } else {
                        val status = PlayersDaoResponseStatus(false, PlayersDaoResponseStatus.Errors.UNKNOWN)
                        return DaoResult(null, status)
                    }
                }
                else -> {
                    val status = PlayersDaoResponseStatus(false, PlayersDaoResponseStatus.Errors.UNKNOWN)
                    return DaoResult(null, status)
                }
            }
        } else {
            val status = PlayersDaoResponseStatus(false, PlayersDaoResponseStatus.Errors.UNKNOWN)
            return DaoResult(null, status)
        }
    }

    override suspend fun playerIcon(
        userId: Int,
        accessToken: String
    ): DaoResult<Bitmap?, PlayersDaoResponseStatus> {
        val response = retrofitPlayers.icon(accessToken, userId)

        if (response.isSuccessful) {
            when (response.code()) {
                200 -> {
                    val playerPhotoResponseData = response.body()
                    val stream = playerPhotoResponseData?.byteStream()
                    if (stream != null) {
                        val bitmap = BitmapFactory.decodeStream(stream)
                        val status = PlayersDaoResponseStatus(true, null)
                        return DaoResult(bitmap, status)
                    } else {
                        val status = PlayersDaoResponseStatus(false, PlayersDaoResponseStatus.Errors.UNKNOWN)
                        return DaoResult(null, status)
                    }
                }
                404 -> {
                    val errorBody = response.errorBody()?.string() ?: ""
                    if (errorBody.contains(PlayersDaoResponseStatus.Errors.PLAYER_NOT_FOUND.toString(), true)) {
                        val status = PlayersDaoResponseStatus(false, PlayersDaoResponseStatus.Errors.PLAYER_NOT_FOUND)
                        return DaoResult(null, status)
                    } else if (errorBody.contains(PlayersDaoResponseStatus.Errors.ICON_NOT_FOUND.toString(), true)) {
                        val status = PlayersDaoResponseStatus(false, PlayersDaoResponseStatus.Errors.ICON_NOT_FOUND)
                        return DaoResult(null, status)
                    } else {
                        val status = PlayersDaoResponseStatus(false, PlayersDaoResponseStatus.Errors.UNKNOWN)
                        return DaoResult(null, status)
                    }
                }
                else -> {
                    val status = PlayersDaoResponseStatus(false, PlayersDaoResponseStatus.Errors.UNKNOWN)
                    return DaoResult(null, status)
                }
            }
        } else {
            val status = PlayersDaoResponseStatus(false, PlayersDaoResponseStatus.Errors.UNKNOWN)
            return DaoResult(null, status)
        }
    }

    private interface RetrofitPlayers {
        @GET("players")
        suspend fun players(
            @Query("page") page: Int,
            @Query("page_size") pageSize: Int,
            @Query("city_id") cityId: Int?,
            @Query("instrument_id") instrumentId: Int?,
            @Query("text") text: String?,
            @Header("Authorization") accessToken: String
        ): Response<PlayersResponse>

        @GET("players/details") suspend fun playerDetails(@Header("Authorization") accessToken: String, @Query("id") id: Int) : Response<PlayerDetailsResponse>
        @GET("players/icon") suspend fun icon(@Header("Authorization") accessToken: String, @Query("id") id: Int): Response<ResponseBody>

        @Headers("Connection: close")
        @GET("players/photo")
        suspend fun photo(@Header("Authorization") accessToken: String, @Query("id") id: Int): Response<ResponseBody>
    }
}