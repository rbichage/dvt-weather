package com.dvt.weatherforecast.di

import android.content.Context
import androidx.room.Room
import com.dvt.weatherforecast.db.ForeCastDatabase
import com.dvt.weatherforecast.db.LocationDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideCitiesDatabase(@ApplicationContext context: Context) = Room.databaseBuilder(
        context.applicationContext,
        LocationDatabase::class.java,
        "cities.db"
    ).fallbackToDestructiveMigration()
        .build()


    @Provides
    @Singleton
    fun provideForeCastDatabase(@ApplicationContext context: Context) = Room.databaseBuilder(
        context.applicationContext,
        ForeCastDatabase::class.java,
        "forecast.db"
    ).fallbackToDestructiveMigration()
        .build()


    @Provides
    fun provideCitiesDao(citesDatabase: LocationDatabase) = citesDatabase.citiesDao()

    @Provides
    fun provideForeCastDao(foreCastDatabase: ForeCastDatabase) = foreCastDatabase.foreCastDao()

}