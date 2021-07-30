package com.dvt.weatherforecast.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dvt.weatherforecast.data.models.db.LocationEntity


@Database(version = 5, entities = [LocationEntity::class], exportSchema = false)
abstract class LocationDatabase : RoomDatabase() {

    abstract fun citiesDao(): LocationDao
}