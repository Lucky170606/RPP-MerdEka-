package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PromesDao {
    @Query("SELECT * FROM promes_table ORDER BY updatedAt DESC")
    fun getAllPromes(): Flow<List<PromesEntity>>

    @Query("SELECT * FROM promes_table ORDER BY updatedAt DESC")
    suspend fun getAllPromesDirect(): List<PromesEntity>

    @Query("SELECT * FROM promes_table WHERE id = :id")
    fun getPromesById(id: Long): Flow<PromesEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPromes(promes: PromesEntity): Long

    @Update
    suspend fun updatePromes(promes: PromesEntity)

    @Query("SELECT COUNT(*) FROM promes_table")
    fun getPromesCount(): Flow<Int>

    @Query("DELETE FROM promes_table WHERE id = :id")
    suspend fun deletePromesById(id: Long)
}
