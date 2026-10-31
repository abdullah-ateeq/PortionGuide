package com.example.portionguide.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CalculationDao {

    @Query("SELECT * FROM calculations ORDER BY timestamp DESC")
    fun getAllCalculations(): Flow<List<CalculationEntity>>

    @Query("SELECT * FROM calculations ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentCalculations(limit: Int): Flow<List<CalculationEntity>>

    @Query("SELECT * FROM calculations WHERE id = :id LIMIT 1")
    suspend fun getCalculationById(id: Long): CalculationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCalculation(entity: CalculationEntity): Long

    @Query("DELETE FROM calculations WHERE id = :id")
    suspend fun deleteById(id: Long): Int

    @Query("DELETE FROM calculations")
    suspend fun deleteAll(): Int
}
