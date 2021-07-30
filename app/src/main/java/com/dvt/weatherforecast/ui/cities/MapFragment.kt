package com.dvt.weatherforecast.ui.cities

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.dvt.weatherforecast.databinding.FragmentMapBinding
import com.dvt.weatherforecast.utils.permmissions.isLocationPermissionEnabled
import com.dvt.weatherforecast.utils.view.showErrorSnackbar
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.material.snackbar.Snackbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MapFragment : Fragment(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap

    private val binding: FragmentMapBinding by lazy {
        FragmentMapBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(binding.mapView.id) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if (binding.root.context.isLocationPermissionEnabled()) {


            mMap.apply {
                setMinZoomPreference(5F)

                isBuildingsEnabled = false
                uiSettings.isRotateGesturesEnabled = true
                uiSettings.isMapToolbarEnabled = false
                uiSettings.isMyLocationButtonEnabled = false
                isMyLocationEnabled = true


            }
        } else {
            requestLocationPermission()
        }
    }

    private fun requestLocationPermission() {
        val permissions = listOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        )

        activity?.let {
            Dexter.withContext(it.applicationContext)
                    .withPermissions(permissions)
                    .withListener(object : MultiplePermissionsListener {
                        override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                            report?.let { result ->
                                if (result.areAllPermissionsGranted()) {
                                    Timber.d("permissions allowed")
                                    setupMap()
                                } else {
                                    binding.root.showErrorSnackbar(
                                            "Permissions denied",
                                            Snackbar.LENGTH_LONG
                                    )
                                }
                            }
                        }

                        override fun onPermissionRationaleShouldBeShown(
                                p0: MutableList<PermissionRequest>?,
                                p1: PermissionToken?
                        ) {
                            Toast.makeText(it, "enable location", Toast.LENGTH_SHORT).show()
                        }

                    }).check()
        }
    }

    @SuppressLint("MissingPermission")
    private fun setupMap() {

        if (binding.root.context.isLocationPermissionEnabled()) {


            mMap.apply {
                setMinZoomPreference(5F)

                isBuildingsEnabled = false
                uiSettings.isRotateGesturesEnabled = true
                uiSettings.isMapToolbarEnabled = false
                uiSettings.isMyLocationButtonEnabled = false
                isMyLocationEnabled = true

            }
        } else {
            requestLocationPermission()
        }
    }


}