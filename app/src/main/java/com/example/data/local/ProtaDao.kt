package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProtaDao {
    @Query("SELECT * FROM prota_table ORDER BY updatedAt DESC")
    fun getAllProta(): Flow<List<ProtaEntity>>

    @Query("SELECT * FROM prota_table ORDER BY updatedAt DESC")
    suspend fun getAllProtaDirect(): List<ProtaEntity>

    @Query("SELECT * FROM prota_table WHERE id = :id")
    fun getProtaById(id: Long): Flow<ProtaEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProta(prota: ProtaEntity): Long

    @Update
    suspend fun updateProta(prota: ProtaEntity)

    @Query("SELECT COUNT(*) FROM prota_table")
    fun getProtaCount(): Flow<Int>

    @Query("DELETE FROM prota_table WHERE id = :id")
    suspend fun deleteProtaById(id: Long)
}
