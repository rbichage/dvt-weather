package com.dvt.weatherforecast.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dvt.weatherforecast.data.models.db.LocationEntity

@Database(entities = [LocationEntity::class], version = 1, exportSchema = false)
abstract class AllLocationsDatabase : RoomDatabase() {
    abstract fun locationDao(): LocationDao
}