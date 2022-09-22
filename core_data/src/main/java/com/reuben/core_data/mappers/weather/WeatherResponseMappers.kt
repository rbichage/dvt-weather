package com.reuben.core_data.mappers.weather

import com.reuben.core_common.date.convertTimeStamp
import com.reuben.core_data.models.db.ForeCastEntity
import com.reuben.core_data.models.db.LocationEntity
import com.reuben.core_data.models.weather.CurrentWeatherResponse
import com.reuben.core_data.models.weather.OneCallForeCastResponse

fun CurrentWeatherResponse.toCurrentLocationEntity(locationName: String = ""): LocationEntity {

    return LocationEntity(
            name = if (locationName.trim().isNotEmpty()) locationName else name,
            lat = coord.lat,
            lng = coord.lon,
            normalTemp = this.main.temp.toInt(),
            highTemp = main.tempMax.toInt(),
            lowTemp = main.tempMin.toInt(),
            lastUpdated = System.currentTimeMillis(),
            isCurrent = true,
            weatherCondition = weather[0].id.toString(),
            weatherConditionName = weather[0].description,
            locationId = "${coord.lat}${coord.lon}"
    )
}

fun CurrentWeatherResponse.toNewLocationEntity(locationName: String): LocationEntity {

    return LocationEntity(
            name = locationName,
            lat = coord.lat,
            lng = coord.lon,
            normalTemp = this.main.temp.toInt(),
            highTemp = main.tempMax.toInt(),
            lowTemp = main.tempMin.toInt(),
            lastUpdated = System.currentTimeMillis(),
            isCurrent = false,
            weatherCondition = weather[0].id.toString(),
            weatherConditionName = weather[0].description,
            locationId = "${coord.lat}${coord.lon}"
    )
}


fun OneCallForeCastResponse.toForeCastEntity(name: String): List<ForeCastEntity> =
        daily.map { forecast ->
            ForeCastEntity(
                    day = convertTimeStamp(forecast.dt.toLong()),
                    locationName = name,
                    lat = lat,
                    lng = lon,
                    normalTemp = forecast.temp.day.toInt(),
                    lastUpdated = forecast.dt.toLong(),
                    weatherCondition = forecast.weather[0].id.toString()
            )
        }