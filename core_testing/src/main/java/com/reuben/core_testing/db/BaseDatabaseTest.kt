package com.reuben.core_testing.db

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.reuben.core_database.ForeCastDao
import com.reuben.core_database.LocationDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before

open class BaseDatabaseTest {
    open lateinit var weatherDatabase: com.reuben.core_database.WeatherDatabase
    open lateinit var locationDao: LocationDao
    open lateinit var foreCastDao: ForeCastDao

    @ExperimentalCoroutinesApi
    private val testDispatcher = StandardTestDispatcher()

    @Before
    @ExperimentalCoroutinesApi
    open fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        Dispatchers.setMain(testDispatcher)

        weatherDatabase = Room.inMemoryDatabaseBuilder(context,
                com.reuben.core_database.WeatherDatabase::class.java)
                .allowMainThreadQueries()
                .setTransactionExecutor(testDispatcher.asExecutor())
                .setQueryExecutor(testDispatcher.asExecutor())
                .build()

        foreCastDao = weatherDatabase.foreCastDao()

        locationDao = weatherDatabase.locationDao()
    }

    @After
    @ExperimentalCoroutinesApi
    open fun tearDown() {
        Dispatchers.resetMain()
        weatherDatabase.close()
    }
}