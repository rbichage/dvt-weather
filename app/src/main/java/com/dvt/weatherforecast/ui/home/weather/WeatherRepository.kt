package com.dvt.weatherforecast.ui.home.weather

import android.location.Location
import com.dvt.weatherforecast.data.base.BaseRepository
import com.dvt.weatherforecast.di.NetworkModule
import com.dvt.weatherforecast.network.ApiService
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val apiService: ApiService,
    @NetworkModule.WeatherApiKey private val apiKey: String
) : BaseRepository() {

    suspend fun getByCityName(cityName: String) = apiCall {
        apiService.getCurrentByName(cityName, apiKey)
    }

    suspend fun getByLocation(location: Location) = apiCall {
        apiService.getCurrentByLocation(
            location.latitude.toString(),
            location.longitude.toString(),
            apiKey
        )
    }

    suspend fun getForeCastByLocation(location: Location) = apiCall {
        apiService.getForecastByLocation(
            location.latitude.toString(),
            location.longitude.toString(),
            apiKey
        )
    }

}