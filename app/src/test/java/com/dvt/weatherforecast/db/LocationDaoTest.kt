package com.dvt.weatherforecast.db

import android.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dvt.weatherforecast.BaseTest
import com.dvt.weatherforecast.data.sample.SamplePayLoads
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config


@RunWith(AndroidJUnit4::class)
@Config(sdk = [Config.OLDEST_SDK], manifest = Config.NONE)
class LocationDaoTest : BaseTest() {

    @get: Rule
    val rule = InstantTaskExecutorRule()


    @ExperimentalCoroutinesApi
    @Test
    fun `test insert and retrieve location`() = runBlockingTest {
        locationDao.insertLocation(locationEntity = SamplePayLoads.sampleLocation)

        val location = locationDao.getAllLocation().first().toList()[0]

        assertThat(location.name, `is`(SamplePayLoads.sampleLocation.name))
    }
}