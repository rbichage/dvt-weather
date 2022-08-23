package com.reuben.weatherforecast

import android.app.Application
import com.dvt.weatherforecast.BuildConfig
import com.dvt.weatherforecast.utils.storage.UserPreferences
import com.google.android.libraries.places.api.Places
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class WeatherForeCast : Application() {
    override fun onCreate() {
        super.onCreate()

        UserPreferences(this)
        Places.initialize(this, BuildConfig.GOOGLE_MAPS_KEY)
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}