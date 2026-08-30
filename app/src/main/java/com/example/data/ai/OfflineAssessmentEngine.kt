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

        val levelKognitifList = listOf("C4 (Menganalisis)", "C5 (Mengevaluasi)", "C3 (Menerapkan)", "C4 (Menganalisis)", "C6 (Mencipta/Merancang)", "C2 (Memahami)", "C3 (Menerapkan)", "C5 (Mengevaluasi)", "C4 (Menganalisis)", "C6 (Mencipta/Merancang)")

        // Contextual scenario templates to ensure questions are rich and non-repetitive
        val scenarios = listOf(
            Triple(
                "Studi Kasus Kontekstual A (Praktik Lapangan):",
                "Dalam kegiatan observasi lapangan mengenai '$topic' di lingkungan sekolah, kelompok 1 mencatat data kuantitatif yang menunjukkan fluktuasi signifikan antar waktu pengukuran.",
                "Berdasarkan anomali data tersebut, tindakan analitis paling tepat yang harus dilakukan untuk memastikan validitas kesimpulan adalah..."
            ),
            Triple(
                "Studi Kasus Kontekstual B (Eksperimen Komparatif):",
                "Dua metode pendekatan pembelajaran terkait '$topic' diuji coba pada dua kelas paralel. Kelas A menggunakan alat peraga manipulatif, sedangkan Kelas B menggunakan simulasi digital interaktif.",
                "Jika hasil evaluasi menunjukkan kelas A unggul pada aspek pemahaman konsep dasar namun kelas B unggul pada kecepatan aplikasi, analisis evaluatif yang paling objektif adalah..."
            ),
            Triple(
                "Studi Kasus Kontekstual C (Pemecahan Masalah Warga):",
                "Sebuah komunitas warga merancang proyek mini berbasis '$topic' untuk efisiensi energi dan sumber daya lokal. Terdapat perdebatan antara prioritas kecepatan pengerjaan atau ketepatan standar kualitas.",
                "Langkah penerapan strategis yang paling tepat untuk mendamaikan kedua prioritas tersebut dengan kaidah ilmiah adalah..."
            ),
            Triple(
                "Studi Kasus Kontekstual D (Analisis Studi Literatur):",
                "Dalam kajian literatur ilmiah mengenai perkembangan konsep '$topic', ditemukan perbedaan interpretasi antara temuan riset dekade lalu dengan riset modern berbasis teknologi mutakhir.",
                "Sikap kritis dan evaluatif yang seharusnya diambil oleh seorang peneliti muda terhadap perbedaan temuan tersebut adalah..."
            ),
            Triple(
                "Studi Kasus Kontekstual E (Studi Kasus Proyek Kolaboratif):",
                "Tim proyek siswa mengalami kendala ketidaksesuaian antara estimasi waktu perencanaan awal dengan realisasi pengerjaan materi '$topic' di lapangan.",
                "Evaluasi manajerial yang paling tepat untuk memperbaiki efisiensi dan efektivitas kerja tim pada siklus berikutnya adalah..."
            )
        )

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
            pedomanPenskoran = pedoman
        )
    }
}

