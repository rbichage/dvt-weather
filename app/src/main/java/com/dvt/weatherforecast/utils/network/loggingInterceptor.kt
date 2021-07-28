package com.dvt.weatherforecast.utils.network

import okhttp3.logging.HttpLoggingInterceptor
import com.dvt.weatherforecast.BuildConfig

val loggingInterceptor: HttpLoggingInterceptor
    get() {
        val httpLoggingInterceptor = HttpLoggingInterceptor()

        httpLoggingInterceptor.apply {
            level =
                    if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        }
        return httpLoggingInterceptor
    }