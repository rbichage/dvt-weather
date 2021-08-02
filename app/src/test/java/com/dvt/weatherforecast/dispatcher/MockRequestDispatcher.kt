package com.dvt.weatherforecast.dispatcher

import com.google.common.io.Resources.getResource
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import java.io.File
import java.net.HttpURLConnection


open class WeatherRequestDispatcher : Dispatcher() {
    override fun dispatch(request: RecordedRequest): MockResponse {

        return when {
            request.path!!.contains("weather", true) -> {
                MockResponse()
                        .setResponseCode(HttpURLConnection.HTTP_OK)
                        .setBody(getJson("json/weatherresponse.json"))
            }
            request.path!!.contains("onecall", true) -> {
                MockResponse()
                        .setResponseCode(HttpURLConnection.HTTP_OK)
                        .setBody(getJson("json/forecast.json"))
            }
            else -> {
                throw IllegalArgumentException("Unknown path ${request.path}")
            }
        }

    }


    fun getJson(path: String): String {

        val uri = getResource(path)
        val file = File(uri.path)

        return String(file.readBytes())
    }
}