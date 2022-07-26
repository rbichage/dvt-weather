package com.dvt.weatherforecast.utils.location

import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.provider.Settings
import androidx.core.location.LocationManagerCompat
import com.dvt.weatherforecast.BuildConfig
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import timber.log.Timber
import kotlin.system.exitProcess

fun Context.isLocationEnabled(): Boolean {
    val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return LocationManagerCompat.isLocationEnabled(locationManager)

}

fun Context.goToLocationSettings() {

    MaterialAlertDialogBuilder(this).apply {
        setMessage("Enable location to continue")
        setPositiveButton("ENABLE") { dialog, _ ->
            val packageName = BuildConfig.APPLICATION_ID
            dialog.dismiss()
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.parse("package:$packageName")
                startActivity(this)
            }
        }
        setNegativeButton("CANCEL") { dialog, _ ->
            dialog.dismiss()
            exitProcess(0)
        }
        show()
    }
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
){
    val addresses = geocoder.getFromLocationName(address, 1).orEmpty()

    Timber.e("addresses $addresses")
    if (addresses.isNotEmpty()) {
        val firstAddress = addresses.first()
        onAddressRetrieved(firstAddress)
    } else {
        onAddressNotFound()
    }
}

