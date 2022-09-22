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
import com.reuben.core_data.mappers.weather.toForeCastEntity
import com.reuben.core_data.models.db.LocationEntity
import com.reuben.core_data.models.weather.CurrentWeatherResponse
import com.reuben.core_data.models.weather.Daily
import com.reuben.core_data.models.weather.OneCallForeCastResponse
import com.reuben.core_di.CoroutineIODispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
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
class WeatherViewModel @Inject constructor(
        private val currentLocationHelper: CurrentLocationHelper,
        private val geocoder: Geocoder,
        private val foreCastRepository: ForeCastRepository,
        @CoroutineIODispatcher val dispatcher: CoroutineDispatcher
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


    fun getForeCastFromLocation(location: Location) {
        viewModelScope.launch {
            _uiState.value = (HomeUiState.Loading)

            when (val response = withContext(Dispatchers.IO) {
                foreCastRepository.getForeCastForLocation(location)
            }) {

                is ApiResponse.Success -> {


                    val currentCondition = response.value.currentWeather
                    val hourlyForeCast = response.value.hourlyForecast
                    val today = response.value.daily[0]

                    _uiState.value = (HomeUiState.ForecastData(
                            currentCondition = currentCondition,
                            todaysWeather = today,
                            weeklyForecast = response.value.daily,
                            hourlyForeCast = hourlyForeCast

                    ))
                }
                is ApiResponse.Failure -> {
                    _uiState.value = (HomeUiState.Error(response.errorHolder))
                }
            }
        }

    }

    private fun insertToForecastDb(
            oneCallForeCastResponse: OneCallForeCastResponse
    ) {

        viewModelScope.launch(Dispatchers.IO) {
            val entities = oneCallForeCastResponse
                    .toForeCastEntity("")
                    .toMutableList()
                    .also { it.removeAt(0) }


            Timber.e("entities $entities")
            for (entity in entities) {
                weatherRepository.insertForeCast(entity)
            }

        }
    }


    private fun insertLocation(locationEntity: LocationEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            weatherRepository.insertCurrentLocation(locationEntity)
        }
    }


    fun getForecasts() = weatherRepository.getAllForeCasts()

    fun getAllLocations() = weatherRepository.getAllLocations()

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
            weatherRepository.updateLocation(entity)
        }
    }

}

sealed interface HomeUiState {
    object Loading : HomeUiState
    object Initial : HomeUiState
    data class CurrentWeather(val data: CurrentWeatherResponse) : HomeUiState
    data class ForecastData(
            val weeklyForecast: List<Daily>,
            val currentCondition: Daily,
            val todaysWeather: Daily,
            val hourlyForeCast: List<Daily>
    ) : HomeUiState

    data class CurrentLocation(val location: Location) : HomeUiState
    data class Error(val errorHolder: ErrorHolder) : HomeUiState
    data class GeocodeError(val message: String) : HomeUiState
    data class GeoCodeData(val addressName: String) : HomeUiState
}