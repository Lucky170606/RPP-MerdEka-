package com.example.data.backup

import com.example.data.local.AssessmentEntity
import com.example.data.local.AtpEntity
import com.example.data.local.ModulAjarEntity
import com.example.data.local.P5AssessmentEntity
import com.example.data.local.PromesEntity
import com.example.data.local.ProtaEntity
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DatabaseBackup(
    val modulAjarList: List<ModulAjarEntity>,
    val protaList: List<ProtaEntity>,
    val promesList: List<PromesEntity>,
    val atpList: List<AtpEntity>,
    val assessmentList: List<AssessmentEntity> = emptyList(),
    val p5AssessmentList: List<P5AssessmentEntity> = emptyList(),
    val teacherProfile: com.example.data.model.TeacherProfile? = null
)
