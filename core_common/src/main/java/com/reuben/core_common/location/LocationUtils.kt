package com.reuben.core_common.location

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import androidx.annotation.RequiresPermission
import androidx.core.location.LocationManagerCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import timber.log.Timber
import javax.inject.Inject

class CurrentLocationHelper @Inject constructor(
        private val client: FusedLocationProviderClient
) {
    @RequiresPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
    fun fetchCurrentLocation() = callbackFlow {
        val locationRequest = LocationRequest.create().apply {
            interval = UPDATE_INTERVAL
            fastestInterval = FASTEST_UPDATE_INTERVAL
            priority = Priority.PRIORITY_HIGH_ACCURACY
        }
        val callBack = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                val location = locationResult.lastLocation
                location?.let {
                    trySend(location)
                }
            }
        }
        client.requestLocationUpdates(locationRequest, callBack, Looper.getMainLooper())
        awaitClose { client.removeLocationUpdates(callBack) }
    }

    companion object {
        private const val UPDATE_INTERVAL = 1000L
        private const val FASTEST_UPDATE_INTERVAL = UPDATE_INTERVAL
    }
}


fun Context.isLocationEnabled(): Boolean {
    val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return LocationManagerCompat.isLocationEnabled(locationManager)

}


fun geoCodeThisLocation(
        geocoder: Geocoder,
        location: Location,
        onAddressRetrieved: (Address) -> Unit,
        onAddressNotFound: () -> Unit,
) {
    val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1).orEmpty()

    Timber.e("addresses $addresses")
    if (addresses.isNotEmpty()) {
        val firstAddress = addresses.first()
        onAddressRetrieved(firstAddress)
    } else {
        onAddressNotFound()
    }

}

fun geoCodeThisAddress(
        geocoder: Geocoder,
        address: String,
        onAddressRetrieved: (Address) -> Unit,
        onAddressNotFound: () -> Unit,
) {
    val addresses = geocoder.getFromLocationName(address, 1).orEmpty()

    Timber.e("addresses $addresses")
    if (addresses.isNotEmpty()) {
        val firstAddress = addresses.first()
        onAddressRetrieved(firstAddress)
    } else {
        onAddressNotFound()
    }
}


