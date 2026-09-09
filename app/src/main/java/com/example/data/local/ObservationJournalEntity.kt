package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "observation_journal_history")
data class ObservationJournalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val studentName: String,
    val classOrPhase: String,
    val dimension: String,
    val observationNote: String,
    val attitudeStatus: String,
    val date: String,
    val createdAt: Long = System.currentTimeMillis()
)
