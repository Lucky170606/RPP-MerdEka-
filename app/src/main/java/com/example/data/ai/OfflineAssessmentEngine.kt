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
        jumlahSoal: Int,
        pgRatioPercent: Int = 60 // Fleksibel: persentase soal PG (default 60%, sisanya Uraian)
    ): AssessmentDocument {
        val count = jumlahSoal.coerceIn(3, 20)
        val pgCount = ((count * pgRatioPercent) / 100).coerceIn(1, count - 1).let { if (count == 3) 2 else it }
        val uraianCount = count - pgCount

        val kisiList = mutableListOf<KisiKisiItem>()
        val soalList = mutableListOf<SoalHotsItem>()

        val (levelKognitifList, scenarios) = getSubjectSpecificScenarios(subject, fase, topic, jenisAsesmen)

        for (i in 1..count) {
            val level = levelKognitifList[(i - 1) % levelKognitifList.size]
            val bentuk = if (i <= pgCount) "Pilihan Ganda (PG)" else "Uraian Analisis Kasus"
            val scenario = scenarios[(i - 1) % scenarios.size]

            val capaianElemen = getCapaianPembelajaranForSubject(subject, fase, topic)

            kisiList.add(
                KisiKisiItem(
                    nomorUrut = i,
                    capaianElemen = capaianElemen,
                    materiPokok = "$topic (Sub-Elemen Pengujian #${i})",
                    indikatorSoal = "Disajikan ${scenario.first.lowercase()} berkaitan dengan $topic pada konteks nyata $subject, peserta didik mampu $level secara kritis, analitis, dan solutif.",
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
                    "A. Melakukan analisis mendalam terhadap parameter $topic serta mengendalikan variabel pengganggu untuk memastikan validitas kesimpulan.",
                    "B. Mengabaikan anomali data empiris yang muncul di lapangan tanpa melakukan verifikasi ulang secara metodologis.",
                    "C. Mengubah asumsi dasar secara sepihak agar hasil perhitungan sesuai dengan estimasi awal tanpa dasar teoretis.",
                    "D. Menyamakan seluruh variabel pengamatan tanpa memperhitungkan perbedaan karakteristik kondisi dan lingkungan."
                )

                soalList.add(
                    SoalHotsItem(
                        nomor = i,
                        bentukSoal = "Pilihan Ganda",
                        levelKognitif = level,
                        stimulusText = "Ilustrasi Kontekstual & Studi Kasus Pembelajaran:\n${scenario.first}\n${scenario.second}\nFokus Kajian Materi: $subject - $topic (Butir Soal Nomor $i)",
                        pertanyaan = scenario.third,
                        pilihanOpsi = options,
                        kunciJawaban = correctKey,
                        pembahasanDanAlasan = "Analisis dan Pembahasan Soal #${i}: Pilihan $correctKey adalah jawaban paling tepat karena mencerminkan penalaran tingkat tinggi (HOTS) berbasis prinsip keilmuan $subject, di mana pemecahan masalah menuntut ketepatan analisis, verifikasi data, dan logika berpikir logis.",
                        skorMaksimal = 5
                    )
                )
            } else {
                soalList.add(
                    SoalHotsItem(
                        nomor = i,
                        bentukSoal = "Uraian Analisis Kasus",
                        levelKognitif = level,
                        stimulusText = "Kompleksitas Studi Kasus Uraian Mendalam #${i}:\n${scenario.first}\n${scenario.second}\nDalam konteks penerapan Kurikulum Merdeka pada mata pelajaran $subject materi '$topic', dituntut kemampuan berpikir kritis tingkat tinggi.",
                        pertanyaan = "1) Uraikan analisis kritis mengenai faktor pendorong serta hambatan utama yang dihadapi dalam penerapan konsep $topic pada kasus di atas!\n2) Rumuskan rancangan tindakan atau solusi inovatif yang terukur beserta argumen ilmiah yang mendasarinya!",
                        pilihanOpsi = emptyList(),
                        kunciJawaban = "Pedoman Kunci Jawaban Uraian #${i}:\n1. Analisis Komprehensif: Menguraikan minimal 2 faktor pendukung dan 2 kendala faktual secara sistematis.\n2. Solusi Inovatif: Menyusun langkah taktis operasional yang memadukan teori $subject dengan konteks nyata kehidupan peserta didik.",
                        pembahasanDanAlasan = "Rubrik Penilaian Penskoran Uraian #${i}:\n• Skor 20-25: Menunjukkan analisis sangat tajam, komprehensif, dan menyajikan solusi solutif yang aplikatif.\n• Skor 15-19: Analisis cukup mendalam namun penjelasan solusi kurang terperinci.\n• Skor 10-14: Analisis terbatas pada permukaan masalah.\n• Skor 5-9: Jawaban kurang relevan dengan substansi $topic.",
                        skorMaksimal = 20
                    )
                )
            }
        }

        val totalPgScore = pgCount * 5
        val totalUraianScore = uraianCount * 20
        val maxTotalScore = totalPgScore + totalUraianScore

        val pedoman = """
        PEDOMAN PENSKORAN & ANALISIS HASIL ASESMEN KURIKULUM MERDEKA:
        1. Bobot Soal: Pilihan Ganda (5 poin/butir), Uraian Analisis Kasus (20 poin/butir).
        2. Rumus Penghitungan Nilai Akhir (NA): 
           NA = (Total Perolehan Skor Siswa / $maxTotalScore) x 100
        3. Proporsi Butir Soal: $pgCount Pilihan Ganda ($pgRatioPercent%) & $uraianCount Uraian Analisis (${100 - pgRatioPercent}%).
        
        Kriteria Ketercapaian Tujuan Pembelajaran (KKTP):
        • 0 - 64   : Belum Tercapai (Wajib Remedial Komprehensif)
        • 65 - 75  : Cukup Tercapai (Remedial pada Indikator Tertentu)
        • 76 - 88  : Sudah Tercapai / Tuntas (Kompetensi Baik)
        • 89 - 100 : Sangat Mahir / Istimewa (Diberikan Tantangan Pengayaan HOTS Lanjut)
        """.trimIndent()

        return AssessmentDocument(
            title = "Kisi-Kisi & Bank Soal HOTS: $subject - $topic",
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
            engineName = "Offline Smart Engine (Kurikulum Merdeka v3)"
        )
    }

    private fun getCapaianPembelajaranForSubject(subject: String, fase: String, topic: String): String {
        return when (subject) {
            "Matematika" -> "Peserta didik dapat menggeneralisasi pemahaman $topic melalui penalaran proporsional, pemecahan masalah numerik, dan pemodelan matematis."
            "Bahasa Indonesia" -> "Peserta didik mampu mengevaluasi gagasan, informasi, dan pesan dari teks multimodal bertema $topic secara kritis dan kreatif."
            "Bahasa Inggris" -> "Students are able to analyze, interpret, and respond to spoken and written transactional texts regarding $topic effectively."
            "IPAS", "IPA" -> "Peserta didik mampu melakukan penyelidikan ilmiah, menganalisis gejala alam dan sosial terkait $topic, serta merumuskan argumen berbasis bukti."
            "IPS", "Sejarah", "Geografi", "Ekonomi", "Sosiologi" -> "Peserta didik memahami pola interaksi sosial, ekonomi, dan spasial yang berkaitan dengan $topic dalam dinamika masyarakat."
            "Pendidikan Pancasila" -> "Peserta didik menunjukkan kesadaran nilai-nilai Pancasila dan kewarganegaraan dalam menganalisis kasus $topic."
            else -> "Peserta didik menguasai konsep esensial, menganalisis studi kasus, dan mengaplikasikan pemecahan masalah pada materi $topic."
        }
    }

    private fun getSubjectSpecificScenarios(subject: String, fase: String, topic: String, jenisAsesmen: String): Pair<List<String>, List<Triple<String, String, String>>> {
        val (baseLevels, scenarios) = when (subject) {
            "Matematika", "Fisika", "Kimia", "Biologi" -> when (fase) {
                "Fase F", "Fase E" -> Pair(
                    listOf("C4 (Menganalisis)", "C5 (Mengevaluasi)", "C6 (Mencipta)"),
                    listOf(
                        Triple("Eksperimen Laboratorium:", "Sebuah percobaan pengukuran fenomena $topic menghasilkan variasi data yang dipengaruhi oleh suhu dan tekanan.", "Bagaimana cara mengevaluasi galat pengukuran agar diperoleh nilai konstanta yang valid?"),
                        Triple("Optimasi Sistem:", "Penerapan model matematika pada sistem $topic menunjukkan grafik non-linear dengan titik balik maksimum.", "Tentukan langkah analitis untuk menentukan nilai optimal dari sistem tersebut!"),
                        Triple("Rancangan Pemodelan:", "Dibutuhkan rekayasa sistem untuk mengontrol laju perubahan $topic dalam skala industri.", "Rancanglah persamaan matematis atau skema eksperimen yang paling efektif!")
                    )
                )
                else -> Pair(
                    listOf("C2 (Memahami)", "C3 (Menerapkan)", "C4 (Menganalisis)"),
                    listOf(
                        Triple("Studi Kasus Kontekstual:", "Koperasi sekolah mencatat distribusi pembagian benda pada materi $topic untuk sejumlah kelompok belajar.", "Berapakah proporsi bagian yang diterima oleh masing-masing kelompok secara adil?"),
                        Triple("Penerapan Pola Bilangan:", "Pola pertumbuhan objek pada materi $topic mengikuti urutan tertentu setiap pekannya.", "Analisis berapa jumlah akumulasi objek pada pekan kelima!"),
                        Triple("Pemecahan Masalah Sehari-hari:", "Sebuah wadah penampungan air mengalami kebocoran yang berkaitan dengan konsep $topic.", "Bagaimana cara menghitung sisa volume air secara akurat?")
                    )
                )
            }
            "Bahasa Indonesia", "Bahasa Inggris" -> when (fase) {
                "Fase F", "Fase E" -> Pair(
                    listOf("C4 (Menganalisis)", "C5 (Mengevaluasi)", "C6 (Mencipta)"),
                    listOf(
                        Triple("Analisis Wacana Kritis:", "Editorial berita mengangkat isu krusial mengenai dampak sosial dari $topic di era digital.", "Identifikasi asumsi terselubung dan bias argumentasi penulis dalam artikel tersebut!"),
                        Triple("Evaluasi Struktur Teks:", "Sebuah karya sastra/naskah argumentatif membahas polemik seputar $topic.", "Evaluasilah kohesi, koherensi, serta kekuatan diksi yang digunakan!"),
                        Triple("Produksi Teks Kreatif:", "Diperlukan kampanye literasi publik bertema $topic untuk meningkatkan kesadaran masyarakat.", "Susunlah draf teks persuasi atau resensi kritis yang memuat argumen kuat!")
                    )
                )
                else -> Pair(
                    listOf("C2 (Memahami)", "C3 (Menerapkan)", "C4 (Menganalisis)"),
                    listOf(
                        Triple("Pemahaman Pesan Teks:", "Sebuah cerita naratif pendek menceritakan petualangan tokoh yang mempelajari nilai dari $topic.", "Apa pesan moral utama yang ingin disampaikan pengarang kepada pembaca?"),
                        Triple("Penggunaan Kosakata:", "Dalam percakapan sehari-hari mengenai $topic, ditemukan beberapa istilah khusus.", "Bagaimana makna istilah tersebut dalam konteks kalimat yang disediakan?"),
                        Triple("Penyusunan Kalimat Efektif:", "Terdapat beberapa kalimat acak mengenai topik $topic yang belum tersusun padu.", "Susunlah kalimat-kalimat tersebut menjadi paragraf deskripsi yang runtut!")
                    )
                )
            }
            else -> Pair(
                listOf("C3 (Menerapkan)", "C4 (Menganalisis)", "C5 (Mengevaluasi)"),
                listOf(
                    Triple("Fenomena Sosial/Alam:", "Perubahan dinamika lingkungan dan masyarakat terkait isu $topic memicu berbagai perdebatan.", "Analisis faktor utama yang menjadi pemicu utama fenomena tersebut!"),
                    Triple("Evaluasi Kebijakan/Praktik:", "Penerapan suatu metode atau aturan baru dalam ruang lingkup $topic diuji coba di beberapa daerah.", "Bagaimana efektivitas kebijakan tersebut berdasarkan indikator keberhasilan?"),
                    Triple("Studi Kasus Komunitas:", "Sebuah komunitas lokal melakukan inovasi berbasis kearifan lokal dalam mengelola $topic.", "Apa pelajaran bermakna yang dapat diadopsi untuk skala yang lebih luas?")
                )
            )
        }

        val adjustedLevels = if (jenisAsesmen.contains("Formatif")) {
            baseLevels.filter { !it.contains("C6") }
        } else {
            baseLevels
        }

        return Pair(adjustedLevels, scenarios)
    }
}
