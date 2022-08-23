package com.reuben.weatherforecast

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.reuben.weatherforecast.dispatcher.WeatherRequestDispatcher
import com.reuben.core_network.api.ApiService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

open class BaseTest {
    // database and dao
    open lateinit var weatherDatabase: com.reuben.core_database.WeatherDatabase
    open lateinit var locationDao: com.reuben.core_database.LocationDao

    // mock web server and network api
    open lateinit var mockWebServer: MockWebServer
    open lateinit var okHttpClient: OkHttpClient
    open lateinit var loggingInterceptor: HttpLoggingInterceptor
    open lateinit var apiService: com.reuben.core_network.api.ApiService
    open lateinit var foreCastDao: com.reuben.core_database.ForeCastDao

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

        mockWebServer = MockWebServer().apply {
            dispatcher = WeatherRequestDispatcher()
            start()
        }

        loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        okHttpClient = buildOkhttpClient(loggingInterceptor)

        val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

        apiService = Retrofit.Builder()
                .baseUrl(mockWebServer.url("/"))
                .client(okHttpClient)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(com.reuben.core_network.api.ApiService::class.java)

    }

    @After
    @Throws(IOException::class)
    @ExperimentalCoroutinesApi
    open fun tearDown() {
        Dispatchers.resetMain()
        weatherDatabase.close()
        mockWebServer.shutdown()
    }

    private fun buildOkhttpClient(httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build()
    }
}