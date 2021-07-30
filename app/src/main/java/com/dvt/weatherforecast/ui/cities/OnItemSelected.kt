package com.dvt.weatherforecast.ui.cities

import com.dvt.weatherforecast.data.models.db.LocationEntity

interface OnItemSelected {
    fun onClick(locationEntity: LocationEntity)
    fun onLongClick(locationEntity: LocationEntity)
}