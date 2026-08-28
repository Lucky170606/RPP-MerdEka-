package com.example.data.ai

import com.example.data.model.*

object AdvancedCurriculumEngine {

    fun generateProta(
        subject: String,
        fase: String,
        grade: String,
        academicYear: String
    ): ProtaDocument {
        val topicsGanjil = when (subject) {
            "Matematika" -> listOf(
                "Bilangan Cacah sampai 10.000 (Operasi Hitung & Nilai Tempat)" to 24,
                "Pecahan Senilai, Desimal, dan Persen" to 20,
                "Pola Gambar dan Pola Bilangan" to 16,
                "Pengukuran Panjang, Berat, dan Waktu" to 12
            )
            "IPAS" -> listOf(
                "Tumbuhan Sumber Kehidupan di Bumi (Fotosintesis & Bagian Tumbuhan)" to 20,
                "Wujud Zat dan Perubahannya (Padat, Cair, Gas, Kalor)" to 20,
                "Gaya di Sekitar Kita (Gaya Gesek, Magnet, Gravitasi, Pegas)" to 18,
                "Mengubah Bentuk Energi (Energi Kinetik, Potensial, Listrik, Terbarukan)" to 14
            )
            "Bahasa Indonesia" -> listOf(
                "Membaca Teks Narasi & Menemukan Ide Pokok" to 20,
                "Menulis Surat Pribadi & Teks Petunjuk" to 18,
                "Wawancara & Laporan Pengamatan Sederhana" to 18,
                "Puisi dan Cerita Rakyat Kontekstual" to 16
            )
            "Pendidikan Pancasila" -> listOf(
                "Pancasila Sebagai Panduan Hidup & Nilai Sila" to 18,
                "Konstitusi dan Norma di Lingkungan Sekolah & Masyarakat" to 18,
                "Membangun Jati Diri dalam Kebhinekaan" to 18,
                "Negara Kesatuan Republik Indonesia & Cinta Tanah Air" to 18
            )
            "Informatika" -> listOf(
                "Berpikir Komputasional (Dekomposisi, Pola, Abstraksi, Algoritma)" to 20,
                "Teknologi Informasi & Komunikasi (Aplikasi Perkantoran)" to 18,
                "Sistem Komputer & Perangkat Keras/Lunak" to 18,
                "Dampak Sosial Informatika & Etika Digital" to 16
            )
            else -> listOf(
                "Konsep Dasar & Pengantar $subject" to 20,
                "Eksplorasi Kontekstual $subject" to 20,
                "Penerapan & Studi Kasus Dunia Nyata" to 18,
                "Projek Kolaboratif & Evaluasi Akhir" to 14
            )
        }

        val topicsGenap = when (subject) {
            "Matematika" -> listOf(
                "Keliling dan Luas Bangun Datar (Persegi, Persegi Panjang, Segitiga)" to 20,
                "Sudut, Garis Sejajar, dan Hubungan Antargaris" to 16,
                "Penyajian dan Analisis Data (Tabel, Diagram Batang, Piktogram)" to 20,
                "Peluang Sederhana & Eksperimen Statistika" to 16
            )
            "IPAS" -> listOf(
                "Cerita Tentang Daerahku (Sejarah Lokal & Keragaman Budaya)" to 20,
                "Indonesiaku Kaya Budaya (Kearifan Lokal & Pelestarian Tradisi)" to 18,
                "Bagaimana Mendapatkan Semua Kebutuhan Kita? (Ekonomi & Pasar)" to 18,
                "Membangun Masyarakat yang Beradab & Peduli Lingkungan" to 16
            )
            "Bahasa Indonesia" -> listOf(
                "Teks Eksposisi & Argumentasi Berbasis Fakta" to 20,
                "Menyimak dan Menyajikan Pidato Singkat" to 18,
                "Membaca Kritis Teks Eksplanasi Ilmiah Populer" to 18,
                "Menulis Teks Narasi Kreatif & Reflektif" to 16
            )
            "Pendidikan Pancasila" -> listOf(
                "Musyawarah untuk Mufakat dalam Kehidupan Sehari-hari" to 18,
                "Hak dan Kewajiban Warga Negara Secara Adil" to 18,
                "Gotong Royong dalam Keberagaman Budaya Nusantara" to 18,
                "Menjaga Keutuhan NKRI & Ketertiban Sosial" to 18
            )
            "Informatika" -> listOf(
                "Jaringan Komputer dan Internet (Topologi & Keamanan Data)" to 20,
                "Analisis Data dan Visualisasi Spreadsheet" to 18,
                "Algoritma Pemrograman Visual (Scratch/Blockly/Python)" to 20,
                "Projek Praktik Lintas Bidang (PLB)" to 14
            )
            else -> listOf(
                "Pengembangan Lanjut Materi $subject" to 20,
                "Kajian Analisis Terpadu $subject" to 20,
                "Investigasi Masalah & Problem Solving" to 18,
                "Gelar Karya & Penilaian Sumatif Akhir Fase" to 14
            )
        }

        var itemNo = 1
        val itemsList = mutableListOf<ProtaItem>()

        topicsGanjil.forEach { (materi, jp) ->
            itemsList.add(
                ProtaItem(
                    nomor = itemNo++,
                    semester = "Semester 1 (Ganjil)",
                    babMateri = materi,
                    capaianTujuan = "Peserta didik mampu memahami, menganalisis, dan mengaplikasikan konsep $materi secara bermakna melalui investigasi kontekstual.",
                    alokasiJp = jp
                )
            )
        }

        topicsGenap.forEach { (materi, jp) ->
            itemsList.add(
                ProtaItem(
                    nomor = itemNo++,
                    semester = "Semester 2 (Genap)",
                    babMateri = materi,
                    capaianTujuan = "Peserta didik mampu merancang, memecahkan masalah, dan mengomunikasikan pemahaman terkait $materi dalam kehidupan bermasyarakat.",
                    alokasiJp = jp
                )
            )
        }

        val totalJp = itemsList.sumOf { it.alokasiJp }

        return ProtaDocument(
            title = "Program Tahunan (PROTA) - $subject",
            subject = subject,
            fase = fase,
            grade = grade,
            academicYear = academicYear,
            totalJp = totalJp,
            items = itemsList
        )
    }

