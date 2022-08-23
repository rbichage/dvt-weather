package com.reuben.feature_locations.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reuben.core_data.models.db.LocationEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationsListViewModel @Inject constructor(
        private val locationsListRepository: LocationsListRepository
) : ViewModel() {

    suspend fun getLocations() = locationsListRepository.getAllLocations()

    fun deleteEntity(locationEntity: LocationEntity) = viewModelScope.launch {
        locationsListRepository.deleteLocation(locationEntity)
    }
}