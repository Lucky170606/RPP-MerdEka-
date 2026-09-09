package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ObservationJournalDao {
    @Query("SELECT * FROM observation_journal_history ORDER BY createdAt DESC")
    fun getAllObservations(): Flow<List<ObservationJournalEntity>>

    @Query("SELECT * FROM observation_journal_history ORDER BY createdAt DESC")
    suspend fun getAllObservationsDirect(): List<ObservationJournalEntity>

    @Query("SELECT * FROM observation_journal_history WHERE id = :id")
    suspend fun getObservationById(id: Long): ObservationJournalEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertObservation(observation: ObservationJournalEntity): Long

    @Query("DELETE FROM observation_journal_history WHERE id = :id")
    suspend fun deleteObservationById(id: Long)

    @Query("DELETE FROM observation_journal_history")
    suspend fun clearAllObservations()
}
