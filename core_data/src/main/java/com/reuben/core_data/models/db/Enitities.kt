package com.reuben.core_data.models.db

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "locations")
@Parcelize
data class LocationEntity(
        @ColumnInfo(name = "locationName") val name: String,
        @PrimaryKey(autoGenerate = false)
        @ColumnInfo(name = "locationId") val locationId: String,
        @ColumnInfo(name = "lat") val lat: Double,
        @ColumnInfo(name = "lng") val lng: Double,
        @ColumnInfo(name = "normalTemp") val normalTemp: Int,
        @ColumnInfo(name = "highTemp") val highTemp: Int,
        @ColumnInfo(name = "lowTemp") val lowTemp: Int,
        @ColumnInfo(name = "lastUpdated") val lastUpdated: Long,
        @ColumnInfo(name = "isCurrent") val isCurrent: Boolean = false,
        @ColumnInfo(name = "condition") val weatherCondition: String,
        @ColumnInfo(name = "conditionName") val weatherConditionName: String,
) : Parcelable


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

@Entity(tableName = "forecasts")
data class HourlyForeCastEntity(
        @PrimaryKey(autoGenerate = false) @ColumnInfo(name = "timeOfDay")
        val timeOfDay: Long,
        @ColumnInfo(name = "normalTemp") val temp: String,
        @ColumnInfo(name = "weatherCondition") val weatherCondition: String
)