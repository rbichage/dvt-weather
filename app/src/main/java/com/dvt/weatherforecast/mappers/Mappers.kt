package com.dvt.weatherforecast.mappers

import com.dvt.weatherforecast.data.models.CurrentWeatherResponse
import com.dvt.weatherforecast.data.models.OneShotForeCastResponse
import com.dvt.weatherforecast.data.models.db.ForeCastEntity
import com.dvt.weatherforecast.data.models.db.LocationEntity
import com.dvt.weatherforecast.utils.convertTimeStamp


fun CurrentWeatherResponse.toLocationEntity(): LocationEntity = LocationEntity(
    name = name,
    lat = coord.lat,
    lng = coord.lon,
    normalTemp = this.main.temp.toInt(),
    highTemp = main.tempMax.toInt(),
    lowTemp = main.tempMin.toInt(),
    lastUpdated = System.currentTimeMillis(),
    weatherCondition = weather[0].id.toString(),
    weatherConditionName = weather[0].main
)


fun OneShotForeCastResponse.toForeCastEntity(name: String): List<ForeCastEntity> {
    val items: MutableList<ForeCastEntity> = ArrayList()

    for (forecast in daily) {

        val foreCastEntity = ForeCastEntity(
            day = convertTimeStamp(forecast.dt.toLong()),
            locationName = name,
            lat = lat,
            lng = lon,
            normalTemp = forecast.temp.day.toInt(),
            lastUpdated = forecast.dt.toLong(),
            weatherCondition = forecast.weather[0].id.toString()
        )

        items.add(foreCastEntity)
    }

    items.removeAt(items.lastIndex)

    return items
}