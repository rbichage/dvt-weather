package com.reuben.feature_weather.util.ui

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.Window
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.palette.graphics.Palette
import com.reuben.core_data.models.db.LocationEntity
import com.reuben.core_data.models.weather.CurrentWeatherResponse
import com.reuben.core_ui.getBitMap
import com.reuben.feature_weather.R
import com.reuben.feature_weather.databinding.FragmentHomeBinding

fun FragmentHomeBinding.updateBackgrounds(
        thisColor: Int,
        drawable: Drawable,
        @DrawableRes drawableId: Int
) {
    root.setBackgroundColor(
            ContextCompat.getColor(this.root.context, thisColor)
    )

    weatherLayout.background = drawable

    root.context.getBitMap(drawableId)

}

fun Activity.updateStatusBarColor(bitMap: Bitmap) {

    Palette.Builder(bitMap)
            .generate { result ->

                result?.let {
                    val dominantSwatch = it.dominantSwatch


                    if (dominantSwatch != null) {
                        val window: Window = window
                        window.statusBarColor = dominantSwatch.rgb

                    }
                }
            }
}


fun FragmentHomeBinding.changeBackground(locationEntity: LocationEntity? = null, weatherResponse: CurrentWeatherResponse? = null): Bitmap {
    val id = locationEntity?.weatherCondition ?: weatherResponse!!.weather[0].id.toString()
    val context = root.context

    return when {

        //Thunderstorm
        id.startsWith("2", true) -> {
            val cloudyBackground = ContextCompat.getDrawable(context, R.drawable.forest_rainy)
            updateBackgrounds(R.color.colorRainy, cloudyBackground!!, R.drawable.forest_rainy)
            root.context.getBitMap(R.drawable.forest_rainy)
        }

        //drizzle
        id.startsWith("3", true) -> {
            val cloudyBackground = ContextCompat.getDrawable(context, R.drawable.forest_rainy)

            updateBackgrounds(R.color.colorRainy, cloudyBackground!!, R.drawable.forest_rainy)
            root.context.getBitMap(R.drawable.forest_rainy)

        }

        // Rain
        id.startsWith("5", true) -> {
            val cloudyBackground = ContextCompat.getDrawable(context, R.drawable.forest_rainy)

            updateBackgrounds(R.color.colorRainy, cloudyBackground!!, R.drawable.forest_rainy)
            root.context.getBitMap(R.drawable.forest_rainy)

        }

        //Snow
        id.startsWith("6", true) -> {
            val cloudyBackground = ContextCompat.getDrawable(context, R.drawable.forest_rainy)

            updateBackgrounds(R.color.colorRainy, cloudyBackground!!, R.drawable.forest_cloudy)
            root.context.getBitMap(R.drawable.forest_rainy)

        }

        //Atmosphere
        id.startsWith("7", true) -> {
            val cloudyBackground = ContextCompat.getDrawable(context, R.drawable.forest_cloudy)

            updateBackgrounds(R.color.colorCloudy, cloudyBackground!!, R.drawable.forest_cloudy)
            root.context.getBitMap(R.drawable.forest_cloudy)

        }

        //sunny/clear

        id.equals("800", true) -> {
            val cloudyBackground = ContextCompat.getDrawable(context, R.drawable.forest_sunny)

            updateBackgrounds(R.color.colorSunny, cloudyBackground!!, R.drawable.forest_sunny)
            root.context.getBitMap(R.drawable.forest_sunny)

        }

        // cloudy
        id.toInt() > 800 -> {

            val cloudy = ContextCompat.getDrawable(context, R.drawable.forest_cloudy)

            updateBackgrounds(R.color.colorCloudy, cloudy!!, R.drawable.forest_cloudy)
            root.context.getBitMap(R.drawable.forest_cloudy)
        }

        else -> {
            root.context.getBitMap(R.drawable.forest_cloudy)
        }


    }

}