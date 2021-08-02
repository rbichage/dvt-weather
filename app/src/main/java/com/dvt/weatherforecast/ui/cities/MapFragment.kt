package com.dvt.weatherforecast.ui.cities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.dvt.weatherforecast.BuildConfig
import com.dvt.weatherforecast.R
import com.dvt.weatherforecast.databinding.FragmentMapBinding
import com.dvt.weatherforecast.ui.home.HomeActivity
import com.dvt.weatherforecast.ui.home.weather.HomeViewModel
import com.dvt.weatherforecast.utils.getLocationIcon
import com.dvt.weatherforecast.utils.location.isLocationEnabled
import com.dvt.weatherforecast.utils.moveCameraWithAnim
import com.dvt.weatherforecast.utils.permmissions.isLocationPermissionEnabled
import com.dvt.weatherforecast.utils.view.showErrorSnackbar
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import timber.log.Timber

@AndroidEntryPoint
class MapFragment : Fragment(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap

    private val binding: FragmentMapBinding by lazy {
        FragmentMapBinding.inflate(layoutInflater)
    }

    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(binding.mapView.id) as SupportMapFragment
        mapFragment.getMapAsync(this)
        checkForLocationPermission()
        observeViewModel()

    }

    private fun observeViewModel() {
        homeViewModel.currentLocation.observe(viewLifecycleOwner) { location ->
            setupMap(location)
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
            setMapStyle(MapStyleOptions.loadRawResourceStyle(binding.root.context, R.raw.maps_style))

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
                                    getCurrentLocation()
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


    private fun setupMap(location: Location) {
        val latLng = LatLng(location.latitude, location.longitude)
        mMap.moveCameraWithAnim(latLng)

        lifecycleScope.launchWhenStarted {
            homeViewModel.getAllLocations().collect { locations ->

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
                                            Intent(binding.root.context, HomeActivity::class.java).apply {
                                                putExtra("locationEntity", selectedLocation)
                                                activity?.setResult(Activity.RESULT_OK, this)
                                                activity?.finish()
                                            }

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
                askForLocation()
            }
        } else {
            requestLocationPermission()
        }
    }

    private fun askForLocation() {
        MaterialAlertDialogBuilder(binding.root.context).apply {
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
                activity?.onBackPressed()
            }
            show()
        }
    }

    private fun getCurrentLocation() {
        homeViewModel.getCurrentLocation()
    }

}