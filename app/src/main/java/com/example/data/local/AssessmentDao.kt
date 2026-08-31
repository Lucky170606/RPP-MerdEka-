package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AssessmentDao {
    @Query("SELECT * FROM assessment_history ORDER BY createdAt DESC")
    fun getAllAssessments(): Flow<List<AssessmentEntity>>

    @Query("SELECT * FROM assessment_history ORDER BY createdAt DESC")
    suspend fun getAllAssessmentsDirect(): List<AssessmentEntity>

    @Query("SELECT * FROM assessment_history WHERE id = :id")
    suspend fun getAssessmentById(id: Long): AssessmentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssessment(assessment: AssessmentEntity): Long

    @Query("DELETE FROM assessment_history WHERE id = :id")
    suspend fun deleteAssessmentById(id: Long)

    @Query("DELETE FROM assessment_history")
    suspend fun clearAllAssessments()
}
