package com.dvt.weatherforecast.ui.home.weather

import android.location.Geocoder
import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dvt.weatherforecast.utils.location.GetLocation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val getLocation: GetLocation,
    private val geocoder: Geocoder,
    private val homeRepository: WeatherRepository
) : ViewModel() {

    private var _currentLocation: MutableLiveData<Location> = MutableLiveData()
    val currentLocation
        get() = _currentLocation

    private var _geoCoderError: MutableLiveData<Boolean> = MutableLiveData()
    val geocoderError
        get() = _geoCoderError


    fun getCurrentLocation() = viewModelScope.launch(Dispatchers.IO) {

        getLocation.fetchCurrentLocation().collect { location ->
            _currentLocation.postValue(location)
            cancel("Location is $location")
        }
    }

    suspend fun getDataFromLocation(location: Location) = flow {
        emit(homeRepository.getByLocation(location))
    }

    suspend fun getForeCastFromLocation(location: Location) = flow {
        emit(homeRepository.getForeCastByLocation(location))
    }

    suspend fun geoCodeThisLocation(thisLocation: Location) = flow {


        try {
            val addresses =
                geocoder.getFromLocation(thisLocation.latitude, thisLocation.longitude, 1)

            if (addresses.isNotEmpty()) {
                val address0 = addresses[0]
                emit(address0)
            }

        } catch (e: Exception) {
            _geoCoderError.value = true
        }

    }

}