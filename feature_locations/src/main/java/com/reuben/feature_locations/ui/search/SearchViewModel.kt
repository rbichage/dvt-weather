package com.reuben.feature_locations.ui.search

import android.location.Geocoder
import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.reuben.core_common.location.geoCodeThisAddress
import com.reuben.core_common.network.ApiResponse
import com.reuben.core_data.mappers.weather.toNewLocationEntity
import com.reuben.core_data.models.db.LocationEntity
import com.reuben.core_di.CoroutineIODispatcher
import com.reuben.feature_locations.data.LocationSearchRepository
import com.reuben.feature_locations.data.places.CustomAddressModel
import com.reuben.feature_locations.data.places.CustomPlaceDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
        private val locationSearchRepository: LocationSearchRepository,
        private val geocoder: Geocoder,
        @CoroutineIODispatcher private val dispatcher: CoroutineDispatcher
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

            withContext(dispatcher) {
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
        viewModelScope.launch {
            _uiState.value = SearchUIState.Loading

            when (val response = withContext(dispatcher) { locationSearchRepository.getCurrentWeatherByLocation(location) }) {
                is ApiResponse.Success -> {
                    val data = response.value
                    val entity = data.toNewLocationEntity(locationName)
                    insertLocation(entity)
                }
                is ApiResponse.Failure -> {
                    _uiState.value = SearchUIState.Error(message = response.errorHolder.message)
                }
            }
        }

    }

    private fun insertLocation(entity: LocationEntity) {
        _uiState.value = SearchUIState.Loading
        viewModelScope.launch(dispatcher) {
            locationSearchRepository.insertCurrentLocation(entity)
        }.invokeOnCompletion {
            _uiState.value = SearchUIState.WeatherAdded
        }
    }

    fun reverseGeoCodeLocation(address: CustomAddressModel) {
        viewModelScope.launch(exceptionHandler) {
            withContext(dispatcher) {
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