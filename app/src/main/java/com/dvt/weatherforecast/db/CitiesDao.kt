package com.dvt.weatherforecast.db

import androidx.room.*
import com.dvt.weatherforecast.data.models.db.CityEntity
import com.dvt.weatherforecast.data.models.db.LocationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(locationEntity: LocationEntity)


    @Query("SELECT *FROM cities WHERE locationName LIKE :cityName")
    fun getCurrentLocation(cityName: String): Flow<List<LocationEntity>>

    @Query("SELECT * FROM cities")
    fun getAllLocation(): Flow<List<LocationEntity>>

    @Delete
    suspend fun deleteLocation(locationEntity: LocationEntity)

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