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
        "Sosiologi",
        // Mapel Khusus Madrasah (Kemenag)
        "Al-Qur'an Hadis",
        "Akidah Akhlak",
        "Fikih",
        "Sejarah Kebudayaan Islam (SKI)",
        "Bahasa Arab",
        "Ushul Fikih",
        "Ilmu Tafsir",
        "Ilmu Hadis"
    )

    fun getSubjectsForFase(fase: String): List<String> {
        return when (fase) {
            "Fase A", "Fase B", "Fase C" -> listOf(
                "Matematika", "Bahasa Indonesia", "Ilmu Pengetahuan Alam dan Sosial (IPAS)",
                "Pendidikan Pancasila", "Bahasa Inggris", "Informatika",
                "Pendidikan Jasmani, Olahraga, dan Kesehatan (PJOK)",
                "Seni Rupa", "Seni Musik", "Pendidikan Agama Islam dan Budi Pekerti",
                "Al-Qur'an Hadis", "Akidah Akhlak", "Fikih", "Sejarah Kebudayaan Islam (SKI)", "Bahasa Arab"
            )
            "Fase D" -> listOf(
                "Matematika", "Bahasa Indonesia", "Ilmu Pengetahuan Alam (IPA)", "Ilmu Pengetahuan Sosial (IPS)",
                "Pendidikan Pancasila", "Bahasa Inggris", "Informatika",
                "Pendidikan Jasmani, Olahraga, dan Kesehatan (PJOK)",
                "Seni Rupa", "Seni Musik", "Pendidikan Agama Islam dan Budi Pekerti",
                "Al-Qur'an Hadis", "Akidah Akhlak", "Fikih", "Sejarah Kebudayaan Islam (SKI)", "Bahasa Arab"
            )
            "Fase E" -> listOf(
                "Matematika", "Bahasa Indonesia", "Bahasa Inggris", "Pendidikan Pancasila", "Informatika",
                "Fisika", "Biologi", "Kimia", "Sejarah", "Geografi", "Ekonomi", "Sosiologi",
                "Pendidikan Jasmani, Olahraga, dan Kesehatan (PJOK)",
                "Seni Rupa", "Seni Musik", "Pendidikan Agama Islam dan Budi Pekerti",
                // Rumpun PAI & Bahasa Arab Wajib Madrasah Aliyah Kelas 10
                "Al-Qur'an Hadis", "Akidah Akhlak", "Fikih", "Sejarah Kebudayaan Islam (SKI)", "Bahasa Arab"
            )
            "Fase F" -> listOf(
                "Matematika", "Bahasa Indonesia", "Bahasa Inggris", "Pendidikan Pancasila", "Informatika",
                "Fisika", "Biologi", "Kimia", "Sejarah", "Geografi", "Ekonomi", "Sosiologi",
                "Pendidikan Jasmani, Olahraga, dan Kesehatan (PJOK)",
                "Seni Rupa", "Seni Musik", "Pendidikan Agama Islam dan Budi Pekerti",
                // Rumpun PAI Wajib & Peminatan Keagamaan Madrasah Aliyah Kelas 11-12
                "Al-Qur'an Hadis", "Akidah Akhlak", "Fikih", "Sejarah Kebudayaan Islam (SKI)", "Bahasa Arab",
                "Ushul Fikih", "Ilmu Tafsir", "Ilmu Hadis"
            )
            else -> MATA_PELAJARAN_LIST
        }
    }

    fun isMadrasahSubject(subject: String): Boolean {
        return when (subject) {
            "Al-Qur'an Hadis", "Akidah Akhlak", "Fikih", "Sejarah Kebudayaan Islam (SKI)",
            "Bahasa Arab", "Ushul Fikih", "Ilmu Tafsir", "Ilmu Hadis" -> true
            else -> false
        }
    }

    val JENIS_ASESMEN_LIST = listOf(
        "Asesmen Formatif (Selama Proses)",
        "Asesmen Sumatif (Akhir Lingkup Materi)",
        "Asesmen Sumatif (Akhir Semester)"
    )

    fun getSuggestedTopics(fase: String, subject: String): List<String> {
        android.util.Log.d("WizardDebug", "getSuggestedTopics called: fase='$fase', subject='$subject'")
        
        val results = CP_DATABASE
            .filter { it.subject.equals(subject, ignoreCase = true) && it.fase.equals(fase, ignoreCase = true) }
        android.util.Log.d("WizardDebug", "Found ${results.size} matches in CP_DATABASE")
        
        val topics = results.flatMap { it.suggestedTujuan }.distinct()
        if (topics.isNotEmpty()) {
            return topics
        }
        
        // Fallback suggestions for any subject/fase combination to ensure suggestions are never empty
        return listOf(
            "Pengantar dan Pemahaman Konsep Dasar $subject",
            "Eksplorasi Materi Esensial $subject ($fase)",
            "Aktivitas Praktikum dan Diskusi Kontekstual",
            "Penerapan Konsep dalam Kehidupan Sehari-hari",
            "Refleksi dan Evaluasi Pemahaman Peserta Didik"
        )
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

    // 10 Nilai Utama Profil Pelajar Rahmatan Lil 'Alamin (PPRA) Kemenag RI (KMA No. 347/2022)
    val PROFIL_PELAJAR_RAHMATAN_LIL_ALAMIN = listOf(
        PpraValueDimension(
            id = "ppra_berkeadaban",
            title = "Berkeadaban (Ta'addub)",
            description = "Menjunjung tinggi akhlak mulia, karakter kesantunan, adab, dan sopan santun dalam berinteraksi.",
            subValues = listOf("Kesantunan berbahasa dan bersikap", "Menghormati guru, orang tua, dan sesama", "Integritas moral")
        ),
        PpraValueDimension(
            id = "ppra_keteladanan",
            title = "Keteladanan (Qudwah)",
            description = "Mengambil inisiatif, memelopori kebaikan, dan menjadi uswah hasanah (panutan teladan) bagi orang lain.",
            subValues = listOf("Memelopori kebaikan", "Konsistensi ucapan dan perbuatan", "Kepemimpinan amanah")
        ),
        PpraValueDimension(
            id = "ppra_kewarganegaraan",
            title = "Kewarganegaraan & Kebangsaan (Muwathanah)",
            description = "Menunjukkan rasa cinta tanah air, kesetiaan pada NKRI, dan komitmen menegakkan persatuan bangsa.",
            subValues = listOf("Cinta tanah air", "Kepatuhan pada hukum dan norma", "Penghargaan keragaman suku bangsa")
        ),
        PpraValueDimension(
            id = "ppra_moderat",
            title = "Mengambil Jalan Tengah (Tawassuth)",
            description = "Memahami dan mengamalkan ajaran agama secara tidak ekstrem (ifrath dan tafrith) dan bersikap proporsional.",
            subValues = listOf("Menghindari pemikiran ekstrem", "Menjaga keseimbangan dalam beragama", "Sikap bijaksana dalam menyikapi perbedaan")
        ),
        PpraValueDimension(
            id = "ppra_berimbang",
            title = "Berimbang (Tawazun)",
            description = "Keseimbangan menyeluruh antara kepentingan dunia dan akhirat, rasio dan wahyu, hak dan kewajiban.",
            subValues = listOf("Keseimbangan rohani dan jasmani", "Keseimbangan ilmu dan amal", "Keseimbangan hak dan kewajiban")
        ),
        PpraValueDimension(
            id = "ppra_adil",
            title = "Lurus dan Tegas / Adil (I'tidal)",
            description = "Menempatkan sesuatu pada tempatnya dan memperlakukan hak serta kewajiban secara proporsional dan adil.",
            subValues = listOf("Bertindak adil tanpa diskriminasi", "Objektivitas dalam menilai", "Membela kebenaran secara santun")
        ),
        PpraValueDimension(
            id = "ppra_kesetaraan",
            title = "Kesetaraan (Musawah)",
            description = "Menghargai martabat sesama manusia tanpa diskriminasi latar belakang suku, ras, gender, dan strata sosial.",
            subValues = listOf("Menghormati kesetaraan gender dan sosial", "Anti-bullying dan anti-perundungan", "Inklusivitas belajar")
        ),
        PpraValueDimension(
            id = "ppra_musyawarah",
            title = "Musyawarah (Syura)",
            description = "Mengutamakan dialog, konsultasi timbal balik, dan mufakat dalam memecahkan masalah bersama.",
            subValues = listOf("Keterbukaan menerima kritik dan saran", "Mencari konsensus kemaslahatan", "Demokratis islami")
        ),
        PpraValueDimension(
            id = "ppra_toleransi",
            title = "Toleransi (Tasamuh)",
            description = "Menghargai perbedaan keyakinan, budaya, tradisi, dan pendapat dengan penuh kedewasaan.",
            subValues = listOf("Menghargai perbedaan pendapat fiqhiyyah", "Kerukunan antarumat beragama", "Saling menghargai hak beribadah")
        ),
        PpraValueDimension(
            id = "ppra_dinamis",
            title = "Dinamis dan Inovatif (Tathawwur wa Ibtikar)",
            description = "Selalu terbuka pada perubahan positif, pembaharuan iptek, dan adaptif terhadap kemajuan zaman.",
            subValues = listOf("Kreativitas pemikiran", "Pemanfaatan teknologi secara bijak", "Pikiran kritis dan solutif")
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
        // FASE A - SD / MI
        CapaianPembelajaranItem("Matematika", "Fase A", "Bilangan", "Pemahaman bilangan cacah sampai 100", listOf("Membilang 1-20", "Penjumlahan 1-20", "Pengurangan 1-20", "Nilai tempat puluhan", "Membandingkan bilangan", "Pola gambar", "Urutan bilangan", "Satuan waktu dasar", "Satuan panjang tidak baku", "Pengenalan bangun datar"), listOf("bilangan", "tambah", "kurang", "cacah", "bangun")),
        CapaianPembelajaranItem("Bahasa Indonesia", "Fase A", "Membaca", "Pemahaman teks sederhana", listOf("Suku kata", "Cerita pendek", "Kosakata baru", "Membaca nyaring", "Kalimat sederhana", "Teks deskripsi diri", "Puisi anak", "Dongeng", "Urutan cerita", "Informasi penting"), listOf("baca", "cerita", "kalimat", "puisi", "dongeng")),
        CapaianPembelajaranItem("Pendidikan Pancasila", "Fase A", "Pancasila", "Pengenalan nilai Pancasila", listOf("Simbol Pancasila", "Sila pertama", "Aturan di rumah", "Gotong royong", "Identitas diri", "Hak anak", "Kewajiban di rumah", "Keragaman teman", "Saling menghormati", "Norma kesopanan"), listOf("pancasila", "aturan", "identitas", "norma", "hak")),
        CapaianPembelajaranItem("Ilmu Pengetahuan Alam dan Sosial (IPAS)", "Fase A", "Sains", "Pengenalan diri dan lingkungan", listOf("Anggota tubuh", "Pancaindera", "Benda di sekitar", "Cuaca", "Tumbuhan di rumah", "Hewan peliharaan", "Kebersihan diri", "Kesehatan makanan", "Lingkungan rumah", "Perubahan musim"), listOf("tubuh", "benda", "cuaca", "tumbuhan", "sehat")),
        CapaianPembelajaranItem("Seni Rupa", "Fase A", "Menggambar", "Eksplorasi garis dan bentuk", listOf("Garis lurus", "Garis lengkung", "Bentuk dasar", "Warna primer", "Warna sekunder", "Gambar bebas", "Teknik arsir dasar", "Kolase", "Mozaik", "Bahan alam"), listOf("garis", "bentuk", "warna", "gambar", "seni")),
        // MI FASE A (Kemenag)
        CapaianPembelajaranItem("Al-Qur'an Hadis", "Fase A", "Huruf Hijaiyah & Surat Pendek", "Mengenal huruf hijaiyah berharakat dan surat-surat pendek", listOf("Mengenal Huruf Hijaiyah Berharakat Tunggal", "Membaca Surat Al-Fatihah dan An-Nas", "Membaca Surat Al-Falaq dan Al-Ikhlas", "Hukum Bacaan Ghunnah", "Hadis tentang Kebersihan", "Hadis tentang Kasih Sayang"), listOf("hijaiyah", "fatihah", "ikhlas", "ghunnah", "kebersihan")),
        CapaianPembelajaranItem("Akidah Akhlak", "Fase A", "Kalimat Thayyibah & Rukun Iman", "Mengenal kalimat thayyibah, rukun iman, dan akhlak terpuji", listOf("Kalimat Basmalah dan Hamdalah", "Rukun Iman kepada Allah dan Malaikat", "Asmaul Husna: Ar-Rahman dan Ar-Rahim", "Adab Mandi dan Berpakaian", "Sikap Hormat kepada Orang Tua dan Guru", "Menghindari Sikap Pemarah"), listOf("basmalah", "hamdalah", "iman", "rahman", "adab")),
        CapaianPembelajaranItem("Fikih", "Fase A", "Rukun Islam & Thaharah", "Mengenal rukun Islam, thaharah, dan tata cara wudhu", listOf("Mengenal Lima Rukun Islam", "Tata Cara Bersuci (Thaharah) dari Najis", "Tata Cara dan Rukun Berwudhu", "Adab Buang Air (Istinja')", "Praktik Shalat Fardhu Sederhana", "Azan dan Iqamah"), listOf("rukun islam", "wudhu", "thaharah", "shalat", "istinja")),
        CapaianPembelajaranItem("Bahasa Arab", "Fase A", "Kosakata Dasar", "Mengenal kosakata bahasa Arab seputar diri dan keluarga", listOf("Kosakata Anggota Tubuh (A'dha-ul Jismi)", "Peralatan Madrasah (Al-Adawatul Madrasaniyyah)", "Warna-Warna (Al-Alwan)", "Keluargaku (Usrati)", "Angka 1-10 dalam Bahasa Arab"), listOf("jismi", "madrasah", "alwan", "usrah", "angka arab")),
        
        // FASE B - SD / MI
        CapaianPembelajaranItem("Matematika", "Fase B", "Bilangan", "Pemahaman bilangan cacah sampai 10.000", listOf("Perkalian susun", "Pembagian susun", "Pecahan senilai", "Pola bilangan", "Uang dan nilai", "Bangun datar", "Keliling", "Luas", "Simetri", "Sudut"), listOf("kali", "bagi", "pecahan", "uang", "bangun")),
        CapaianPembelajaranItem("Ilmu Pengetahuan Alam dan Sosial (IPAS)", "Fase B", "Sains", "Hubungan bentuk dan fungsi tubuh", listOf("Fotosintesis", "Siklus hidup hewan", "Wujud zat", "Gaya di sekitar", "Transformasi energi", "Ekosistem sungai", "Daur air", "Magnet", "Bunyi", "Cahaya"), listOf("tumbuhan", "hewan", "zat", "gaya", "energi")),
        CapaianPembelajaranItem("Pendidikan Pancasila", "Fase B", "Pancasila", "Penerapan nilai Pancasila", listOf("Sila dalam kehidupan", "Gotong royong", "Aturan sekolah", "Hak dan kewajiban", "Keragaman budaya", "Musyawarah", "Simbol negara", "Pahlawan", "Lingkungan masyarakat", "Keberagaman adat"), listOf("pancasila", "hak", "kewajiban", "budaya")),
        CapaianPembelajaranItem("Bahasa Indonesia", "Fase B", "Menulis", "Menulis teks deskripsi", listOf("Deskripsi benda", "Teks narasi", "Surat pribadi", "Kalimat efektif", "Paragraf sederhana", "Laporan pengamatan", "Pesan singkat", "Puisi bebas", "Ringkasan cerita", "Informasi poster"), listOf("deskripsi", "narasi", "surat", "paragraf")),
        CapaianPembelajaranItem("Seni Musik", "Fase B", "Bunyi", "Eksplorasi bunyi dan irama", listOf("Nada dasar", "Tempo", "Dinamika", "Alat musik ritmis", "Alat musik melodis", "Lagu wajib nasional", "Lagu daerah", "Pola irama", "Notasi angka", "Bernyanyi bersama"), listOf("bunyi", "irama", "nada", "musik", "lagu")),
        // MI FASE B (Kemenag)
        CapaianPembelajaranItem("Al-Qur'an Hadis", "Fase B", "Hukum Tajwid & Surat Pilihan", "Membaca dan memahami hukum tajwid serta surat pilihan", listOf("Hukum Nun Sukun & Tanwin (Idzhar, Idgham, Iqlab, Ikhfa')", "Hukum Mim Sukun", "Membaca Surat Al-Qari'ah dan At-Tin", "Memahami Surat Al-Kautsar dan Al-Ma'un", "Hadis tentang Menghormati Orang Tua", "Hadis tentang Persaudaraan Muslim"), listOf("tajwid", "idzhar", "idgham", "ikhfa", "tin", "maun")),
        CapaianPembelajaranItem("Akidah Akhlak", "Fase B", "Asmaul Husna & Akhlak Mahmudah", "Memahami asmaul husna, takdir, dan pembiasaan akhlak mulia", listOf("Asmaul Husna: Al-Adzim, Al-Bashir, Al-Adl", "Iman kepada Kitab-Kitab Allah", "Akhlak Terpuji: Amanah, Jujur, dan Percaya Diri", "Adab Bertamu dan Bersosialisasi", "Kisah Teladan Nabi Ibrahim dan Nabi Ismail", "Menghindari Sikap Kikir dan Bakhil"), listOf("asmaul husna", "kitab", "jujur", "amanah", "ibrahim")),
        CapaianPembelajaranItem("Fikih", "Fase B", "Shalat Sunnah & Puasa", "Memahami shalat fardhu, shalat sunnah, dan puasa Ramadhan", listOf("Tata Cara Shalat Berjamaah dan Masbuq", "Shalat Sunnah Rawatib dan Dhuha", "Syarat dan Rukun Puasa Ramadhan", "Puasa Sunnah (Senin-Kamis, Syawal)", "Tanda-Tanda Baligh (Ihtilam & Haid)", "Zakat Fitrah Dasar"), listOf("shalat berjamaah", "rawatib", "puasa", "baligh", "zakat fitrah")),
        CapaianPembelajaranItem("Sejarah Kebudayaan Islam (SKI)", "Fase B", "Masa Kanak-kanak & Kerasulan Nabi", "Mengenal riwayat masa kecil Nabi hingga peristiwa hijrah", listOf("Kondisi Masyarakat Yatsrib Sebelum Islam", "Kelahiran dan Masa Kanak-kanak Nabi Muhammad SAW", "Peristiwa Kerasulan dan Menerima Wahyu Pertama", "Ketabahan Nabi dan Sahabat dalam Berdakwah", "Peristiwa Hijrah Nabi ke Madinah", "Kisah Sahabat Kaum Muhajirin dan Anshar"), listOf("kelahiran nabi", "wahyu", "hijrah", "muhajirin", "anshar")),
        CapaianPembelajaranItem("Bahasa Arab", "Fase B", "Frasa & Kalimat Sederhana", "Menyusun frasa dan memahami percakapan sehari-hari", listOf("Kegiatan Sehari-hari (Al-Ansyithatul Yaumiyyah)", "Alamat Rumah (Al-'Unwan)", "Profesi / Cita-Cita (Al-Mihnah)", "Di Ruang Tamu dan Ruang Belajar", "Kaidah Isim Isyarah (Hadza, Hadzihi, Dzalika, Tilka)"), listOf("unwan", "mihnah", "isyarah", "hadza", "hadzihi")),

        // FASE C - SD / MI
        CapaianPembelajaranItem("Matematika", "Fase C", "Bilangan", "Operasi hitung bilangan pecahan", listOf("Penjumlahan pecahan", "Perkalian desimal", "Rasio dan perbandingan", "Skala", "Bangun ruang sederhana", "Volume bangun ruang", "Data statistika", "Modus", "Median", "Rata-rata"), listOf("pecahan", "desimal", "rasio", "bangun", "statistika")),
        CapaianPembelajaranItem("Ilmu Pengetahuan Alam dan Sosial (IPAS)", "Fase C", "Sains", "Sistem organ tubuh manusia", listOf("Sistem pencernaan", "Sistem pernapasan", "Rantai makanan", "Ekosistem darat", "Kelestarian lingkungan", "Sistem peredaran darah", "Sistem gerak", "Kelainan organ", "Pelestarian hewan", "Adaptasi makhluk hidup"), listOf("pencernaan", "pernapasan", "ekosistem", "organ")),
        CapaianPembelajaranItem("Bahasa Indonesia", "Fase C", "Membaca", "Menganalisis informasi teks", listOf("Ide pokok", "Teks narasi kompleks", "Teks informasi", "Resensi buku", "Argumen sederhana", "Teks eksposisi", "Wawancara", "Laporan perjalanan", "Cerita rakyat", "Analisis berita"), listOf("ide pokok", "teks", "argumen", "resensi", "wawancara")),
        CapaianPembelajaranItem("Pendidikan Pancasila", "Fase C", "Pancasila", "Konstitusi dan norma", listOf("UUD 1945", "Norma masyarakat", "Demokrasi sekolah", "Pancasila dalam tindakan", "Kebinekaan Indonesia", "Hak asasi manusia", "Kedaulatan rakyat", "Sistem pemerintahan", "Hukum di Indonesia", "Pendidikan karakter"), listOf("uud", "norma", "demokrasi", "pancasila", "hak")),
        CapaianPembelajaranItem("Pendidikan Jasmani, Olahraga, dan Kesehatan (PJOK)", "Fase C", "Gerak", "Aktivitas pola gerak dominan", listOf("Lari cepat", "Lompat jauh", "Lempar roket", "Senam lantai", "Permainan bola besar", "Permainan bola kecil", "Renang dasar", "Pencak silat", "Kebugaran jasmani", "Kesehatan diri"), listOf("gerak", "olahraga", "lari", "senam", "kesehatan")),
        // MI FASE C (Kemenag)
        CapaianPembelajaranItem("Al-Qur'an Hadis", "Fase C", "Mad Thabi'i & Hadis Tematik", "Memahami hukum mad, waqaf, dan hadis tematik sosial", listOf("Hukum Bacaan Mad Thabi'i dan Mad Wajib/Jaiz", "Hukum Bacaan Idgham Bighunnah dan Bilaghunnah", "Surat Al-Insyirah dan Ad-Dhuha", "Surat Al-Bayyinah dan Al-Alaq", "Hadis tentang Menjaga Kelestarian Alam", "Hadis tentang Ciri Orang Munafik"), listOf("mad", "insyirah", "dhuha", "bayyinah", "munafik")),
        CapaianPembelajaranItem("Akidah Akhlak", "Fase C", "Hari Akhir & Qada-Qadar", "Memahami hari akhir, qada-qadar, dan pembiasaan adab pergaulan", listOf("Iman kepada Hari Akhir dan Tanda-Tandanya", "Iman kepada Qada dan Qadar Allah", "Asmaul Husna: Al-Muhyi, Al-Mumit, Al-Baqi", "Adab Bertetangga dan Berteman", "Kisah Keteladanan Sahabat Abu Bakar dan Bilal bin Rabah", "Menghindari Sikap Dengki dan Hasad"), listOf("hari akhir", "kiamat", "qada qadar", "tetangga", "hasad")),
        CapaianPembelajaranItem("Fikih", "Fase C", "Zakat, Qurban, & Haji", "Memahami zakat mal, infak, sedekah, qurban, dan dasar ibadah haji", listOf("Zakat Mal dan Perhitungannya", "Infak, Sedekah, dan Hadiah dalam Islam", "Ketentuan Qurban dan Aqiqah", "Manasik Haji dan Umrah Dasar", "Makanan dan Minuman Halal-Haram", "Jual Beli yang Sah dan Terlarang"), listOf("zakat mal", "sedekah", "qurban", "aqiqah", "haji", "halal haram")),
        CapaianPembelajaranItem("Sejarah Kebudayaan Islam (SKI)", "Fase C", "Khulafaur Rasyidin & Walisongo", "Menganalisis masa Khulafaur Rasyidin dan dakwah Walisongo", listOf("Kepemimpinan Khalifah Abu Bakar dan Umar", "Kepemimpinan Khalifah Utsman dan Ali", "Sejarah Perkembangan Islam di Nusantara", "Peran Sunan Maulana Malik Ibrahim dan Sunan Ampel", "Peran Sunan Bonang, Drajat, dan Giri", "Peran Sunan Kalijaga, Kudus, Muria, Gunung Jati"), listOf("khulafaur rasyidin", "walisongo", "sunan kalijaga", "nusantara")),
        CapaianPembelajaranItem("Bahasa Arab", "Fase C", "Teks Deskripsi & Dialog", "Menganalisis teks narasi dan dialog bahasa Arab", listOf("Jam dan Waktu (As-Sa'ah)", "Di Kantin dan Perpustakaan Madrasah", "Piknik dan Liburan (Al-'Uthlah)", "Di Rumah Sakit / Kesehatan (Fiil Mustasyfa)", "Kaidah Fi'il Mudhari' dan Dhomir Mutthashil"), listOf("saah", "uthlah", "mustasyfa", "mudhari", "dhomir")),
        
        // FASE D - SMP / MTs
        CapaianPembelajaranItem("Bahasa Indonesia", "Fase D", "Membaca", "Mengevaluasi informasi teks", listOf("Teks prosedur kompleks", "Teks eksplanasi", "Teks deskripsi", "Berita", "Teks negosiasi", "Teks pidato", "Teks ulasan", "Teks puisi modern", "Teks cerpen", "Teks drama"), listOf("prosedur", "eksplanasi", "berita", "negosiasi", "pidato")),
        CapaianPembelajaranItem("Ilmu Pengetahuan Alam (IPA)", "Fase D", "Sains", "Klasifikasi makhluk hidup", listOf("Struktur sel", "Sistem organisasi kehidupan", "Pencemaran lingkungan", "Pemanasan global", "Sistem tata surya", "Sistem ekskresi", "Pewarisan sifat", "Bioteknologi sederhana", "Energi alternatif", "Listrik statis"), listOf("sel", "lingkungan", "pemanasan", "surya", "listrik")),
        CapaianPembelajaranItem("Ilmu Pengetahuan Sosial (IPS)", "Fase D", "Sosial", "Interaksi sosial dan lingkungan", listOf("Letak geografis Indonesia", "Kegiatan ekonomi", "Perubahan sosial budaya", "Kondisi penduduk", "Pemberdayaan masyarakat", "Sejarah lokal", "Perdagangan internasional", "Interaksi antarnegara", "Globalisasi", "Lingkungan alam"), listOf("geografis", "ekonomi", "sosial", "penduduk", "globalisasi")),
        CapaianPembelajaranItem("Matematika", "Fase D", "Aljabar", "Pemahaman aljabar", listOf("Persamaan linear satu variabel", "Perbandingan senilai dan berbalik nilai", "Himpunan", "Relasi dan fungsi", "Bangun datar", "Teorema Pythagoras", "Statistika data tunggal", "Peluang empirik", "Aritmatika sosial", "Transformasi geometri"), listOf("persamaan", "perbandingan", "himpunan", "relasi", "statistika")),
        CapaianPembelajaranItem("Informatika", "Fase D", "Algoritma", "Berpikir komputasional", listOf("Algoritma pemrograman", "Struktur data", "Dampak sosial informatika", "Jaringan komputer", "Keamanan data", "Sistem bilangan", "Perangkat keras", "Perangkat lunak", "Interaksi manusia komputer", "Etika digital"), listOf("algoritma", "data", "jaringan", "keamanan", "etika")),
        // MTs FASE D (Kemenag)
        CapaianPembelajaranItem("Al-Qur'an Hadis", "Fase D", "Tajwid Kompleks & Hadis Sosial", "Menganalisis hukum tajwid mad, waqaf, dan hadis etika sosial", listOf("Hukum Mad Silah, Mad Badal, Mad Tamkin, Mad Farqi", "Hukum Waqaf dan Ibtida'", "Ayat Al-Qur'an tentang Optimisme dan Sabar (QS. Al-Balad)", "Ayat tentang Menuntut Ilmu (QS. Al-Mujadilah: 11)", "Hadis tentang Menjaga Lisan dan Tanggung Jawab", "Hadis tentang Kejujuran dalam Muamalah"), listOf("mad silah", "waqaf", "sabar", "mujadilah", "kejujuran")),
        CapaianPembelajaranItem("Akidah Akhlak", "Fase D", "Kalam Dasar & Akhlak Terpuji", "Memahami konsep akidah Islam, asmaul husna, dan adab Islami", listOf("Konsep Dasar Akidah Islam dan Dalil Aqli-Naqli", "Sifat Wajib, Mustahil, dan Jaiz bagi Allah", "Asmaul Husna: Al-Aziz, Al-Ghaffar, Al-Basith, An-Nafi'", "Adab Berbakti kepada Orang Tua dan Guru", "Menghindari Sikap Nifaq, Khianat, dan Ghadab (Marah)", "Kisah Keteladanan Sahabat Ashabul Kahfi"), listOf("akidah", "sifat wajib", "asmaul husna", "ghadab", "nifaq", "ashabul kahfi")),
        CapaianPembelajaranItem("Fikih", "Fase D", "Thaharah Hadats Besar & Sujud", "Menganalisis mandi wajib, sujud sahwi/tilawah/syukur, dan mawaris dasar", listOf("Mandi Wajib dan Sebab-sebab Berhadats Besar", "Shalat Sunnah Muakkad dan Ghairu Muakkad", "Sujud Sahwi, Sujud Tilawah, dan Sujud Syukur", "Zakat Fitrah dan Zakat Mal (Emas, Pertanian, Ternak)", "Hukum Riba, Jual Beli, dan Pinjam Meminjam (Qardh)", "Penyembelihan Hewan dan Binatang Halal-Haram"), listOf("mandi wajib", "sujud sahwi", "sujud tilawah", "riba", "zakat", "sembelih")),
        CapaianPembelajaranItem("Sejarah Kebudayaan Islam (SKI)", "Fase D", "Dinasti Umayyah, Abbasiyah, Ayyubiyah", "Menganalisis sejarah kemajuan Dinasti Umayyah, Abbasiyah, dan Ayyubiyah", listOf("Sejarah Berdirinya Dinasti Bani Umayyah di Damaskus", "Kemajuan Ilmu Pengetahuan Masa Bani Abbasiyah", "Peran Shalahuddin Al-Ayyubi pada Masa Dinasti Ayyubiyah", "Perkembangan Lembaga Pendidikan Islam (Madrasah Nizhamiyah)", "Tradisi dan Upacara Adat Islam di Berbagai Suku Nusantara", "Pondok Pesantren sebagai Pusat Dakwah Islam Nusantara"), listOf("bani umayyah", "abbasiyah", "ayyubiyah", "shalahuddin", "pesantren", "nusantara")),
        CapaianPembelajaranItem("Bahasa Arab", "Fase D", "Percakapan & Tata Bahasa MTs", "Menyusun teks dialog dan narasi dengan kaidah nahwu dasar", listOf("Kegiatan Sekolah (Fil Madrasah)", "Kegiatan di Rumah (Fil Bait)", "Di Rumah Sakit & Kesehatan (At-Tibb)", "Perjalanan dan Pariwisata (Ar-Rihlah)", "Kaidah Fi'il Madhi, Mudhari', dan Amr", "Kaidah Fa'il dan Maf'ul Bih (Jumlah Fi'liyyah)"), listOf("rihlah", "tibb", "madhi", "mudhari", "fail", "maful")),
        
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
        
        // FASE E - MADRASAH ALIYAH (KEMENAG)
        CapaianPembelajaranItem(
            subject = "Al-Qur'an Hadis",
            fase = "Fase E",
            elemen = "Al-Qur'an & Hadis",
            capaianText = "Menganalisis ayat Al-Qur'an dan Hadis tentang penciptaan manusia, keikhlasan beribadah, toleransi beragama, serta etika pergaulan remaja dan keilmuan.",
            suggestedTujuan = listOf(
                "Ayat Al-Qur'an tentang Hakikat Penciptaan Manusia (QS. Al-Mu'minun: 12-14)",
                "Keutamaan Menuntut Ilmu & Menghormati Ulama (QS. At-Taubah: 122 & Hadis)",
                "Ikhlas dalam Beribadah dan Beramal (QS. Al-An'am: 162-163)",
                "Hukum Tajwid Lanjutan: Mad Far'i, Waqaf, dan Ibtida'",
                "Toleransi dan Kerukunan Hidup Berbangsa (QS. Al-Kafirun & QS. Yunus: 40-41)",
                "Menjaga Kelestarian Lingkungan Hidup Menurut Al-Qur'an & Hadis",
                "Etika Bergaul dan Menghindari Pergaulan Bebas (QS. Al-Isra: 32)",
                "Kaidah Memahami Hadis Shahih, Hasan, dan Dha'if"
            ),
            defaultKeywords = listOf("quran", "hadis", "ayat", "tajwid", "ikhlas", "toleransi", "ilmu")
        ),
        CapaianPembelajaranItem(
            subject = "Akidah Akhlak",
            fase = "Fase E",
            elemen = "Akidah & Akhlak Islam",
            capaianText = "Menganalisis konsep akidah Islam, asmaul husna, penghayatan akhlak terpuji (mahmudah), serta pencegahan akhlak tercela (madzmumah) di era modern.",
            suggestedTujuan = listOf(
                "Hakikat Akidah Islam dan Tauhid Rububiyah, Uluhiyah, Asma wa Sifat",
                "Memahami & Meneladani Asmaul Husna (Al-Karim, Al-Mu'min, Al-Wakil, Al-Matin)",
                "Menghayati Nilai Akhlak Terpuji: Syaja'ah (Keberanian) & Hikmah",
                "Menghindari Akhlak Tercela: Israf, Tabdzir, Riya', dan Sum'ah",
                "Etika Berpakaian, Berhias, dan Adab Menggunakan Media Sosial",
                "Kisah Keteladanan Sahabat Nabi (Abu Bakar & Umar bin Khattab)",
                "Menjauhi Perilaku LGBT, Pornografi, dan Minuman Keras",
                "Memperkokoh Kerukunan (Ukhuwah Islamiyah, Wathaniyah, dan Insaniyah)"
            ),
            defaultKeywords = listOf("akidah", "akhlak", "tauhid", "asmaul husna", "syaja'ah", "ukhuwah", "riya")
        ),
        CapaianPembelajaranItem(
            subject = "Fikih",
            fase = "Fase E",
            elemen = "Syariat & Muamalah",
            capaianText = "Menganalisis fikih ibadah praktis, pengurusan jenazah, zakat, wakaf, haji-umrah, serta prinsip dasar fikih muamalah kontemporer.",
            suggestedTujuan = listOf(
                "Fikih Ibadah: Shalat Jamak-Qashar, Khauf, dan Shalat Jenazah",
                "Tata Cara Penyelenggaraan Jenazah Sesuai Sunnah (Memandikan, Mengafani, Menshalati, Mengubur)",
                "Zakat Profesi, Saham, dan Pengelolaan Zakat Produktif",
                "Hukum Wakaf Uang dan Produktif dalam Pemberdayaan Umat",
                "Ibadah Haji & Umrah: Syarat, Rukun, Wajib, dan Manasik Modern",
                "Qurban dan Aqiqah: Tata Cara Penyembelihan Halal & Syarat Hewan",
                "Prinsip Akad Jual Beli (Ba'i), Khiyar, dan Riba dalam Transaksi Digital",
                "Fikih Muamalah: Akad Mudharabah, Musyarakah, dan Wadiah"
            ),
            defaultKeywords = listOf("fikih", "jenazah", "zakat", "wakaf", "haji", "qurban", "muamalah", "riba", "akad")
        ),
        CapaianPembelajaranItem(
            subject = "Sejarah Kebudayaan Islam (SKI)",
            fase = "Fase E",
            elemen = "Sejarah & Peradaban Islam",
            capaianText = "Menganalisis peradaban bangsa Arab pra-Islam, kepemimpinan Rasulullah di Makkah-Madinah, dan masa Khulafaur Rasyidin.",
            suggestedTujuan = listOf(
                "Kondisi Sosial, Politik, dan Agama Masyarakat Arab Pra-Islam",
                "Strategi Dakwah Rasulullah SAW Periode Makkah (Keluarga & Terbuka)",
                "Strategi Dakwah & Pembentukan Piagam Madinah Periode Madinah",
                "Perang Badar, Uhud, dan Khandaq serta Nilai Diplomasi Fathu Makkah",
                "Kepemimpinan Khalifah Abu Bakar Ash-Shiddiq: Penumpasan Nabi Palsu",
                "Kepemimpinan Khalifah Umar bin Khattab: Reformasi Administrasi & Futuhat",
                "Kepemimpinan Khalifah Utsman bin Affan: Kodifikasi Mushaf Al-Qur'an",
                "Kepemimpinan Khalifah Ali bin Abi Thalib: Ujian Perang Saudara & Integritas"
            ),
            defaultKeywords = listOf("ski", "sejarah", "rasulullah", "madinah", "piagam", "khulafaur rasyidin", "makkah")
        ),
        CapaianPembelajaranItem(
            subject = "Bahasa Arab",
            fase = "Fase E",
            elemen = "Istima', Kalam, Qira'ah, Kitabah",
            capaianText = "Menganalisis dan menyusun teks dialog serta narasi bahasa Arab bertema perkenalan diri, madrasah, keluarga, dan hobi menggunakan kaidah nahwu-shorof dasar.",
            suggestedTujuan = listOf(
                "Teks Hiwar: At-Ta'aruf (Perkenalan Diri & Profesi)",
                "Teks Qira'ah: Al-Madrasah wal Hayah al-Yaumiyyah (Kehidupan di Madrasah)",
                "Teks Deskriptif: Al-Usrah wal Bait (Keluarga dan Rumah)",
                "Teks Naratif: Al-Hiwayah wal Ansyithah (Hobi dan Kegemaran)",
                "Tarkib / Kaidah Nahwu: Pembagian Isim, Fi'il, dan Huruf",
                "Tarkib / Kaidah Nahwu: Mubtada' dan Khabar (Jumlah Ismiyyah)",
                "Tarkib / Kaidah Nahwu: Fi'il Madhi, Fi'il Mudhari', dan Dhomir",
                "Kitabah: Menyusun Paragraf Pendek Bahasa Arab Sederhana"
            ),
            defaultKeywords = listOf("bahasa arab", "arab", "hiwar", "nahwu", "qiraah", "tarkib", "isim", "fiil")
        ),
        
        // FASE F - UMUM
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
        CapaianPembelajaranItem("Pendidikan Agama Islam dan Budi Pekerti", "Fase F", "Al-Qur'an", "Pendalaman pemahaman agama", listOf("Studi Qur'an dan hadits", "Pemikiran Islam", "Akhlak mulia", "Fikih kontemporer", "Sejarah peradaban Islam", "Dialog antaragama", "Peran Islam di dunia", "Etika dan moral", "Ibadah dalam konteks modern", "Filosofi ibadah"), listOf("agama", "islam", "quran", "akhlak")),

        // FASE F - MADRASAH ALIYAH (KEMENAG)
        CapaianPembelajaranItem(
            subject = "Al-Qur'an Hadis",
            fase = "Fase F",
            elemen = "Al-Qur'an & Hadis Lanjutan",
            capaianText = "Menganalisis ayat Al-Qur'an dan Hadis tentang pola hidup sederhana, tanggung jawab sosial, kepemimpinan adil, serta amar ma'ruf nahi munkar.",
            suggestedTujuan = listOf(
                "Pola Hidup Sederhana & Menyantuni Kaum Dhu'afa (QS. Al-Qashash: 79-82)",
                "Tanggung Jawab Pemimpin dan Keadilan Hukum (QS. An-Nisa: 58-59)",
                "Kewajiban Dakwah & Amar Ma'ruf Nahi Munkar (QS. Ali Imran: 104 & 110)",
                "Etika Demokrasi dan Musyawarah Menurut Al-Qur'an (QS. Asy-Syura: 38)",
                "Kritik Sanad & Matan Hadis serta Derajat Perawi (Jarh wa Ta'dil)",
                "Ayat-Ayat Sains & Fenomena Alam Semesta dalam Al-Qur'an",
                "Metodologi Menolak Hoaks & Tabayyun Informasi (QS. Al-Hujurat: 6)"
            ),
            defaultKeywords = listOf("quran", "hadis", "tabayyun", "pemimpin", "sanad", "matan", "dakwah")
        ),
        CapaianPembelajaranItem(
            subject = "Akidah Akhlak",
            fase = "Fase F",
            elemen = "Ilmu Kalam & Tasawuf",
            capaianText = "Menganalisis sejarah munculnya aliran ilmu kalam (Khawarij, Murji'ah, Mu'tazilah, Asy'ariyah, Maturidiyah), tasawuf amali, dan etika profesi.",
            suggestedTujuan = listOf(
                "Sejarah Lahirnya Ilmu Kalam dan Peristiwa Tahkim (Perang Shiffin)",
                "Doktrin Aliran Kalam: Khawarij, Murji'ah, Syi'ah, dan Mu'tazilah",
                "Pokok Ajaran Ahlussunnah wal Jama'ah (Asy'ariyah dan Maturidiyah)",
                "Peran Tasawuf dalam Pembinaan Akhlak: Maqamat dan Ahwal",
                "Tokoh-Tokoh Tasawuf Terkemuka (Imam Al-Ghazali, Rabi'ah Al-Adawiyah)",
                "Etika Profesi, Integritas Kerja, dan Anti-Korupsi dalam Islam",
                "Menghindari Sikap Ekstremisme (Ghuluw) & Meneguhkan Moderasi Beragama"
            ),
            defaultKeywords = listOf("akidah", "akhlak", "kalam", "asyariyah", "tasawuf", "ghazali", "moderasi")
        ),
        CapaianPembelajaranItem(
            subject = "Fikih",
            fase = "Fase F",
            elemen = "Munakahat, Jinayat, Mawaris, Muamalah Kontemporer",
            capaianText = "Menganalisis fikih munakahat (pernikahan), fikih mawaris (kewarisan Islam), jinayat (pidana Islam), peradilan Islam, dan transaksi keuangan syariah.",
            suggestedTujuan = listOf(
                "Fikih Munakahat: Syarat, Rukun Nikah, Mahar, Walimah, dan Hak-Kewajiban Suami Istri",
                "Perceraian (Thalaq, Khulu', Fasakh, Iddah, dan Ruju')",
                "Fikih Mawaris (Faraidh): Ahli Waris Ashabah, Dzawil Furudh, dan Hitungan Waris Sederhana",
                "Fikih Jinayat: Hudud, Qishash, Diyat, dan Ta'zir dalam Kerangka Keadilan",
                "Peradilan Islam (Qadha'): Syarat Hakim, Saksi, dan Pembuktian Kasus",
                "Fikih Muamalah Digital: Cryptocurrency, Paylater, dan Fintech Syariah",
                "Fikih Medis: Transplantasi Organ, Bayi Tabung, dan Eutanasia"
            ),
            defaultKeywords = listOf("fikih", "nikah", "munakahat", "waris", "mawaris", "faraidh", "jinayat", "qishash", "fintech")
        ),
        CapaianPembelajaranItem(
            subject = "Sejarah Kebudayaan Islam (SKI)",
            fase = "Fase F",
            elemen = "Dinasti Islam & Sejarah Islam di Nusantara",
            capaianText = "Menganalisis kemajuan peradaban Dinasti Bani Umayyah, Bani Abbasiyah, Tiga Kerajaan Besar (Utsmani, Safawi, Mughal), serta sejarah dakwah Islam di Nusantara.",
            suggestedTujuan = listOf(
                "Kejayaan Dinasti Bani Umayyah di Damaskus & Andalusia (Cordoba)",
                "Zaman Keemasan Dinasti Bani Abbasiyah di Baghdad & Baitul Hikmah",
                "Peradaban Tiga Kerajaan Besar: Turki Utsmani, Safawi Persia, dan Mughal India",
                "Teori Masuknya Islam ke Nusantara (Gujarat, Makkah, Persia, dan China)",
                "Peran Wali Songo dalam Islamisasi Jawa Melalui Pendekatan Budaya",
                "Kerajaan Islam di Indonesia: Samudera Pasai, Demak, Mataram, Aceh, dan Gowa-Tallo",
                "Peran Ormas Islam (NU, Muhammadiyah, SI, Persis) dalam Perjuangan Kemerdekaan RI"
            ),
            defaultKeywords = listOf("ski", "sejarah", "umayyah", "abbasiyah", "utsmani", "wali songo", "nusantara", "demak")
        ),
        CapaianPembelajaranItem(
            subject = "Bahasa Arab",
            fase = "Fase F",
            elemen = "Balaghah & Maharah Lughawiyyah Lanjutan",
            capaianText = "Menganalisis teks sastra, pidato resmi, berita, dan dialog kompleks berbahasa Arab dengan kaidah nahwu tingkat lanjut (manshubat, majrurat).",
            suggestedTujuan = listOf(
                "Teks Khutbah / Pidato Bahasa Arab: Asy-Syabab wal Mustaqbal",
                "Teks Diskusi Ilmiah: As-Sihhah wal Bi'ah (Kesehatan dan Lingkungan)",
                "Teks Resensi / Opini: Ats-Tsaqafah wal Hadharah al-Islamiyyah",
                "Tarkib / Nahwu Lanjut: Maf'ul Bih, Maf'ul Muthlaq, Maf'ul Li Ajlih",
                "Tarkib / Nahwu Lanjut: Hal, Tamyiz, dan Istitsna'",
                "Pengenalan Balaghah: Tasybih (Penyerupaan) dan Majaz",
                "Insya' / Kitabah: Menyusun Esai dan Naskah Pidato Bahasa Arab"
            ),
            defaultKeywords = listOf("bahasa arab", "arab", "nahwu", "balaghah", "maful", "tamyiz", "tasybih", "insya")
        ),
        CapaianPembelajaranItem(
            subject = "Ushul Fikih",
            fase = "Fase F",
            elemen = "Metodologi Istinbath Hukum",
            capaianText = "Menganalisis konsep dasar Ushul Fikih, sumber hukum Islam (Al-Qur'an, Hadis, Ijma', Qiyas), kaidah ushuliyyah, maqashid syariah, dan ijtihad.",
            suggestedTujuan = listOf(
                "Pengertian, Ruang Lingkup, dan Urgensi Ushul Fikih dalam Istinbath Hukum",
                "Sumber Hukum Islam Muttafaq: Al-Qur'an, As-Sunnah, Ijma', dan Qiyas",
                "Sumber Hukum Islam Mukhtalaf: Istihsan, Maslahah Mursalah, 'Urf, Istishab, Saddudz Dzari'ah",
                "Kaidah Ushuliyyah: Amar (Perintah), Nahi (Larangan), 'Am dan Khas",
                "Kaidah Fiqhiyyah Asasiyyah: Al-Umuru bi Maqashidiha, Al-Yaqinu La Yuzalu bisy-Syakk",
                "Maqashid Asy-Syari'ah: Hifdzud Din, Nafs, 'Aql, Nasl, dan Mal",
                "Konsep Ijtihad, Taqlid, Talfiq, dan Fatwa Majelis Ulama"
            ),
            defaultKeywords = listOf("ushul fikih", "istinbath", "qiyas", "ijma", "maqashid", "ijtihad", "kaidah")
        ),
        CapaianPembelajaranItem(
            subject = "Ilmu Tafsir",
            fase = "Fase F",
            elemen = "Ulumul Qur'an & Kaidah Penafsiran",
            capaianText = "Menganalisis kaidah Ulumul Qur'an, sejarah penafsiran Al-Qur'an, metode tafsir (Tahlili, Ijmali, Muqaran, Maudhu'i), serta corak tafsir bil ma'tsur dan bir ra'yi.",
            suggestedTujuan = listOf(
                "Konsep Dasar Ilmu Tafsir dan Sejarah Perkembangan Tafsir Al-Qur'an",
                "Ulumul Qur'an: Asbabun Nuzul, Makkiyah-Madaniyah, Muhkam-Mutasyabih",
                "Nasikh dan Mansukh dalam Ayat-Ayat Hukum Al-Qur'an",
                "Metode Penafsiran Al-Qur'an: Tahlili, Ijmali, Muqaran, dan Maudhu'i (Tematik)",
                "Corak Tafsir: Tafsir bil Ma'tsur, Tafsir bir Ra'yi, dan Tafsir Ilmi (Sains)",
                "Studi Tokoh Mufassir: Ibnu Katsir, Thabari, Quraish Shihab (Tafsir Al-Mishbah)",
                "Praktik Menafsirkan Ayat Al-Qur'an Tematik Kontemporer"
            ),
            defaultKeywords = listOf("ilmu tafsir", "tafsir", "ulumul quran", "makkiyah", "madaniyah", "asbabun nuzul", "mufassir")
        ),
        CapaianPembelajaranItem(
            subject = "Ilmu Hadis",
            fase = "Fase F",
            elemen = "Ulumul Hadis & Kritik Sanad-Matan",
            capaianText = "Menganalisis sejarah kodifikasi hadis, pembagian hadis dari segi kuantitas dan kualitas, kaidah jarh wa ta'dil, serta kitab-kitab hadis mu'tabarah (Kutubus Sittah).",
            suggestedTujuan = listOf(
                "Konsep Dasar Ilmu Hadis (Riwayah & Dirayah) dan Sejarah Kodifikasi Hadis",
                "Klasifikasi Hadis dari Segi Kuantitas Perawi: Mutawatir dan Ahad (Masyhur, Aziz, Gharib)",
                "Klasifikasi Hadis dari Segi Kualitas: Shahih Lidzatihi/Lighairihi, Hasan, Dha'if, dan Maudhu'",
                "Sebab-Sebab Kelemahan Hadis: Gugurnya Sanad dan Cacat pada Perawi",
                "Pengantar Ilmu Jarh wa Ta'dil dan Tingkatan Martabat Perawi",
                "Mengenal Kitab Induk Hadis (Kutubus Sittah: Bukhari, Muslim, Abu Dawud, dll.)",
                "Metode Takhrij Hadis Sederhana Menggunakan Mu'jam / Aplikasi Hadis Digital"
            ),
            defaultKeywords = listOf("ilmu hadis", "hadis", "sanad", "matan", "mutawatir", "shahih", "dhaif", "jarh", "takhrij")
        )

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
        ),
        QuickPreset(
            title = "Hukum Mad Far'i & Tajwid (Al-Qur'an Hadis MA)",
            fase = "Fase E",
            grade = "Kelas 10",
            subject = "Al-Qur'an Hadis",
            topic = "Hukum Bacaan Tajwid Lanjutan (Mad Far'i, Waqaf, dan Ibtida')",
            timeAllocation = "2 JP (2 x 45 Menit)",
            model = "Direct Instruction (Eksplisit Interaktif)",
            dimensi = listOf("Beriman, Bertakwa kepada Tuhan YME, dan Berakhlak Mulia", "Bernalar Kritis", "Mandiri"),
            ppra = listOf("Berkeadaban (Ta'addub)", "Keteladanan (Qudwah)")
        ),
        QuickPreset(
            title = "Fikih Munakahat & Syariat (Fikih MA)",
            fase = "Fase F",
            grade = "Kelas 11",
            subject = "Fikih",
            topic = "Fikih Munakahat: Syarat, Rukun Nikah, Mahar, Walimah, dan Hak-Kewajiban Suami Istri",
            timeAllocation = "2 JP (2 x 45 Menit)",
            model = "Problem-Based Learning (PBL)",
            dimensi = listOf("Beriman, Bertakwa kepada Tuhan YME, dan Berakhlak Mulia", "Bernalar Kritis", "Bergotong Royong"),
            ppra = listOf("Mengambil Jalan Tengah (Tawassuth)", "Berkeadaban (Ta'addub)", "Lurus dan Tegas (I'tidal)")
        ),
        QuickPreset(
            title = "Sejarah Bani Abbasiyah & Baitul Hikmah (SKI MTs)",
            fase = "Fase D",
            grade = "Kelas 8",
            subject = "Sejarah Kebudayaan Islam (SKI)",
            topic = "Kemajuan Ilmu Pengetahuan Masa Bani Abbasiyah",
            timeAllocation = "2 JP (2 x 40 Menit)",
            model = "Inquiry Learning",
            dimensi = listOf("Berkebinekaan Global", "Bernalar Kritis", "Kreatif"),
            ppra = listOf("Toleransi (Tasamuh)", "Dinamis dan Inovatif (Tathawwur wa Ibtikar)")
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
    val dimensi: List<String>,
    val ppra: List<String> = emptyList()
)

data class PpraValueDimension(
    val id: String,
    val title: String,
    val description: String,
    val subValues: List<String>
)
