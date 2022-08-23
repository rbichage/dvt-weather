package com.dvt.weatherforecast.ui.home.weather

import android.location.Geocoder
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dvt.weatherforecast.data.sample.SampleResponses.fakeForeCasts
import com.dvt.weatherforecast.data.sample.SampleResponses.fakeWeatherResponse
import com.dvt.weatherforecast.data.sample.SampleResponses.testLocation
import com.dvt.weatherforecast.utils.location.CurrentLocationHelper
import com.dvt.weatherforecast.utils.network.ApiResponse
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verifyOrder
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config


@RunWith(AndroidJUnit4::class)
@Config(manifest = Config.NONE)
class HomeViewModelTests {


    private lateinit var homeViewModel: HomeViewModel

    private val currentLocationHelper: CurrentLocationHelper = mockk(relaxed = true)

    private val homeRepository: com.reuben.feature_weather.ui.HomeRepository = mockk(relaxed = true)

    private val geoCoder: Geocoder = mockk(relaxed = true)

    /**
     * Swaps the background executor used by the Architecture Components with a different one which
     * executes each task synchronously.
     **/
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val observer: Observer<HomeUiState> = mockk(relaxed = true)

    @Before
    fun setup() {
        homeViewModel = HomeViewModel(currentLocationHelper, geoCoder, homeRepository)
        homeViewModel.uiState.observeForever(observer)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `test get current weather successfully`() {

        runTest {

            coEvery {
                homeRepository.getCurrentWeatherByLocation(testLocation)
            } returns ApiResponse.Success(fakeWeatherResponse)


            homeViewModel.getCurrentWeather(testLocation, false)

            verifyOrder {
                observer.onChanged(HomeUiState.Loading)
                observer.onChanged(HomeUiState.CurrentWeather(fakeWeatherResponse))
            }
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `test get forecast successfully`() {
        runTest {
            coEvery {
                homeRepository.getNextForeCastByLocation(testLocation)
            } returns ApiResponse.Success(fakeForeCasts)

            homeViewModel.getForeCastFromLocation(testLocation)

            verifyOrder {
                observer.onChanged(HomeUiState.Loading)
                observer.onChanged(HomeUiState.ForecastData(fakeForeCasts))
            }

        }
    }


}