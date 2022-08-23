package com.reuben.core_navigation.navigation
import androidx.navigation.NavController


interface WeatherNavDirections {
    fun navigateToHome(navController: NavController)
    fun navigateToLocations(navController: NavController)
    fun navigateToSearch(navController: NavController)
}