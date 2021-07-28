package com.dvt.weatherforecast.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dvt.weatherforecast.data.models.db.ForeCastEntity


@Database(entities = [ForeCastEntity::class], version = 2, exportSchema = false)
abstract class ForeCastDatabase : RoomDatabase() {
    abstract fun foreCastDao(): ForeCastDao
}