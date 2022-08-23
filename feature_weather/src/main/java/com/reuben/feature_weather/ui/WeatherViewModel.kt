package com.reuben.feature_weather.ui

import android.Manifest
import android.location.Geocoder
import android.location.Location
import androidx.annotation.RequiresPermission
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reuben.core_common.location.CurrentLocationHelper
import com.reuben.core_common.location.geoCodeThisLocation
import com.reuben.core_common.network.ApiResponse
import com.reuben.core_common.network.ErrorHolder
import com.reuben.core_data.mappers.weather.toCurrentLocationEntity
import com.reuben.core_data.mappers.weather.toForeCastEntity
import com.reuben.core_data.models.db.LocationEntity
import com.reuben.core_data.models.weather.CurrentWeatherResponse
import com.reuben.core_data.models.weather.OneShotForeCastResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
        private val currentLocationHelper: CurrentLocationHelper,
        private val geocoder: Geocoder,
        private val homeRepository: HomeRepository,
) : ViewModel() {

    private val _uiState: MutableStateFlow<HomeUiState> = MutableStateFlow(HomeUiState.Initial)
    val uiState: StateFlow<HomeUiState>
        get() = _uiState

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        _uiState.value = (HomeUiState.GeocodeError(throwable.message.orEmpty()))
    }

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    fun getCurrentLocation() {
        viewModelScope.launch(Dispatchers.IO) {
            currentLocationHelper.fetchCurrentLocation().collect { location ->
                _uiState.value = (HomeUiState.CurrentLocation(location))
                cancel("Location is $location")
            }
        }
    }


    fun getCurrentWeather(location: Location, shouldGeoCode: Boolean = true, locationName: String = "") {
        _uiState.value = HomeUiState.Loading

        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) { homeRepository.getCurrentWeatherByLocation(location) }

            when (response) {
                is ApiResponse.Success -> {
                    val data = response.value
                    val entity = data.toCurrentLocationEntity(locationName = locationName)
                    insertLocation(entity)
                    if (shouldGeoCode) {
                        geoCodeLocation(location, entity)
                    }
                    getForeCastFromLocation(location)
                    _uiState.value = (HomeUiState.CurrentWeather(data))
                }
                is ApiResponse.Failure -> {
                    _uiState.value = (HomeUiState.Error(response.errorHolder))

                }
            }
        }

    }


    private fun getForeCastFromLocation(location: Location) {
        viewModelScope.launch {
            _uiState.value = (HomeUiState.Loading)

            when (val response = withContext(Dispatchers.IO) {
                homeRepository.getNextForeCastByLocation(location)
            }) {
                is ApiResponse.Success -> {
                    val data = response.value
                    insertToForecastDb(data)
                    _uiState.value = (HomeUiState.ForecastData(data))
                }
                is ApiResponse.Failure -> {
                    _uiState.value = (HomeUiState.Error(response.errorHolder))
                }
            }
        }

    }

    private fun insertToForecastDb(
            oneShotForeCastResponse: OneShotForeCastResponse
    ) {

        viewModelScope.launch(Dispatchers.IO) {
            val entities = oneShotForeCastResponse
                    .toForeCastEntity("")
                    .toMutableList()
                    .also { it.removeAt(0) }


            Timber.e("entities $entities")
            for (entity in entities) {
                homeRepository.insertForeCast(entity)
            }

        }
    }


    fun deleteCurrentLocation() = viewModelScope.launch(Dispatchers.IO) {
        homeRepository.deleteCurrentLocation()
    }

    private fun insertLocation(locationEntity: LocationEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            homeRepository.insertCurrentLocation(locationEntity)
        }
    }


    fun getForecasts() = homeRepository.getAllForeCasts()

    fun getAllLocations() = homeRepository.getAllLocations()

    private fun geoCodeLocation(location: Location, entity: LocationEntity) {
        viewModelScope.launch(exceptionHandler) {
            withContext(Dispatchers.IO) {
                geoCodeThisLocation(
                        geocoder = geocoder,
                        location = location,
                        onAddressRetrieved = { address ->
                            val locationName = address.locality
                            _uiState.value = (HomeUiState.GeoCodeData(locationName))

                            if (!entity.name.equals(locationName, true)) {
                                updateCurrentEntity(entity.copy(name = locationName))
                            }
                        },
                        onAddressNotFound = {
                            Timber.e("no addresses found")
                        }
                )
            }
        }
    }

    private fun updateCurrentEntity(entity: LocationEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            homeRepository.updateLocation(entity)
        }
    }

}

sealed interface HomeUiState {
    object Loading : HomeUiState
    object Initial : HomeUiState
    data class CurrentWeather(val data: CurrentWeatherResponse) : HomeUiState
    data class ForecastData(val data: OneShotForeCastResponse) : HomeUiState
    data class CurrentLocation(val location: Location) : HomeUiState
    data class Error(val errorHolder: ErrorHolder) : HomeUiState
    data class GeocodeError(val message: String) : HomeUiState
    data class GeoCodeData(val addressName: String) : HomeUiState
}