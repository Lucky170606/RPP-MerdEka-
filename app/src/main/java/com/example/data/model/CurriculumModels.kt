package com.example.data.model

enum class Fase(val code: String, val label: String, val jenjang: String, val grades: List<String>) {
    FASE_A("Fase A", "Fase A (Kelas 1 - 2 SD)", "SD/MI", listOf("Kelas 1", "Kelas 2")),
    FASE_B("Fase B", "Fase B (Kelas 3 - 4 SD)", "SD/MI", listOf("Kelas 3", "Kelas 4")),
    FASE_C("Fase C", "Fase C (Kelas 5 - 6 SD)", "SD/MI", listOf("Kelas 5", "Kelas 6")),
    FASE_D("Fase D", "Fase D (Kelas 7 - 9 SMP)", "SMP/MTs", listOf("Kelas 7", "Kelas 8", "Kelas 9")),
    FASE_E("Fase E", "Fase E (Kelas 10 SMA/SMK)", "SMA/SMK/MA", listOf("Kelas 10")),
    FASE_F("Fase F", "Fase F (Kelas 11 - 12 SMA/SMK)", "SMA/SMK/MA", listOf("Kelas 11", "Kelas 12"))
}

data class CapaianPembelajaranItem(
    val subject: String,
    val fase: String,
    val elemen: String,
    val capaianText: String,
    val suggestedTujuan: List<String>,
    val defaultKeywords: List<String>
)

data class ProfilPancasilaDimension(
    val id: String,
    val title: String,
    val subElements: List<String>,
    val description: String
)

data class ModelPembelajaranOption(
    val id: String,
    val name: String,
    val description: String,
    val sintaks: List<String>
)

data class DiferensiasiConfig(
    val focusGayaBelajar: List<String> = listOf("Visual", "Auditori", "Kinestetik"),
    val kesiapanBelajar: List<String> = listOf("Perlu Bimbingan", "Cukup / Berkembang", "Mahir / Mahir Lanjut"),
    val aspekDiferensiasi: List<String> = listOf("Konten", "Proses", "Produk"),
    val catatanKhusus: String = ""
)

data class GeneratedModulContent(
    val identitas: String,
    val kompetensiAwal: String,
    val profilPelajarPancasila: String,
    val saranaPrasarana: String,
    val targetPesertaDidik: String,
    val modelPembelajaran: String,
    val tujuanPembelajaran: String,
    val pemahamanBermakna: String,
    val pertanyaanPemantik: String,
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
    val lkpdDanMateri: String
)