    fun generatePromes(
        subject: String,
        fase: String,
        grade: String,
        semester: String,
        academicYear: String
    ): PromesDocument {
        val isGanjil = semester.contains("1") || semester.lowercase().contains("ganjil")
        val months = if (isGanjil) listOf("Juli", "Agustus", "September", "Oktober", "November", "Desember")
        else listOf("Januari", "Februari", "Maret", "April", "Mei", "Juni")

        val prota = generateProta(subject, fase, grade, academicYear)
        val filteredProta = prota.items.filter {
            if (isGanjil) it.semester.contains("Ganjil") else it.semester.contains("Genap")
        }

        val promesItems = filteredProta.mapIndexed { index, pItem ->
            val weeklyDistribution = months.mapIndexed { mIndex, monthName ->
                // Distribute JP logically across weeks (4 weeks per month)
                val weeks = when {
                    index == 0 && (mIndex == 0 || mIndex == 1) -> listOf(4, 4, 4, 0)
                    index == 1 && (mIndex == 1 || mIndex == 2) -> listOf(0, 4, 4, 4)
                    index == 2 && (mIndex == 3 || mIndex == 4) -> listOf(4, 4, 4, 0)
                    index == 3 && (mIndex == 4 || mIndex == 5) -> listOf(0, 4, 4, 2)
                    else -> listOf(0, 0, 0, 0)
                }
                PromesWeeklyMatrix(bulan = monthName, weeks = weeks)
            }

            PromesItem(
                nomor = index + 1,
                materiPokok = pItem.babMateri,
                tujuanPembelajaran = pItem.capaianTujuan,
                alokasiJp = pItem.alokasiJp,
                weeklyDistribution = weeklyDistribution
            )
        }

        val totalJp = promesItems.sumOf { it.alokasiJp }

        return PromesDocument(
            title = "Program Semester (PROMES) - $subject (${if (isGanjil) "Semester 1" else "Semester 2"})",
            subject = subject,
            fase = fase,
            grade = grade,
            semester = if (isGanjil) "Semester 1 (Ganjil)" else "Semester 2 (Genap)",
            academicYear = academicYear,
            totalJp = totalJp,
            items = promesItems
        )
    }

