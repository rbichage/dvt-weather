package com.dvt.weatherforecast.ui.home

import android.content.Context
import android.location.Geocoder
import android.location.Location
import androidx.test.core.app.ApplicationProvider
import com.dvt.weatherforecast.BaseViewModelTest
import com.dvt.weatherforecast.data.sample.SampleResponses.fakeWeatherResponse
import com.dvt.weatherforecast.ui.home.weather.HomeRepository
import com.dvt.weatherforecast.ui.home.weather.HomeViewModel
import com.dvt.weatherforecast.utils.location.GetLocation
import com.dvt.weatherforecast.utils.network.ApiResponse
import com.google.common.truth.Truth
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class HomeViewModelTest : BaseViewModelTest() {


    private lateinit var homeViewModel: HomeViewModel

    private val getLocation: GetLocation = mockk(relaxed = true)

    private val homeRepository: HomeRepository = mockk(relaxed = true)

    private lateinit var geoCoder: Geocoder

    @Before
    fun setup() {

        val context = ApplicationProvider.getApplicationContext<Context>()
        geoCoder = Geocoder(context)

        homeViewModel = HomeViewModel(getLocation, geoCoder, homeRepository)
    }

    @Test
    fun test_getCurrentWeatherSuccessfully() {
        val testLocation = Location("this").apply {
            latitude = -1.2907344085176307
            longitude = 36.82093485505406
        }

        runBlocking {
            coEvery {
                homeRepository.getByLocation(testLocation)
            } returns ApiResponse.Success(fakeWeatherResponse)

            homeViewModel.getDataFromLocation(testLocation)

            coVerify { homeRepository.getByLocation(testLocation) }

            homeViewModel.getDataFromLocation(testLocation).collect {
                Truth.assertThat(it).isEqualTo(ApiResponse.Success(fakeWeatherResponse))
            }
        }
    }


}