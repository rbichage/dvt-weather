package com.reuben.core_di

import com.reuben.core_navigation.navigation.WeatherNavDirections
import com.reuben.core_navigation.navigation.WeatherNavDirectionsImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object NavigationModule {

    @Provides
    @Singleton
    fun provideNavigation(): WeatherNavDirections = WeatherNavDirectionsImpl()
}

