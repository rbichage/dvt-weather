package com.dvt.weatherforecast.ui.home

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.dvt.weatherforecast.R
import com.dvt.weatherforecast.data.models.CurrentWeatherResponse
import com.dvt.weatherforecast.data.models.db.LocationEntity
import com.dvt.weatherforecast.databinding.FragmentHomeBinding
import com.dvt.weatherforecast.ui.home.weather.ForeCastAdapter
import com.dvt.weatherforecast.ui.home.weather.HomeUiState
import com.dvt.weatherforecast.ui.home.weather.HomeViewModel
import com.dvt.weatherforecast.utils.StringUtils.capitalizeWords
import com.dvt.weatherforecast.utils.convertToDateTime
import com.dvt.weatherforecast.utils.location.goToLocationSettings
import com.dvt.weatherforecast.utils.location.isLocationEnabled
import com.dvt.weatherforecast.utils.permmissions.isLocationPermissionEnabled
import com.dvt.weatherforecast.utils.permmissions.requestLocationPermission
import com.dvt.weatherforecast.utils.view.changeBackground
import com.dvt.weatherforecast.utils.view.makeInvisible
import com.dvt.weatherforecast.utils.view.show
import com.dvt.weatherforecast.utils.view.showErrorDialog
import com.dvt.weatherforecast.utils.view.toast
import com.dvt.weatherforecast.utils.view.updateStatusBarColor
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {
    private val binding: FragmentHomeBinding by lazy {
        FragmentHomeBinding.inflate(layoutInflater)
    }

    private val homeViewModel: HomeViewModel by viewModels()

    private val foreCastAdapter by lazy {
        ForeCastAdapter()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkForLocationPermission()
        observeViewModel()
        getLocationsFromDb()
        setClickListeners()
    }

    private fun setClickListeners() {
        binding.fabCities.setOnClickListener {
            HomeFragmentDirections.toSavedLocations().also {
                findNavController().navigate(it)
            }
        }
    }

    private fun getLocationsFromDb() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {

            awaitAll(
                    async {
                        homeViewModel.getAllLocations().collect { locationEntities ->

                            if (locationEntities.isNotEmpty()) {
                                val locationEntity = locationEntities.filter { it.isCurrent }

                                if (locationEntity.isNotEmpty()) {

                                    val current = locationEntity.first()

                                    val bitMap = binding.changeBackground(current)

                                    activity?.updateStatusBarColor(bitMap)

                                    Timber.e("location entity $locationEntity")

                                    updateViewsFromDb(current, locationEntities)

                                }
                            }

                        }
                    },

                    async {
                        homeViewModel.getForecasts().collectLatest { foreCasts ->
                            Timber.e("forecast: $foreCasts")
                            if (foreCasts.isNotEmpty()) {
                                with(binding.weeeklyRecycler) {
                                    adapter = foreCastAdapter
                                    foreCastAdapter.submitList(foreCasts)
                                }
                            }
                        }
                    }
            )


        }
    }

    private fun checkForLocationPermission() {
        val currentContext = binding.root.context

        if (currentContext.isLocationPermissionEnabled()) {
            Timber.e("is location enabled ${currentContext.isLocationEnabled()}")
            if (currentContext.isLocationEnabled()) {
                homeViewModel.getCurrentLocation()
            } else {
                currentContext.goToLocationSettings()
            }
        } else {
            currentContext.requestLocationPermission(
                    onPermissionAccepted = {
                        homeViewModel.getCurrentLocation()
                    },
                    onPermissionDenied = {
                        currentContext.toast("Well, well. well...")

                    },
                    shouldShowRationale = {
                        currentContext.toast("Enable permissions hombre")
                    }
            )
        }
    }

    private fun observeViewModel() {
        homeViewModel.uiState.observe(viewLifecycleOwner) { homeState ->
            when (homeState) {
                is HomeUiState.CurrentWeather -> {
                    updateViews(homeState.data)
                }
                is HomeUiState.Error -> {
                    with(binding) {
                        imgRefresh.show()
                        progressBar.makeInvisible()
                    }
                    binding.root.context.showErrorDialog(
                            message = homeState.errorHolder.message,
                            positiveText = "Retry",
                            negativeText = "Cancel",
                            positiveAction = { },
                            negativeAction = { activity?.onBackPressedDispatcher?.onBackPressed() }
                    )
                }
                is HomeUiState.ForecastData -> {

                }
                HomeUiState.Loading -> {
                    with(binding) {
                        imgRefresh.makeInvisible()
                        progressBar.show()
                    }
                }
                is HomeUiState.CurrentLocation -> {
                    getCurrentWeather(location = homeState.location, shouldGeoCode = true)
                }
                is HomeUiState.GeoCodeData -> {

                }
                is HomeUiState.GeocodeError -> {
                    activity?.toast(homeState.message)
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateViews(response: CurrentWeatherResponse) {
        val bitMap = binding.changeBackground(null, response)
        activity?.updateStatusBarColor(bitMap)


        with(binding) {
            tvLastUpdated.text = "Last updated: ${convertToDateTime(System.currentTimeMillis())}"
            imgRefresh.show()
            progressBar.makeInvisible()
            tvTemp.text = "${response.main.temp.toInt()} ℃"
            tvWeatherDesc.text = response.weather[0].description.capitalizeWords()
            tvMinTitle.text = "${response.main.tempMin.toInt()} ℃"
            tvCurrentTitle.text = "${response.main.temp.toInt()} ℃"
            tvMaxTitle.text = "${response.main.tempMax.toInt()} ℃"
        }
    }


    @SuppressLint("SetTextI18n")
    private fun updateViewsFromDb(current: LocationEntity, entities: List<LocationEntity>) {
        val withoutCurrent = entities.map { it.name }.toMutableList()

        val adapter = ArrayAdapter(
                binding.root.context,
                android.R.layout.simple_spinner_dropdown_item,
                withoutCurrent
        )

        with(binding) {
            imgRefresh.show()
            progressBar.makeInvisible()
            tvLocationName.setAdapter(adapter)
            tvLocationName.setText(current.name, false)
            tvLastUpdated.text = "Last updated: ${convertToDateTime(current.lastUpdated)}"
            tvTemp.text = current.normalTemp.toString() + " \u2103"
            tvWeatherDesc.text = current.weatherConditionName.capitalizeWords()
            tvMinTitle.text = current.lowTemp.toString() + " \u2103"
            tvCurrentTitle.text = current.normalTemp.toString() + " \u2103"
            tvMaxTitle.text = current.highTemp.toString() + " \u2103"



            imgRefresh.setOnClickListener {
                val location = Location("this").apply {
                    latitude = current.lat
                    longitude = current.lng
                }

                getCurrentWeather(location, false)

            }

            tvLocationName.setOnItemClickListener { _, _, position, _ ->

                val entity = entities[position]

                val location = Location(javaClass.name).apply {
                    latitude = entity.lat
                    longitude = entity.lng
                }

                getCurrentWeather(location, shouldGeoCode = false)
            }

        }
    }

    private fun getCurrentWeather(location: Location, shouldGeoCode: Boolean) {
        homeViewModel.getCurrentWeather(location, shouldGeoCode)
    }

}