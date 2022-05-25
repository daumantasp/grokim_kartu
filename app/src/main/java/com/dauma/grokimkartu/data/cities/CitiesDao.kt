package com.dauma.grokimkartu.data.cities

import com.dauma.grokimkartu.data.cities.entities.CityResponse

interface CitiesDao {
    fun cities(accessToken: String, onComplete: (List<CityResponse>?, CityDaoResponseStatus) -> Unit)
    fun search(value: String, accessToken: String, onComplete: (List<CityResponse>?, CityDaoResponseStatus) -> Unit)
}