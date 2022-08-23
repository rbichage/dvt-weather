package com.reuben.feature_search.data

import android.location.Location
import com.reuben.core_common.network.ApiResponse
import com.reuben.core_data.models.db.LocationEntity
import com.reuben.core_data.models.weather.CurrentWeatherResponse
import com.reuben.core_database.LocationDao
import com.reuben.core_di.WeatherApiKey
import com.reuben.core_network.api.ApiService
import com.reuben.core_network.util.apiCall
import javax.inject.Inject

class LocationSearchRepositoryImpl @Inject constructor(
        private val apiService: ApiService,
        private val locationDao: LocationDao,
        @WeatherApiKey private val apiKey: String
) : LocationSearchRepository {
    override suspend fun getCurrentWeatherByLocation(location: Location): ApiResponse<CurrentWeatherResponse> {
        return apiCall {
            apiService.getCurrentByLocation(
                    lat = location.latitude.toString(),
                    lon = location.longitude.toString(),
                    apiKey = apiKey
            )
        }
    }

    override suspend fun insertCurrentLocation(locationEntity: LocationEntity) {
        locationDao.insertLocation(locationEntity)
    }

}