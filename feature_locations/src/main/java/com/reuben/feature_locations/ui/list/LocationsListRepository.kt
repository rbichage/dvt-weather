package com.reuben.feature_locations.ui.list

import com.reuben.core_data.models.db.LocationEntity
import javax.inject.Inject

class LocationsListRepository @Inject constructor(
        private val locationDao: com.reuben.core_database.LocationDao
) {

    suspend fun getAllLocations() = locationDao.getAllLocation()

    suspend fun deleteLocation(locationEntity: LocationEntity) = locationDao.deleteLocation(locationEntity)
}