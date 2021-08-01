package com.dvt.weatherforecast.utils

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import androidx.core.content.ContextCompat
import com.dvt.weatherforecast.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng

const val MAP_CAMERA_ZOOM = 5F


fun GoogleMap.moveCameraWithAnim(latLng: LatLng) {
    animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, MAP_CAMERA_ZOOM))
}


fun Context.getLocationIcon(): BitmapDescriptor {
    val vehicleIcon = ContextCompat.getDrawable(this, R.drawable.location_icon) as BitmapDrawable


    val color = ContextCompat.getColor(this, R.color.colorPrimary)

    val smallMarker = Bitmap.createScaledBitmap(vehicleIcon.bitmap, 100, 100, false)

    val paint = Paint()
    val canvas = Canvas(smallMarker)

    paint.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
    canvas.drawBitmap(smallMarker, 0F, 0F, paint)
    return BitmapDescriptorFactory.fromBitmap(smallMarker)

}





