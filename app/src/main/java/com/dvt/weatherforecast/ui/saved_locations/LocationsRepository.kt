package com.dvt.weatherforecast.ui.saved_locations

import com.dvt.weatherforecast.data.models.db.LocationEntity
import com.dvt.weatherforecast.db.LocationDao
import javax.inject.Inject

class LocationsRepository @Inject constructor(
        private val locationDao: LocationDao
) {

    suspend fun getAllLocations() = locationDao.getAllLocation()

    suspend fun deleteLocation(locationEntity: LocationEntity) = locationDao.deleteLocation(locationEntity)
}