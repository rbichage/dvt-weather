package com.dvt.weatherforecast.utils.storage

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

object UserPreferences {
    private lateinit var sharedPreferences: SharedPreferences

    operator fun invoke(context: Context) {

        sharedPreferences = context.getSharedPreferences("weather_prefs", Context.MODE_PRIVATE)

    }

    fun saveLatestLocation(locationName: String) {
        sharedPreferences.edit {
            putString("last_location", locationName)
        }
    }

    val lastLocation
        get() = sharedPreferences.getString("last_location", "")!!
}