    fun generateAtp(
        subject: String,
        fase: String,
        grade: String
    ): AtpDocument {
        val steps = mutableListOf<AtpStepItem>()

        val rawData = when (subject) {
            "Matematika" -> listOf(
                Triple("Bilangan", "Memahami nilai tempat bilangan cacah sampai 10.000 dan operasinya", 18),
                Triple("Bilangan", "Membandingkan, mengurutkan, dan menjumlahkan pecahan senilai", 18),
                Triple("Aljabar", "Mengidentifikasi, meniru, dan mengembangkan pola bilangan membesar dan mengecil", 16),
                Triple("Pengukuran", "Mengukur panjang, luas, dan volume dengan satuan baku", 18),
                Triple("Geometri", "Mendeskripsikan ciri bangun datar dan mengelompokkannya", 16),
                Triple("Analisis Data & Peluang", "Menyajikan dan menginterpretasikan data dalam diagram batang", 16)
            )
            "IPAS" -> listOf(
                Triple("Pemahaman IPAS", "Menganalisis hubungan antara bentuk serta fungsi bagian tubuh pada tumbuhan", 18),
                Triple("Pemahaman IPAS", "Mengidentifikasi wujud zat dan mendeskripsikan perubahan wujud zat akibat kalor", 18),
                Triple("Keterampilan Proses", "Melakukan penyelidikan pengaruh gaya terhadap gerak dan bentuk benda", 16),
                Triple("Pemahaman IPAS", "Mengidentifikasi ragam sumber energi dan transformasinya dalam kehidupan", 18),
                Triple("Pemahaman IPAS", "Menganalisis peranan manusia dalam menjaga kelestarian ekosistem lokal", 16),
                Triple("Keterampilan Proses", "Mengembangkan peta konsep pemanfaatan sumber daya alam daerah secara bijak", 16)
            )
            else -> listOf(
                Triple("Elemen Fondasi", "Memahami prinsip dasar keilmuan $subject secara analitis", 20),
                Triple("Elemen Inti", "Menerapkan metodologi dan penalaran kritis dalam studi kasus $subject", 20),
                Triple("Elemen Aplikasi", "Merancang solusi praktis atas permasalahan kontekstual di lingkungan sekitar", 20),
                Triple("Elemen Reflektif", "Mengevaluasi hasil karya dan mempresentasikan temuan secara komunikatif", 20)
            )
        }

        val p3Tags = listOf("Bernalar Kritis, Mandiri", "Gotong Royong, Kreatif", "Bernalar Kritis, Kreatif", "Mandiri, Berakhlak Mulia")

        rawData.forEachIndexed { idx, item ->
            val p3 = p3Tags[idx % p3Tags.size]
            val kode = "${fase.replace("Fase ", "")}.${idx + 1}"
            steps.add(
                AtpStepItem(
                    nomorUrut = kode,
                    elemen = item.first,
                    capaianPembelajaran = "Peserta didik menguasai domain '${item.first}' untuk menganalisis fenomena nyata sesuai tingkat perkembangan $grade.",
                    tujuanPembelajaran = "$kode. ${item.second}",
                    materiPokok = item.second.split(" ").take(4).joinToString(" "),
                    alokasiJp = item.third,
                    profilPancasila = p3,
                    indikatorKetercapaian = "Peserta didik dapat membuktikan penguasaan konsep melalui asesmen unjuk kerja dan tes tertulis HOTS secara mandiri."
                )
            )
        }

        return AtpDocument(
            title = "Alur Tujuan Pembelajaran (ATP) - $subject $fase",
            subject = subject,
            fase = fase,
            grade = grade,
            totalJp = steps.sumOf { it.alokasiJp },
            rasional = "Penyusunan Alur Tujuan Pembelajaran (ATP) $subject ini disusun secara linear dan hierarkis berdasarkan tingkat kesulitan dan kebutuhan prasyarat kognitif peserta didik, memastikan pembelajaran bermakna (meaningful learning) dan berkesinambungan.",
            karakteristikMataPelajaran = "Mata pelajaran $subject membekali peserta didik dengan kecakapan literasi, numerasi, nalar kritis, dan kemampuan pemecahan masalah (problem solving) berlandaskan nilai-nilai Profil Pelajar Pancasila.",
            alurTujuanList = steps
        )
    }

