package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface P5AssessmentDao {
    @Query("SELECT * FROM p5_assessment_history ORDER BY createdAt DESC")
    fun getAllP5Assessments(): Flow<List<P5AssessmentEntity>>

    @Query("SELECT * FROM p5_assessment_history ORDER BY createdAt DESC")
    suspend fun getAllP5AssessmentsDirect(): List<P5AssessmentEntity>

    @Query("SELECT * FROM p5_assessment_history WHERE id = :id")
    suspend fun getP5AssessmentById(id: Long): P5AssessmentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertP5Assessment(assessment: P5AssessmentEntity): Long

    @Query("DELETE FROM p5_assessment_history WHERE id = :id")
    suspend fun deleteP5AssessmentById(id: Long)

    @Query("DELETE FROM p5_assessment_history")
    suspend fun clearAllP5Assessments()
}
