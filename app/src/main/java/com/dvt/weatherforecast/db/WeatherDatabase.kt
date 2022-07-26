package com.dvt.weatherforecast.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dvt.weatherforecast.data.models.db.ForeCastEntity
import com.dvt.weatherforecast.data.models.db.LocationEntity


@Database(version = 7, entities = [LocationEntity::class, ForeCastEntity::class], exportSchema = false)
abstract class WeatherDatabase : RoomDatabase() {

    abstract fun locationDao(): LocationDao

    abstract fun foreCastDao(): ForeCastDao
}