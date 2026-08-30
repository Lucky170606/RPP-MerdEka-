package com.example.data.ai

import com.example.data.model.*

object AdvancedCurriculumEngine {

    fun generateProta(
        subject: String,
        fase: String,
        grade: String,
        academicYear: String
    ): ProtaDocument {
        android.util.Log.d("CurriculumEngine", "generateProta: subject=$subject, fase=$fase, grade=$grade")
        
        val isFaseA = fase == "Fase A"
        val isFaseB = fase == "Fase B"
        val isFaseC = fase == "Fase C"
        val isSmp = fase == "Fase D"
        val isSmaSmk = fase == "Fase E" || fase == "Fase F"
        
        android.util.Log.d("CurriculumEngine", "isFaseA=$isFaseA, isFaseB=$isFaseB, isFaseC=$isFaseC, isSmp=$isSmp, isSmaSmk=$isSmaSmk")

        val topicsGanjil = when (subject) {
            "Matematika" -> when {
                isSmaSmk -> listOf(
                    "Eksponen dan Logaritma (Sifat & Penerapan)" to 24,
                    "Barisan dan Deret Aritmatika serta Geometri" to 20,
                    "Trigonometri Dasar (Perbandingan & Sudut Istimewa)" to 18,
                    "Sistem Persamaan dan Pertidaksamaan Linear" to 14
                )
                isSmp -> listOf(
                    "Bilangan Berpangkat dan Bentuk Akar" to 22,
                    "Teorema Pythagoras dan Penerapannya" to 20,
                    "Persamaan dan Pertidaksamaan Linear Satu Variabel" to 18,
                    "Relasi, Fungsi, dan Grafik Koordinat Cartesius" to 16
                )
                isFaseC -> listOf(
                    "Operasi Hitung Pecahan & Desimal Lanjutan" to 24,
                    "KPK dan FPB dalam Pemecahan Masalah" to 20,
                    "Volume dan Luas Permukaan Bangun Ruang (Kubus, Balok)" to 18,
                    "Kecepatan, Debit, dan Perbandingan" to 14
                )
                isFaseB -> listOf(
                    "Bilangan Cacah sampai 10.000 (Operasi Hitung & Nilai Tempat)" to 24,
                    "Pecahan Senilai, Desimal, dan Persen" to 20,
                    "Pola Gambar dan Pola Bilangan" to 16,
                    "Pengukuran Panjang, Berat, dan Waktu" to 12
                )
                isFaseA -> listOf(
                    "Bilangan Cacah sampai 100 (Nilai Tempat & Operasi Hitung)" to 24,
                    "Penjumlahan dan Pengurangan Bilangan sampai 20" to 20,
                    "Mengenal Bentuk Bangun Datar dan Ruang" to 16,
                    "Pengukuran Panjang & Berat Tidak Baku" to 12
                )
                else -> listOf(
                    "Bilangan Dasar dan Operasi Hitung" to 20,
                    "Eksplorasi Kontekstual" to 20,
                    "Penerapan Praktis" to 18,
                    "Evaluasi Akhir" to 14
                )
            }
            "IPAS", "Fisika", "Kimia", "Biologi" -> when {
                isSmaSmk -> listOf(
                    "Hakikat Ilmu Sains & Metode Ilmiah" to 20,
                    "Struktur Atom dan Sistem Periodik Unsur" to 20,
                    "Hukum Dasar Kimia & Stoikiometri" to 18,
                    "Energi Terbarukan & Perubahan Iklim Global" to 16
                )
                isSmp -> listOf(
                    "Sistem Organisasi Kehidupan Makhluk Hidup" to 20,
                    "Zat dan Perubahannya (Wujud, Massa Jenis, Kalor)" to 20,
                    "Suhu, Kalor, dan Pemuaian dalam Kehidupan" to 18,
                    "Gerak dan Gaya (Hukum Newton)" to 16
                )
                isFaseC -> listOf(
                    "Rantai Makanan & Keseimbangan Ekosistem" to 20,
                    "Magnet, Listrik, dan Pemanfaatannya dalam Kehidupan" to 20,
                    "Sistem Organ Tubuh Manusia (Pernapasan & Pencernaan)" to 18,
                    "Bumi dan Antatasiksa: Rotasi & Revolusi Bumi" to 14
                )
                isFaseB -> listOf(
                    "Tumbuhan Sumber Kehidupan di Bumi (Fotosintesis)" to 20,
                    "Wujud Zat dan Perubahannya (Padat, Cair, Gas)" to 20,
                    "Gaya di Sekitar Kita (Gesek, Magnet, Gravitasi)" to 18,
                    "Transformasi Energi di Lingkungan Sekitar" to 14
                )
                isFaseA -> listOf(
                    "Bagian Tubuh Manusia dan Pancaindra" to 20,
                    "Mengenal Hewan dan Tumbuhan di Sekitar Kita" to 20,
                    "Benda Padat, Cair, dan Sifat Sederhananya" to 18,
                    "Lingkungan Sehat dan Cara Merawatnya" to 14
                )
                else -> listOf(
                    "Pengenalan Lingkungan & Alam Sekitar" to 20,
                    "Eksplorasi Fenomena Alam" to 20,
                    "Penerapan Sains Sederhana" to 18,
                    "Refleksi dan Proyek Sains" to 14
                )
            }
            "Bahasa Indonesia" -> when {
                isSmaSmk -> listOf(
                    "Mengkritisi Teks Laporan Hasil Observasi (LHO)" to 20,
                    "Mengembangkan Pendapat dalam Debat Ilmiah" to 18,
                    "Negosiasi dan Resolusi Konflik Sosial" to 18,
                    "Biografi dan Nilai Keteladanan Tokoh" to 16
                )
                isSmp -> listOf(
                    "Teks Deskripsi & Narasi Fantasi" to 20,
                    "Surat Pribadi dan Surat Dinas Resmi" to 18,
                    "Teks Prosedur Membuat Sesuatu" to 18,
                    "Puisi Rakyat (Pantun, Syair, Gurindam)" to 16
                )
                isFaseC -> listOf(
                    "Menulis Surat Resmi dan Formulir Sederhana" to 20,
                    "Menyimak Wawancara dan Menggali Informasi Penting" to 18,
                    "Membaca Cerita Anak Berilustrasi & Menemukan Pesan Moral" to 18,
                    "Menulis Puisi Bebas dan Pantun Kreatif" to 16
                )
                isFaseB -> listOf(
                    "Membaca Teks Narasi & Menemukan Ide Pokok" to 20,
                    "Menulis Surat Pribadi & Teks Petunjuk" to 18,
                    "Wawancara & Laporan Pengamatan Sederhana" to 18,
                    "Puisi dan Cerita Rakyat Kontekstual" to 16
                )
                isFaseA -> listOf(
                    "Mengenal Huruf, Suku Kata, dan Kata Sederhana" to 22,
                    "Membaca Nyaring Teks Pendek Bergambar" to 20,
                    "Menulis Nama Sendiri dan Pengalaman Singkat" to 18,
                    "Menyimak Cerita Dongeng dan Pesan Moral" to 14
                )
                else -> listOf(
                    "Literasi Dasar & Pemahaman Teks" to 20,
                    "Menulis Kreatif & Terstruktur" to 18,
                    "Keterampilan Berbicara & Presentasi" to 18,
                    "Apresiasi Sastra" to 16
                )
            }
            "Pendidikan Pancasila" -> listOf(
                "Pancasila Sebagai Panduan Hidup & Nilai Sila" to 18,
                "Konstitusi dan Norma di Lingkungan Sekolah & Masyarakat" to 18,
                "Membangun Jati Diri dalam Kebhinekaan" to 18,
                "Negara Kesatuan Republik Indonesia & Cinta Tanah Air" to 18
            )
            "Informatika" -> when {
                isSmaSmk -> listOf(
                    "Berpikir Komputasional Tingkat Lanjut & Struktur Data" to 20,
                    "Analisis Data Besar (Big Data) & SQL Dasar" to 18,
                    "Pemrograman Prosedural & Berorientasi Objek" to 18,
                    "Dampak Sosial Informatika & Keamanan Siber" to 16
                )
                isSmp -> listOf(
                    "Berpikir Komputasional & Automasi Tugas" to 20,
                    "Analisis Data dan Spreadsheet Lanjut" to 18,
                    "Algoritma Pemrograman Blok / Visual" to 18,
                    "Dampak Sosial dan Etika Internet" to 16
                )
                else -> listOf(
                    "Pengenalan Perangkat Keras Komputer Sederhana" to 20,
                    "Berpikir Komputasional Dasar (Pola & Urutan)" to 18,
                    "Navigasi Aplikasi Edukasi & Pengenalan Keyboard/Mouse" to 18,
                    "Etika Digital & Keselamatan Bermain Gawai" to 14
                )
            }
            else -> listOf(
                "Konsep Dasar & Pengantar $subject ($fase)" to 20,
                "Eksplorasi Kontekstual & Analisis $subject" to 20,
                "Penerapan & Studi Kasus Dunia Nyata" to 18,
                "Projek Kolaboratif & Evaluasi Akhir" to 14
            )
        }

        val topicsGenap = when (subject) {
            "Matematika" -> when {
                isSmaSmk -> listOf(
                    "Vektor dan Operasi Aljabar Vektor" to 20,
                    "Statistika Inferensial (Pemusatan & Penyebaran Data)" to 18,
                    "Peluang Kejadian Majemuk" to 18,
                    "Fungsi Kuadrat, Eksponen, dan Komposisi" to 14
                )
                isSmp -> listOf(
                    "Statistika (Mean, Median, Modus, dan Penyajian Data)" to 20,
                    "Peluang Empiris dan Teoretis" to 16,
                    "Lingkaran (Unsur, Keliling, Luas, dan Garis Singgung)" to 20,
                    "Bangun Ruang Sisi Datar (Kubus, Balok, Prisma, Limas)" to 14
                )
                isFaseC -> listOf(
                    "Luas dan Keliling Bangun Datar Gabungan" to 20,
                    "Penyajian Data dalam Bentuk Tabel dan Diagram Lingkaran" to 18,
                    "Pengenalan Bilangan Bulat Negatif" to 18,
                    "Pengukuran Sudut Menggunakan Busur Derajat" to 14
                )
                isFaseB -> listOf(
                    "Keliling dan Luas Bangun Datar (Persegi, Persegi Panjang, Segitiga)" to 20,
                    "Sudut, Garis Sejajar, dan Hubungan Antargaris" to 16,
                    "Penyajian dan Analisis Data (Tabel, Diagram Batang)" to 20,
                    "Peluang Sederhana & Eksperimen Statistika" to 16
                )
                isFaseA -> listOf(
                    "Pengurangan Bilangan sampai 50" to 20,
                    "Mengenal Nilai Uang Rupiah (Logam dan Kertas)" to 18,
                    "Panjang, Berat, dan Waktu dengan Satuan Baku (cm, kg, jam)" to 18,
                    "Pola Pengubinan dan Bangun Datar Sederhana" to 14
                )
                else -> listOf(
                    "Pengembangan Lanjut Materi" to 20,
                    "Kajian Analisis Terpadu" to 20,
                    "Investigasi Masalah" to 18,
                    "Penilaian Sumatif Akhir" to 14
                )
            }
            "IPAS", "Fisika", "Kimia", "Biologi" -> when {
                isSmaSmk -> listOf(
                    "Dinamika Gerak (Hukum Newton & Analisis Vektor Gaya)" to 20,
                    "Usaha, Energi, dan Momentum" to 18,
                    "Termodinamika & Teori Kinetik Gas" to 18,
                    "Gelombang, Bunyi, dan Optik Fisis" to 14
                )
                isSmp -> listOf(
                    "Sistem Pencernaan dan Peredaran Darah Manusia" to 20,
                    "Sistem Ekskresi dan Koordinasi Manusia" to 18,
                    "Tekanan Zat dan Penerapannya dalam Kehidupan Sehari-hari" to 18,
                    "Getaran, Gelombang, dan Bunyi" to 16
                )
                isFaseC -> listOf(
                    "Ekosistem Nusantara & Jaring-jaring Makanan" to 20,
                    "Listrik Magnet & Teknologi Penerapannya" to 18,
                    "Sistem Reproduksi & Kesehatan Tubuh Manusia" to 18,
                    "Pelestarian Sumber Daya Alam & Mitigasi Bencana" to 16
                )
                isFaseB -> listOf(
                    "Cerita Tentang Daerahku (Sejarah Lokal & Budaya)" to 20,
                    "Indonesiaku Kaya Budaya (Kearifan Lokal)" to 18,
                    "Bagaimana Mendapatkan Kebutuhan Kita? (Ekonomi Pasar)" to 18,
                    "Masyarakat yang Beradab & Peduli Lingkungan" to 16
                )
                isFaseA -> listOf(
                    "Mengenal Anggota Keluarga & Silsilah" to 20,
                    "Aturan di Rumah dan di Sekolah" to 18,
                    "Lingkungan Sekitar Rumah & Denah Sederhana" to 18,
                    "Merawat Hewan dan Tumbuhan Peliharaan" to 14
                )
                else -> listOf(
                    "Kajian Lanjutan Alam & Sosial" to 20,
                    "Proyek Peduli Lingkungan" to 20,
                    "Eksplorasi Teknologi Tepat Guna" to 18,
                    "Evaluasi Akhir" to 14
                )
            }
            "Bahasa Indonesia" -> when {
                isSmaSmk -> listOf(
                    "Menulis Novel dan Apresiasi Sastra Indonesia" to 20,
                    "Karya Ilmiah Remaja & Artikel Jurnal" to 18,
                    "Resensi Buku Fiksi dan Non-Fiksi secara Kritis" to 18,
                    "Drama Teater dan Pementasan Kreatif" to 16
                )
                isSmp -> listOf(
                    "Menulis Surat Pribadi dan Surat Dinas" to 20,
                    "Teks Ulasan (Review) Karya Seni & Buku" to 16,
                    "Teks Persuasi & Kampanye Sosial" to 18,
                    "Drama dan Pementasan Sederhana" to 14
                )
                isFaseC -> listOf(
                    "Menulis Teks Pidato & Berpidato di Depan Kelas" to 20,
                    "Menyusun Laporan Pengamatan / Eksperimen Sains" to 18,
                    "Membaca Cerita Fiksi Sejarah & Menulis Resensi" to 18,
                    "Pementasan Drama Pendek & Apresiasi Sastra" to 16
                )
                isFaseB -> listOf(
                    "Teks Eksposisi & Argumentasi Berbasis Fakta" to 20,
                    "Menyimak dan Menyajikan Pidato Singkat" to 18,
                    "Membaca Kritis Teks Eksplanasi Ilmiah Populer" to 18,
                    "Menulis Teks Narasi Kreatif & Reflektif" to 16
                )
                isFaseA -> listOf(
                    "Membaca Lancar Kata Berimbuhan Dasar" to 20,
                    "Menulis Kalimat Sederhana dengan Tanda Baca" to 18,
                    "Menceritakan Kembali Dongeng yang Didengar" to 18,
                    "Berbicara Santun di Depan Teman Sekelas" to 14
                )
                else -> listOf(
                    "Pengembangan Keterampilan Berbahasa" to 20,
                    "Penulisan Karya Ilmiah/Kreatif" to 20,
                    "Analisis Teks Kritis" to 18,
                    "Uji Kompetensi Akhir" to 14
                )
            }
            "Pendidikan Pancasila" -> listOf(
                "Musyawarah untuk Mufakat dalam Kehidupan Sehari-hari" to 18,
                "Hak dan Kewajiban Warga Negara Secara Adil" to 18,
                "Gotong Royong dalam Keberagaman Budaya Nusantara" to 18,
                "Menjaga Keutuhan NKRI & Ketertiban Sosial" to 18
            )
            "Informatika" -> when {
                isSmaSmk -> listOf(
                    "Jaringan Komputer Lanjut & Protokol Keamanan" to 20,
                    "Kecerdasan Buatan (AI) & Pembelajaran Mesin Dasar" to 18,
                    "Pengembangan Aplikasi Berbasis Web/Mobile" to 18,
                    "Projek Akhir Lintas Bidang Informatika" to 14
                )
                isSmp -> listOf(
                    "Jaringan Komputer dan Internet (Topologi & Keamanan Data)" to 20,
                    "Analisis Data dan Visualisasi Spreadsheet" to 18,
                    "Algoritma Pemrograman Visual (Scratch/Blockly/Python)" to 20,
                    "Projek Praktik Lintas Bidang (PLB)" to 14
                )
                else -> listOf(
                    "Pengenalan Internet & Penelusuran Informasi Aman" to 20,
                    "Menggambar dan Mewarnai dengan Aplikasi Grafis" to 18,
                    "Logika Sederhana & Game Edukasi Pemecahan Masalah" to 18,
                    "Kolaborasi Digital & Berbagi Berkas Aman" to 14
                )
            }
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
        android.util.Log.d("CurriculumEngine", "generateAtp: subject=$subject, fase=$fase, grade=$grade")
        
        val steps = mutableListOf<AtpStepItem>()
        val isFaseA = fase == "Fase A"
        val isFaseB = fase == "Fase B"
        val isFaseC = fase == "Fase C"
        val isSmp = fase == "Fase D"
        val isSmaSmk = fase == "Fase E" || fase == "Fase F"
        
        android.util.Log.d("CurriculumEngine", "isFaseA=$isFaseA, isFaseB=$isFaseB, isFaseC=$isFaseC, isSmp=$isSmp, isSmaSmk=$isSmaSmk")

        val rawData = when (subject) {
            "Matematika" -> when (fase) {
                "Fase F" -> listOf(
                    Triple("Statistika", "Menganalisis data statistik inferensial untuk pengambilan keputusan", 24),
                    Triple("Kalkulus", "Menerapkan konsep turunan dan integral untuk memodelkan fenomena", 20),
                    Triple("Geometri Lanjut", "Menganalisis transformasi geometri dan penerapannya", 18),
                    Triple("Peluang Lanjut", "Menghitung peluang kejadian majemuk yang kompleks", 16)
                )
                "Fase E" -> listOf(
                    Triple("Aljabar & Fungsi", "Menjelaskan sifat eksponen dan logaritma dalam pemodelan", 24),
                    Triple("Barisan & Deret", "Menentukan pola barisan dan deret aritmatika/geometri", 20),
                    Triple("Trigonometri", "Menggunakan perbandingan trigonometri dalam masalah nyata", 18),
                    Triple("Sistem Persamaan", "Menyelesaikan sistem pertidaksamaan linear dua variabel", 16)
                )
                "Fase D" -> listOf(
                    Triple("Bilangan", "Memahami bilangan bulat dan pecahan berpangkat", 20),
                    Triple("Aljabar", "Menyelesaikan persamaan linear satu variabel", 18),
                    Triple("Geometri", "Menggunakan teorema Pythagoras", 18),
                    Triple("Analisis Data", "Menganalisis rata-rata dan peluang", 16)
                )
                "Fase C" -> listOf(
                    Triple("Bilangan", "Operasi pecahan dan desimal", 20),
                    Triple("Geometri", "Luas dan volume bangun ruang", 18),
                    Triple("Aljabar", "Pola bilangan sederhana", 16),
                    Triple("Data", "Diagram dan tabel", 16)
                )
                "Fase B" -> listOf(
                    Triple("Bilangan", "Nilai tempat sampai 10.000", 18),
                    Triple("Pecahan", "Operasi pecahan senilai", 18),
                    Triple("Geometri", "Luas bangun datar", 16),
                    Triple("Data", "Diagram batang", 16)
                )
                "Fase A" -> listOf(
                    Triple("Bilangan", "Bilangan sampai 100", 18),
                    Triple("Operasi", "Penjumlahan dan pengurangan sampai 20", 18),
                    Triple("Geometri", "Bangun datar", 16),
                    Triple("Pengukuran", "Panjang dan berat", 16)
                )
                else -> listOf(
                    Triple("Bilangan", "Dasar operasi hitung", 18),
                    Triple("Geometri", "Bentuk dasar", 16),
                    Triple("Pengukuran", "Waktu dan durasi", 16)
                )
            }
            "IPAS" -> when (fase) {
                "Fase D" -> listOf(
                    Triple("Sistem Organel", "Menganalisis sistem organel sel dan organisasi kehidupan", 20),
                    Triple("Zat & Kalor", "Menyelidiki pengaruh kalor terhadap wujud zat", 18),
                    Triple("Mekanika", "Menerapkan hukum Newton tentang gerak", 18),
                    Triple("Ekologi", "Menganalisis interaksi antar komponen ekosistem", 18)
                )
                "Fase C" -> listOf(
                    Triple("Ekosistem", "Menganalisis rantai makanan", 18),
                    Triple("Energi", "Memahami konsep magnet dan listrik sederhana", 18),
                    Triple("Tubuh Manusia", "Mengenal organ tubuh manusia", 16),
                    Triple("Bumi & Antariksa", "Memahami rotasi bumi", 16)
                )
                "Fase B" -> listOf(
                    Triple("Tumbuhan", "Mempelajari fotosintesis", 18),
                    Triple("Zat", "Membedakan benda padat, cair, gas", 16),
                    Triple("Gaya", "Mengenal gaya gesek dan gravitasi", 16),
                    Triple("Energi", "Mengidentifikasi perubahan energi", 14)
                )
                "Fase A" -> listOf(
                    Triple("Tubuh Manusia", "Mengenal bagian tubuh", 18),
                    Triple("Makhluk Hidup", "Mengenal hewan dan tumbuhan", 16),
                    Triple("Benda", "Mengelompokkan benda di sekitar", 16),
                    Triple("Lingkungan", "Mengenal lingkungan sehat", 14)
                )
                else -> listOf(
                    Triple("Sains Dasar", "Pengamatan alam sekitar", 18)
                )
            }
            "Bahasa Indonesia" -> when (fase) {
                "Fase F" -> listOf(
                    Triple("Karya Ilmiah", "Menyusun karya ilmiah dan artikel jurnal", 20),
                    Triple("Resensi", "Menganalisis dan meresensi buku fiksi/nonfiksi secara kritis", 18),
                    Triple("Drama", "Mementaskan drama teater kreatif", 18),
                    Triple("Apresiasi", "Mengapresiasi sastra Indonesia modern", 16)
                )
                "Fase E" -> listOf(
                    Triple("Laporan", "Mengkritisi dan menyusun teks laporan hasil observasi", 20),
                    Triple("Debat", "Mengembangkan pendapat dalam debat ilmiah", 18),
                    Triple("Negosiasi", "Melakukan negosiasi dan resolusi konflik sosial", 18),
                    Triple("Biografi", "Menganalisis nilai keteladanan tokoh dalam biografi", 16)
                )
                "Fase D" -> listOf(
                    Triple("Deskripsi", "Memahami dan menulis teks deskripsi", 20),
                    Triple("Surat", "Menulis surat pribadi dan dinas", 18),
                    Triple("Prosedur", "Menyusun teks prosedur", 18),
                    Triple("Puisi", "Menganalisis puisi rakyat", 16)
                )
                "Fase C" -> listOf(
                    Triple("Pidato", "Menulis dan menyajikan pidato", 20),
                    Triple("Laporan", "Laporan pengamatan eksperimen", 18),
                    Triple("Resensi", "Resensi cerita fiksi", 18),
                    Triple("Drama", "Pementasan drama pendek", 16)
                )
                "Fase B" -> listOf(
                    Triple("Eksposisi", "Teks eksposisi berbasis fakta", 20),
                    Triple("Pidato", "Pidato singkat", 18),
                    Triple("Eksplanasi", "Membaca kritis teks ilmiah populer", 18),
                    Triple("Narasi", "Narasi kreatif", 16)
                )
                "Fase A" -> listOf(
                    Triple("Membaca", "Membaca kata berimbuhan", 20),
                    Triple("Menulis", "Kalimat sederhana", 18),
                    Triple("Cerita", "Menceritakan kembali dongeng", 18),
                    Triple("Berbicara", "Berbicara santun", 14)
                )
                else -> listOf(
                    Triple("Literasi", "Dasar literasi", 18)
                )
            }
            "Fisika", "Kimia", "Biologi" -> when (fase) {
                "Fase F" -> listOf(
                    Triple("Analisis Lanjut", "Menganalisis fenomena tingkat lanjut $subject", 20),
                    Triple("Proyek Sains", "Merancang eksperimen $subject mandiri", 20),
                    Triple("Penerapan", "Menerapkan $subject dalam teknologi modern", 18),
                    Triple("Evaluasi", "Evaluasi dampak sosial $subject", 14)
                )
                "Fase E" -> listOf(
                    Triple("Hakikat Sains", "Menerapkan metode ilmiah dalam laboratorium", 20),
                    Triple("Struktur", "Struktur dasar dan sistem periodik unsur", 18),
                    Triple("Hukum Dasar", "Hukum-hukum dasar $subject", 18),
                    Triple("Energi", "Energi dan lingkungan", 18)
                )
                else -> listOf(
                    Triple("Pengenalan", "Pengenalan konsep dasar $subject", 20),
                    Triple("Eksplorasi", "Eksplorasi fenomena $subject", 20),
                    Triple("Penerapan", "Penerapan konsep $subject", 18),
                    Triple("Evaluasi", "Evaluasi pemahaman $subject", 14)
                )
            }
            "Seni Musik" -> when (fase) {
                "Fase F" -> listOf(
                    Triple("Komposisi", "Menyusun komposisi musik orisinal dengan struktur yang kompleks", 20),
                    Triple("Analisis Kritis", "Menganalisis estetika dan konteks budaya dalam karya musik", 20),
                    Triple("Teknik Lanjut", "Menguasai teknik instrumen musik/vokal tingkat lanjut", 20),
                    Triple("Pertunjukan", "Menyelenggarakan pertunjukan musik dengan manajemen produksi", 20)
                )
                "Fase E" -> listOf(
                    Triple("Apresiasi Musik", "Mengapresiasi elemen musik dalam karya musik populer dan tradisional", 20),
                    Triple("Kreasi Musik", "Mengeksplorasi teknik dasar instrumen musik dan vokal", 20),
                    Triple("Analisis Dasar", "Menganalisis bentuk dan struktur lagu sederhana", 20),
                    Triple("Projek Musik", "Menampilkan karya musik kreasi secara berkelompok", 20)
                )
                "Fase D" -> listOf(
                    Triple("Musik Tradisi", "Memainkan instrumen musik tradisional", 18),
                    Triple("Musik Modern", "Bernyanyi lagu populer", 18),
                    Triple("Teori Musik", "Memahami dasar notasi musik", 18),
                    Triple("Apresiasi", "Menghargai keragaman musik", 16)
                )
                else -> listOf(
                    Triple("Dasar Bunyi", "Mengenal berbagai macam bunyi", 16),
                    Triple("Bernyanyi", "Bernyanyi lagu anak-anak", 16),
                    Triple("Gerak", "Bergerak sesuai irama", 14)
                )
            }
            else -> when (fase) {
                "Fase F" -> listOf(
                    Triple("Analisis Lanjut", "Menganalisis prinsip kompleks $subject", 20),
                    Triple("Studi Kasus", "Melakukan kajian mendalam terhadap kasus $subject", 20),
                    Triple("Penerapan Kritis", "Mengaplikasikan $subject dalam konteks global", 20),
                    Triple("Refleksi", "Mengevaluasi dampak $subject bagi masyarakat", 20)
                )
                "Fase E" -> listOf(
                    Triple("Analisis Dasar", "Menganalisis prinsip dasar $subject", 18),
                    Triple("Studi Kasus", "Melakukan kajian terhadap kasus $subject", 18),
                    Triple("Penerapan", "Mengaplikasikan $subject dalam konteks", 18),
                    Triple("Refleksi", "Mengevaluasi dampak $subject", 16)
                )
                "Fase D" -> listOf(
                    Triple("Konsep Utama", "Memahami konsep utama $subject", 18),
                    Triple("Penerapan", "Menerapkan $subject dalam kehidupan sehari-hari", 18),
                    Triple("Analisis", "Menganalisis fenomena terkait $subject", 18),
                    Triple("Komunikasi", "Mengomunikasikan hasil belajar $subject", 16)
                )
                else -> listOf(
                    Triple("Pengenalan", "Mengenal dasar-dasar $subject", 16),
                    Triple("Eksplorasi", "Eksplorasi sederhana tentang $subject", 16),
                    Triple("Aplikasi", "Menerapkan $subject dalam permainan/kegiatan", 14)
                )
            }
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
