package com.dvt.weatherforecast.ui.saved_locations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dvt.weatherforecast.data.models.db.LocationEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationsViewModel @Inject constructor(
        private val locationsRepository: LocationsRepository
) : ViewModel() {

    suspend fun getLocations() = locationsRepository.getAllLocations()

    fun deleteEntity(locationEntity: LocationEntity) = viewModelScope.launch {
        locationsRepository.deleteLocation(locationEntity)
    }
}