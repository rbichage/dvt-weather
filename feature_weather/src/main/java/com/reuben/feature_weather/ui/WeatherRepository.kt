package com.reuben.feature_weather.ui

import android.location.Location
import com.reuben.core_data.models.db.ForeCastEntity
import com.reuben.core_data.models.db.LocationEntity
import com.reuben.core_database.LocationDao
import com.reuben.core_di.WeatherApiKey
import com.reuben.core_network.api.ApiService
import com.reuben.core_network.util.apiCall
import javax.inject.Inject

class HomeRepository @Inject constructor(
        private val apiService: ApiService,
        @WeatherApiKey private val apiKey: String,
        private val foreCastDao: com.reuben.core_database.ForeCastDao,
        val locationDao: LocationDao,
) {

    suspend fun getCurrentWeatherByLocation(location: Location) = apiCall {
        apiService.getCurrentByLocation(
                location.latitude.toString(),
                location.longitude.toString(),
                apiKey
        )
    }

    suspend fun getNextForeCastByLocation(location: Location) = apiCall {
        apiService.getForecastByLocation(
                location.latitude.toString(),
                location.longitude.toString(),
                apiKey
        )
    }

    suspend fun insertForeCast(foreCastEntity: ForeCastEntity) =
            foreCastDao.insertForeCast(foreCastEntity)

    suspend fun insertCurrentLocation(locationEntity: LocationEntity) =
            locationDao.insertLocation(locationEntity)

    fun getAllLocations() = locationDao.getAllLocation()


    suspend fun deleteCurrentLocation() = locationDao.deleteCurrentLocation(1)

    fun getAllForeCasts() = foreCastDao.getAllForeCasts()

    fun getCurrentLocation() = locationDao.getCurrentLocation()

    fun getLocationByLatitude(lat: Double) = locationDao.getLocationByLatitude(lat)

    suspend fun deleteLocation(locationEntity: LocationEntity) = locationDao.deleteLocation(locationEntity)

    fun updateLocation(locationEntity: LocationEntity) = locationDao.updateLocation(locationEntity)
}