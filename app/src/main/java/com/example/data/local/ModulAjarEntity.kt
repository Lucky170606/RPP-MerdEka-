package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "modul_ajar")
data class ModulAjarEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val subject: String,
    val fase: String,
    val grade: String,
    val topic: String,
    val timeAllocation: String,
    val teacherName: String,
    val schoolName: String,
    val semester: String,
    val academicYear: String,
    val modelPembelajaran: String,
    val dimensiP3: String, // Comma separated list of dimensions
    val kompetensiAwal: String = "Peserta didik memahami pengetahuan prasyarat terkait topik.",
    val profilPelajarPancasila: String = "",
    val capaianPembelajaran: String,
    val tujuanPembelajaran: String,
    val pemahamanBermakna: String,
    val pertanyaanPemantik: String,
    val saranaPrasarana: String,
    val targetPesertaDidik: String,
    val kegiatanPendahuluan: String,
    val kegiatanInti: String,
    val kegiatanPenutup: String,
    val diferensiasiKonten: String,
    val diferensiasiProses: String,
    val diferensiasiProduk: String,
    val asesmenDiagnostik: String,
    val asesmenFormatif: String,
    val asesmenSumatif: String,
    val rubrikPenilaian: String,
    val remedialDanPengayaan: String,
    val lkpdDanMateri: String,
    val rawCompiledDocument: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
)
