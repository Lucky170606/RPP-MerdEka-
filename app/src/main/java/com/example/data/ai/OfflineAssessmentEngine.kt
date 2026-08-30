package com.example.data.ai

import com.example.data.model.AssessmentDocument
import com.example.data.model.KisiKisiItem
import com.example.data.model.SoalHotsItem

object OfflineAssessmentEngine {

    fun generateAssessment(
        subject: String,
        fase: String,
        grade: String,
        topic: String,
        jenisAsesmen: String,
        semester: String,
        jumlahSoal: Int
    ): AssessmentDocument {
        val count = jumlahSoal.coerceIn(3, 10)

        val kisiList = mutableListOf<KisiKisiItem>()
        val soalList = mutableListOf<SoalHotsItem>()

        // Differentiate cognitive levels and scenarios based on fase, subject, and jenisAsesmen
        val (levelKognitifList, scenarios) = getSubjectSpecificScenarios(subject, fase, topic, jenisAsesmen)

        for (i in 1..count) {
            val level = levelKognitifList[(i - 1) % levelKognitifList.size]
            val bentuk = if (i <= (count * 0.6).toInt().coerceAtLeast(2)) "Pilihan Ganda (PG)" else "Uraian Analisis Kasus"
            val scenario = scenarios[(i - 1) % scenarios.size]

            kisiList.add(
                KisiKisiItem(
                    nomorUrut = i,
                    capaianElemen = "Pemahaman & Penerapan Konseptual $subject ($topic)",
                    materiPokok = "$topic (Fokus Aspek $i)",
                    indikatorSoal = "Disajikan ${scenario.first.lowercase()} terkait $topic, peserta didik mampu $level secara kritis dan solutif.",
                    levelKognitif = level,
                    bentukSoal = bentuk,
                    nomorSoal = i
                )
            )

            if (bentuk.startsWith("Pilihan Ganda")) {
                val correctKey = when (i % 4) {
                    1 -> "A"
                    2 -> "B"
                    3 -> "C"
                    else -> "D"
                }

                val options = listOf(
                    "A. Melakukan kalibrasi ulang instrumen ukur dan mengontrol variabel luar yang dapat mendistorsi hasil eksperimen $topic.",
                    "B. Mengabaikan data outlier yang menyimpang jauh dari rata-rata kelompok tanpa dokumentasi logis.",
                    "C. Mengubah hipotesis awal agar sesuai dengan tren data empiris yang diperoleh di lapangan secara instan.",
                    "D. Menyamakan seluruh parameter pengamatan tanpa memperhitungkan perbedaan karakteristik subjek uji."
                )

                soalList.add(
                    SoalHotsItem(
                        nomor = i,
                        bentukSoal = "Pilihan Ganda",
                        levelKognitif = level,
                        stimulusText = "${scenario.first}\n${scenario.second}\nTopik Kajian: $subject - $topic (Butir Soal #$i)",
                        pertanyaan = scenario.third,
                        pilihanOpsi = options,
                        kunciJawaban = correctKey,
                        pembahasanDanAlasan = "Pembahasan Soal #$i: Pilihan $correctKey adalah jawaban paling tepat karena didasarkan pada prinsip metodologi ilmiah $subject pada materi $topic, di mana pengendalian variabel dan validasi data menjadi pilar utama objektivitas.",
                        skorMaksimal = 10
                    )
                )
            } else {
                soalList.add(
                    SoalHotsItem(
                        nomor = i,
                        bentukSoal = "Uraian Analisis Kasus",
                        levelKognitif = level,
                        stimulusText = "${scenario.first}\nKompleksitas Masalah Uraian #$i untuk materi '$topic' dalam bidang $subject:\n${scenario.second}",
                        pertanyaan = "1) Lakukan analisis kritis terhadap akar permasalahan utama pada kasus $topic di atas dengan menguraikan minimal 2 faktor penyebab utama!\n2) Susunlah rancangan solusi operasional yang inovatif, terukur, dan dapat diimplementasikan secara berkelanjutan!",
                        pilihanOpsi = emptyList(),
                        kunciJawaban = "Pedoman Kunci Jawaban Uraian #$i:\n1. Analisis Faktor Penyebab: Mengidentifikasi faktor teknis dan non-teknis terkait $topic secara komprehensif.\n2. Rancangan Solusi: Menyediakan langkah aksi terstruktur yang memadukan prinsip teoretis $subject dengan kearifan lokal atau teknologi tepat guna.",
                        pembahasanDanAlasan = "Rubrik Penilaian Uraian #$i:\n• Skor 20: Analisis sangat tajam (2+ faktor) + Solusi inovatif dan terukur.\n• Skor 15: Analisis cukup mendalam namun solusi kurang operasional.\n• Skor 10: Analisis terbatas pada permukaan masalah.\n• Skor 5: Jawaban kurang relevan dengan esensi $topic.",
                        skorMaksimal = 20
                    )
                )
            }
        }

        val pedoman = """
        PEDOMAN PENSKORAN & PENILAIAN NILAI AKHIR (NA):
        1. Nilai Soal Pilihan Ganda = (Jumlah Benar / Jumlah Soal PG) x 100
        2. Nilai Soal Uraian = (Total Skor Perolehan Uraian / Total Skor Maksimal Uraian) x 100
        3. Nilai Akhir (NA) = (60% x Nilai PG) + (40% x Nilai Uraian)
        
        Kriteria Ketercapaian Tujuan Pembelajaran (KKTP):
        • 0 - 65%   : Belum Mencapai TP (Perlu Remedial di seluruh materi)
        • 66 - 75%  : Cukup Mencapai TP (Perlu Remedial di indikator yang belum tuntas)
        • 76 - 88%  : Sudah Mencapai TP (Tuntas)
        • 89 - 100% : Sangat Mahir Mencapai TP (Diberikan materi Pengayaan/Tantangan HOTS Lanjut)
        """.trimIndent()

        return AssessmentDocument(
            title = "Kisi-Kisi & Bank Soal Asesmen: $subject - $topic",
            subject = subject,
            fase = fase,
            grade = grade,
            semester = semester,
            topikUjian = topic,
            jenisAsesmen = jenisAsesmen,
            jumlahSoal = count,
            kisiKisiList = kisiList,
            soalList = soalList,
            pedomanPenskoran = pedoman,
            isOnlineAiGenerated = false,
            engineName = "Engine Standar Kurikulum (Offline)"
        )
    }

