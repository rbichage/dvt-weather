package com.dvt.weatherforecast.ui.search

import com.dvt.weatherforecast.data.models.places.CustomPlaceDetails

interface PlacesRecyclerListener {
    fun requestStarted()
    fun requestSuccessful()
    fun placeSelected(place: CustomPlaceDetails)
    fun onNoConnection()
    fun hideKeyboard()
}