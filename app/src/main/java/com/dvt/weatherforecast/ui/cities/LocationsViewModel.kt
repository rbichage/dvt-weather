package com.dvt.weatherforecast.ui.cities

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LocationsViewModel @Inject constructor(
        private val locationsRepository: LocationsRepository
) : ViewModel() {

    suspend fun getLocations() = locationsRepository.getAllLocations()
}