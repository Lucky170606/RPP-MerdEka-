package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ModulAjarDao {
    @Query("SELECT * FROM modul_ajar ORDER BY updatedAt DESC")
    fun getAllModul(): Flow<List<ModulAjarEntity>>

    @Query("SELECT * FROM modul_ajar ORDER BY updatedAt DESC")
    suspend fun getAllModulDirect(): List<ModulAjarEntity>

    @Query("SELECT * FROM modul_ajar WHERE id = :id")
    fun getModulById(id: Long): Flow<ModulAjarEntity?>

    @Query("SELECT * FROM modul_ajar WHERE id = :id")
    suspend fun getModulDirect(id: Long): ModulAjarEntity?

    @Query("SELECT * FROM modul_ajar WHERE isFavorite = 1 ORDER BY updatedAt DESC")
    fun getFavoriteModul(): Flow<List<ModulAjarEntity>>

    @Query("SELECT * FROM modul_ajar WHERE subject = :subject ORDER BY updatedAt DESC")
    fun getModulBySubject(subject: String): Flow<List<ModulAjarEntity>>

    @Query("SELECT * FROM modul_ajar WHERE title LIKE '%' || :query || '%' OR topic LIKE '%' || :query || '%' OR subject LIKE '%' || :query || '%' ORDER BY updatedAt DESC")
    fun searchModul(query: String): Flow<List<ModulAjarEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModul(modul: ModulAjarEntity): Long

    @Update
    suspend fun updateModul(modul: ModulAjarEntity)

    @Query("DELETE FROM modul_ajar WHERE id = :id")
    suspend fun deleteModulById(id: Long)

    @Query("UPDATE modul_ajar SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun setFavorite(id: Long, isFavorite: Boolean)

    @Query("SELECT COUNT(*) FROM modul_ajar")
    fun getModulCount(): Flow<Int>
}
