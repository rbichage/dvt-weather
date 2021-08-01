package com.dvt.weatherforecast.db

import androidx.room.*
import com.dvt.weatherforecast.data.models.db.LocationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(locationEntity: LocationEntity)


    @Query("SELECT * FROM locations ORDER BY lastUpdated DESC")
    fun getAllLocation(): Flow<List<LocationEntity>>

    @Delete
    suspend fun deleteLocation(locationEntity: LocationEntity)

    @Query("DELETE FROM locations")
    suspend fun nukeTable()

    @Query("DELETE FROM locations WHERE isCurrent =:isCurrent")
    suspend fun deleteCurrentLocation(isCurrent: Int = 1)

    @Query("SELECT * FROM locations WHERE isCurrent =:isCurrent")
    fun getCurrentLocation(isCurrent: Int = 1): Flow<List<LocationEntity>>

    @Update
    fun updateLocation(locationEntity: LocationEntity)

}