    private fun getSubjectSpecificScenarios(subject: String, fase: String, topic: String, jenisAsesmen: String): Pair<List<String>, List<Triple<String, String, String>>> {
        val (baseLevels, scenarios) = when (subject) {
            "Matematika", "Fisika", "Kimia", "Biologi" -> when (fase) {
                "Fase F" -> Pair(
                    listOf("C4 (Menganalisis)", "C5 (Mengevaluasi)", "C6 (Mencipta)"),
                    listOf(
                        Triple("Analisis Konseptual:", "Dalam model $topic, variabel yang saling bergantung menunjukkan tren nonlinear.", "Bagaimana cara melakukan optimasi terhadap model tersebut untuk hasil maksimal?"),
                        Triple("Evaluasi Penerapan:", "$topic diterapkan dalam teknologi modern.", "Uraikan efisiensi dari model tersebut dalam konteks nyata!"),
                        Triple("Kreasi Model:", "Fenomena $topic di dunia nyata.", "Rancanglah sebuah model untuk memprediksi perubahan $topic!")
                    )
                )
                else -> Pair(
                    listOf("C2 (Memahami)", "C3 (Menerapkan)"),
                    listOf(
                        Triple("Penerapan Dasar:", "Rumus $topic digunakan pada situasi X.", "Hitunglah nilai berdasarkan $topic!"),
                        Triple("Eksplorasi:", "Pola $topic pada angka.", "Sebutkan pola yang terbentuk pada $topic!")
                    )
                )
            }
            "Bahasa Indonesia", "Bahasa Inggris" -> when (fase) {
                "Fase F" -> Pair(
                    listOf("C4 (Menganalisis)", "C5 (Mengevaluasi)", "C6 (Mencipta)"),
                    listOf(
                        Triple("Analisis Kritis:", "Sebuah teks mengenai $topic.", "Identifikasi bias atau sudut pandang penulis dalam teks tersebut!"),
                        Triple("Evaluasi Estetika:", "Karya sastra bertema $topic.", "Evaluasilah gaya bahasa dan pesan moral yang terkandung di dalamnya!"),
                        Triple("Kreasi Teks:", "Topik $topic.", "Buatlah sebuah esai kritis yang mengulas fenomena $topic!")
                    )
                )
                else -> Pair(
                    listOf("C2 (Memahami)", "C3 (Menerapkan)"),
                    listOf(
                        Triple("Pemahaman:", "Teks tentang $topic.", "Apa gagasan utama dari teks tersebut?"),
                        Triple("Penerapan:", "$topic dalam percakapan.", "Bagaimana cara menggunakan ungkapan $topic dalam konteks yang tepat?")
                    )
                )
            }
            "IPAS", "IPA", "IPS", "Sejarah", "Geografi", "Ekonomi", "Sosiologi", "Pendidikan Pancasila" -> when (fase) {
                "Fase F", "Fase E" -> Pair(
                    listOf("C4 (Menganalisis)", "C5 (Mengevaluasi)"),
                    listOf(
                        Triple("Analisis Fenomena:", "Data perubahan $topic dalam masyarakat/alam.", "Mengapa $topic tersebut berpengaruh signifikan?"),
                        Triple("Evaluasi Sosial/Sains:", "Penerapan kebijakan/metode $topic.", "Apakah hasil $topic sudah tepat guna? Analisis faktor-faktor penyebabnya!")
                    )
                )
                else -> Pair(
                    listOf("C2 (Memahami)", "C3 (Menerapkan)"),
                    listOf(
                        Triple("Pengenalan:", "Pengamatan $topic di lingkungan.", "Apa peran $topic dalam kehidupan?"),
                        Triple("Aplikasi:", "Percobaan/Studi kasus $topic sederhana.", "Bagaimana cara kerja $topic tersebut?")
                    )
                )
            }
            else -> Pair(
                listOf("C1 (Mengingat)", "C2 (Memahami)", "C3 (Menerapkan)"),
                listOf(
                    Triple("Pengenalan:", "Sebuah pengenalan dasar mengenai '$topic'.", "Apa pengertian dasar dari '$topic'?"),
                    Triple("Eksplorasi:", "Eksplorasi sederhana tentang '$topic'.", "Sebutkan contoh penerapan '$topic' di sekitar kita!"),
                    Triple("Aplikasi Dasar:", "Menerapkan '$topic' dalam kegiatan sederhana.", "Bagaimana cara melakukan '$topic' dengan benar?")
                )
            )
        }

        val adjustedLevels = if (jenisAsesmen.contains("Formatif")) {
            baseLevels.filter { it.contains("C1") || it.contains("C2") || it.contains("C3") }
                .ifEmpty { listOf("C1 (Mengingat)", "C2 (Memahami)", "C3 (Menerapkan)") }
        } else {
            baseLevels
        }

        return Pair(adjustedLevels, scenarios)
    }
}

