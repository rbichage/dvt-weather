package com.reuben.core_common.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

fun Context.isLocationPermissionEnabled(): Boolean {
    return ActivityCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}

fun Context.requestLocationPermission(
        onPermissionAccepted: () -> Unit,
        onPermissionDenied: () -> Unit,
        shouldShowRationale: () -> Unit,

        ) {

    val permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    )

    Dexter.withContext(this)
            .withPermissions(permissions)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {

                    when {
                        report.areAllPermissionsGranted() -> {
                            onPermissionAccepted()
                        }
                        report.isAnyPermissionPermanentlyDenied -> {
                            onPermissionDenied()
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(request: MutableList<PermissionRequest>, token: PermissionToken) {
                    shouldShowRationale()
                }

            }).check()
}