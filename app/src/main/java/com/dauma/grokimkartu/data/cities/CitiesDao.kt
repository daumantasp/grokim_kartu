package com.dauma.grokimkartu.data.cities

import com.dauma.grokimkartu.data.DaoResult
import com.dauma.grokimkartu.data.cities.entities.CityResponse

interface CitiesDao {
    suspend fun cities(accessToken: String): DaoResult<List<CityResponse>?, CityDaoResponseStatus>
    suspend fun search(value: String, accessToken: String): DaoResult<List<CityResponse>?, CityDaoResponseStatus>
}