package com.dvt.weatherforecast.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.location.Address
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Window
import android.view.WindowManager
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.palette.graphics.Palette
import com.dvt.weatherforecast.BuildConfig
import com.dvt.weatherforecast.data.models.CurrentWeatherResponse
import com.dvt.weatherforecast.data.models.db.LocationEntity
import com.dvt.weatherforecast.databinding.ActivityHomeBinding
import com.dvt.weatherforecast.ui.cities.LocationsActivity
import com.dvt.weatherforecast.ui.home.weather.ForeCastAdapter
import com.dvt.weatherforecast.ui.home.weather.HomeViewModel
import com.dvt.weatherforecast.utils.convertToDateTime
import com.dvt.weatherforecast.utils.location.isLocationEnabled
import com.dvt.weatherforecast.utils.network.ApiResponse
import com.dvt.weatherforecast.utils.permmissions.isLocationPermissionEnabled
import com.dvt.weatherforecast.utils.storage.UserPreferences
import com.dvt.weatherforecast.utils.view.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import timber.log.Timber
import kotlin.system.exitProcess


@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {


    private val binding: ActivityHomeBinding by lazy {
        ActivityHomeBinding.inflate(layoutInflater)
    }

    private lateinit var foreCastAdapter: ForeCastAdapter
    private lateinit var locationContract: ActivityResultLauncher<Intent>

    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        registerResult()
        checkForLocationPermission()
        initViews()
        getItemsFromDb()
        observeViewModel()

    }


    private fun initViews() {
        binding.fabCities.setOnClickListener {
            locationContract.launch(
                    Intent(this, LocationsActivity::class.java).also {
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    }
            )
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

                        val bitMap = binding.changeBackground(current)

                        updateStatusBarColor(bitMap)

                        Timber.e("location entity $locationEntity")

                        updateViewsFromDb(current, locationEntities)

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

    private fun updateStatusBarColor(bitMap: Bitmap) {

        Palette.Builder(bitMap)
                .generate { result ->

                    result?.let {
                        val dominantSwatch = it.dominantSwatch

                        Timber.e("dominant color is ${dominantSwatch?.rgb}")


                        if (dominantSwatch != null) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                val window: Window = window
                                window.statusBarColor = dominantSwatch.rgb

                            } else {
                                val window: Window = window
                                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                                window.statusBarColor = dominantSwatch.rgb
                            }
                        }
                    }
                }
    }

    @SuppressLint("SetTextI18n")
    private fun updateViewsFromDb(current: LocationEntity, locationEntities: List<LocationEntity>) {

        Timber.e("locations size ${locationEntities.size}")
        val withoutCurrent = locationEntities.map { it.name }.toMutableList()

        val adapter = ArrayAdapter(
                this@HomeActivity,
                android.R.layout.simple_spinner_dropdown_item,
                withoutCurrent
        )

        with(binding) {
            tvLocationName.setAdapter(adapter)
            tvLocationName.setText(current.name, false)
            tvLastUpdated.text = "Last updated: ${convertToDateTime(current.lastUpdated)}"
            tvTemp.text = current.normalTemp.toString() + " \u2103"
            tvWeatherDesc.text = current.weatherConditionName
            tvMinTitle.text = current.lowTemp.toString() + " \u2103"
            tvCurrentTitle.text = current.normalTemp.toString() + " \u2103"
            tvMaxTitle.text = current.highTemp.toString() + " \u2103"

            imgRefresh.setOnClickListener {
                val location = Location("this").apply {
                    latitude = current.lat
                    longitude = current.lng
                }

                getFromLocation(location, false, current)

            }

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

    private fun observeViewModel() {
        with(homeViewModel) {
            currentLocation.observe(this@HomeActivity) { location ->
                Timber.e("location is $location")
                getFromLocation(location, true)

            }

            isLoading.observe(this@HomeActivity) { isLoading ->
                when (isLoading) {
                    true -> {
                        with(binding) {
                            imgRefresh.makeInvisible()
                            progressBar.show()
                        }
                    }

                    false -> {
                        with(binding) {
                            imgRefresh.show()
                            progressBar.hide()
                        }
                    }
                }

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

                    is ApiResponse.Success -> {


                        if (geocodeResult) {
                            geoCodeLocation(location, response.value)

                        } else {

                            updateViews(response.value)

                        }
                    }
                    is ApiResponse.Failure -> {

                        if (response.errorHolder.statusCode != 1) {
                            showErrorDialog(
                                    message = response.errorHolder.message,
                                    positiveText = "Retry",
                                    negativeText = "Cancel",
                                    positiveAction = { getFromLocation(location, geocodeResult, entity) },
                                    negativeAction = { onBackPressed() }
                            )
                        } else {
                            binding.root.showErrorSnackbar(response.errorHolder.message, Snackbar.LENGTH_LONG)
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

    @SuppressLint("SetTextI18n")
    private fun updateViews(response: CurrentWeatherResponse) {

        val bitMap = binding.changeBackground(null, response)
        updateStatusBarColor(bitMap)

        with(binding) {
            tvLastUpdated.text = "Last updated: ${convertToDateTime(System.currentTimeMillis())}"
            tvTemp.text = "${response.main.temp.toInt()} ℃"
            tvWeatherDesc.text = response.weather[0].main
            tvMinTitle.text = "${response.main.tempMin.toInt()} ℃"
            tvCurrentTitle.text = "${response.main.temp.toInt()} ℃"
            tvMaxTitle.text = "${response.main.tempMax.toInt()} ℃"
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

    private fun registerResult() {
        locationContract = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            if (result.resultCode == RESULT_OK && result.data != null) {
                val data = result.data!!

                val locationEntity = data.getParcelableExtra("locationEntity") as LocationEntity?

                if (locationEntity != null) {
                    binding.tvLocationName.setText(locationEntity.name, false)
                    binding.tvLastUpdated.text = convertToDateTime(locationEntity.lastUpdated)

                    val location = Location("this").apply {
                        latitude = locationEntity.lat
                        longitude = locationEntity.lng
                    }

                    lifecycleScope.launchWhenStarted {
                        homeViewModel.getAllLocations().collect { entities ->

                            if (entities.isNotEmpty()) {
                                val filter = entities.filter { it.name == locationEntity.name }

                                if (filter.isNotEmpty()) {
                                    val current = filter.first()
                                    val bitmap = binding.changeBackground(current, null)
                                    updateStatusBarColor(bitmap)
                                    updateViewsFromDb(current, entities)

                                    getFromLocation(location, false, locationEntity)

                                }
                            }
                        }
                    }


                }

            }
        }
    }

    private fun askForLocation() {

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


}