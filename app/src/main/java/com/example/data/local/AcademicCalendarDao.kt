package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AcademicCalendarDao {
    @Query("SELECT * FROM academic_calendar_table WHERE academicYear = :year AND semester = :semester ORDER BY weekNumber ASC")
    fun getCalendarWeeks(year: String, semester: String): Flow<List<AcademicCalendarEntity>>

    @Query("SELECT * FROM academic_calendar_table ORDER BY academicYear DESC, semester ASC, weekNumber ASC")
    fun getAllCalendarWeeks(): Flow<List<AcademicCalendarEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeek(week: AcademicCalendarEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(weeks: List<AcademicCalendarEntity>)

    @Update
    suspend fun updateWeek(week: AcademicCalendarEntity)

    @Query("DELETE FROM academic_calendar_table WHERE academicYear = :year AND semester = :semester")
    suspend fun deleteForSemester(year: String, semester: String)
}
