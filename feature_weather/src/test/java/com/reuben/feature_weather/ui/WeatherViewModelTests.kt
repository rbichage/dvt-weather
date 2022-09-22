package com.reuben.feature_weather.ui

import android.location.Geocoder
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.reuben.core_common.location.CurrentLocationHelper
import com.reuben.core_common.network.ApiResponse
import com.reuben.core_testing.sample.SampleResponses.fakeForeCasts
import com.reuben.core_testing.sample.SampleResponses.fakeWeatherResponse
import com.reuben.core_testing.sample.SampleResponses.testLocation
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config


@RunWith(AndroidJUnit4::class)
@Config(manifest = Config.NONE)
class WeatherViewModelTests {


    private lateinit var weatherViewModel: WeatherViewModel

    private val currentLocationHelper: CurrentLocationHelper = mockk(relaxed = true)

    private val weatherRepository: WeatherRepository = mockk(relaxed = true)

    private val geoCoder: Geocoder = mockk(relaxed = true)

    /**
     * Swaps the background executor used by the Architecture Components with a different one which
     * executes each task synchronously.
     **/
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()


    @Before
    fun setup() {
        weatherViewModel = WeatherViewModel(currentLocationHelper, geoCoder, weatherRepository)

    }

    @ExperimentalCoroutinesApi
    @Test
    fun `test get current weather successfully`() {

        runTest {

            coEvery {
                weatherRepository.getCurrentWeatherByLocation(testLocation)
            } returns ApiResponse.Success(fakeWeatherResponse)


            weatherViewModel.getCurrentWeather(testLocation, false)

            weatherViewModel.uiState.test {
                assertEquals(HomeUiState.Loading, awaitItem())
                assertEquals(HomeUiState.CurrentWeather(fakeWeatherResponse), awaitItem())
            }

        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `test get forecast successfully`() {
        runTest {
            coEvery {
                weatherRepository.getNextForeCastByLocation(testLocation)
            } returns ApiResponse.Success(fakeForeCasts)

            weatherViewModel.getForeCastFromLocation(testLocation)

            weatherViewModel.uiState.test {
                assertEquals(HomeUiState.Loading, awaitItem())
                assertEquals(HomeUiState.ForecastData(fakeForeCasts), awaitItem())
            }


        }
    }


}