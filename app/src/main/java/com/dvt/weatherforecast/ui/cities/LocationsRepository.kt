package com.dvt.weatherforecast.ui.cities

import com.dvt.weatherforecast.db.LocationDao
import javax.inject.Inject

class LocationsRepository @Inject constructor(
        private val locationDao: LocationDao
) {

    suspend fun getAllLocations() = locationDao.getAllLocation()

}