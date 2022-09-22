package com.reuben.weatherforecast.ui.navigation

import androidx.navigation.NavController
import com.reuben.core_navigation.navigation.WeatherNavDirections
import com.reuben.feature_locations.ui.list.LocationsListFragmentDirections
import com.reuben.feature_weather.ui.WeatherFragmentDirections

class WeatherNavDirectionsImpl : WeatherNavDirections {
    override fun navigateToHome(navController: NavController) {

    }

    override fun navigateToLocations(navController: NavController) {
        val action = WeatherFragmentDirections
                .toSavedLocations()

        navController.navigate(action)
    }

    override fun navigateToSearch(navController: NavController) {
        val action = LocationsListFragmentDirections.toMapFragment()
    }
}