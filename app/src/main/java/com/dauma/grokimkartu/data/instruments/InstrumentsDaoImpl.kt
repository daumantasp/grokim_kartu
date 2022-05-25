package com.dauma.grokimkartu.data.instruments

import com.dauma.grokimkartu.data.instruments.entities.InstrumentResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

class InstrumentsDaoImpl(retrofit: Retrofit) : InstrumentsDao {
    private val retrofitCities: RetrofitInstruments = retrofit.create(RetrofitInstruments::class.java)

    override fun instruments(
        accessToken: String,
        onComplete: (List<InstrumentResponse>?, InstrumentsDaoResponseStatus) -> Unit
    ) {
        retrofitCities.instruments(accessToken).enqueue(object : Callback<ArrayList<InstrumentResponse>> {
            override fun onResponse(
                call: Call<ArrayList<InstrumentResponse>>,
                response: Response<ArrayList<InstrumentResponse>>
            ) {
                when (response.code()) {
                    200 -> {
                        val instrumentsResponse = response.body()
                        val status = InstrumentsDaoResponseStatus(true, null)
                        onComplete(instrumentsResponse, status)
                    }
                    else -> {
                        val status = InstrumentsDaoResponseStatus(false, InstrumentsDaoResponseStatus.Errors.UNKNOWN)
                        onComplete(null, status)
                    }
                }
            }

            override fun onFailure(call: Call<ArrayList<InstrumentResponse>>, t: Throwable) {
                val status = InstrumentsDaoResponseStatus(false, InstrumentsDaoResponseStatus.Errors.UNKNOWN)
                onComplete(null, status)
            }
        })
    }

    override fun search(
        value: String,
        accessToken: String,
        onComplete: (List<InstrumentResponse>?, InstrumentsDaoResponseStatus) -> Unit
    ) {
        retrofitCities.search(accessToken, value).enqueue(object :
            Callback<ArrayList<InstrumentResponse>> {
            override fun onResponse(
                call: Call<ArrayList<InstrumentResponse>>,
                response: Response<ArrayList<InstrumentResponse>>
            ) {
                when (response.code()) {
                    200 -> {
                        val instrumentsResponse = response.body()
                        val status = InstrumentsDaoResponseStatus(true, null)
                        onComplete(instrumentsResponse, status)
                    }
                    else -> {
                        val status = InstrumentsDaoResponseStatus(false, InstrumentsDaoResponseStatus.Errors.UNKNOWN)
                        onComplete(null, status)
                    }
                }
            }

            override fun onFailure(call: Call<ArrayList<InstrumentResponse>>, t: Throwable) {
                val status = InstrumentsDaoResponseStatus(false, InstrumentsDaoResponseStatus.Errors.UNKNOWN)
                onComplete(null, status)
            }
        })
    }

    private interface RetrofitInstruments {
        @GET("instruments") fun instruments(@Header("Authorization") accessToken: String): Call<ArrayList<InstrumentResponse>>
        @GET("instrument/search/{value}") fun search(@Header("Authorization") accessToken: String, @Path("value") value: String): Call<ArrayList<InstrumentResponse>>
    }
}