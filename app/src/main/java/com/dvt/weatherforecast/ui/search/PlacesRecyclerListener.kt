package com.dvt.weatherforecast.ui.search

import com.dvt.weatherforecast.data.models.places.CustomPlaceDetails

interface PlacesRecyclerListener {
    fun requestStarted()
    fun requestSuccessful()
    fun placeSelected(place: CustomPlaceDetails)
    fun onError(e: Exception)
    fun hideKeyboard()
}