package com.dvt.weatherforecast.ui.home.weather

import android.location.Location
import com.dvt.weatherforecast.data.base.BaseRepository
import com.dvt.weatherforecast.data.models.db.ForeCastEntity
import com.dvt.weatherforecast.data.models.db.LocationEntity
import com.dvt.weatherforecast.db.ForeCastDao
import com.dvt.weatherforecast.db.LocationDao
import com.dvt.weatherforecast.di.NetworkModule
import com.dvt.weatherforecast.network.ApiService
import javax.inject.Inject

class HomeRepository @Inject constructor(
        private val apiService: ApiService,
        private val foreCastDao: ForeCastDao,
        val locationDao: LocationDao,
        @NetworkModule.WeatherApiKey val apiKey: String
) : BaseRepository() {

    suspend fun getByLocation(location: Location) = apiCall {
        apiService.getCurrentByLocation(
                location.latitude.toString(),
                location.longitude.toString(),
                apiKey
        )
    }

    suspend fun getForeCastByLocation(location: Location) = apiCall {
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

    suspend fun deleteLocation(locationEntity: LocationEntity) = locationDao.deleteLocation(locationEntity)

    fun updateLocation(locationEntity: LocationEntity) = locationDao.updateLocation(locationEntity)
}