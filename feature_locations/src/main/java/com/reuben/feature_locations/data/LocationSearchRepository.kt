package com.reuben.feature_locations.data

import android.location.Location
import com.reuben.core_common.network.ApiResponse
import com.reuben.core_data.models.db.LocationEntity
import com.reuben.core_data.models.weather.CurrentWeatherResponse

interface LocationSearchRepository {
    suspend fun getCurrentWeatherByLocation(location: Location): ApiResponse<CurrentWeatherResponse>
    suspend fun insertCurrentLocation(locationEntity: LocationEntity)
}