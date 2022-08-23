package com.reuben.feature_search.di

import com.reuben.feature_search.data.LocationSearchRepository
import com.reuben.feature_search.data.LocationSearchRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SearchModule {
    @Singleton
    @Provides
    fun provideSearchRepositoryInterface(searchRepositoryImpl: LocationSearchRepositoryImpl): LocationSearchRepository = searchRepositoryImpl
}