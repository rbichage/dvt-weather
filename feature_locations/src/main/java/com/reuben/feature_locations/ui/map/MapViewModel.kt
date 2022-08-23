package com.reuben.feature_locations.ui.map

import android.annotation.SuppressLint
import android.location.Geocoder
import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reuben.core_common.location.CurrentLocationHelper
import com.reuben.core_common.location.geoCodeThisLocation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
        private val currentLocationHelper: CurrentLocationHelper,
        private val geocoder: Geocoder,
        private val locationsMapRepository: LocationsMapRepository
) : ViewModel() {

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->

    }

    private val _uiState: MutableLiveData<MapUiState> = MutableLiveData()
    val uiState: LiveData<MapUiState>
        get() = _uiState

    @SuppressLint("MissingPermission")
    fun getCurrentLocation() {
        viewModelScope.launch(Dispatchers.IO) {
            currentLocationHelper.fetchCurrentLocation().collect { location ->
                _uiState.postValue(MapUiState.LocationData(location))
                cancel("Location is $location")
            }
        }
    }

    fun getAllLocations() = locationsMapRepository.getAllocations()

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