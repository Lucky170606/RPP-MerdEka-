package com.example.data.model

object CurriculumConstants {
    // Definisi jenjang
    val JENJANG_SD = "SD"
    val JENJANG_SMP = "SMP"
    val JENJANG_SMA_SMK = "SMA/SMK"

    // Mapping Mata Pelajaran per Jenjang
    val MATA_PELAJARAN_MAP = mapOf(
        JENJANG_SD to listOf(
            "Matematika",
            "Bahasa Indonesia",
            "Ilmu Pengetahuan Alam dan Sosial (IPAS)",
            "Pendidikan Pancasila",
            "Bahasa Inggris",
            "Informatika",
            "Pendidikan Jasmani, Olahraga, dan Kesehatan (PJOK)",
            "Seni Rupa",
            "Seni Musik",
            "Pendidikan Agama Islam dan Budi Pekerti"
        ),
        JENJANG_SMP to listOf(
            "Matematika",
            "Bahasa Indonesia",
            "Ilmu Pengetahuan Alam (IPA)",
            "Ilmu Pengetahuan Sosial (IPS)",
            "Pendidikan Pancasila",
            "Bahasa Inggris",
            "Informatika",
            "Pendidikan Jasmani, Olahraga, dan Kesehatan (PJOK)",
            "Seni Rupa",
            "Seni Musik",
            "Pendidikan Agama Islam dan Budi Pekerti"
        ),
        JENJANG_SMA_SMK to listOf(
            "Matematika",
            "Bahasa Indonesia",
            "Pendidikan Pancasila",
            "Bahasa Inggris",
            "Informatika",
            "Pendidikan Jasmani, Olahraga, dan Kesehatan (PJOK)",
            "Seni Rupa",
            "Seni Musik",
            "Pendidikan Agama Islam dan Budi Pekerti",
            "Fisika",
            "Biologi",
            "Kimia",
            "Sejarah",
            "Geografi",
            "Ekonomi",
            "Sosiologi"
        )
    )

    // Helper untuk mendapatkan jenjang berdasarkan Fase
    fun getJenjangByFase(faseCode: String): String {
        return when {
            faseCode.contains("A") || faseCode.contains("B") || faseCode.contains("C") -> JENJANG_SD
            faseCode.contains("D") -> JENJANG_SMP
            faseCode.contains("E") || faseCode.contains("F") -> JENJANG_SMA_SMK
            else -> JENJANG_SD // Default
        }
    }
}
