package com.dvt.weatherforecast.ui.home.weather

import android.location.Geocoder
import android.location.Location
import androidx.lifecycle.*
import com.dvt.weatherforecast.data.models.CurrentWeatherResponse
import com.dvt.weatherforecast.data.models.OneShotForeCastResponse
import com.dvt.weatherforecast.data.models.db.LocationEntity
import com.dvt.weatherforecast.mappers.toCurrentLocationEntity
import com.dvt.weatherforecast.mappers.toForeCastEntity
import com.dvt.weatherforecast.mappers.toNewLocationEntity
import com.dvt.weatherforecast.utils.location.GetLocation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
    val currentLocation: LiveData<Location>
        get() = _currentLocation


    private var _isLoading: MutableLiveData<Boolean> = MutableLiveData()
    val isLoading: LiveData<Boolean> = _isLoading

    @ExperimentalCoroutinesApi
    fun getCurrentLocation() = viewModelScope.launch(IO) {

        getLocation.fetchCurrentLocation().collect { location ->
            _currentLocation.postValue(location)
            cancel("Location is $location")
        }
    }

    fun geoCodeThisLocation(thisLocation: Location) = liveData {

        try {
            val addresses = geocoder.getFromLocation(thisLocation.latitude, thisLocation.longitude, 1)

            if (addresses.isNotEmpty()) {
                val address0 = addresses[0]
                emit(address0)
            }

        } catch (e: Exception) {

        }
    }

    fun getDataFromLocation(location: Location) = liveData {
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

    fun insertCurrentToDb(response: CurrentWeatherResponse, locationName: String) = viewModelScope.launch(IO) {
        val current = response.toCurrentLocationEntity(locationName)
        homeRepository.insertCurrentLocation(current)
    }

    fun insertNewToDb(response: CurrentWeatherResponse, locationName: String) = viewModelScope.launch(IO) {

        Timber.e("place name $locationName")
        val current = response.toNewLocationEntity(locationName)
        homeRepository.insertCurrentLocation(current)
    }


    fun insertNewToDb(locationEntity: LocationEntity) = viewModelScope.launch(IO) {

        Timber.e("place name ${locationEntity.name}")
        homeRepository.insertCurrentLocation(locationEntity)
    }


    fun deleteCurrentLocation() = viewModelScope.launch(IO) {
        homeRepository.deleteCurrentLocation()
    }


    fun getForecasts() = homeRepository.getAllForeCasts()

    fun getAllLocations() = homeRepository.getAllLocations()

    suspend fun deleteEntity(locationEntity: LocationEntity) = viewModelScope.launch(IO) {
        homeRepository.deleteLocation(locationEntity)
    }

}