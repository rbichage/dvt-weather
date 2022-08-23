package com.reuben.core_database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.reuben.core_data.models.db.ForeCastEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ForeCastDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertForeCast(foreCastEntity: ForeCastEntity)

    @Query("DELETE FROM forecasts")
    suspend fun nukeTable()

    @Query("SELECT * FROM forecasts ORDER by lastUpdated ASC")
    fun getAllForeCasts(): Flow<List<ForeCastEntity>>

    @Query("DELETE FROM forecasts where locationName LIKE :locationName ")
    suspend fun deleteForeCast(locationName: String)
}