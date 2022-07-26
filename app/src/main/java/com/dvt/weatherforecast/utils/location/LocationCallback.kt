package com.dvt.weatherforecast.utils.location

import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class GetLocation @Inject constructor(
        private val client: FusedLocationProviderClient
) {
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

