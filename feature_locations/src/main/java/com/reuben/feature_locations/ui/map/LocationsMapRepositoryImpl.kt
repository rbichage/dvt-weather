package com.reuben.feature_locations.ui.map

import com.reuben.core_database.LocationDao
import javax.inject.Inject

class LocationsMapRepositoryImpl @Inject constructor(
        private val locationDao: LocationDao
) : LocationsMapRepository {
    override fun getAllocations()= locationDao.getAllLocation()
}