package com.reuben.weatherforecast

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.reuben.core_testing.db.BaseDatabaseTest
import com.reuben.core_testing.sample.SamplePayLoads
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config


@RunWith(AndroidJUnit4::class)
@Config(sdk = [Config.OLDEST_SDK], manifest = Config.NONE)
class LocationDaoTest : BaseDatabaseTest() {
    @get: Rule
    val rule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @Test
    fun `test insert and retrieve location`() = runTest {
        locationDao.insertLocation(locationEntity = SamplePayLoads.sampleLocation)

        val location = locationDao.getAllLocation().first().toList()[0]

        assertThat(location.name, `is`(SamplePayLoads.sampleLocation.name))
    }
}