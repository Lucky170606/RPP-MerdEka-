package com.example.data.model

data class ProtaItem(
    val nomor: Int,
    val semester: String,
    val babMateri: String,
    val capaianTujuan: String,
    val alokasiJp: Int
)

data class ProtaDocument(
    val title: String,
    val subject: String,
    val fase: String,
    val grade: String,
    val academicYear: String,
    val totalJp: Int,
    val items: List<ProtaItem>
)

data class PromesWeeklyMatrix(
    val bulan: String,
    val weeks: List<Int> // list of JP assigned to week 1..4 or 5
)

data class PromesItem(
    val nomor: Int,
    val materiPokok: String,
    val tujuanPembelajaran: String,
    val alokasiJp: Int,
    val weeklyDistribution: List<PromesWeeklyMatrix>
)

data class PromesDocument(
    val title: String,
    val subject: String,
    val fase: String,
    val grade: String,
    val semester: String,
    val academicYear: String,
    val totalJp: Int,
    val items: List<PromesItem>
)

data class AtpStepItem(
    val nomorUrut: String,
    val elemen: String,
    val capaianPembelajaran: String,
    val tujuanPembelajaran: String,
    val materiPokok: String,
    val alokasiJp: Int,
    val profilPancasila: String,
    val indikatorKetercapaian: String
)

data class AtpDocument(
    val title: String,
    val subject: String,
    val fase: String,
    val grade: String,
    val totalJp: Int,
    val rasional: String,
    val karakteristikMataPelajaran: String,
    val alurTujuanList: List<AtpStepItem>
)

data class StudentRaporEntry(
    val id: String = java.util.UUID.randomUUID().toString(),
    val namaSiswa: String,
    val nilaiAkhir: Int,
    val materiTinggi: String,
    val materiRendah: String,
    val deskripsiCapaian: String = ""
)

data class KktpInterval(
    val rentang: String,
    val predikat: String,
    val deskripsiKriteria: String,
    val tindakLanjut: String
)

data class JurnalObservasiItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val tanggal: String,
    val namaSiswa: String,
    val dimensiP3: String,
    val catatanPerilaku: String,
    val butirSikapPositifNegatif: String, // "Positif (+)" / "Perlu Pembinaan (-)"
    val rencanaTindakLanjut: String
)

data class PeerAssessmentQuestion(
    val no: Int,
    val pernyataan: String,
    val dimensiTerkait: String
)
