package com.dauma.grokimkartu.data.cities

import com.dauma.grokimkartu.data.DaoResult
import com.dauma.grokimkartu.data.cities.entities.CityResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

class CitiesDaoImpl(retrofit: Retrofit) : CitiesDao {
    private val retrofitCities: RetrofitCities = retrofit.create(RetrofitCities::class.java)

    override suspend fun cities(accessToken: String): DaoResult<List<CityResponse>?, CityDaoResponseStatus> {
        val response = retrofitCities.cities(accessToken)

        if (response.isSuccessful) {
            when (response.code()) {
                200 -> {
                    val citiesResponse = response.body()
                    val status = CityDaoResponseStatus(true, null)
                    return DaoResult(citiesResponse, status)
                }
                else -> {
                    val status = CityDaoResponseStatus(false, CityDaoResponseStatus.Errors.UNKNOWN)
                    return DaoResult(null, status)
                }
            }
        } else {
            val status = CityDaoResponseStatus(false, CityDaoResponseStatus.Errors.UNKNOWN)
            return DaoResult(null, status)
        }
    }

    override suspend fun search(
        value: String,
        accessToken: String
    ) : DaoResult<List<CityResponse>?, CityDaoResponseStatus> {
        val response = retrofitCities.search(accessToken, value)

        if (response.isSuccessful) {
            when (response.code()) {
                200 -> {
                    val citiesResponse = response.body()
                    val status = CityDaoResponseStatus(true, null)
                    return DaoResult(citiesResponse, status)
                }
                else -> {
                    val status = CityDaoResponseStatus(false, CityDaoResponseStatus.Errors.UNKNOWN)
                    return DaoResult(null, status)
                }
            }
        } else {
            val status = CityDaoResponseStatus(false, CityDaoResponseStatus.Errors.UNKNOWN)
            return DaoResult(null, status)
        }
    }

    private interface RetrofitCities {
        @GET("cities") suspend fun cities(@Header("Authorization") accessToken: String): Response<ArrayList<CityResponse>>
        @GET("cities/search") suspend fun search(@Header("Authorization") accessToken: String, @Query("value") value: String): Response<ArrayList<CityResponse>>
    }
}