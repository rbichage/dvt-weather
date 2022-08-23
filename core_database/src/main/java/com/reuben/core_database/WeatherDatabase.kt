package com.reuben.core_database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.reuben.core_data.models.db.ForeCastEntity
import com.reuben.core_data.models.db.LocationEntity


@Database(version = 7, entities = [LocationEntity::class, ForeCastEntity::class], exportSchema = false)
abstract class WeatherDatabase : RoomDatabase() {

    abstract fun locationDao(): LocationDao

    abstract fun foreCastDao(): ForeCastDao
}