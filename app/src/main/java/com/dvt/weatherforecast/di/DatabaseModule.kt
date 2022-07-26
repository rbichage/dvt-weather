package com.dvt.weatherforecast.di

import android.content.Context
import androidx.room.Room
import com.dvt.weatherforecast.db.WeatherDatabase
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
            WeatherDatabase::class.java,
            "cities.db"
    ).fallbackToDestructiveMigration()
            .build()


    @Provides
    fun provideCitiesDao(weatherDatabase: WeatherDatabase) = weatherDatabase.locationDao()

    @Provides
    fun provideForeCastDao(weatherDatabase: WeatherDatabase) = weatherDatabase.foreCastDao()

}