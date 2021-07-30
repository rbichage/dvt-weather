package com.dvt.weatherforecast.utils.network

sealed class ApiResponse<out T> {
    data class Success<out T>(val value: T) : ApiResponse<T>()
    data class Failure(val errorHolder: ErrorHolder) : ApiResponse<Nothing>()
}

data class ErrorHolder(override val message: String, val statusCode: Int?, val body: String) : Exception(message)