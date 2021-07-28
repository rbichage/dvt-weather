package com.dvt.weatherforecast

import android.app.Application
import com.dvt.weatherforecast.utils.storage.UserPreferences
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class WeatherForeCast : Application() {
    override fun onCreate() {
        super.onCreate()

        UserPreferences(this)
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}