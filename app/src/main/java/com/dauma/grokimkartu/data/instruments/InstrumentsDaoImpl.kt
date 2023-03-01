package com.dauma.grokimkartu.data.instruments

import com.dauma.grokimkartu.data.DaoResult
import com.dauma.grokimkartu.data.instruments.entities.InstrumentResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

class InstrumentsDaoImpl(retrofit: Retrofit) : InstrumentsDao {
    private val retrofitCities: RetrofitInstruments = retrofit.create(RetrofitInstruments::class.java)

    override suspend fun instruments(accessToken: String): DaoResult<List<InstrumentResponse>?, InstrumentsDaoResponseStatus> {
        val response = retrofitCities.instruments(accessToken)

        if (response.isSuccessful) {
            when (response.code()) {
                200 -> {
                    val instrumentsResponse = response.body()
                    val status = InstrumentsDaoResponseStatus(true, null)
                    return DaoResult(instrumentsResponse, status)
                }
                else -> {
                    val status = InstrumentsDaoResponseStatus(false, InstrumentsDaoResponseStatus.Errors.UNKNOWN)
                    return DaoResult(null, status)
                }
            }
        } else {
            val status = InstrumentsDaoResponseStatus(false, InstrumentsDaoResponseStatus.Errors.UNKNOWN)
            return DaoResult(null, status)
        }
    }

    override suspend fun search(
        value: String,
        accessToken: String
    ): DaoResult<List<InstrumentResponse>?, InstrumentsDaoResponseStatus> {
        val response = retrofitCities.search(accessToken, value)

        if (response.isSuccessful) {
            when (response.code()) {
                200 -> {
                    val instrumentsResponse = response.body()
                    val status = InstrumentsDaoResponseStatus(true, null)
                    return DaoResult(instrumentsResponse, status)
                }
                else -> {
                    val status = InstrumentsDaoResponseStatus(false, InstrumentsDaoResponseStatus.Errors.UNKNOWN)
                    return DaoResult(null, status)
                }
            }
        } else {
            val status = InstrumentsDaoResponseStatus(false, InstrumentsDaoResponseStatus.Errors.UNKNOWN)
            return DaoResult(null, status)
        }
    }

    private interface RetrofitInstruments {
        @GET("instruments") suspend fun instruments(@Header("Authorization") accessToken: String): Response<ArrayList<InstrumentResponse>>
        @GET("instruments/search") suspend fun search(@Header("Authorization") accessToken: String, @Query("value") value: String): Response<ArrayList<InstrumentResponse>>
    }
}