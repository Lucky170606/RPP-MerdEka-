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
        android.util.Log.d("WizardDebug", "getSuggestedTopics called: fase='$fase', subject='$subject'")
        
        // Debug: Log some database entries
        android.util.Log.d("WizardDebug", "Database size: ${CP_DATABASE.size}")
        CP_DATABASE.take(5).forEach { 
            android.util.Log.d("WizardDebug", "DB Entry: ${it.fase} - ${it.subject}")
        }
        
        val results = CP_DATABASE
            .filter { it.subject.equals(subject, ignoreCase = true) && it.fase.equals(fase, ignoreCase = true) }
        android.util.Log.d("WizardDebug", "Found ${results.size} matches in CP_DATABASE")
        return results
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
        CapaianPembelajaranItem("Matematika", "Fase A", "Bilangan", "Pemahaman bilangan cacah sampai 100", listOf("Membilang 1-20", "Penjumlahan 1-20", "Pengurangan 1-20", "Nilai tempat puluhan", "Membandingkan bilangan", "Pola gambar", "Urutan bilangan", "Satuan waktu dasar", "Satuan panjang tidak baku", "Pengenalan bangun datar"), listOf("bilangan", "tambah", "kurang", "cacah", "bangun")),
        CapaianPembelajaranItem("Bahasa Indonesia", "Fase A", "Membaca", "Pemahaman teks sederhana", listOf("Suku kata", "Cerita pendek", "Kosakata baru", "Membaca nyaring", "Kalimat sederhana", "Teks deskripsi diri", "Puisi anak", "Dongeng", "Urutan cerita", "Informasi penting"), listOf("baca", "cerita", "kalimat", "puisi", "dongeng")),
        CapaianPembelajaranItem("Pendidikan Pancasila", "Fase A", "Pancasila", "Pengenalan nilai Pancasila", listOf("Simbol Pancasila", "Sila pertama", "Aturan di rumah", "Gotong royong", "Identitas diri", "Hak anak", "Kewajiban di rumah", "Keragaman teman", "Saling menghormati", "Norma kesopanan"), listOf("pancasila", "aturan", "identitas", "norma", "hak")),
        CapaianPembelajaranItem("Ilmu Pengetahuan Alam dan Sosial (IPAS)", "Fase A", "Sains", "Pengenalan diri dan lingkungan", listOf("Anggota tubuh", "Pancaindera", "Benda di sekitar", "Cuaca", "Tumbuhan di rumah", "Hewan peliharaan", "Kebersihan diri", "Kesehatan makanan", "Lingkungan rumah", "Perubahan musim"), listOf("tubuh", "benda", "cuaca", "tumbuhan", "sehat")),
        CapaianPembelajaranItem("Seni Rupa", "Fase A", "Menggambar", "Eksplorasi garis dan bentuk", listOf("Garis lurus", "Garis lengkung", "Bentuk dasar", "Warna primer", "Warna sekunder", "Gambar bebas", "Teknik arsir dasar", "Kolase", "Mozaik", "Bahan alam"), listOf("garis", "bentuk", "warna", "gambar", "seni")),
        
        // FASE B
        CapaianPembelajaranItem("Matematika", "Fase B", "Bilangan", "Pemahaman bilangan cacah sampai 10.000", listOf("Perkalian susun", "Pembagian susun", "Pecahan senilai", "Pola bilangan", "Uang dan nilai", "Bangun datar", "Keliling", "Luas", "Simetri", "Sudut"), listOf("kali", "bagi", "pecahan", "uang", "bangun")),
        CapaianPembelajaranItem("Ilmu Pengetahuan Alam dan Sosial (IPAS)", "Fase B", "Sains", "Hubungan bentuk dan fungsi tubuh", listOf("Fotosintesis", "Siklus hidup hewan", "Wujud zat", "Gaya di sekitar", "Transformasi energi", "Ekosistem sungai", "Daur air", "Magnet", "Bunyi", "Cahaya"), listOf("tumbuhan", "hewan", "zat", "gaya", "energi")),
        CapaianPembelajaranItem("Pendidikan Pancasila", "Fase B", "Pancasila", "Penerapan nilai Pancasila", listOf("Sila dalam kehidupan", "Gotong royong", "Aturan sekolah", "Hak dan kewajiban", "Keragaman budaya", "Musyawarah", "Simbol negara", "Pahlawan", "Lingkungan masyarakat", "Keberagaman adat"), listOf("pancasila", "hak", "kewajiban", "budaya")),
        CapaianPembelajaranItem("Bahasa Indonesia", "Fase B", "Menulis", "Menulis teks deskripsi", listOf("Deskripsi benda", "Teks narasi", "Surat pribadi", "Kalimat efektif", "Paragraf sederhana", "Laporan pengamatan", "Pesan singkat", "Puisi bebas", "Ringkasan cerita", "Informasi poster"), listOf("deskripsi", "narasi", "surat", "paragraf")),
        CapaianPembelajaranItem("Seni Musik", "Fase B", "Bunyi", "Eksplorasi bunyi dan irama", listOf("Nada dasar", "Tempo", "Dinamika", "Alat musik ritmis", "Alat musik melodis", "Lagu wajib nasional", "Lagu daerah", "Pola irama", "Notasi angka", "Bernyanyi bersama"), listOf("bunyi", "irama", "nada", "musik", "lagu")),

        // FASE C
        CapaianPembelajaranItem("Matematika", "Fase C", "Bilangan", "Operasi hitung bilangan pecahan", listOf("Penjumlahan pecahan", "Perkalian desimal", "Rasio dan perbandingan", "Skala", "Bangun ruang sederhana", "Volume bangun ruang", "Data statistika", "Modus", "Median", "Rata-rata"), listOf("pecahan", "desimal", "rasio", "bangun", "statistika")),
        CapaianPembelajaranItem("Ilmu Pengetahuan Alam dan Sosial (IPAS)", "Fase C", "Sains", "Sistem organ tubuh manusia", listOf("Sistem pencernaan", "Sistem pernapasan", "Rantai makanan", "Ekosistem darat", "Kelestarian lingkungan", "Sistem peredaran darah", "Sistem gerak", "Kelainan organ", "Pelestarian hewan", "Adaptasi makhluk hidup"), listOf("pencernaan", "pernapasan", "ekosistem", "organ")),
        CapaianPembelajaranItem("Bahasa Indonesia", "Fase C", "Membaca", "Menganalisis informasi teks", listOf("Ide pokok", "Teks narasi kompleks", "Teks informasi", "Resensi buku", "Argumen sederhana", "Teks eksposisi", "Wawancara", "Laporan perjalanan", "Cerita rakyat", "Analisis berita"), listOf("ide pokok", "teks", "argumen", "resensi", "wawancara")),
        CapaianPembelajaranItem("Pendidikan Pancasila", "Fase C", "Pancasila", "Konstitusi dan norma", listOf("UUD 1945", "Norma masyarakat", "Demokrasi sekolah", "Pancasila dalam tindakan", "Kebinekaan Indonesia", "Hak asasi manusia", "Kedaulatan rakyat", "Sistem pemerintahan", "Hukum di Indonesia", "Pendidikan karakter"), listOf("uud", "norma", "demokrasi", "pancasila", "hak")),
        CapaianPembelajaranItem("Pendidikan Jasmani, Olahraga, dan Kesehatan (PJOK)", "Fase C", "Gerak", "Aktivitas pola gerak dominan", listOf("Lari cepat", "Lompat jauh", "Lempar roket", "Senam lantai", "Permainan bola besar", "Permainan bola kecil", "Renang dasar", "Pencak silat", "Kebugaran jasmani", "Kesehatan diri"), listOf("gerak", "olahraga", "lari", "senam", "kesehatan")),
        
        // FASE D
        CapaianPembelajaranItem("Bahasa Indonesia", "Fase D", "Membaca", "Mengevaluasi informasi teks", listOf("Teks prosedur kompleks", "Teks eksplanasi", "Teks deskripsi", "Berita", "Teks negosiasi", "Teks pidato", "Teks ulasan", "Teks puisi modern", "Teks cerpen", "Teks drama"), listOf("prosedur", "eksplanasi", "berita", "negosiasi", "pidato")),
        CapaianPembelajaranItem("Ilmu Pengetahuan Alam (IPA)", "Fase D", "Sains", "Klasifikasi makhluk hidup", listOf("Struktur sel", "Sistem organisasi kehidupan", "Pencemaran lingkungan", "Pemanasan global", "Sistem tata surya", "Sistem ekskresi", "Pewarisan sifat", "Bioteknologi sederhana", "Energi alternatif", "Listrik statis"), listOf("sel", "lingkungan", "pemanasan", "surya", "listrik")),
        CapaianPembelajaranItem("Ilmu Pengetahuan Sosial (IPS)", "Fase D", "Sosial", "Interaksi sosial dan lingkungan", listOf("Letak geografis Indonesia", "Kegiatan ekonomi", "Perubahan sosial budaya", "Kondisi penduduk", "Pemberdayaan masyarakat", "Sejarah lokal", "Perdagangan internasional", "Interaksi antarnegara", "Globalisasi", "Lingkungan alam"), listOf("geografis", "ekonomi", "sosial", "penduduk", "globalisasi")),
        CapaianPembelajaranItem("Matematika", "Fase D", "Aljabar", "Pemahaman aljabar", listOf("Persamaan linear satu variabel", "Perbandingan senilai dan berbalik nilai", "Himpunan", "Relasi dan fungsi", "Bangun datar", "Teorema Pythagoras", "Statistika data tunggal", "Peluang empirik", "Aritmatika sosial", "Transformasi geometri"), listOf("persamaan", "perbandingan", "himpunan", "relasi", "statistika")),
        CapaianPembelajaranItem("Informatika", "Fase D", "Algoritma", "Berpikir komputasional", listOf("Algoritma pemrograman", "Struktur data", "Dampak sosial informatika", "Jaringan komputer", "Keamanan data", "Sistem bilangan", "Perangkat keras", "Perangkat lunak", "Interaksi manusia komputer", "Etika digital"), listOf("algoritma", "data", "jaringan", "keamanan", "etika")),
        
        // FASE E
        CapaianPembelajaranItem("Bahasa Indonesia", "Fase E", "Menulis", "Menulis gagasan kreatif", listOf("Teks Laporan Hasil Observasi", "Teks Negosiasi", "Teks Anekdot", "Hikayat", "Artikel populer", "Esai", "Resensi film", "Puisi kontemporer", "Teks opini", "Karya tulis ilmiah"), listOf("lho", "negosiasi", "anekdot", "hikayat", "artikel")),
        CapaianPembelajaranItem("Bahasa Inggris", "Fase E", "Komunikasi", "Interaksi sosial berbasis teks", listOf("Descriptive Text", "Recount Text", "Narrative Text", "Procedure Text", "Report Text", "Greeting Cards", "Announcement", "Invitation", "Advertisement", "Song Lyrics"), listOf("descriptive", "narrative", "procedure", "report", "text")),
        CapaianPembelajaranItem("Pendidikan Pancasila", "Fase E", "Pancasila", "Analisis rumusan Pancasila", listOf("Sejarah Pancasila", "Nilai-nilai Pancasila", "Norma dan hukum", "Peluang dan tantangan penerapan Pancasila", "Konstitusi", "Demokrasi Pancasila", "Hak dan kewajiban warga negara", "Penyelesaian konflik", "Harmoni dalam keberagaman", "Negara hukum"), listOf("pancasila", "norma", "hukum", "konstitusi")),
        CapaianPembelajaranItem("Informatika", "Fase E", "Algoritma", "Strategi algoritmik", listOf("Berpikir komputasional", "Flowchart dan pseudocode", "Pemrograman dasar", "Sistem komputer", "Dampak informatika", "Analisis data", "Model komputasi", "Pemrograman modular", "Basis data", "Jaringan internet"), listOf("algoritma", "flowchart", "coding", "komputer", "data")),
        CapaianPembelajaranItem("Matematika", "Fase E", "Aljabar", "Pemodelan matematika", listOf("Eksponen dan logaritma", "Barisan dan deret", "Sistem persamaan linear tiga variabel", "Fungsi kuadrat", "Statistika deskriptif", "Trigonometri dasar", "Vektor", "Peluang", "Geometri transformasi", "Analisis data"), listOf("eksponen", "logaritma", "deret", "fungsi", "statistika")),
        CapaianPembelajaranItem("Fisika", "Fase E", "Sains", "Pengukuran dan energi", listOf("Besaran dan satuan", "Vektor", "Gerak lurus", "Hukum Newton", "Energi dan daya", "Usaha dan energi", "Kalor", "Listrik dinamis", "Gelombang", "Optik"), listOf("fisika", "energi", "gerak", "listrik", "optik")),
        CapaianPembelajaranItem("Biologi", "Fase E", "Sains", "Keanekaragaman hayati dan lingkungan", listOf("Keanekaragaman hayati", "Ekosistem", "Perubahan lingkungan", "Pencemaran", "Pelestarian lingkungan", "Virus", "Struktur sel", "Metabolisme sel", "Pertumbuhan", "Perkembangan"), listOf("biologi", "ekosistem", "sel", "lingkungan")),
        CapaianPembelajaranItem("Kimia", "Fase E", "Sains", "Struktur atom dan reaksi kimia", listOf("Struktur atom", "Tabel periodik", "Ikatan kimia", "Hukum dasar kimia", "Persamaan reaksi", "Konsep mol", "Termokimia", "Laju reaksi", "Kesetimbangan kimia", "Asam dan basa"), listOf("kimia", "atom", "reaksi", "ikatan")),
        CapaianPembelajaranItem("Sejarah", "Fase E", "Sosial", "Peristiwa sejarah dunia dan Indonesia", listOf("Sejarah sebagai ilmu", "Manusia dan waktu", "Manusia dan ruang", "Asal usul nenek moyang", "Kerajaan Hindu-Buddha", "Kerajaan Islam", "Kolonialisme", "Pergerakan Nasional", "Proklamasi", "Masa Orde Baru"), listOf("sejarah", "peristiwa", "kerajaan", "kolonial")),
        CapaianPembelajaranItem("Geografi", "Fase E", "Sosial", "Dasar-dasar geografi", listOf("Pengetahuan dasar geografi", "Peta dan pemetaan", "Penelitian geografi", "Fenomena geosfer", "Litosfer", "Atmosfer", "Hidrosfer", "Biosfer", "Antroposfer", "Mitigasi bencana"), listOf("geografi", "peta", "bencana", "bumi")),
        CapaianPembelajaranItem("Ekonomi", "Fase E", "Sosial", "Konsep dasar ilmu ekonomi", listOf("Kelangkaan", "Biaya peluang", "Masalah ekonomi", "Sistem ekonomi", "Peran pelaku ekonomi", "Permintaan dan penawaran", "Harga keseimbangan", "Pasar", "Inflasi", "Kebijakan moneter"), listOf("ekonomi", "uang", "pasar", "permintaan")),
        CapaianPembelajaranItem("Sosiologi", "Fase E", "Sosial", "Konsep dasar sosiologi", listOf("Sosiologi sebagai ilmu", "Individu dan masyarakat", "Interaksi sosial", "Sosialisasi", "Nilai dan norma", "Penyimpangan sosial", "Pengendalian sosial", "Kelompok sosial", "Konflik sosial", "Mobilitas sosial"), listOf("sosiologi", "masyarakat", "sosial", "norma")),
        CapaianPembelajaranItem("Pendidikan Jasmani, Olahraga, dan Kesehatan (PJOK)", "Fase E", "Gerak", "Aktivitas pola gerak dominan", listOf("Keterampilan gerak", "Pola hidup sehat", "Aktivitas kebugaran", "Permainan bola besar", "Permainan bola kecil", "Atletik", "Beladiri", "Senam", "Aktivitas air", "Pendidikan kesehatan"), listOf("gerak", "olahraga", "kesehatan", "permainan")),
        CapaianPembelajaranItem("Seni Rupa", "Fase E", "Karya", "Eksplorasi seni rupa", listOf("Dasar seni rupa", "Gambar ilustrasi", "Seni grafis", "Desain dasar", "Warna dan bentuk", "Teknik menggambar", "Kritik seni", "Apresiasi karya", "Proyek seni", "Karya tiga dimensi"), listOf("seni", "rupa", "gambar", "karya")),
        CapaianPembelajaranItem("Seni Musik", "Fase E", "Bunyi", "Eksplorasi musik", listOf("Teori musik", "Notasi balok", "Alat musik ritmis", "Alat musik melodis", "Harmoni dasar", "Aransemen lagu", "Bernyanyi", "Apresiasi musik", "Sejarah musik", "Musik tradisional"), listOf("musik", "bunyi", "nada", "seni")),
        CapaianPembelajaranItem("Pendidikan Agama Islam dan Budi Pekerti", "Fase E", "Al-Qur'an", "Pengembangan pemahaman agama", listOf("Qur'an dan hadits", "Akidah", "Akhlak", "Fikih", "Sejarah peradaban Islam", "Membaca Qur'an", "Hukum tajwid", "Penerapan akhlak", "Ibadah praktis", "Zakat dan wakaf"), listOf("agama", "islam", "quran", "akhlak")),
        
        // FASE F
        CapaianPembelajaranItem("Matematika", "Fase F", "Kalkulus", "Konsep limit dan turunan", listOf("Turunan fungsi aljabar", "Aplikasi turunan", "Integral tak tentu", "Integral tentu", "Vektor", "Matriks", "Transformasi geometri", "Peluang kejadian", "Statistika inferensial", "Deret tak hingga"), listOf("turunan", "integral", "limit", "vektor", "matriks")),
        CapaianPembelajaranItem("Bahasa Inggris", "Fase F", "Membaca", "Analisis teks kompleks", listOf("Analytical Exposition Text", "Discussion Text", "Report Text", "Narrative Text", "Review Text", "Procedure Text", "Hortatory Exposition", "Letter Writing", "News Item", "Explanation Text"), listOf("exposition", "discussion", "report", "narrative", "review")),
        CapaianPembelajaranItem("Biologi", "Fase F", "Sains", "Struktur dan fungsi biologis", listOf("Sel dan metabolisme", "Genetika dan pewarisan sifat", "Evolusi", "Bioteknologi", "Sistem imun", "Sistem reproduksi", "Sistem koordinasi", "Pertumbuhan dan perkembangan", "Lingkungan dan ekosistem", "Struktur jaringan"), listOf("sel", "genetika", "evolusi", "bioteknologi", "imun")),
        CapaianPembelajaranItem("Fisika", "Fase F", "Sains", "Mekanika dan gelombang", listOf("Gerak lurus dan melingkar", "Hukum Newton", "Usaha dan energi", "Gelombang bunyi dan cahaya", "Listrik statis dan dinamis", "Termodinamika", "Fisika inti", "Relativitas", "Optik geometri", "Fluida"), listOf("mekanika", "newton", "energi", "gelombang", "listrik")),
        CapaianPembelajaranItem("Kimia", "Fase F", "Sains", "Struktur atom dan reaksi", listOf("Struktur atom dan tabel periodik", "Ikatan kimia", "Termokimia", "Laju reaksi", "Kesetimbangan kimia", "Asam Basa", "Redoks", "Senyawa karbon", "Polimer", "Kimia lingkungan"), listOf("atom", "ikatan", "kimia", "reaksi", "kesetimbangan")),
        CapaianPembelajaranItem("Sejarah", "Fase F", "Sosial", "Peristiwa sejarah kontemporer", listOf("Dunia setelah Perang Dunia II", "Perang Dingin", "Dekolonisasi", "Organisasi Internasional", "Konflik Timur Tengah", "Perkembangan Teknologi", "Globalisasi", "Sejarah Indonesia Kontemporer", "Reformasi", "Demokrasi"), listOf("sejarah", "dunia", "perang", "globalisasi")),
        CapaianPembelajaranItem("Geografi", "Fase F", "Sosial", "Geografi pembangunan", listOf("Wilayah dan tata ruang", "Pembangunan berkelanjutan", "Kependudukan", "Ketahanan pangan", "Energi dan industri", "Lingkungan hidup", "Mitigasi bencana", "Kerjasama antarwilayah", "Geografi ekonomi", "Geopolitik"), listOf("geografi", "wilayah", "pembangunan", "bencana")),
        CapaianPembelajaranItem("Ekonomi", "Fase F", "Sosial", "Ekonomi makro dan internasional", listOf("Pendapatan nasional", "Pertumbuhan ekonomi", "Ketenagakerjaan", "Indeks harga", "Kebijakan fiskal", "Perdagangan internasional", "Neraca pembayaran", "Kerjasama internasional", "Akuntansi dasar", "Sistem keuangan"), listOf("ekonomi", "nasional", "perdagangan", "akuntansi")),
        CapaianPembelajaranItem("Sosiologi", "Fase F", "Sosial", "Permasalahan sosial dan perubahan", listOf("Permasalahan sosial", "Perubahan sosial", "Globalisasi dan dampaknya", "Modernisasi", "Pembangunan sosial", "Penelitian sosial", "Pemberdayaan masyarakat", "Kearifan lokal", "Resolusi konflik", "Masyarakat digital"), listOf("sosiologi", "perubahan", "masalah", "modernisasi")),
        CapaianPembelajaranItem("Bahasa Indonesia", "Fase F", "Analisis", "Analisis teks kompleks", listOf("Teks editorial", "Teks drama", "Novel", "Kritik sastra", "Esai sastra", "Teks laporan penelitian", "Proposal", "Artikel ilmiah", "Pidato persuasif", "Teks berita mendalam"), listOf("teks", "analisis", "sastra", "penelitian", "karya")),
        CapaianPembelajaranItem("Pendidikan Pancasila", "Fase F", "Pancasila", "Analisis demokrasi dan hukum", listOf("Demokrasi di Indonesia", "Sistem hukum dan peradilan", "Peran Indonesia di dunia", "Konflik dan perdamaian", "Pembangunan nasional", "Nilai-nilai Pancasila dalam praktik", "Hak asasi manusia", "Partisipasi warga negara", "Ideologi negara", "Tantangan global"), listOf("demokrasi", "hukum", "pancasila", "indonesia")),
        CapaianPembelajaranItem("Pendidikan Jasmani, Olahraga, dan Kesehatan (PJOK)", "Fase F", "Gerak", "Aktivitas pola gerak dominan lanjutan", listOf("Keterampilan gerak kompleks", "Pola hidup sehat lanjutan", "Aktivitas kebugaran intensif", "Permainan bola besar", "Permainan bola kecil", "Atletik", "Beladiri", "Senam", "Aktivitas air", "Pendidikan kesehatan"), listOf("gerak", "olahraga", "kesehatan", "permainan")),
        CapaianPembelajaranItem("Seni Rupa", "Fase F", "Karya", "Apresiasi dan kreasi seni rupa", listOf("Analisis seni rupa", "Karya seni kontemporer", "Seni instalasi", "Media baru dalam seni", "Desain komunikasi visual", "Kritik seni", "Manajemen pameran", "Seni interaktif", "Proyek seni mandiri", "Apresiasi karya global"), listOf("seni", "rupa", "apresiasi", "karya")),
        CapaianPembelajaranItem("Seni Musik", "Fase F", "Bunyi", "Apresiasi dan pertunjukan musik", listOf("Musik kontemporer", "Musik eksperimental", "Aransemen musik", "Produksi musik", "Pertunjukan musik", "Manajemen pertunjukan", "Sejarah musik dunia", "Musik digital", "Teknik vokal lanjutan", "Ansambel musik"), listOf("musik", "bunyi", "pertunjukan", "seni")),
        CapaianPembelajaranItem("Pendidikan Agama Islam dan Budi Pekerti", "Fase F", "Al-Qur'an", "Pendalaman pemahaman agama", listOf("Studi Qur'an dan hadits", "Pemikiran Islam", "Akhlak mulia", "Fikih kontemporer", "Sejarah peradaban Islam", "Dialog antaragama", "Peran Islam di dunia", "Etika dan moral", "Ibadah dalam konteks modern", "Filosofi ibadah"), listOf("agama", "islam", "quran", "akhlak"))

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
