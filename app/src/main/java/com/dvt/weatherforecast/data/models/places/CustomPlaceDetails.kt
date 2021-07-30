package com.dvt.weatherforecast.data.models.places

import com.google.android.gms.maps.model.LatLng

data class CustomPlaceDetails(
        val name: String,
        val address: String,
        val latLng: LatLng
)

data class CustomAddressModel(
        val name: String,
        val address: String
)