    fun generateRaporSentence(namaSiswa: String, nilai: Int, materiTinggi: String, materiRendah: String): String {
        val nama = namaSiswa.ifBlank { "Ananda" }
        return when {
            nilai >= 88 -> {
                "Menunjukkan penguasaan yang sangat istimewa dan mahir dalam $materiTinggi; serta telah menguasai dengan sangat baik materi $materiRendah."
            }
            nilai >= 76 -> {
                "Menunjukkan pemahaman yang baik dan tuntas dalam $materiTinggi; perlu sedikit bimbingan dan pembiasaan latihan pada materi $materiRendah."
            }
            nilai >= 65 -> {
                "Cukup memahami materi $materiTinggi; namun perlu pendampingan dan bimbingan lebih intensif dalam menguasai kompetensi $materiRendah."
            }
            else -> {
                "Belum mencapai kriteria ketuntasan minimal tujuan pembelajaran; memerlukan program bimbingan remedial khusus pada materi $materiTinggi dan $materiRendah."
            }
        }
    }

    val DEFAULT_KKTP_INTERVALS = listOf(
        KktpInterval("0% - 65%", "Belum Tuntas (Perlu Bimbingan)", "Peserta didik belum mencapai kriteria tujuan pembelajaran", "Diberikan remedial pada seluruh materi"),
        KktpInterval("66% - 75%", "Cukup Tuntas (Berkembang)", "Peserta didik cukup menguasai sebagian besar indikator", "Diberikan bimbingan pada indikator yang belum dipahami"),
        KktpInterval("76% - 88%", "Tuntas (Mahir)", "Peserta didik telah mencapai tujuan pembelajaran secara memuaskan", "Melanjutkan materi berikutnya tanpa remedial"),
        KktpInterval("89% - 100%", "Sangat Mahir (Istimewa)", "Peserta didik menguasai seluruh indikator dengan sangat cermat & analitis", "Diberikan materi pengayaan & tantangan HOTS lanjutan")
    )

    val DEFAULT_PEER_QUESTIONS = listOf(
        PeerAssessmentQuestion(1, "Teman saya aktif berdiskusi dan memberikan pendapat saat kerja kelompok.", "Gotong Royong"),
        PeerAssessmentQuestion(2, "Teman saya menghargai pendapat dan tidak memotong pembicaraan teman lain.", "Kebhinekaan Global"),
        PeerAssessmentQuestion(3, "Teman saya bertanggung jawab menyelesaikan tugas bagiannya dengan tepat waktu.", "Mandiri"),
        PeerAssessmentQuestion(4, "Teman saya menyampaikan ide-ide kreatif dan solutif untuk menyelesaikan tugas.", "Kreatif & Bernalar Kritis"),
        PeerAssessmentQuestion(5, "Teman saya bersikap jujur dan tidak memaksakan kehendak pada kelompok.", "Beriman & Berakhlak Mulia")
    )
}
