package com.reuben.feature_weather.ui

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth
import com.reuben.core_common.network.ApiResponse
import com.reuben.core_testing.sample.SamplePayLoads
import com.reuben.core_testing.sample.SampleResponses
import com.reuben.feature_weather.BaseWeatherTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import java.util.*

@RunWith(AndroidJUnit4::class)
@Config(
        sdk = [Config.OLDEST_SDK],
        manifest = Config.NONE
)
class WeatherRepositoryTests : BaseWeatherTest() {

    private lateinit var weatherRepository: WeatherRepository


    @ExperimentalCoroutinesApi
    override fun setup() {
        super.setup()

        weatherRepository = WeatherRepository(
                apiService = apiService,
                apiKey = UUID.randomUUID().toString(),
                foreCastDao = foreCastDao,
                locationDao = locationDao
        )
    }

    @Test
    @ExperimentalCoroutinesApi
    fun `test getting current weather from API`() {
        runTest {

            val response = weatherRepository.getCurrentWeatherByLocation(location = SampleResponses.testLocation)

            Truth.assertThat(response is ApiResponse.Success)

        }
    }

    @Test
    @ExperimentalCoroutinesApi
    fun `test getting forecast from API`() {
        runTest {
            val response = weatherRepository.getNextForeCastByLocation(location = SampleResponses.testLocation)
            Truth.assertThat(response is ApiResponse.Success)
        }
    }


    @Test
    @ExperimentalCoroutinesApi
    fun `test inserting weather DB`() {
        runTest {
            weatherRepository.insertCurrentLocation(SamplePayLoads.sampleLocation)

            val location = weatherRepository.getAllLocations().first().toList()[0]

            MatcherAssert.assertThat(location.name, `is`(SamplePayLoads.sampleLocation.name))
        }
    }


}