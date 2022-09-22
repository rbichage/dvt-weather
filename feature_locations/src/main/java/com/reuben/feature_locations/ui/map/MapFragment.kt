package com.reuben.feature_locations.ui.map

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.reuben.core_common.location.isLocationEnabled
import com.reuben.core_common.location.isLocationPermissionEnabled
import com.reuben.core_common.location.requestLocationPermission
import com.reuben.core_ui.goToLocationSettings
import com.reuben.core_ui.toast
import com.reuben.feature_locations.R
import com.reuben.feature_locations.databinding.FragmentMapBinding
import com.reuben.feature_locations.util.getLocationIcon
import com.reuben.feature_locations.util.moveCameraWithAnim
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MapFragment : Fragment(R.layout.fragment_map), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap

    private val binding: FragmentMapBinding by lazy {
        FragmentMapBinding.inflate(layoutInflater)
    }

    private val viewModel: MapViewModel by viewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(binding.mapView.id) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    private fun observeViewModel() {
        viewModel.uiState.observe(viewLifecycleOwner) { uiState ->

            when (uiState) {
                is MapUiState.LocationData -> {
                    setupMap(uiState.location)
                }
                is MapUiState.LocationName -> {

                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.apply {
            setMinZoomPreference(2F)

            isBuildingsEnabled = false
            uiSettings.isRotateGesturesEnabled = true
            uiSettings.isMapToolbarEnabled = false
            uiSettings.isMyLocationButtonEnabled = false
            isMyLocationEnabled = true
//            setMapStyle(MapStyleOptions.loadRawResourceStyle(binding.root.context, R.raw.maps_style))

        }

        checkForLocationPermission()

        observeViewModel()

    }


    private fun setupMap(location: Location) {
        val latLng = LatLng(location.latitude, location.longitude)
        mMap.moveCameraWithAnim(latLng)

        lifecycleScope.launchWhenStarted {
            viewModel.getAllLocations().collect { locations ->

                if (locations.isNotEmpty()) {

                    for (item in locations) {
                        val itemLatLng = LatLng(item.lat, item.lng)

                        val markerOption = MarkerOptions().apply {
                            position(itemLatLng)
                            title(item.name)
                            icon(binding.root.context.getLocationIcon())
                        }

                        with(mMap) {
                            addMarker(markerOption)

                            setOnMarkerClickListener { selectedMarker ->

                                val filter = locations.filter { it.lat == selectedMarker.position.latitude }

                                Timber.e("matching location: $filter")

                                if (filter.isNotEmpty()) {
                                    val selectedLocation = filter.first()

                                    MaterialAlertDialogBuilder(binding.root.context).apply {
                                        setTitle(selectedLocation.name)
                                        setMessage("View weather details for ${selectedLocation.name}?")
                                        setPositiveButton("YES") { dialog, _ ->
                                            dialog.dismiss()
                                        }
                                        setNegativeButton("Cancel") { dialog, _ ->
                                            dialog.dismiss()
                                        }
                                        show()
                                    }


                                }
                                return@setOnMarkerClickListener true
                            }
                        }


                    }
                }
            }
        }
    }

    private fun checkForLocationPermission() {

        if (binding.root.context.isLocationPermissionEnabled()) {
            if (binding.root.context.isLocationEnabled()) {
                getCurrentLocation()
            } else {
                activity?.goToLocationSettings()
            }
        } else {
            activity?.requestLocationPermission(
                    onPermissionAccepted = {
                        getCurrentLocation()
                    },
                    onPermissionDenied = {
                        activity?.toast("Permission denied")
                    },
                    shouldShowRationale = {
                        activity?.toast("Accept permissions LOL")
                    }
            )
        }
    }

    private fun getCurrentLocation() {
        viewModel.getCurrentLocation()
    }

}