package com.reuben.feature_locations.ui.map

import com.reuben.core_data.models.db.LocationEntity
import kotlinx.coroutines.flow.Flow

interface LocationsMapRepository {
    fun getAllocations(): Flow<List<LocationEntity>>
}