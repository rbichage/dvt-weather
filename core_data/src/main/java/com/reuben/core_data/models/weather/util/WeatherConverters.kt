package com.reuben.core_data.models.weather.util

import androidx.annotation.DrawableRes


sealed class WeatherType(
        val weatherName: String,
        val weatherId: Int,
        @DrawableRes val weatherIcon: Int
) {
    object Clouds : WeatherType("Clouds", 803, )
}