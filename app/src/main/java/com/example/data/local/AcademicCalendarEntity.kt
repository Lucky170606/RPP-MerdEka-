package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "academic_calendar_table")
data class AcademicCalendarEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val academicYear: String, // e.g. "2024/2025"
    val semester: String,     // e.g. "Semester 1 (Ganjil)" or "Semester 2 (Genap)"
    val weekNumber: Int,      // 1 to 22
    val monthName: String,    // e.g. "Juli", "Agustus"
    val weekLabel: String,    // e.g. "Minggu ke-1"
    val status: String,       // "EFFECTIVE" (Efektif KBM), "HOLIDAY" (Libur), "EXAM" (Ujian/Asesmen), "ACTIVITY" (Kegiatan Khusus)
    val hours: Int = 4,       // Jam Pelajaran (JP) for this week if effective
    val description: String   // e.g. "MPLS & Masa Pengenalan", "KBM Efektif", "Penilaian Tengah Semester"
)
