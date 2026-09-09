package com.example.data.model

object CurriculumConstants {
    // Definisi jenjang
    val JENJANG_SD = "SD"
    val JENJANG_SMP = "SMP"
    val JENJANG_SMA_SMK = "SMA/SMK"

    // Mapping Mata Pelajaran per Jenjang (Umum)
    val MATA_PELAJARAN_UMUM = mapOf(
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

    val MATA_PELAJARAN_MADRASAH = mapOf(
        JENJANG_SD to listOf(
            "Al-Qur'an Hadis",
            "Akidah Akhlak",
            "Fikih",
            "Sejarah Kebudayaan Islam (SKI)",
            "Bahasa Arab"
        ),
        JENJANG_SMP to listOf(
            "Al-Qur'an Hadis",
            "Akidah Akhlak",
            "Fikih",
            "Sejarah Kebudayaan Islam (SKI)",
            "Bahasa Arab"
        ),
        JENJANG_SMA_SMK to listOf(
            "Al-Qur'an Hadis",
            "Akidah Akhlak",
            "Fikih",
            "Sejarah Kebudayaan Islam (SKI)",
            "Bahasa Arab",
            "Ushul Fikih",
            "Ilmu Tafsir",
            "Ilmu Hadis"
        )
    )

    fun getSubjects(jenjang: String, isMadrasah: Boolean): List<String> {
        val umum = MATA_PELAJARAN_UMUM[jenjang] ?: emptyList()
        if (!isMadrasah) return umum
        val filteredUmum = umum.filter { !it.contains("Pendidikan Agama Islam") }
        val madrasah = MATA_PELAJARAN_MADRASAH[jenjang] ?: emptyList()
        return filteredUmum + madrasah
    }

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
