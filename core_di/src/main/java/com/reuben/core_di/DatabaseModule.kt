package com.reuben.core_di

import android.content.Context
import androidx.room.Room
import com.reuben.core_database.WeatherDatabase
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
    @Singleton
    fun provideLocationDao(weatherDatabase: WeatherDatabase) = weatherDatabase.locationDao()

    @Provides
    @Singleton
    fun provideForeCastDao(weatherDatabase: WeatherDatabase) = weatherDatabase.foreCastDao()

}