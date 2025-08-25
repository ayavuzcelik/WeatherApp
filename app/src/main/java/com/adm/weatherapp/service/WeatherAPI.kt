package com.adm.weatherapp.service

import com.adm.weatherapp.model.GetWeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherAPI {
    //lat
    //lon
    //appid
    @GET("weather?")
    suspend fun getWeather(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("appid") key: String,
    ): GetWeatherResponse
}