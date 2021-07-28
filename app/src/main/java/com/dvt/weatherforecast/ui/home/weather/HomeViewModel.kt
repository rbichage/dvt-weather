package com.dvt.weatherforecast.ui.home

import android.location.Geocoder
import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dvt.weatherforecast.data.models.CurrentWeatherResponse
import com.dvt.weatherforecast.data.models.OneShotForeCastResponse
import com.dvt.weatherforecast.mappers.toForeCastEntity
import com.dvt.weatherforecast.mappers.toLocationEntity
import com.dvt.weatherforecast.ui.home.weather.HomeRepository
import com.dvt.weatherforecast.utils.location.GetLocation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getLocation: GetLocation,
    private val geocoder: Geocoder,
    private val homeRepository: HomeRepository
) : ViewModel() {

    private var _currentLocation: MutableLiveData<Location> = MutableLiveData()
    val currentLocation
        get() = _currentLocation


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

    fun insertToForecastDb(
        oneShotForeCastResponse: OneShotForeCastResponse,
        currentLocation: String
    ) =
        viewModelScope.launch(IO) {
            val entities = oneShotForeCastResponse.toForeCastEntity(currentLocation)

            for (entity in entities) {
                homeRepository.insertForeCast(entity)
            }

        }

    fun insertCurrentToDb(response: CurrentWeatherResponse) = viewModelScope.launch(IO) {
        val current = response.toLocationEntity()
        homeRepository.insertCurrentLocation(current)
    }

    fun getForecasts() = homeRepository.getAllForeCasts()

    fun getCurrentCity() = homeRepository.getCurrentLocation()

}