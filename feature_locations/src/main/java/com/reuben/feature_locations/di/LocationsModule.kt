package com.reuben.feature_locations.di

import com.reuben.feature_locations.ui.map.LocationsMapRepository
import com.reuben.feature_locations.ui.map.LocationsMapRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocationsModule {

    @Provides
    @Singleton
    fun provideLocationsMapInterface(locationsMapRepositoryImpl: LocationsMapRepositoryImpl): LocationsMapRepository = locationsMapRepositoryImpl

}