package com.dvt.weatherforecast.network

import com.dvt.weatherforecast.BuildConfig
import com.dvt.weatherforecast.data.models.CurrentWeatherResponse
import com.dvt.weatherforecast.data.models.OneShotForeCastResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("weather")
    suspend fun getCurrentByLocation(
            @Query("lat") lat: String,
            @Query("lon") lon: String,
            @Query("appid") apiKey: String = BuildConfig.OPEN_WEATHER_KEY,
            @Query("units") units: String = "metric"

    ): CurrentWeatherResponse

    @GET("onecall")
    suspend fun getForecastByLocation(
            @Query("lat") lat: String,
            @Query("lon") lon: String,
            @Query("appid") apiKey: String = BuildConfig.OPEN_WEATHER_KEY,
            @Query("units") units: String = "metric",
            @Query("exclude") exclude: String = "current,minutely,hourly,alerts"
    ): OneShotForeCastResponse

}