package com.reuben.core_network.api


import com.reuben.core_data.models.weather.OneCallForeCastResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("onecall")
    suspend fun getForecastByLocation(
            @Query("lat") lat: String,
            @Query("lon") lon: String,
            @Query("appid") apiKey: String,
            @Query("units") units: String = "metric",
            @Query("exclude") exclude: String = "minutely,alerts"
    ): OneCallForeCastResponse

}