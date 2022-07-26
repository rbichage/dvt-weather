package com.dvt.weatherforecast.ui.search

import android.location.Geocoder
import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dvt.weatherforecast.data.models.db.LocationEntity
import com.dvt.weatherforecast.data.models.places.CustomAddressModel
import com.dvt.weatherforecast.data.models.places.CustomPlaceDetails
import com.dvt.weatherforecast.mappers.toNewLocationEntity
import com.dvt.weatherforecast.ui.home.weather.HomeRepository
import com.dvt.weatherforecast.utils.location.geoCodeThisAddress
import com.dvt.weatherforecast.utils.network.ApiResponse
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
        private val homeRepository: HomeRepository,
        private val geocoder: Geocoder
) : ViewModel() {

    private val _uiState: MutableStateFlow<SearchUIState> = MutableStateFlow(SearchUIState.Init)
    val uiState: StateFlow<SearchUIState>
        get() = _uiState

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Timber.e("${throwable.printStackTrace()}")
        val error = throwable.message.orEmpty()
        _uiState.value = (SearchUIState.Error(error))
    }

    fun getPlaceName(
            placesClient: PlacesClient,
            token: AutocompleteSessionToken,
            placeQuery: String,
    ) {
        viewModelScope.launch(exceptionHandler) {

            withContext(Dispatchers.IO) {
                val request = FindAutocompletePredictionsRequest.builder()
                        .setQuery(placeQuery)
                        .setSessionToken(token)

                placesClient.findAutocompletePredictions(request.build())
                        .addOnSuccessListener { response ->
                            Timber.e("places response ${response.autocompletePredictions}")

                            val content = response.autocompletePredictions.map {
                                CustomAddressModel(
                                        name = it.getPrimaryText(null).toString(),
                                        address = it.getSecondaryText(null).toString()
                                )
                            }

                            updatePlacesValues(content)

                        }.addOnFailureListener {
                            Timber.e("$it.printStackTrace()")
                        }

            }


        }
    }

    private fun updatePlacesValues(content: List<CustomAddressModel>) {
        _uiState.value = SearchUIState.Addresses(content)
    }

    fun getWeatherDataFromLocation(location: Location, locationName: String) {
        _uiState.value = SearchUIState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            when (val response = homeRepository.getCurrentWeatherByLocation(location)) {
                is ApiResponse.Failure -> {
                    _uiState.value = SearchUIState.Error(message = response.errorHolder.message)
                }
                is ApiResponse.Success -> {
                    val data = response.value
                    val entity = data.toNewLocationEntity(locationName)
                    insertLocation(entity)
                }
            }

        }
    }

    private fun insertLocation(entity: LocationEntity) {
        _uiState.value = SearchUIState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            homeRepository.insertCurrentLocation(entity)
        }.invokeOnCompletion {
            _uiState.value = SearchUIState.WeatherAdded
        }
    }

    fun reverseGeoCodeLocation(address: CustomAddressModel) {
        viewModelScope.launch(exceptionHandler) {
            withContext(Dispatchers.IO) {
                _uiState.value = SearchUIState.Loading
                geoCodeThisAddress(
                        geocoder = geocoder,
                        address = "${address.name}, ${address.address}",
                        onAddressNotFound = {
                            _uiState.value = SearchUIState.NoAddress

                        },
                        onAddressRetrieved = { currentAddress ->
                            val lat = currentAddress.latitude
                            val lng = currentAddress.longitude
                            val data = CustomPlaceDetails(address.name, currentAddress.featureName.orEmpty(), latLng = LatLng(lat, lng))
                            _uiState.value = SearchUIState.GeocodeData(data)
                        }
                )
            }
        }
    }

}

sealed interface SearchUIState {
    object Init : SearchUIState
    object Loading : SearchUIState
    data class Error(val message: String) : SearchUIState
    data class GeocodeData(val customPlaceDetails: CustomPlaceDetails) : SearchUIState
    object NoAddress : SearchUIState
    object WeatherAdded : SearchUIState
    data class Addresses(val addresses: List<CustomAddressModel>) : SearchUIState
}