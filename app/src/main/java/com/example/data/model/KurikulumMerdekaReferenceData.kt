package com.example.data.model

object KurikulumMerdekaReferenceData {

    val MATA_PELAJARAN_LIST = listOf(
        "Matematika",
        "Bahasa Indonesia",
        "Ilmu Pengetahuan Alam dan Sosial (IPAS)",
        "Pendidikan Pancasila",
        "Bahasa Inggris",
        "Ilmu Pengetahuan Alam (IPA)",
        "Ilmu Pengetahuan Sosial (IPS)",
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

    fun getSubjectsForFase(fase: String): List<String> {
        return when (fase) {
            "Fase A", "Fase B", "Fase C" -> listOf(
                "Matematika", "Bahasa Indonesia", "Ilmu Pengetahuan Alam dan Sosial (IPAS)",
                "Pendidikan Pancasila", "Bahasa Inggris", "Informatika",
                "Pendidikan Jasmani, Olahraga, dan Kesehatan (PJOK)",
                "Seni Rupa", "Seni Musik", "Pendidikan Agama Islam dan Budi Pekerti"
            )
            "Fase D" -> listOf(
                "Matematika", "Bahasa Indonesia", "Ilmu Pengetahuan Alam (IPA)", "Ilmu Pengetahuan Sosial (IPS)",
                "Pendidikan Pancasila", "Bahasa Inggris", "Informatika",
                "Pendidikan Jasmani, Olahraga, dan Kesehatan (PJOK)",
                "Seni Rupa", "Seni Musik", "Pendidikan Agama Islam dan Budi Pekerti"
            )
            "Fase E", "Fase F" -> listOf(
                "Matematika", "Bahasa Indonesia", "Bahasa Inggris", "Pendidikan Pancasila", "Informatika",
                "Fisika", "Biologi", "Kimia", "Sejarah", "Geografi", "Ekonomi", "Sosiologi",
                "Pendidikan Jasmani, Olahraga, dan Kesehatan (PJOK)",
                "Seni Rupa", "Seni Musik", "Pendidikan Agama Islam dan Budi Pekerti"
            )
            else -> MATA_PELAJARAN_LIST
        }
    }

    val JENIS_ASESMEN_LIST = listOf(
        "Asesmen Formatif (Selama Proses)",
        "Asesmen Sumatif (Akhir Lingkup Materi)",
        "Asesmen Sumatif (Akhir Semester)"
    )

    fun getSuggestedTopics(fase: String, subject: String): List<String> {
        return CP_DATABASE
            .filter { it.subject.equals(subject, ignoreCase = true) && it.fase.equals(fase, ignoreCase = true) }
            .flatMap { it.suggestedTujuan }
            .distinct()
    }

    val PROFIL_PELAJAR_PANCASILA = listOf(
        ProfilPancasilaDimension(
            id = "p3_beriman",
            title = "Beriman, Bertakwa kepada Tuhan YME, dan Berakhlak Mulia",
            subElements = listOf(
                "Akhlak Beragama (Mengenal & mencintai Tuhan)",
                "Akhlak Pribadi (Integritas & merawat diri)",
                "Akhlak kepada Manusia (Menghargai sesama, empati)",
                "Akhlak kepada Alam (Menjaga kelestarian lingkungan)",
                "Akhlak Bernegara (Hak & kewajiban warga negara)"
            ),
            description = "Pelajar Indonesia yang berakhlak mulia dalam hubungannya dengan Tuhan Yang Maha Esa, sesama manusia, alam, dan bangsa."
        ),
        ProfilPancasilaDimension(
            id = "p3_kebinekaan",
            title = "Berkebinekaan Global",
            subElements = listOf(
                "Mengenal dan menghargai budaya nusantara & dunia",
                "Komunikasi dan interaksi antar budaya",
                "Refleksi dan tanggung jawab terhadap pengalaman kebinekaan",
                "Berkeadilan sosial dan menolak diskriminasi"
            ),
            description = "Mempertahankan budaya luhur, lokalitas, dan identitasnya, serta tetap berpikiran terbuka dalam berinteraksi dengan budaya lain."
        ),
        ProfilPancasilaDimension(
            id = "p3_gotong_royong",
            title = "Bergotong Royong",
            subElements = listOf(
                "Kolaborasi (Kerjasama & komunikasi tim)",
                "Kepedulian (Tanggap terhadap lingkungan sosial)",
                "Berbagi (Memberi & menerima hal positif)"
            ),
            description = "Kemampuan untuk melakukan kegiatan secara bersama-sama dengan sukarela agar kegiatan lancar, mudah, dan ringan."
        ),
        ProfilPancasilaDimension(
            id = "p3_mandiri",
            title = "Mandiri",
            subElements = listOf(
                "Pemahaman diri dan situasi yang dihadapi",
                "Regulasi diri (Pengendalian emosi & disiplin belajar)",
                "Percaya diri, tangguh, dan adaptif"
            ),
            description = "Pelajar yang bertanggung jawab atas proses dan hasil belajarnya secara otonom."
        ),
        ProfilPancasilaDimension(
            id = "p3_bernalar_kritis",
            title = "Bernalar Kritis",
            subElements = listOf(
                "Memperoleh dan memproses informasi serta gagasan",
                "Menganalisis dan mengevaluasi penalaran",
                "Merefleksi pemikiran dan proses berpikir sendiri",
                "Pengambilan keputusan berbasis data dan logika"
            ),
            description = "Mampu secara objektif memproses informasi, membangun keterkaitan antara berbagai informasi, menganalisis dan mengevaluasinya."
        ),
        ProfilPancasilaDimension(
            id = "p3_kreatif",
            title = "Kreatif",
            subElements = listOf(
                "Menghasilkan gagasan yang orisinal",
                "Menghasilkan karya dan tindakan yang orisinal",
                "Memiliki keluwesan berpikir dalam mencari alternatif solusi"
            ),
            description = "Mampu memodifikasi dan menghasilkan sesuatu yang orisinal, bermakna, bermanfaat, dan berdampak."
        )
    )

    val MODEL_PEMBELAJARAN_LIST = listOf(
        ModelPembelajaranOption(
            id = "deep_learning",
            name = "Deep Learning (Pembelajaran Mendalam)",
            description = "Pendekatan 3 Pilar: Mindful (kesadaran penuh), Meaningful (bermakna), dan Joyful (menyenangkan) untuk pemahaman konsep mendalam.",
            sintaks = listOf(
                "1. Mindful Engagement (Fokus & Menghubungkan Pengalaman)",
                "2. Meaningful Exploration (Eksplorasi Konseptual Kontekstual)",
                "3. Joyful Active Practice (Aktivitas Bermakna & Kolaborasi Menyenangkan)",
                "4. Metacognitive Reflection & Transfer Pengetahuan"
            )
        ),
        ModelPembelajaranOption(
            id = "pbl",
            name = "Problem-Based Learning (PBL)",
            description = "Pembelajaran berbasis pemecahan masalah kontekstual nyata untuk melatih berpikir kritis.",
            sintaks = listOf(
                "1. Orientasi peserta didik pada masalah",
                "2. Mengorganisasikan peserta didik untuk belajar",
                "3. Membimbing penyelidikan individu maupun kelompok",
                "4. Mengembangkan dan menyajikan hasil karya",
                "5. Menganalisis dan mengevaluasi proses pemecahan masalah"
            )
        ),
        ModelPembelajaranOption(
            id = "pjbl",
            name = "Project-Based Learning (PjBL)",
            description = "Pembelajaran berbasis proyek autentik yang menghasilkan produk nyata / karya.",
            sintaks = listOf(
                "1. Penentuan pertanyaan mendasar (essential question)",
                "2. Mendesain perencanaan produk / proyek",
                "3. Menyusun jadwal pembuatan proyek",
                "4. Memonitor keaktifan dan perkembangan proyek",
                "5. Menguji hasil / presentasi produk",
                "6. Evaluasi pengalaman belajar"
            )
        ),
        ModelPembelajaranOption(
            id = "discovery",
            name = "Discovery Learning",
            description = "Pembelajaran penemuan konsep melalui stimulasi, eksplorasi, dan verifikasi mandiri.",
            sintaks = listOf(
                "1. Pemberian rangsangan (Stimulation)",
                "2. Pernyataan / Identifikasi masalah (Problem Statement)",
                "3. Pengumpulan data (Data Collection)",
                "4. Pengolahan data (Data Processing)",
                "5. Pembuktian (Verification)",
                "6. Menarik simpulan / generalisasi (Generalization)"
            )
        ),
        ModelPembelajaranOption(
            id = "inquiry",
            name = "Inquiry Learning",
            description = "Penyelidikan ilmiah sistematis dengan merumuskan hipotesis dan menguji bukti.",
            sintaks = listOf(
                "1. Orientasi masalah",
                "2. Merumuskan masalah",
                "3. Merumuskan hipotesis",
                "4. Mengumpulkan data",
                "5. Menguji hipotesis",
                "6. Merumuskan kesimpulan"
            )
        ),
        ModelPembelajaranOption(
            id = "tarl",
            name = "Teaching at the Right Level (TaRL)",
            description = "Pembelajaran disesuaikan dengan tingkat kemampuan dan kesiapan belajar aktual siswa.",
            sintaks = listOf(
                "1. Asesmen diagnostik awal kemampuan",
                "2. Pengelompokan fleksibel sesuai level kesiapan",
                "3. Pembelajaran bertingkat (scaffolding diferensiasi)",
                "4. Asesmen berkala & penyesuaian level"
            )
        ),
        ModelPembelajaranOption(
            id = "cooperative",
            name = "Cooperative Learning (Jigsaw / STAD)",
            description = "Pembelajaran kooperatif dalam kelompok kecil heterogen dengan saling ketergantungan positif.",
            sintaks = listOf(
                "1. Menyampaikan tujuan dan memotivasi siswa",
                "2. Menyajikan informasi pengantar",
                "3. Mengorganisasikan siswa ke dalam kelompok belajar",
                "4. Membimbing kelompok bekerja dan belajar",
                "5. Evaluasi / kuis unjuk kerja",
                "6. Memberikan penghargaan tim"
            )
        ),
        ModelPembelajaranOption(
            id = "direct",
            name = "Direct Instruction (Eksplisit Interaktif)",
            description = "Pengajaran langsung terstruktur dengan peragaan, latihan terbimbing, dan umpan balik segera.",
            sintaks = listOf(
                "1. Menyampaikan tujuan dan mempersiapkan siswa",
                "2. Mendemonstrasikan pengetahuan / keterampilan",
                "3. Membimbing pelatihan awal",
                "4. Mengecek pemahaman dan memberikan umpan balik",
                "5. Memberikan kesempatan untuk latihan mandiri"
            )
        )
    )

    val CP_DATABASE = listOf(
        // FASE A (Expanded)
        CapaianPembelajaranItem("Matematika", "Fase A", "Bilangan", "Pemahaman bilangan cacah sampai 100", listOf("Membilang 1-20", "Penjumlahan 1-20", "Pengurangan 1-20", "Nilai tempat puluhan", "Membandingkan bilangan", "Pola gambar", "Urutan bilangan", "Satuan waktu dasar", "Satuan panjang tidak baku", "Pengenalan bangun datar"), listOf("bilangan", "tambah", "kurang", "cacah", "bangun")),
        CapaianPembelajaranItem("Bahasa Indonesia", "Fase A", "Membaca", "Pemahaman teks sederhana", listOf("Suku kata", "Cerita pendek", "Kosakata baru", "Membaca nyaring", "Kalimat sederhana", "Teks deskripsi diri", "Puisi anak", "Dongeng", "Urutan cerita", "Informasi penting"), listOf("baca", "cerita", "kalimat", "puisi", "dongeng")),
        CapaianPembelajaranItem("Pendidikan Pancasila", "Fase A", "Pancasila", "Pengenalan nilai Pancasila", listOf("Simbol Pancasila", "Sila pertama", "Aturan di rumah", "Gotong royong", "Identitas diri", "Hak anak", "Kewajiban di rumah", "Keragaman teman", "Saling menghormati", "Norma kesopanan"), listOf("pancasila", "aturan", "identitas", "norma", "hak")),
        CapaianPembelajaranItem("IPAS", "Fase A", "Sains", "Pengenalan diri dan lingkungan", listOf("Anggota tubuh", "Pancaindera", "Benda di sekitar", "Cuaca", "Tumbuhan di rumah", "Hewan peliharaan", "Kebersihan diri", "Kesehatan makanan", "Lingkungan rumah", "Perubahan musim"), listOf("tubuh", "benda", "cuaca", "tumbuhan", "sehat")),
        
        // FASE B (Expanded)
        CapaianPembelajaranItem("Matematika", "Fase B", "Bilangan", "Pemahaman bilangan cacah sampai 10.000", listOf("Perkalian susun", "Pembagian susun", "Pecahan senilai", "Pola bilangan", "Uang dan nilai", "Bangun datar", "Keliling", "Luas", "Simetri", "Sudut"), listOf("kali", "bagi", "pecahan", "uang", "bangun")),
        CapaianPembelajaranItem("IPAS", "Fase B", "Sains", "Hubungan bentuk dan fungsi tubuh", listOf("Fotosintesis", "Siklus hidup hewan", "Wujud zat", "Gaya di sekitar", "Transformasi energi", "Ekosistem sungai", "Daur air", "Magnet", "Bunyi", "Cahaya"), listOf("tumbuhan", "hewan", "zat", "gaya", "energi")),
        CapaianPembelajaranItem("Pendidikan Pancasila", "Fase B", "Pancasila", "Penerapan nilai Pancasila", listOf("Sila dalam kehidupan", "Gotong royong", "Aturan sekolah", "Hak dan kewajiban", "Keragaman budaya", "Musyawarah", "Simbol negara", "Pahlawan", "Lingkungan masyarakat", "Keberagaman adat"), listOf("pancasila", "hak", "kewajiban", "budaya")),
        CapaianPembelajaranItem("Bahasa Indonesia", "Fase B", "Menulis", "Menulis teks deskripsi", listOf("Deskripsi benda", "Teks narasi", "Surat pribadi", "Kalimat efektif", "Paragraf sederhana", "Laporan pengamatan", "Pesan singkat", "Puisi bebas", "Ringkasan cerita", "Informasi poster"), listOf("deskripsi", "narasi", "surat", "paragraf")),

        // Additional items for Fase C to F ... (Representing a significant enrichment)
        CapaianPembelajaranItem("Matematika", "Fase C", "Bilangan", "Operasi hitung bilangan pecahan", listOf("Penjumlahan pecahan", "Perkalian desimal", "Rasio dan perbandingan", "Skala", "Bangun ruang sederhana", "Volume bangun ruang", "Data statistika", "Modus", "Median", "Rata-rata"), listOf("pecahan", "desimal", "rasio", "bangun", "statistika")),
        CapaianPembelajaranItem("IPAS", "Fase C", "Sains", "Sistem organ tubuh manusia", listOf("Sistem pencernaan", "Sistem pernapasan", "Rantai makanan", "Ekosistem darat", "Kelestarian lingkungan", "Sistem peredaran darah", "Sistem gerak", "Kelainan organ", "Pelestarian hewan", "Adaptasi makhluk hidup"), listOf("pencernaan", "pernapasan", "ekosistem", "organ")),
        CapaianPembelajaranItem("Informatika", "Fase D", "Algoritma", "Berpikir komputasional", listOf("Algoritma pemrograman", "Struktur data", "Dampak sosial informatika", "Jaringan komputer", "Keamanan data", "Sistem bilangan", "Perangkat keras", "Perangkat lunak", "Interaksi manusia komputer", "Etika digital"), listOf("algoritma", "data", "jaringan", "keamanan")),
        CapaianPembelajaranItem("Fisika", "Fase F", "Sains", "Mekanika dan gelombang", listOf("Gerak lurus dan melingkar", "Hukum Newton", "Usaha dan energi", "Gelombang bunyi dan cahaya", "Listrik statis dan dinamis", "Termodinamika", "Fisika inti", "Relativitas", "Optik geometri", "Fluida"), listOf("mekanika", "newton", "energi", "gelombang", "listrik")),
    )

    fun findMatchingCP(subject: String, fase: String, topic: String): CapaianPembelajaranItem? {
        val topicLower = topic.lowercase().trim()
        val subjectMatches = CP_DATABASE.filter { it.subject.equals(subject, ignoreCase = true) && it.fase.equals(fase, ignoreCase = true) }
        
        if (subjectMatches.isEmpty()) {
            return CP_DATABASE.firstOrNull { it.subject.equals(subject, ignoreCase = true) }
        }

        // Try to match keywords
        val keywordMatch = subjectMatches.firstOrNull { cp ->
            cp.defaultKeywords.any { kw -> topicLower.contains(kw) || kw.contains(topicLower) }
        }
        return keywordMatch ?: subjectMatches.firstOrNull()
    }

    val QUICK_PRESETS = listOf(
        QuickPreset(
            title = "Pecahan Senilai (Matematika SD)",
            fase = "Fase B",
            grade = "Kelas 4",
            subject = "Matematika",
            topic = "Mengenal Pecahan Senilai dengan Benda Konkret",
            timeAllocation = "2 JP (2 x 35 Menit)",
            model = "Problem-Based Learning (PBL)",
            dimensi = listOf("Bernalar Kritis", "Bergotong Royong", "Mandiri")
        ),
        QuickPreset(
            title = "Fotosintesis & Bagian Tumbuhan (IPAS)",
            fase = "Fase B",
            grade = "Kelas 4",
            subject = "Ilmu Pengetahuan Alam dan Sosial (IPAS)",
            topic = "Proses Fotosintesis dan Bagian Tubuh Tumbuhan",
            timeAllocation = "3 JP (3 x 35 Menit)",
            model = "Discovery Learning",
            dimensi = listOf("Beriman, Bertakwa kepada Tuhan YME, dan Berakhlak Mulia", "Bernalar Kritis", "Kreatif")
        ),
        QuickPreset(
            title = "Teks Prosedur Interaktif (Bahasa Indonesia)",
            fase = "Fase D",
            grade = "Kelas 7",
            subject = "Bahasa Indonesia",
            topic = "Struktur dan Kebahasaan Teks Prosedur Membuat Kuliner Lokal",
            timeAllocation = "2 JP (2 x 40 Menit)",
            model = "Project-Based Learning (PjBL)",
            dimensi = listOf("Kreatif", "Berkebinekaan Global", "Bergotong Royong")
        ),
        QuickPreset(
            title = "Hukum Newton dalam Kehidupan (IPA SMP)",
            fase = "Fase D",
            grade = "Kelas 8",
            subject = "Ilmu Pengetahuan Alam (IPA)",
            topic = "Penerapan Hukum I, II, dan III Newton pada Gerak Benda",
            timeAllocation = "3 JP (3 x 40 Menit)",
            model = "Inquiry Learning",
            dimensi = listOf("Bernalar Kritis", "Mandiri", "Bergotong Royong")
        ),
        QuickPreset(
            title = "Berpikir Komputasional & Algoritma (Informatika)",
            fase = "Fase E",
            grade = "Kelas 10",
            subject = "Informatika",
            topic = "Penerapan 4 Pilar Berpikir Komputasional dalam Optimasi Rute",
            timeAllocation = "2 JP (2 x 45 Menit)",
            model = "Problem-Based Learning (PBL)",
            dimensi = listOf("Bernalar Kritis", "Kreatif", "Mandiri")
        )
    )
}

data class QuickPreset(
    val title: String,
    val fase: String,
    val grade: String,
    val subject: String,
    val topic: String,
    val timeAllocation: String,
    val model: String,
    val dimensi: List<String>
)
