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

        val levelKognitifList = listOf("C4 (Menganalisis)", "C5 (Mengevaluasi)", "C3 (Menerapkan)", "C4 (Menganalisis)", "C6 (Mencipta/Merancang)", "C2 (Memahami)", "C4 (Menganalisis)")

        for (i in 1..count) {
            val level = levelKognitifList[(i - 1) % levelKognitifList.size]
            val bentuk = if (i <= (count * 0.6).toInt().coerceAtLeast(2)) "Pilihan Ganda (PG)" else "Uraian Analisis Kasus"

            kisiList.add(
                KisiKisiItem(
                    nomorUrut = i,
                    capaianElemen = "Pemahaman & Penerapan Konsep $subject",
                    materiPokok = "$topic (Sub-Materi $i)",
                    indikatorSoal = "Disajikan stimulus kontekstual berupa studi kasus/data $topic, peserta didik mampu $level untuk memecahkan persoalan dunia nyata.",
                    levelKognitif = level,
                    bentukSoal = bentuk,
                    nomorSoal = i
                )
            )

            if (bentuk.startsWith("Pilihan Ganda")) {
                soalList.add(
                    SoalHotsItem(
                        nomor = i,
                        bentukSoal = "Pilihan Ganda",
                        levelKognitif = level,
                        stimulusText = "Perhatikan narasi kasus berikut:\nDi sebuah lingkungan satuan pendidikan, sekelompok siswa sedang mengamati fenomena terkait '$topic'. Mereka menemukan perbedaan hasil antara kelompok A dan kelompok B saat melakukan pengukuran dan percobaan langsung di lapangan.",
                        pertanyaan = "Berdasarkan data observasi dan konsep dasar $topic, kesimpulan logis yang paling tepat untuk menjelaskan perbedaan tersebut adalah...",
                        pilihanOpsi = listOf(
                            "A. Variabel kontrol tidak dijaga secara konsisten sehingga mempengaruhi kestabilan proses $topic.",
                            "B. Pengaruh faktor eksternal lingkungan diabaikan tanpa pencatatan kondisi awal yang valid.",
                            "C. Terjadi kesalahan sistematik pada alat ukur yang digunakan kedua kelompok.",
                            "D. Konsep dasar yang diterapkan kelompok A bertentangan dengan kaidah ilmiah $topic."
                        ),
                        kunciJawaban = "A",
                        pembahasanDanAlasan = "Pilihan A benar karena dalam penyelidikan ilmiah kontekstual materi $topic, konsistensi variabel kontrol merupakan faktor kunci yang menentukan validitas dan perbandingan hasil yang adil.",
                        skorMaksimal = 10
                    )
                )
            } else {
                soalList.add(
                    SoalHotsItem(
                        nomor = i,
                        bentukSoal = "Uraian Analisis Kasus",
                        levelKognitif = level,
                        stimulusText = "Studi Kasus Kontekstual:\nSebuah komunitas warga menghadapi kendala nyata terkait penerapan '$topic' dalam kehidupan sehari-hari. Beberapa warga berpendapat metode konvensional lebih cepat, sedangkan generasi muda mengusulkan solusi inovatif berbasis prinsip ramah lingkungan dan efisiensi.",
                        pertanyaan = "1) Analisislah 2 kelebihan dan 2 kelemahan dari kedua sudut pandang tersebut berdasarkan prinsip materi $topic!\n2) Berikan rekomendasi solusi kompromi yang aplikatif dan mudah diterapkan oleh seluruh warga!",
                        pilihanOpsi = emptyList(),
                        kunciJawaban = "Pedoman Jawaban Uraian:\n1. Analisis sudut pandang: Menguraikan perbandingan efisiensi waktu vs keberlanjutan jangka panjang materi $topic secara komprehensif.\n2. Rekomendasi solusi: Memberikan langkah aksi terukur yang memadukan kebiasaan warga dengan teknologi/metode baru ramah lingkungan.",
                        pembahasanDanAlasan = "Rubrik Jawaban Uraian:\n• Skor 20: Analisis lengkap 2 sudut pandang + rekomendasi solutif & logis.\n• Skor 15: Analisis lengkap namun rekomendasi kurang spesifik.\n• Skor 10: Analisis hanya 1 sudut pandang.\n• Skor 5: Jawaban sangat terbatas.",
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
