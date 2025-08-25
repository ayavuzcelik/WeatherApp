package com.adm.weatherapp.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adm.weatherapp.model.GetWeatherResponse
import com.adm.weatherapp.repository.DataStoreRepository
import com.adm.weatherapp.repository.WeatherRepository
import com.adm.weatherapp.service.DataStoreAPI
import com.adm.weatherapp.util.Constants.LAT_KEY
import com.adm.weatherapp.util.Constants.LON_KEY
import com.adm.weatherapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val dataStoreRepository: DataStoreAPI
) : ViewModel() {
    var weatherData = mutableStateOf<GetWeatherResponse?>(null)
    var errorMessage = mutableStateOf("")
    var isLoading = mutableStateOf(false)

    init {
        viewModelScope.launch {
            val lat = dataStoreRepository.getString(LAT_KEY)
            val lon = dataStoreRepository.getString(LON_KEY)

            if (lat != null && lon != null) {
                fetchWeather(lat, lon)
            }
        }
    }

    fun saveLocation(lat: Double, lon: Double) {
        viewModelScope.launch {
            dataStoreRepository.putString(LAT_KEY, lat.toString())
            dataStoreRepository.putString(LON_KEY, lon.toString())
        }
    }


    fun fetchWeather(lat: String, lon: String) {
        isLoading.value = true
        viewModelScope.launch {
            val result = weatherRepository.getWeather(lat,lon);
            when (result) {
                is Resource.Success -> {
                    weatherData.value = result.data!!
                    isLoading.value = false
                    errorMessage.value = ""
                }
                is Resource.Error -> {
                    errorMessage.value = result.message!!
                    isLoading.value = false
                }
                is Resource.Loading -> {
                    isLoading.value = true
                }
            }
        }
    }
}
