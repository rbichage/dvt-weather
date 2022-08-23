package com.reuben.feature_weather.ui

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.reuben.core_common.date.convertToDateTime
import com.reuben.core_common.location.goToLocationSettings
import com.reuben.core_common.location.isLocationEnabled
import com.reuben.core_common.location.isLocationPermissionEnabled
import com.reuben.core_common.location.requestLocationPermission
import com.reuben.core_common.strings.StringUtils.capitalizeWords
import com.reuben.core_data.models.db.LocationEntity
import com.reuben.core_data.models.weather.CurrentWeatherResponse
import com.reuben.core_navigation.navigation.WeatherNavDirections
import com.reuben.core_ui.binding.viewBinding
import com.reuben.core_ui.makeInvisible
import com.reuben.core_ui.show
import com.reuben.core_ui.showErrorDialog
import com.reuben.core_ui.toast
import com.reuben.feature_weather.R
import com.reuben.feature_weather.databinding.FragmentWeatherBinding
import com.reuben.feature_weather.util.ui.changeBackground
import com.reuben.feature_weather.util.ui.updateStatusBarColor
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class WeatherFragment : Fragment(R.layout.fragment_weather) {
    private val binding by viewBinding(factory = FragmentWeatherBinding::bind)

    @Inject
    lateinit var weatherNavDirections: WeatherNavDirections

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
            weatherNavDirections.navigateToLocations(findNavController())
        }
    }

    private fun getLocationsFromDb() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {

            awaitAll(
                    async {
                        homeViewModel.getAllLocations().collectLatest { locationEntities ->

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

    @SuppressLint("MissingPermission")
    private fun checkForLocationPermission() {
        val currentContext = binding.root.context

        if (currentContext.isLocationPermissionEnabled()) {
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
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.uiState.collect { homeState ->
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
                        HomeUiState.Initial -> {}
                    }
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

        val celsiusSymbol = binding.root.context.getString(R.string.symbol_degrees_celsius)

        with(binding) {
            imgRefresh.show()
            progressBar.makeInvisible()
            tvLocationName.setAdapter(adapter)
            tvLocationName.setText(current.name, false)
            tvLastUpdated.text = "Last updated: ${convertToDateTime(current.lastUpdated)}"
            tvTemp.text = "${current.normalTemp} $celsiusSymbol"
            tvWeatherDesc.text = current.weatherConditionName.capitalizeWords()
            tvMinTitle.text = "${current.lowTemp} $celsiusSymbol"
            tvCurrentTitle.text = "${current.normalTemp} $celsiusSymbol"
            tvMaxTitle.text = "${current.highTemp} $celsiusSymbol"


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