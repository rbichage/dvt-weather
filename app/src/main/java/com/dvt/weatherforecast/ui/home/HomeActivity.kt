package com.dvt.weatherforecast.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.location.Address
import android.location.Location
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dvt.weatherforecast.data.models.CurrentWeatherResponse
import com.dvt.weatherforecast.data.models.db.LocationEntity
import com.dvt.weatherforecast.databinding.ActivityMainBinding
import com.dvt.weatherforecast.mappers.toNewLocationEntity
import com.dvt.weatherforecast.ui.cities.LocationsActivity
import com.dvt.weatherforecast.ui.home.weather.ForeCastAdapter
import com.dvt.weatherforecast.ui.home.weather.HomeViewModel
import com.dvt.weatherforecast.utils.convertToDateTime
import com.dvt.weatherforecast.utils.location.isLocationEnabled
import com.dvt.weatherforecast.utils.network.ApiResponse
import com.dvt.weatherforecast.utils.permmissions.isLocationPermissionEnabled
import com.dvt.weatherforecast.utils.storage.UserPreferences
import com.dvt.weatherforecast.utils.view.changeBackground
import com.dvt.weatherforecast.utils.view.navigateTo
import com.dvt.weatherforecast.utils.view.showErrorDialog
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import timber.log.Timber

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {


    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private lateinit var foreCastAdapter: ForeCastAdapter

    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initViews()
        getItemsFromDb()
        observeViewModel()
        checkForLocationPermission()

    }

    private fun initViews() {
        binding.fabCities.setOnClickListener {
            navigateTo<LocationsActivity> { }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getItemsFromDb() {
        lifecycleScope.launchWhenStarted {
            homeViewModel.getAllLocations().collect { locationEntities ->

                Timber.e("locations $locationEntities")
                if (locationEntities.isNotEmpty()) {
                    val locationEntity = locationEntities.filter { it.isCurrent }

                    if (locationEntity.isNotEmpty()) {

                        val current = locationEntity.first()

                        val withoutCurrent = locationEntities.map { it.name }.toMutableList()

                        val adapter = ArrayAdapter(
                                this@HomeActivity,
                                android.R.layout.simple_spinner_dropdown_item,
                                withoutCurrent
                        )


                        binding.changeBackground(current)

                        Timber.e("location entity $locationEntity")
                        with(binding) {
                            tvLocationName.setAdapter(adapter)
                            tvLocationName.setText(current.name, false)
                            tvLastUpdated.text = "Last updated: ${convertToDateTime(current.lastUpdated)}"
                            tvTemp.text = current.normalTemp.toString() + " \u2103"
                            tvWeatherDesc.text = current.weatherConditionName
                            tvMinTitle.text = current.lowTemp.toString() + " \u2103"
                            tvCurrentTitle.text = current.normalTemp.toString() + " \u2103"
                            tvMaxTitle.text = current.highTemp.toString() + " \u2103"


                            tvLocationName.setOnItemClickListener { _, _, position, _ ->

                                val entity = locationEntities[position]

                                val location = Location("this").apply {
                                    latitude = entity.lat
                                    longitude = entity.lng
                                }

                                getFromLocation(location, false, entity)
                            }

                        }
                    }


                }

            }
        }

        lifecycleScope.launchWhenStarted {
            homeViewModel.getForecasts().collect { foreCasts ->
                if (foreCasts.isNotEmpty()) {

                    with(binding.weeeklyRecycler) {
                        foreCastAdapter = ForeCastAdapter()
                        adapter = foreCastAdapter
                        foreCastAdapter.submitList(foreCasts)
                    }
                }

            }
        }
    }

    private fun observeViewModel() {
        with(homeViewModel) {
            currentLocation.observe(this@HomeActivity) { location ->
                Timber.e("location is $location")
                getFromLocation(location, true)

            }

        }
    }

    private fun getCurrentLocation() {
        homeViewModel.getCurrentLocation()
    }

    @SuppressLint("SetTextI18n")
    private fun getFromLocation(location: Location, geocodeResult: Boolean, entity: LocationEntity? = null) {
        lifecycleScope.launchWhenStarted {
            homeViewModel.getDataFromLocation(location).collect { response ->

                when (response) {
                    is ApiResponse.Failure -> {

                        showErrorDialog(
                                message = response.errorHolder.message,
                                positiveText = "Retry",
                                negativeText = "Cancel",
                                positiveAction = { getFromLocation(location, geocodeResult, entity) },
                                negativeAction = { onBackPressed() }
                        )
                    }
                    is ApiResponse.Success -> {


                        if (geocodeResult) {
                            geoCodeLocation(location, response.value)

                        } else {

                            val locationName = entity?.name ?: ""
                            val newEntity = response.value.toNewLocationEntity(locationName)

                            homeViewModel.deleteEntity(entity!!).invokeOnCompletion {
                                homeViewModel.insertNewToDb(newEntity)
                            }

                        }
                    }
                }
            }

            homeViewModel.getForeCastFromLocation(location).collect { response ->

                when (response) {
                    is ApiResponse.Success -> {
                        val weatherData = response.value.daily

                        if (weatherData.isNotEmpty()) {
                            homeViewModel.insertToForecastDb(
                                    response.value,
                                    UserPreferences.lastLocation
                            )
                        }
                    }
                    is ApiResponse.Failure -> {

                    }
                }
            }
        }
    }

    private fun geoCodeLocation(location: Location, response: CurrentWeatherResponse) {

        lifecycleScope.launchWhenStarted {
            homeViewModel.geoCodeThisLocation(location).collect { address: Address ->

                Timber.e("address details $address")
                var locationName = address.getAddressLine(0)

                if (locationName.trim().isEmpty()) {
                    // pick admin area
                    locationName = address.subLocality
                }

                UserPreferences.saveLatestLocation(locationName)
                homeViewModel.deleteCurrentLocation()
                        .invokeOnCompletion {
                            homeViewModel.insertCurrentToDb(response, locationName)
                        }

            }
        }
    }


    private fun checkForLocationPermission() {

        if (isLocationPermissionEnabled()) {
            if (isLocationEnabled()) {
                getCurrentLocation()
            } else {
                askForLocation()
            }
        } else {
            requestLocationPermission()
        }
    }

    private fun requestLocationPermission() {

        Dexter.withContext(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(ermissionGrantedResponse: PermissionGrantedResponse) {
                        getCurrentLocation()
                    }

                    override fun onPermissionDenied(permissionDeniedResponse: PermissionDeniedResponse) {
                        //permission denied
                    }

                    override fun onPermissionRationaleShouldBeShown(
                            permissionRequest: PermissionRequest,
                            permissionToken: PermissionToken
                    ) {
                    }

                }).check()
    }

    private fun askForLocation() {
    }

}