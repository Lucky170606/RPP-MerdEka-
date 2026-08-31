package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "p5_assessment_history")
data class P5AssessmentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val tema: String,
    val fase: String,
    val grade: String,
    val jsonContent: String, // Moshi serialized P5ProjectModul
    val createdAt: Long = System.currentTimeMillis()
)
