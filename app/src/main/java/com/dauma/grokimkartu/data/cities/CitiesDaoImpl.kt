package com.dauma.grokimkartu.data.cities

import com.dauma.grokimkartu.data.cities.entities.CityResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

class CitiesDaoImpl(retrofit: Retrofit) : CitiesDao {
    private val retrofitCities: RetrofitCities = retrofit.create(RetrofitCities::class.java)

    override fun cities(
        accessToken: String,
        onComplete: (List<CityResponse>?, CityDaoResponseStatus) -> Unit
    ) {
        retrofitCities.cities(accessToken).enqueue(object : Callback<ArrayList<CityResponse>> {
            override fun onResponse(
                call: Call<ArrayList<CityResponse>>,
                response: Response<ArrayList<CityResponse>>
            ) {
                when (response.code()) {
                    200 -> {
                        val citiesResponse = response.body()
                        val status = CityDaoResponseStatus(true, null)
                        onComplete(citiesResponse, status)
                    }
                    else -> {
                        val status = CityDaoResponseStatus(false, CityDaoResponseStatus.Errors.UNKNOWN)
                        onComplete(null, status)
                    }
                }
            }

            override fun onFailure(call: Call<ArrayList<CityResponse>>, t: Throwable) {
                val status = CityDaoResponseStatus(false, CityDaoResponseStatus.Errors.UNKNOWN)
                onComplete(null, status)
            }
        })
    }

    override fun search(
        value: String,
        accessToken: String,
        onComplete: (List<CityResponse>?, CityDaoResponseStatus) -> Unit
    ) {
        retrofitCities.search(accessToken, value).enqueue(object : Callback<ArrayList<CityResponse>> {
            override fun onResponse(
                call: Call<ArrayList<CityResponse>>,
                response: Response<ArrayList<CityResponse>>
            ) {
                when (response.code()) {
                    200 -> {
                        val citiesResponse = response.body()
                        val status = CityDaoResponseStatus(true, null)
                        onComplete(citiesResponse, status)
                    }
                    else -> {
                        val status = CityDaoResponseStatus(false, CityDaoResponseStatus.Errors.UNKNOWN)
                        onComplete(null, status)
                    }
                }
            }

            override fun onFailure(call: Call<ArrayList<CityResponse>>, t: Throwable) {
                val status = CityDaoResponseStatus(false, CityDaoResponseStatus.Errors.UNKNOWN)
                onComplete(null, status)
            }
        })
    }

    private interface RetrofitCities {
        @GET("cities") fun cities(@Header("Authorization") accessToken: String): Call<ArrayList<CityResponse>>
        @GET("city/search/{value}") fun search(@Header("Authorization") accessToken: String, @Path("value") value: String): Call<ArrayList<CityResponse>>
    }
}