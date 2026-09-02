package com.example.data.model

import android.content.Context
import android.content.SharedPreferences

data class TeacherProfile(
    val teacherName: String = "Budi Santoso, S.Pd.",
    val teacherNip: String = "19850712 201001 1 008",
    val schoolName: String = "SD Negeri Merdeka Belajar 01",
    val npsn: String = "20103456",
    val principalName: String = "Dra. Hj. Siti Rohmah, M.Pd.",
    val principalNip: String = "19720415 199603 2 003",
    val cityAndDate: String = "Jakarta, 15 Juli 2024",
    val defaultAcademicYear: String = "2024/2025",
    val defaultSemester: String = "Semester 1 (Ganjil)",
    val printLayoutMode: String = "STANDAR" // STANDAR, RINGKAS (1-2 Halaman), KOMPREHENSIF (Lengkap)
) {
    companion object {
        private const val PREF_NAME = "teacher_school_profile_pref"

        fun loadFromPreferences(context: Context): TeacherProfile {
            val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            return TeacherProfile(
                teacherName = pref.getString("teacherName", "Budi Santoso, S.Pd.") ?: "Budi Santoso, S.Pd.",
                teacherNip = pref.getString("teacherNip", "19850712 201001 1 008") ?: "19850712 201001 1 008",
                schoolName = pref.getString("schoolName", "SD Negeri Merdeka Belajar 01") ?: "SD Negeri Merdeka Belajar 01",
                npsn = pref.getString("npsn", "20103456") ?: "20103456",
                principalName = pref.getString("principalName", "Dra. Hj. Siti Rohmah, M.Pd.") ?: "Dra. Hj. Siti Rohmah, M.Pd.",
                principalNip = pref.getString("principalNip", "19720415 199603 2 003") ?: "19720415 199603 2 003",
                cityAndDate = pref.getString("cityAndDate", "Jakarta, 15 Juli 2024") ?: "Jakarta, 15 Juli 2024",
                defaultAcademicYear = pref.getString("academicYear", "2024/2025") ?: "2024/2025",
                defaultSemester = pref.getString("semester", "Semester 1 (Ganjil)") ?: "Semester 1 (Ganjil)",
                printLayoutMode = pref.getString("printLayoutMode", "STANDAR") ?: "STANDAR"
            )
        }

        fun saveToPreferences(context: Context, profile: TeacherProfile, sync: Boolean = false) {
            val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val editor = pref.edit()
                .putString("teacherName", profile.teacherName)
                .putString("teacherNip", profile.teacherNip)
                .putString("schoolName", profile.schoolName)
                .putString("npsn", profile.npsn)
                .putString("principalName", profile.principalName)
                .putString("principalNip", profile.principalNip)
                .putString("cityAndDate", profile.cityAndDate)
                .putString("academicYear", profile.defaultAcademicYear)
                .putString("semester", profile.defaultSemester)
                .putString("printLayoutMode", profile.printLayoutMode)
            
            if (sync) {
                editor.commit()
            } else {
                editor.apply()
            }
        }
    }
}
