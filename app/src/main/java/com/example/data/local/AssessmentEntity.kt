package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "assessment_history")
data class AssessmentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val subject: String,
    val fase: String,
    val grade: String,
    val semester: String,
    val topikUjian: String,
    val jenisAsesmen: String,
    val jumlahSoal: Int,
    val jsonContent: String, // Moshi serialized AssessmentDocument
    val createdAt: Long = System.currentTimeMillis()
)
