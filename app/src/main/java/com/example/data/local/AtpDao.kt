package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AtpDao {
    @Query("SELECT * FROM atp_table ORDER BY updatedAt DESC")
    fun getAllAtp(): Flow<List<AtpEntity>>

    @Query("SELECT * FROM atp_table ORDER BY updatedAt DESC")
    suspend fun getAllAtpDirect(): List<AtpEntity>

    @Query("SELECT * FROM atp_table WHERE id = :id")
    fun getAtpById(id: Long): Flow<AtpEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAtp(atp: AtpEntity): Long

    @Update
    suspend fun updateAtp(atp: AtpEntity)

    @Query("SELECT COUNT(*) FROM atp_table")
    fun getAtpCount(): Flow<Int>

    @Query("DELETE FROM atp_table WHERE id = :id")
    suspend fun deleteAtpById(id: Long)
}
