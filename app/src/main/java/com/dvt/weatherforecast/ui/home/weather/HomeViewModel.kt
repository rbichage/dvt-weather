package com.dvt.weatherforecast.ui.home.weather

import android.location.Geocoder
import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dvt.weatherforecast.data.models.CurrentWeatherResponse
import com.dvt.weatherforecast.data.models.OneShotForeCastResponse
import com.dvt.weatherforecast.data.models.db.LocationEntity
import com.dvt.weatherforecast.mappers.toCurrentLocationEntity
import com.dvt.weatherforecast.mappers.toForeCastEntity
import com.dvt.weatherforecast.utils.location.GetLocation
import com.dvt.weatherforecast.utils.location.geoCodeThisLocation
import com.dvt.weatherforecast.utils.network.ApiResponse
import com.dvt.weatherforecast.utils.network.ErrorHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
        private val getLocation: GetLocation,
        private val geocoder: Geocoder,
        private val homeRepository: HomeRepository,
) : ViewModel() {

    private val _uiState: MutableLiveData<HomeUiState> = MutableLiveData()
    val uiState: LiveData<HomeUiState>
        get() = _uiState

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        _uiState.postValue(HomeUiState.GeocodeError(throwable.message.orEmpty()))
    }

    fun getCurrentLocation() {

        viewModelScope.launch(Dispatchers.IO) {
            getLocation.fetchCurrentLocation().collect { location ->
                _uiState.postValue(HomeUiState.CurrentLocation(location))
                cancel("Location is $location")
            }
        }
    }


    fun getCurrentWeather(location: Location, shouldGeoCode: Boolean = true, locationName: String = "") {
        _uiState.value = HomeUiState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            when (val response = homeRepository.getCurrentWeatherByLocation(location)) {
                is ApiResponse.Success -> {
                    val data = response.value
                    val entity = data.toCurrentLocationEntity(locationName = locationName)
                    insertLocation(entity)
                    if (shouldGeoCode) {
                        geoCodeLocation(location, entity)
                    }
                    getForeCastFromLocation(location)
                    _uiState.postValue(HomeUiState.CurrentWeather(data))
                }
                is ApiResponse.Failure -> {
                    _uiState.postValue(HomeUiState.Error(response.errorHolder))

                }
            }
        }
    }


    suspend fun getForeCastFromLocation(location: Location) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.postValue(HomeUiState.Loading)

            when (val response = homeRepository.getNextForeCastByLocation(location)) {
                is ApiResponse.Success -> {
                    val data = response.value
                    insertToForecastDb(data)
                    _uiState.postValue(HomeUiState.ForecastData(data))
                }
                is ApiResponse.Failure -> {
                    _uiState.postValue(HomeUiState.Error(response.errorHolder))
                }
            }
        }

    }

    private fun insertToForecastDb(
            oneShotForeCastResponse: OneShotForeCastResponse
    ) {

        viewModelScope.launch(Dispatchers.IO) {
            val entities = oneShotForeCastResponse.toForeCastEntity("").toMutableList()

            entities.removeAt(0)

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
                            _uiState.postValue(HomeUiState.GeoCodeData(locationName))

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
    data class CurrentWeather(val data: CurrentWeatherResponse) : HomeUiState
    data class ForecastData(val data: OneShotForeCastResponse) : HomeUiState
    data class CurrentLocation(val location: Location) : HomeUiState
    data class Error(val errorHolder: ErrorHolder) : HomeUiState
    data class GeocodeError(val message: String) : HomeUiState
    data class GeoCodeData(val addressName: String) : HomeUiState
}