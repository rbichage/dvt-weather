package com.dvt.weatherforecast.ui.home.weather

import android.Manifest
import android.annotation.SuppressLint
import android.location.Address
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.dvt.weatherforecast.databinding.FragmentWeatherBinding
import com.dvt.weatherforecast.utils.location.isLocationEnabled
import com.dvt.weatherforecast.utils.network.ApiResponse
import com.dvt.weatherforecast.utils.permmissions.isLocationPermissionEnabled
import com.dvt.weatherforecast.utils.view.changeBackground
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.coroutines.flow.collect
import timber.log.Timber

class WeatherFragment : Fragment() {

    private val binding: FragmentWeatherBinding by lazy {
        FragmentWeatherBinding.inflate(layoutInflater)
    }

    private val weatherViewModel: WeatherViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeViewModel()
        checkForLocationPermission()


    }


    private fun observeViewModel() {
        with(weatherViewModel) {
            currentLocation.observe(viewLifecycleOwner) { location ->

                Timber.e("location is $location")

                getFromLocation(location)
                geoCodeLocation(location)
            }

            geocoderError.observe(viewLifecycleOwner) {

            }
        }
    }

    private fun geoCodeLocation(location: Location) {

        lifecycleScope.launchWhenStarted {
            weatherViewModel.geoCodeThisLocation(location).collect { address: Address ->

                Timber.e("address details  $address")

                with(binding) {
                    tvCityName.text = address.locality
                }

            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getFromLocation(location: Location) {
        lifecycleScope.launchWhenStarted {
            weatherViewModel.getDataFromLocation(location).collect { response ->

                when (response) {
                    is ApiResponse.Failure -> {

                        Timber.e("error ${response.errorHolder}")
                    }
                    is ApiResponse.Success -> {
                        val weatherData = response.value

                        binding.changeBackground(weatherData.weather[0])

                        with(binding) {
                            tvTemp.text = weatherData.main.temp.toInt().toString() + " \u2103"
                            tvWeatherDesc.text = weatherData.weather[0].main
                            tvMinTitle.text =
                                weatherData.main.tempMin.toInt().toString() + " \u2103"
                            tvCurrentTitle.text =
                                weatherData.main.feelsLike.toInt().toString() + " \u2103"
                            tvMaxTitle.text =
                                weatherData.main.tempMax.toInt().toString() + " \u2103"
                        }

                    }
                }

            }

            weatherViewModel.getForeCastFromLocation(location).collect { response ->

                when (response) {
                    is ApiResponse.Success -> {
                        val weatherData = response.value.daily

                        if (weatherData.isNotEmpty()) {
                            with(binding.weeeklyRecycler) {
                                val foreCastAdapter = ForeCastAdapter()
                                adapter = foreCastAdapter
                                foreCastAdapter.submitList(weatherData.subList(1, 7))
                            }
                        }
                    }
                    is ApiResponse.Failure -> {

                    }
                }
            }
        }
    }

    private fun checkForLocationPermission() {

        if (binding.root.context.isLocationPermissionEnabled()) {
            if (binding.root.context.isLocationEnabled()) {
                weatherViewModel.getCurrentLocation()
            } else {
                askForLocation()
            }
        } else {
            requestLocationPermission()
        }
    }

    private fun requestLocationPermission() {

        Dexter.withContext(binding.root.context)
            .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(ermissionGrantedResponse: PermissionGrantedResponse) {
                    weatherViewModel.getCurrentLocation()
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