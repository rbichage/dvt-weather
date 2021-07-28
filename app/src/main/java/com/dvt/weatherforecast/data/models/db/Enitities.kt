package com.dvt.weatherforecast.data.models.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cities")
data class LocationEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "locationName") val name: String = "",
    @ColumnInfo(name = "lat") val lat: Double,
    @ColumnInfo(name = "lng") val lng: Double,
    @ColumnInfo(name = "normalTemp") val normalTemp: Int,
    @ColumnInfo(name = "highTemp") val highTemp: Int,
    @ColumnInfo(name = "lowTemp") val lowTemp: Int,
    @ColumnInfo(name = "lastUpdated") val lastUpdated: Long,
    @ColumnInfo(name = "condition") val weatherCondition: String,
    @ColumnInfo(name = "conditionName") val weatherConditionName: String,
)

@Entity(tableName = "all_cities")
data class CityEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "locationName") val name: String = "",
    @ColumnInfo(name = "lat") val lat: Double,
    @ColumnInfo(name = "lng") val lng: Double,
    @ColumnInfo(name = "normalTemp") val normalTemp: Int,
    @ColumnInfo(name = "highTemp") val highTemp: Int,
    @ColumnInfo(name = "lowTemp") val lowTemp: Int,
    @ColumnInfo(name = "lastUpdated") val lastUpdated: Long,
    @ColumnInfo(name = "condition") val weatherCondition: String,
    @ColumnInfo(name = "conditionName") val weatherConditionName: String,
)


@Entity(tableName = "forecasts")
data class ForeCastEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "day")
    val day: String = "",
    @ColumnInfo(name = "locationName") val locationName: String,
    @ColumnInfo(name = "lat") val lat: Double,
    @ColumnInfo(name = "lng") val lng: Double,
    @ColumnInfo(name = "normalTemp") val normalTemp: Int,
    @ColumnInfo(name = "lastUpdated") val lastUpdated: Long,
    @ColumnInfo(name = "weatherCondition") val weatherCondition: String
)