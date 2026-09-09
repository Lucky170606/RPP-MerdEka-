package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface KktpDao {
    @Query("SELECT * FROM kktp_rapor_history ORDER BY createdAt DESC")
    fun getAllKktp(): Flow<List<KktpEntity>>

    @Query("SELECT * FROM kktp_rapor_history ORDER BY createdAt DESC")
    suspend fun getAllKktpDirect(): List<KktpEntity>

    @Query("SELECT * FROM kktp_rapor_history WHERE id = :id")
    suspend fun getKktpById(id: Long): KktpEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKktp(kktp: KktpEntity): Long

    @Query("DELETE FROM kktp_rapor_history WHERE id = :id")
    suspend fun deleteKktpById(id: Long)

    @Query("DELETE FROM kktp_rapor_history")
    suspend fun clearAllKktp()
}
