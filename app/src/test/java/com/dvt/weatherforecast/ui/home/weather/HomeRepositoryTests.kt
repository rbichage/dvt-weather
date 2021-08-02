package com.dvt.weatherforecast.ui.home.weather

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dvt.weatherforecast.BaseTest
import com.dvt.weatherforecast.data.sample.SamplePayLoads
import com.dvt.weatherforecast.data.sample.SampleResponses
import com.dvt.weatherforecast.utils.network.ApiResponse
import com.google.common.truth.Truth
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(
        sdk = [Config.OLDEST_SDK],
        manifest = Config.NONE
) // https://stackoverflow.com/questions/56821193/does-robolectric-require-java-9
class HomeRepositoryTests : BaseTest() {

    private lateinit var homeRepository: HomeRepository


    @Before
    override fun setup() {
        super.setup()
        homeRepository = HomeRepository(apiService, "random string", foreCastDao, locationDao)
    }


    @Test
    fun `test getting current weather from API`() {
        runBlocking {

            val response = homeRepository.getByLocation(location = SampleResponses.testLocation)

            Truth.assertThat(response is ApiResponse.Success)
        }
    }

    @Test
    fun `test getting forecast from API`() {
        runBlocking {
            val response = homeRepository.getForeCastByLocation(location = SampleResponses.testLocation)

            Truth.assertThat(response is ApiResponse.Success)
        }
    }

    @Test
    fun `test inserting weather DB`() {
        runBlocking {
            homeRepository.insertCurrentLocation(SamplePayLoads.sampleLocation)

            val location = homeRepository.getAllLocations().first().toList()[0]

            MatcherAssert.assertThat(location.name, `is`(SamplePayLoads.sampleLocation.name))
        }
    }

}