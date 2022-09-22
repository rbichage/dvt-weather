package com.reuben.core_data.models.weather


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OneCallForeCastResponse(
        @Json(name = "daily") val daily: List<Daily>,
        @Json(name = "lat") val lat: Double,
        @Json(name = "lon") val lon: Double,
        @Json(name = "timezone") val timezone: String,
        @Json(name = "timezone_offset") val timezoneOffset: Int,
        @Json(name = "current") val currentWeather: Daily,
        @Json(name = "hourly") val hourlyForecast: List<Daily>
)

@JsonClass(generateAdapter = true)
data class Daily(
        @Json(name = "clouds") val clouds: Int,
        @Json(name = "dew_point") val dewPoint: Double,
        @Json(name = "dt") val dt: Int,
        @Json(name = "feels_like") val feelsLike: FeelsLike,
        @Json(name = "humidity") val humidity: Int,
        @Json(name = "moon_phase") val moonPhase: Double,
        @Json(name = "moonrise") val moonrise: Int,
        @Json(name = "moonset") val moonset: Int,
        @Json(name = "pop") val pop: Double,
        @Json(name = "pressure") val pressure: Int,
        @Json(name = "sunrise") val sunrise: Int,
        @Json(name = "sunset") val sunset: Int,
        @Json(name = "temp") val temp: Temp,
        @Json(name = "uvi") val uvi: Double,
        @Json(name = "weather") val weather: List<Weather>,
        @Json(name = "wind_deg") val windDeg: Int,
        @Json(name = "wind_gust") val windGust: Double,
        @Json(name = "wind_speed") val windSpeed: Double
)

@JsonClass(generateAdapter = true)
data class Weather(
        @Json(name = "description")
        val description: String,
        @Json(name = "icon")
        val icon: String,
        @Json(name = "id")
        val id: Int,
        @Json(name = "main")
        val main: String
)

@JsonClass(generateAdapter = true)
data class Main(
        @Json(name = "feels_like")
        val feelsLike: Double,
        @Json(name = "humidity")
        val humidity: Int,
        @Json(name = "pressure")
        val pressure: Int,
        @Json(name = "temp")
        val temp: Double,
        @Json(name = "temp_max")
        val tempMax: Double,
        @Json(name = "temp_min")
        val tempMin: Double
)