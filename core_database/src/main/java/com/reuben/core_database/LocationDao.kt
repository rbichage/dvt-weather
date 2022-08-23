package com.reuben.core_database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.reuben.core_data.models.db.LocationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(locationEntity: LocationEntity)


    @Query("SELECT * FROM locations ORDER BY isCurrent DESC")
    fun getAllLocation(): Flow<List<LocationEntity>>

    @Delete
    suspend fun deleteLocation(locationEntity: LocationEntity)

    @Query("DELETE FROM locations")
    suspend fun nukeTable()

    @Query("DELETE FROM locations WHERE isCurrent =:isCurrent")
    suspend fun deleteCurrentLocation(isCurrent: Int = 1)

    @Query("SELECT * FROM locations WHERE isCurrent =:isCurrent")
    fun getCurrentLocation(isCurrent: Int = 1): Flow<List<LocationEntity>>

    @Query("SELECT * FROM locations WHERE lat =:latitude")
    fun getLocationByLatitude(latitude: Double): List<LocationEntity>

    @Update
    fun updateLocation(locationEntity: LocationEntity)

}

