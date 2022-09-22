package com.reuben.feature_weather.util

import com.reuben.core_testing.util.genericError
import com.reuben.core_testing.util.getJson
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import java.net.HttpURLConnection


enum class WeatherDispatcherTypes {
    WEATHER_SUCCESS, WEATHER_FAIL, FORECAST_SUCCESS, FORECAST_FAIL
}


val successWeatherAndForecastRequestDispatcher = object : Dispatcher() {
    override fun dispatch(request: RecordedRequest): MockResponse {
        val path = request.path.orEmpty()

        return when {
            path.contains("weather", true) -> {
                MockResponse().apply {
                    setResponseCode(HttpURLConnection.HTTP_OK)
                    setBody(getJson("json/weatherresponse.json"))
                }
            }

            path.contains("onecall", true) -> {
                MockResponse().apply {
                    setResponseCode(HttpURLConnection.HTTP_OK)
                    setBody(getJson("json/forecast.json"))
                }
            }

            else -> throw IllegalArgumentException("Invalid url path #$path")
        }
    }


}

val failureWeatherRequestDispatcher = object : Dispatcher() {
    override fun dispatch(request: RecordedRequest) = MockResponse().apply {
        setResponseCode(HttpURLConnection.HTTP_UNAUTHORIZED)
        setBody(genericError)
    }

}

val failureForeCastRequestDispatcher = object : Dispatcher() {
    override fun dispatch(request: RecordedRequest) = MockResponse().apply {
        setResponseCode(HttpURLConnection.HTTP_NOT_FOUND)
        setBody(genericError)
    }
}

sealed class WeatherAndForeCastDispatcher