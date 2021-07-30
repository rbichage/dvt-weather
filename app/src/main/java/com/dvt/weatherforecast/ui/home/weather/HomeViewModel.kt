package com.dvt.weatherforecast.ui.home.weather

import android.location.Geocoder
import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dvt.weatherforecast.data.models.CurrentWeatherResponse
import com.dvt.weatherforecast.data.models.OneShotForeCastResponse
import com.dvt.weatherforecast.mappers.toCurrentLocationEntity
import com.dvt.weatherforecast.mappers.toForeCastEntity
import com.dvt.weatherforecast.mappers.toNewLocationEntity
import com.dvt.weatherforecast.utils.location.GetLocation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import timber.log.Timber
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


    private var _isLoading: MutableLiveData<Boolean> = MutableLiveData()
    val isLoading
        get() = _isLoading


    fun getCurrentLocation() = viewModelScope.launch(IO) {

        getLocation.fetchCurrentLocation().collect { location ->
            _currentLocation.postValue(location)
            cancel("Location is $location")
        }
    }

    suspend fun getDataFromLocation(location: Location) = flow {
        _isLoading.value = true
        emit(homeRepository.getByLocation(location))
        _isLoading.value = false
    }

    suspend fun getForeCastFromLocation(location: Location) = flow {
        emit(homeRepository.getForeCastByLocation(location))
    }

    fun insertToForecastDb(
        oneShotForeCastResponse: OneShotForeCastResponse,
        currentLocation: String
    ) =
        viewModelScope.launch(IO) {
            val entities = oneShotForeCastResponse.toForeCastEntity(currentLocation).toMutableList()

            entities.removeAt(0)

            Timber.e("entities $entities")
            for (entity in entities) {
                homeRepository.insertForeCast(entity)
            }

        }

    fun insertCurrentToDb(response: CurrentWeatherResponse) = viewModelScope.launch(IO) {
        val current = response.toCurrentLocationEntity()
        homeRepository.insertCurrentLocation(current)
    }

    fun insertNewToDb(response: CurrentWeatherResponse) = viewModelScope.launch(IO) {
        val current = response.toNewLocationEntity()
        homeRepository.insertCurrentLocation(current)
    }

    suspend fun deleteCurrentLocation() = viewModelScope.launch {
        homeRepository.deleteCurrentLocation()
    }


    fun getForecasts() = homeRepository.getAllForeCasts()

    fun getCurrentCity() = homeRepository.getCurrentLocation()

}