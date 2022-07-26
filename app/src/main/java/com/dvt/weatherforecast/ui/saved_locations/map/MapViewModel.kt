package com.dvt.weatherforecast.ui.saved_locations.map

import android.location.Geocoder
import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dvt.weatherforecast.ui.home.weather.HomeRepository
import com.dvt.weatherforecast.utils.location.GetLocation
import com.dvt.weatherforecast.utils.location.geoCodeThisLocation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor (
        private val getLocation: GetLocation,
        private val geocoder: Geocoder,
        private val homeRepository: HomeRepository
) : ViewModel() {

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->

    }

    private val _uiState: MutableLiveData<MapUiState> = MutableLiveData()
    val uiState: LiveData<MapUiState>
        get() = _uiState

    fun getCurrentLocation() {
        viewModelScope.launch(Dispatchers.IO) {
            getLocation.fetchCurrentLocation().collect { location ->
                _uiState.postValue(MapUiState.LocationData(location))
                cancel("Location is $location")
            }
        }
    }

    fun getAllLocations() = homeRepository.getAllLocations()

    fun geoCodeLocation(location: Location) {
        viewModelScope.launch(exceptionHandler) {
            withContext(Dispatchers.IO) {
                geoCodeThisLocation(
                        geocoder = geocoder,
                        location = location,
                        onAddressRetrieved = { address ->
                            val locationName = address.getAddressLine(0).ifEmpty { address.subLocality }

                            _uiState.postValue(MapUiState.LocationName(locationName))
                        },
                        onAddressNotFound = {

                        }
                )
            }
        }
    }
}

sealed interface MapUiState {
    data class LocationData(val location: Location) : MapUiState
    data class LocationName(val addressName: String) : MapUiState
}