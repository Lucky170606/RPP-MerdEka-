package com.example.data.model

data class KisiKisiItem(
    val nomorUrut: Int,
    val capaianElemen: String,
    val materiPokok: String,
    val indikatorSoal: String,
    val levelKognitif: String, // C1 (Mengingat), C2 (Memahami), C3 (Menerapkan), C4 (Menganalisis), C5 (Mengevaluasi), C6 (Mencipta)
    val bentukSoal: String, // Pilihan Ganda (PG), Pilihan Ganda Kompleks (PGK), Menjodohkan, Uraian Singkat, Uraian Analisis
    val nomorSoal: Int
)

data class SoalHotsItem(
    val nomor: Int,
    val bentukSoal: String,
    val levelKognitif: String,
    val stimulusText: String, // Bacaan, studi kasus, data tabel, atau infografis naratif
    val pertanyaan: String,
    val pilihanOpsi: List<String> = emptyList(), // Untuk PG: A, B, C, D, E
    val kunciJawaban: String,
    val pembahasanDanAlasan: String,
    val skorMaksimal: Int
)

data class AssessmentDocument(
    val id: Long = 0,
    val title: String,
    val subject: String,
    val fase: String,
    val grade: String,
    val semester: String,
    val topikUjian: String,
    val jenisAsesmen: String, // Asesmen Formatif Akhir Bab, Sumatif Tengah Semester (STS), Sumatif Akhir Semester (SAS)
    val jumlahSoal: Int,
    val kisiKisiList: List<KisiKisiItem>,
    val soalList: List<SoalHotsItem>,
    val pedomanPenskoran: String,
    val isOnlineAiGenerated: Boolean = true,
    val engineName: String = "Gemini AI (Online)",
    val createdAt: Long = System.currentTimeMillis()
)
