package com.dvt.weatherforecast.data.base

import com.dvt.weatherforecast.utils.network.ApiResponse
import com.dvt.weatherforecast.utils.network.ErrorHolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

abstract class BaseRepository {

    suspend fun <T> apiCall(apiCall: suspend () -> T): ApiResponse<T> {


        return withContext(Dispatchers.IO) {

            try {
                ApiResponse.Success(apiCall.invoke())
            } catch (e: Exception) {
                e.printStackTrace()
                Timber.e("exception $e")

                when (e) {
                    is IOException -> ApiResponse.Failure(
                            ErrorHolder("Unable to connect, check your connection", 1, "")
                    )

                    is HttpException -> ApiResponse.Failure(extractHttpExceptions(e))

                    is UnknownHostException -> ApiResponse.Failure(
                            ErrorHolder("Unable to connect, check your connection", 1, "")
                    )

                    is SocketTimeoutException -> ApiResponse.Failure(
                            ErrorHolder("Unable to connect, check your connection", 1, "")
                    )
                    else -> ApiResponse.Failure(ErrorHolder(e.message.orEmpty(), 1, ""))
                }
            }
        }
    }

    private fun extractHttpExceptions(e: HttpException): ErrorHolder {
        val body = e.response()?.errorBody()
        val jsonString = body?.string()
        Timber.e("body ${e.response()?.body().toString()}")
        Timber.e("json string $jsonString")

        val message = try {
            val jsonObject = JSONObject(jsonString.orEmpty())
            jsonObject.getString("message")
        } catch (exception: JSONException) {
            when (e.code()) {
                500 -> {
                    "Unable to complete request your request, try again later"

                }
                503 -> {
                    "Service temporarily unavailable, try again in a few minutes"
                }
                else -> {
                    "Unable to complete request your request, try again later"

                }
            }
        }

        val errorCode = e.response()?.code() ?: 0
        return ErrorHolder(message, errorCode, jsonString.orEmpty())
    }
}
