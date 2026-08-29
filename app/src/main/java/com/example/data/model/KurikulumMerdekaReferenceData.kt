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
        // FASE A - Matematika
        CapaianPembelajaranItem(
            subject = "Matematika",
            fase = "Fase A",
            elemen = "Bilangan",
            capaianText = "Peserta didik menunjukkan pemahaman dan intuisi bilangan (number sense) pada bilangan cacah sampai 100, dapat membaca, menulis, menentukan nilai tempat, membandingkan, mengurutkan, serta melakukan operasi penjumlahan dan pengurangan menggunakan benda-benda konkret.",
            suggestedTujuan = listOf(
                "Peserta didik mampu membilang dan menuliskan lambang bilangan cacah 1-50 secara tepat.",
                "Peserta didik mampu membandingkan dan mengurutkan dua bilangan cacah menggunakan benda konkret.",
                "Peserta didik mampu menyelesaikan masalah penjumlahan dan pengurangan bilangan cacah dalam kehidupan sehari-hari."
            ),
            defaultKeywords = listOf("bilangan", "penjumlahan", "pengurangan", "angka", "cacah", "nilai tempat")
        ),
        CapaianPembelajaranItem(
            subject = "Matematika",
            fase = "Fase A",
            elemen = "Geometri",
            capaianText = "Peserta didik dapat mengenal berbagai bangun datar (segitiga, segiempat, lingkaran) dan bangun ruang (balok, kubus, kerucut, bola) serta menyusun dan mengurai bangun datar.",
            suggestedTujuan = listOf(
                "Peserta didik dapat mengidentifikasi berbagai bentuk bangun datar di lingkungan sekitar.",
                "Peserta didik mampu menyusun bangun datar menjadi bentuk baru (tangram)."
            ),
            defaultKeywords = listOf("bangun datar", "geometri", "segitiga", "lingkaran", "kubus")
        ),

        // FASE B - Matematika
        CapaianPembelajaranItem(
            subject = "Matematika",
            fase = "Fase B",
            elemen = "Bilangan",
            capaianText = "Peserta didik menunjukkan pemahaman dan intuisi bilangan (number sense) pada bilangan cacah sampai 10.000, pecahan senilai, pecahan desimal persepuluhan dan perseratusan, serta menghubungkan pecahan desimal dan persen.",
            suggestedTujuan = listOf(
                "Peserta didik dapat menyajikan dan mengidentifikasi pecahan senilai menggunakan gambar dan benda konkret.",
                "Peserta didik dapat membandingkan dan mengurutkan pecahan dengan penyebut sama dan berbeda.",
                "Peserta didik mampu melakukan perkalian dan pembagian bilangan cacah sampai 100."
            ),
            defaultKeywords = listOf("pecahan", "pecahan senilai", "perkalian", "pembagian", "persen", "desimal")
        ),
        CapaianPembelajaranItem(
            subject = "Matematika",
            fase = "Fase B",
            elemen = "Pengukuran",
            capaianText = "Peserta didik dapat mengukur panjang dan berat benda menggunakan satuan baku (cm, m, gram, kg), serta menentukan keliling dan luas bangun datar menggunakan satuan baku dan satuan tidak baku.",
            suggestedTujuan = listOf(
                "Peserta didik dapat menghitung keliling dan luas persegi dan persegi panjang menggunakan rumus dan media kotak satuan.",
                "Peserta didik dapat mengukur berat benda menggunakan timbangan satuan baku kg dan gram."
            ),
            defaultKeywords = listOf("luas", "keliling", "pengukuran", "panjang", "berat", "satuan baku")
        ),

        // FASE C - Matematika
        CapaianPembelajaranItem(
            subject = "Matematika",
            fase = "Fase C",
            elemen = "Bilangan",
            capaianText = "Peserta didik dapat melakukan operasi hitung penjumlahan, pengurangan, perkalian, dan pembagian bilangan pecahan dan desimal, serta memahami konsep perbandingan, skala, FPB dan KPK.",
            suggestedTujuan = listOf(
                "Peserta didik dapat menyelesaikan operasi perkalian dan pembagian pecahan dengan teliti.",
                "Peserta didik mampu menerapkan konsep perbandingan dan skala pada denah dan peta.",
                "Peserta didik mampu menentukan FPB dan KPK dari dua bilangan untuk menyelesaikan masalah kontekstual."
            ),
            defaultKeywords = listOf("pecahan desimal", "perbandingan", "skala", "fpb", "kpk", "rasio")
        ),

        // FASE B - IPAS
        CapaianPembelajaranItem(
            subject = "Ilmu Pengetahuan Alam dan Sosial (IPAS)",
            fase = "Fase B",
            elemen = "Pemahaman IPAS (Sains & Sosial)",
            capaianText = "Peserta didik menganalisis hubungan antara bentuk serta fungsi bagian tubuh pada hewan dan tumbuhan, siklus hidup makhluk hidup, wujud zat dan perubahannya, bentuk energi dan perubahannya, serta keragaman kearifan lokal daerah setempat.",
            suggestedTujuan = listOf(
                "Peserta didik dapat mengidentifikasi bagian-bagian tubuh tumbuhan dan fungsinya melalui pengamatan langsung.",
                "Peserta didik dapat mendeskripsikan proses fotosintesis pada tumbuhan hijau dengan tepat.",
                "Peserta didik dapat menganalisis perubahan wujud zat dalam kehidupan sehari-hari (mencair, membeku, menguap, mengembun)."
            ),
            defaultKeywords = listOf("bagian tubuh tumbuhan", "fotosintesis", "wujud zat", "energi", "kearifan lokal", "siklus hidup")
        ),

        // FASE C - IPAS
        CapaianPembelajaranItem(
            subject = "Ilmu Pengetahuan Alam dan Sosial (IPAS)",
            fase = "Fase C",
            elemen = "Pemahaman IPAS (Sains & Sosial)",
            capaianText = "Peserta didik merefleksikan bagaimana sistem organ tubuh manusia (pernapasan, pencernaan, peredaran darah) bekerja, menganalisis hubungan antar makhluk hidup pada ekosistem (rantai makanan, jaring makanan), gaya dan gerak, serta sistem tata surya.",
            suggestedTujuan = listOf(
                "Peserta didik dapat menjelaskan alur sistem pencernaan manusia dan cara menjaga kesehatannya.",
                "Peserta didik mampu menganalisis peran produsen, konsumen, dan dekomposer dalam rantai makanan pada suatu ekosistem.",
                "Peserta didik dapat membuat model sederhana jaring-jaring makanan dan memprediksi dampak kepunahan suatu organisme."
            ),
            defaultKeywords = listOf("sistem organ", "pencernaan", "ekosistem", "rantai makanan", "tata surya", "gaya magnet")
        ),

        // FASE D - IPA
        CapaianPembelajaranItem(
            subject = "Ilmu Pengetahuan Alam (IPA)",
            fase = "Fase D",
            elemen = "Pemahaman IPA",
            capaianText = "Peserta didik mampu melakukan klasifikasi makhluk hidup, memahami struktur sel, sistem organisasi kehidupan, interaksi makhluk hidup dengan lingkungan, sifat fisika dan kimia zat, hukum gerak Newton, usaha dan energi, gelombang, serta struktur bumi dan tata surya.",
            suggestedTujuan = listOf(
                "Peserta didik dapat membedakan struktur sel hewan dan sel tumbuhan melalui pengamatan mikroskop/gambar.",
                "Peserta didik dapat menganalisis penerapan Hukum Newton I, II, dan III dalam fenomena gerak sehari-hari.",
                "Peserta didik mampu merancang solusi mitigasi pencemaran lingkungan berbasis pemanfaatan bioteknologi ramah lingkungan."
            ),
            defaultKeywords = listOf("sel", "hukum newton", "energi", "pencemaran lingkungan", "zat dan perubahan", "kalor", "gelombang")
        ),

        // FASE D - Bahasa Indonesia
        CapaianPembelajaranItem(
            subject = "Bahasa Indonesia",
            fase = "Fase D",
            elemen = "Membaca dan Memirsa",
            capaianText = "Peserta didik mampu mengevaluasi informasi berupa gagasan, pikiran, pandangan, arahan atau pesan dari teks deskripsi, laporan, narasi, eksplanasi, eksposisi dari teks tulis dan visual untuk menemukan makna yang tersurat dan tersirat.",
            suggestedTujuan = listOf(
                "Peserta didik mampu mengidentifikasi struktur dan ciri kebahasaan teks prosedur secara tepat.",
                "Peserta didik mampu menganalisis gagasan utama dan gagasan pendukung dalam teks eksplanasi ilmiah.",
                "Peserta didik dapat menulis teks deskripsi objek wisata daerah dengan memperhatikan kaidah bahasa Indonesia yang baik."
            ),
            defaultKeywords = listOf("teks deskripsi", "teks prosedur", "teks eksplanasi", "gagasan utama", "menulis", "membaca")
        ),

        // FASE E - Bahasa Indonesia
        CapaianPembelajaranItem(
            subject = "Bahasa Indonesia",
            fase = "Fase E",
            elemen = "Menulis",
            capaianText = "Peserta didik mampu menulis gagasan, pikiran, pandangan, arahan atau pesan tertulis untuk berbagai tujuan secara logis, kritis, dan kreatif dalam bentuk teks negosiasi, biografi, anekdot, laporan hasil observasi (LHO), dan puisi.",
            suggestedTujuan = listOf(
                "Peserta didik mampu menyusun Laporan Hasil Observasi (LHO) yang objektif berdasarkan fakta lapangan.",
                "Peserta didik mampu menganalisis struktur dan kaidah kebahasaan teks negosiasi dalam transaksi sosial.",
                "Peserta didik dapat memproduksi teks anekdot kritis yang santun dan sarat makna sosial."
            ),
            defaultKeywords = listOf("lho", "laporan observasi", "negosiasi", "anekdot", "biografi", "menulis kritis")
        ),

        // FASE E - Pendidikan Pancasila
        CapaianPembelajaranItem(
            subject = "Pendidikan Pancasila",
            fase = "Fase E",
            elemen = "Pancasila dan UUD NRI 1945",
            capaianText = "Peserta didik mampu menganalisis cara pandang para pendiri bangsa tentang rumusan Pancasila, mengkaji penerapan nilai-nilai Pancasila dalam kehidupan bermasyarakat dan bernegara, serta menganalisis norma dan hierarki perundang-undangan.",
            suggestedTujuan = listOf(
                "Peserta didik mampu menganalisis dinamika kelahiran Pancasila dari sidang BPUPK hingga penetapan PPKI.",
                "Peserta didik dapat memetakan contoh konkret penerapan nilai keadilan sosial dalam pencegahan intoleransi.",
                "Peserta didik dapat mengevaluasi kepatuhan terhadap norma hukum di lingkungan sekolah dan masyarakat."
            ),
            defaultKeywords = listOf("pancasila", "bpupk", "uud 1945", "norma", "hukum", "hak asasi", "kebinekaan")
        ),

        // FASE E - Informatika
        CapaianPembelajaranItem(
            subject = "Informatika",
            fase = "Fase E",
            elemen = "Berpikir Komputasional (BK) & Algoritma",
            capaianText = "Peserta didik mampu menerapkan strategi algoritmik standar pada kehidupan sehari-hari maupun pemrograman terstruktur, memahami konsep struktur data graf, pohon, serta analisis efisiensi pencarian dan pengurutan.",
            suggestedTujuan = listOf(
                "Peserta didik dapat menerapkan 4 pilar berpikir komputasional (dekomposisi, pengenalan pola, abstraksi, algoritma) dalam memecahkan masalah kompleks.",
                "Peserta didik mampu membuat alur logika program menggunakan flowchart dan pseudocode.",
                "Peserta didik dapat mengimplementasikan struktur kendali percabangan dan perulangan dalam bahasa pemrograman."
            ),
            defaultKeywords = listOf("berpikir komputasional", "algoritma", "flowchart", "pemrograman", "struktur data", "coding")
        ),

        // FASE F - Matematika
        CapaianPembelajaranItem(
            subject = "Matematika",
            fase = "Fase F",
            elemen = "Kalkulus & Aljabar",
            capaianText = "Peserta didik dapat memahami konsep limit fungsi, turunan fungsi aljabar, integral, serta menerapkan matriks dan transformasi geometri dalam pemodelan fenomena nyata.",
            suggestedTujuan = listOf(
                "Peserta didik dapat menentukan turunan fungsi aljabar menggunakan konsep limit dan rumus dasar turunan.",
                "Peserta didik mampu menerapkan turunan pertama untuk menentukan titik stasioner, nilai maksimum, dan minimum dalam masalah optimasi.",
                "Peserta didik dapat melakukan operasi perkalian matriks dan menentukan determinan serta invers matriks 2x2."
            ),
            defaultKeywords = listOf("turunan", "integral", "limit", "matriks", "transformasi geometri", "kalkulus")
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
