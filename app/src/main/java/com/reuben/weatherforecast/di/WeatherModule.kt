package com.reuben.weatherforecast.di

import com.reuben.feature_weather.ui.ForeCastRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object WeatherModule {
    @Module
    @InstallIn(SingletonComponent::class)
    object WeatherModule {

        @Provides
        @Singleton
        fun provideForeCastRepository(foreCastRepository: ForeCastRepository): ForeCastRepository = foreCastRepository
    }
}