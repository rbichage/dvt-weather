package com.dvt.weatherforecast.db

import androidx.room.*
import com.dvt.weatherforecast.data.models.db.CityEntity
import com.dvt.weatherforecast.data.models.db.LocationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(locationEntity: LocationEntity)


    @Query("SELECT *FROM locations WHERE locationName LIKE :cityName")
    fun getCurrentLocation(cityName: String): Flow<List<LocationEntity>>

    @Query("SELECT * FROM locations ORDER BY lastUpdated DESC")
    fun getAllLocation(): Flow<List<LocationEntity>>

    @Delete
    suspend fun deleteLocation(locationEntity: LocationEntity)

    @Query("DELETE FROM locations")
    suspend fun nukeTable()

    @Query("DELETE FROM locations WHERE isCurrent =:isCurrent")
    suspend fun deleteCurrentLocation(isCurrent: Int = 1)

}

@Dao
interface CitiesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(locationEntity: CityEntity)


    @Query("SELECT *FROM all_cities WHERE locationName LIKE :cityName")
    fun getCurrentLocation(cityName: String): Flow<List<LocationEntity>>

    @Query("SELECT * FROM all_cities")
    fun getAllLocation(): Flow<List<LocationEntity>>

    @Delete
    fun deleteCity(cityEntity: CityEntity)

    @Delete
    suspend fun deleteLocation(locationEntity: LocationEntity)
}