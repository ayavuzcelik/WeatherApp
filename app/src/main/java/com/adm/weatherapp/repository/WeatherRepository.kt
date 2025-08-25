package com.adm.weatherapp.repository

import com.adm.weatherapp.model.GetWeatherResponse
import com.adm.weatherapp.service.WeatherAPI
import com.adm.weatherapp.util.Constants.API_KEY
import com.adm.weatherapp.util.Resource
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class WeatherRepository @Inject constructor(
    private val api: WeatherAPI
) {
    suspend fun getWeather(lat: String, lon: String): Resource<GetWeatherResponse> {
        val response = try {
            api.getWeather(lat = lat, lon = lon, API_KEY)
        } catch (e: Exception) {
            return Resource.Error("Error" + e.message)
        }
        return Resource.Success(response)
    }
}