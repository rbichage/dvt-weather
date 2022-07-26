package com.dvt.weatherforecast.utils.network

import com.dvt.weatherforecast.BuildConfig
import okhttp3.logging.HttpLoggingInterceptor

val loggingInterceptor: HttpLoggingInterceptor
    get() {
        return HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        }

    }