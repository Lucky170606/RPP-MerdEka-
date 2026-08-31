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
        // FASE A
        CapaianPembelajaranItem("Matematika", "Fase A", "Bilangan", "Pemahaman bilangan cacah sampai 100", listOf("Membilang 1-20", "Penjumlahan 1-20", "Pengurangan 1-20"), listOf("bilangan", "tambah", "kurang")),
        CapaianPembelajaranItem("Bahasa Indonesia", "Fase A", "Membaca", "Pemahaman teks sederhana", listOf("Suku kata", "Cerita pendek", "Kosakata baru"), listOf("baca", "cerita")),
        
        // FASE B
        CapaianPembelajaranItem("Matematika", "Fase B", "Bilangan", "Pemahaman bilangan cacah sampai 10.000", listOf("Perkalian", "Pembagian", "Pecahan sederhana"), listOf("kali", "bagi", "pecahan")),
        CapaianPembelajaranItem("IPAS", "Fase B", "Sains", "Hubungan bentuk dan fungsi tubuh", listOf("Tumbuhan", "Hewan", "Wujud zat"), listOf("tumbuhan", "hewan", "zat")),
        CapaianPembelajaranItem("Pendidikan Pancasila", "Fase B", "Pancasila", "Penerapan nilai Pancasila", listOf("Sila 1-3", "Gotong royong", "Aturan sekolah"), listOf("pancasila", "gotong royong")),

        // FASE C
        CapaianPembelajaranItem("Matematika", "Fase C", "Bilangan", "Operasi hitung bilangan pecahan", listOf("Penjumlahan pecahan", "Perkalian desimal", "Rasio"), listOf("pecahan", "desimal")),
        CapaianPembelajaranItem("IPAS", "Fase C", "Sains", "Sistem organ tubuh manusia", listOf("Pencernaan", "Pernapasan", "Rantai makanan"), listOf("pencernaan", "organ")),
        CapaianPembelajaranItem("Bahasa Indonesia", "Fase C", "Membaca", "Menganalisis informasi teks", listOf("Ide pokok", "Teks narasi", "Teks informasi"), listOf("ide pokok", "teks")),

        // FASE D
        CapaianPembelajaranItem("Bahasa Indonesia", "Fase D", "Membaca", "Mengevaluasi informasi teks", listOf("Teks prosedur", "Teks eksplanasi", "Teks deskripsi"), listOf("prosedur", "eksplanasi", "deskripsi")),
        CapaianPembelajaranItem("IPA", "Fase D", "Sains", "Klasifikasi makhluk hidup", listOf("Struktur sel", "Sistem organisasi", "Ekosistem"), listOf("sel", "ekosistem")),
        CapaianPembelajaranItem("IPS", "Fase D", "Sosial", "Interaksi sosial dan lingkungan", listOf("Letak geografis", "Kegiatan ekonomi", "Perubahan sosial"), listOf("geografis", "ekonomi")),
        CapaianPembelajaranItem("Matematika", "Fase D", "Aljabar", "Pemahaman aljabar", listOf("Persamaan linear", "Perbandingan", "Himpunan"), listOf("persamaan", "aljabar")),

        // FASE E
        CapaianPembelajaranItem("Bahasa Indonesia", "Fase E", "Menulis", "Menulis gagasan kreatif", listOf("Laporan Observasi", "Negosiasi", "Anekdot"), listOf("lho", "negosiasi", "anekdot")),
        CapaianPembelajaranItem("Pendidikan Pancasila", "Fase E", "Pancasila", "Analisis rumusan Pancasila", listOf("Sejarah Pancasila", "Nilai Pancasila", "Norma"), listOf("pancasila", "norma")),
        CapaianPembelajaranItem("Informatika", "Fase E", "Algoritma", "Strategi algoritmik", listOf("Berpikir komputasional", "Flowchart", "Pemrograman"), listOf("algoritma", "flowchart", "coding")),
        CapaianPembelajaranItem("Matematika", "Fase E", "Aljabar", "Pemodelan matematika", listOf("Eksponen", "Logaritma", "Sistem persamaan"), listOf("eksponen", "logaritma")),

        // FASE F
        CapaianPembelajaranItem("Matematika", "Fase F", "Kalkulus", "Konsep limit dan turunan", listOf("Turunan fungsi", "Aplikasi turunan", "Integral"), listOf("turunan", "integral", "limit")),
        CapaianPembelajaranItem("Bahasa Inggris", "Fase F", "Membaca", "Analisis teks kompleks", listOf("Analytical Exposition", "Discussion Text", "Report Text"), listOf("exposition", "discussion", "report")),
        CapaianPembelajaranItem("Biologi", "Fase F", "Sains", "Struktur dan fungsi biologis", listOf("Sel dan metabolisme", "Genetika", "Evolusi"), listOf("sel", "genetika", "evolusi")),
        CapaianPembelajaranItem("Fisika", "Fase F", "Sains", "Mekanika dan gelombang", listOf("Gerak lurus", "Hukum Newton", "Gelombang bunyi"), listOf("mekanika", "newton", "gelombang")),
        CapaianPembelajaranItem("Kimia", "Fase F", "Sains", "Struktur atom dan reaksi", listOf("Struktur atom", "Termokimia", "Laju reaksi"), listOf("atom", "kimia", "reaksi"))
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
