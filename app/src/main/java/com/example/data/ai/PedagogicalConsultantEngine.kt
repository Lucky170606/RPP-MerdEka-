package com.example.data.ai

data class ChatMessage(
    val sender: String, // "USER" or "AI"
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val source: String? = null,
    val isFallback: Boolean = false,
    val originalQuery: String? = null,
    val errorReason: String? = null
)

data class PromptInspiration(
    val title: String,
    val query: String,
    val category: String
)

object PedagogicalConsultantEngine {

    val PROMPT_INSPIRATIONS = listOf(
        PromptInspiration(
            title = "Ide Ice Breaking Kreatif",
            query = "Berikan 3 ide ice breaking 5 menit yang seru dan relevan untuk membuka pelajaran dengan energi positif!",
            category = "Manajemen Kelas"
        ),
        PromptInspiration(
            title = "Strategi Diferensiasi Kelas Heterogen",
            query = "Bagaimana cara efektif mengajar di kelas yang separuh siswanya belum lancar materi dasar sedangkan separuhnya sudah sangat mahir?",
            category = "Diferensiasi"
        ),
        PromptInspiration(
            title = "Pertanyaan Pemantik HOTS",
            query = "Bagaimana teknik menyusun pertanyaan pemantik yang bisa memicu rasa ingin tahu (curiosity) tanpa langsung memberikan jawaban?",
            category = "Pedagogik"
        ),
        PromptInspiration(
            title = "Mengatasi Siswa Pasif / Kurang Percaya Diri",
            query = "Bagaimana tips memotivasi siswa yang pendiam agar mau berpendapat saat diskusi kelompok tanpa merasa dihakimi?",
            category = "Psikologi Belajar"
        ),
        PromptInspiration(
            title = "Asesmen Formatif Tanpa Kertas (Paperless)",
            query = "Berikan 4 metode asesmen formatif cepat dan menyenangkan tanpa membebani guru dengan koreksi kertas!",
            category = "Asesmen"
        ),
        PromptInspiration(
            title = "Metode Deep Learning",
            query = "Bagaimana menerapkan metode Deep Learning (pembelajaran mendalam) dalam Kurikulum Merdeka agar siswa lebih kritis?",
            category = "Pedagogik"
        )
    )

    fun answerPedagogicalQuery(query: String): String {
        val lower = query.lowercase().trim()
        return when {
            // 1. Deep Learning / Pembelajaran Mendalam
            lower.contains("deep learning") || lower.contains("pembelajaran mendalam") || lower.contains("meaningful learning") -> {
                """
                🚀 Penerapan Metode Deep Learning (Pembelajaran Mendalam) dalam Kurikulum Merdeka:
                
                Deep Learning (Mindful, Meaningful, & Joyful Learning) menekankan penguasaan esensi konsep yang mendalam, bukan sekadar hafalan materi yang luas.
                
                1. 🔍 Eksplorasi Kontekstual Berbasis Masalah Nyata:
                   • Hadapkan siswa pada fenomena atau isu nyata di lingkungan sekitar mereka (contoh: krisis air bersih, fenomena harga pasar, atau pengelolaan sampah sekolah).
                   • Ajak siswa melakukan investigasi mandiri dan bertanya 'Mengapa ini terjadi?' dan 'Bagaimana solusinya?'.
                
                2. 🧠 Aktivitas Penalaran & Diskusi Kritis:
                   • Gunakan teknik 'Socratic Questioning' (pertanyaan berantai yang menantang asumsi awal siswa).
                   • Fasilitasi debat argumentatif terstruktur di mana setiap klaim harus didukung bukti/data yang valid.
                
                3. 🔄 Transfer Pengetahuan ke Situasi Baru:
                   • Minta siswa menguji konsep yang telah dipelajari ke studi kasus baru yang belum pernah dibahas sebelumnya.
                   • Ini melatih kemampuan berpikir adaptif dan daya nalar tingkat tinggi.
                
                4. 🪞 Refleksi Metakognitif (Reflective Thinking):
                   • Di akhir sesi, minta siswa mengevaluasi cara berpikir mereka sendiri: 'Bagaimana pemahamanku berubah setelah menganalisis topik ini?'
                """.trimIndent()
            }

            // 2. Model Problem-Based Learning (PBL) & Project-Based Learning (PjBL)
            lower.contains("problem based") || lower.contains("pbl") || lower.contains("berbasis masalah") -> {
                """
                🧩 Penerapan Model Problem-Based Learning (PBL) yang Efektif:
                
                1. Orientasi Siswa pada Masalah Autentik:
                   • Sajikan stimulus video pendek, artikel berita, atau studi kasus nyata yang memicu empati dan rasa ingin tahu.
                2. Mengorganisasi Siswa untuk Belajar:
                   • Bentuk tim kecil heterogen (3-4 siswa) dengan peran yang jelas (ketua, pencatat, pengumpul data, presenter).
                3. Membimbing Penyelidikan Mandiri & Kelompok:
                   • Guru bertindak sebagai fasilitator, memberikan pertanyaan panduan (scaffolding) tanpa mendikte jawaban.
                4. Mengembangkan & Menyajikan Hasil Karya:
                   • Siswa mempresentasikan solusi kreatif (bisa berupa prototipe, poster, atau rekomendasi tertulis).
                5. Menganalisis & Mengevaluasi Proses Pemecahan Masalah:
                   • Lakukan sesi refleksi bersama terkait efektivitas solusi yang diajukan.
                """.trimIndent()
            }

            lower.contains("project based") || lower.contains("pjbl") || lower.contains("berbasis proyek") || lower.contains("p5") -> {
                """
                🛠️ Panduan Merancang Project-Based Learning (PjBL) & P5:
                
                1. Tentukan 'Driving Question' (Pertanyaan Pemantik Proyek):
                   • Buat pertanyaan tantangan terbuka, contoh: 'Bagaimana kita bisa mengurangi jejak sampah di kantin sekolah kita?'
                2. Perencanaan Proyek Kolaboratif:
                   • Libatkan siswa dalam menyusun linimasa (timeline), pembagian tugas, dan kriteria produk akhir yang disepakati bersama.
                3. Monitoring & Umpan Balik Berkala (Milestone Check):
                   • Lakukan pengecekan progres mingguan untuk memberikan umpan balik formatif secara teratur.
                4. Pameran Karya / Gelar Wicara (Exhibition of Learning):
                   • Ajak siswa memamerkan karya kepada orang tua, teman sekelas, atau warga sekolah untuk membangun rasa bangga dan kepemilikan.
                """.trimIndent()
            }

            // 3. Ice Breaking
            lower.contains("ice breaking") || lower.contains("permainan") || lower.contains("game") || lower.contains("semangat") -> {
                """
                🎯 Rekomendasi Ice Breaking Interaktif (5-7 Menit):
                
                1. 'Tebak Gerak Kata Kunci (Kinestetik)'
                   • Guru membisikkan satu kata konsep materi (misal: 'Ekosistem' atau 'Pecahan') ke 1 perwakilan kelompok.
                   • Siswa memperagakan gerak tubuh tanpa bersuara, anggota tim menebak. Sangat efektif membangkitkan fokus dan tawa sehat.
                    
                2. 'Dua Benar Satu Mitos (Two Truths & A Lie)'
                   • Guru menampilkan 3 pernyataan di papan tulis: 2 fakta ilmiah dan 1 mitos unik.
                   • Siswa berdiri jika menganggap nomor 1 mitos, bertepuk tangan untuk nomor 2, atau mengacungkan jempol untuk nomor 3.
                    
                3. 'Rantai Kata Berantai Cepat'
                   • Siswa bergantian menyebutkan 1 kata yang berkaitan dengan topik hari ini dalam waktu 3 detik per siswa. Melatih ketangkasan asosiasi konsep.
                """.trimIndent()
            }

            // 4. Diferensiasi
            lower.contains("diferensiasi") || lower.contains("heterogen") || lower.contains("kesiapan") -> {
                """
                💡 Strategi Mengelola Kelas Heterogen (Diferensiasi Praktis):
                
                1. 'Tiered Assignment (Penugasan Bertingkat)'
                   • Bagi lembar aktivitas menjadi 3 zona:
                     - Tier 1 (Fondasi): Panduan bertahap (scaffolding tinggi) dengan diagram bantuan.
                     - Tier 2 (Standar): Penerapan mandiri konsep umum.
                     - Tier 3 (Tantangan Ekstensi): Analisis kasus terbuka, investigasi problem solving tingkat lanjut.
                      
                2. 'Peer Tutoring & Rotasi Stasiun Belajar'
                   • Tempatkan siswa mahir sebagai 'Duta Konsep' di pos belajar tertentu untuk melatih komunikasi mereka sekaligus membantu teman yang butuh penjelasan dengan bahasa sebaya.
                    
                3. 'Variasi Pilihan Produk Akhir'
                   • Berikan kebebasan siswa mengekspresikan pemahaman: boleh membuat rekaman suara penjelasan (auditori), infografis gambar (visual), atau simulasi peragaan (kinestetik).
                """.trimIndent()
            }

            // 5. Pertanyaan Pemantik & Soal HOTS
            lower.contains("pemantik") || (lower.contains("pertanyaan") && lower.contains("hots")) -> {
                """
                🧠 Rumus Menyusun Pertanyaan Pemantik HOTS yang Kuat:
                
                1. Gunakan Pertanyaan Terbuka Kontradiktif:
                   • Bukan: 'Apa pengertian sampah plastik?' (LOTS)
                   • Melainkan: 'Mengapa plastik yang sangat murah dan membantu hidup kita justru bisa menjadi ancaman terbesar bagi masa depan bumi kita?' (HOTS)
                    
                2. Kaitkan dengan Dilema Sehari-hari:
                   • 'Jika kamu diberi dana 1 juta rupiah untuk memperbaiki lingkungan sekolah, bagian mana yang akan kamu ubah terlebih dahulu dan mengapa?'
                    
                3. Ciri Pertanyaan Pemantik Efektif:
                   • Tidak memiliki jawaban tunggal 'Ya/Tidak'.
                   • Mengundang perdebatan argumentatif yang sehat.
                   • Membuat siswa ingin segera melakukan eksplorasi / mencari tahu jawabannya.
                """.trimIndent()
            }

            lower.contains("soal hots") || lower.contains("rubrik hots") -> {
                """
                📝 Panduan Menyusun Butir Asesmen HOTS:
                
                1. Wajib Menggunakan Stimulus Autentik:
                   • Awali soal dengan grafik data, kutipan berita pendek, atau diagram kasus nyata, bukan soal teori hafalan langsung.
                2. Gunakan Kata Kerja Operasional (KKO) Level C4 - C6:
                   • Menganalisis (C4): Membandingkan, mendeteksi pola hubungan.
                   • Mengevaluasi (C5): Menilai efektivitas solusi berdasarkan kriteria tertentu.
                   • Mengkreasi (C6): Merancang gagasan orisinal atau merumuskan alternatif baru.
                3. Format Soal Pilihan Ganda Kompleks:
                   • Gunakan pilihan ganda dengan lebih dari satu jawaban benar atau pernyataan Benar/Salah beralasan.
                """.trimIndent()
            }

            // 6. Siswa Pasif
            lower.contains("pasif") || lower.contains("pendiam") || lower.contains("percaya diri") || lower.contains("motivasi") -> {
                """
                🤝 Langkah Pendekatan untuk Siswa Pasif / Pemalu:
                
                1. 'Metode Think-Pair-Share':
                   • Berikan waktu 1 menit untuk berpikir sendiri (Think), lalu diskusikan berpasangan dengan 1 teman sebangku (Pair), baru kemudian berbagi ke kelompok besar (Share). Ini mengurangi kecemasan berbicara di depan umum.
                    
                2. 'Kertas Pendapat Anonim (Sticky Notes)':
                   • Minta siswa menuliskan 1 ide atau pertanyaan di kertas tempel tanpa nama dan menempelkannya di papan dinding.
                    
                3. 'Apresiasi Proses, Bukan Hanya Jawaban Benar':
                   • Puji keberanian siswa mengemukakan pendapat dengan kalimat: 'Terima kasih atas sudut pandang menarikmu, ini memperkaya diskusi kita!'
                """.trimIndent()
            }

            // 7. Asesmen Formatif
            lower.contains("asesmen") || lower.contains("formatif") || lower.contains("koreksi") -> {
                """
                📋 4 Metode Asesmen Formatif Cepat Tanpa Beban Kertas:
                
                1. 'Kartu Lampu Lalu Lintas (Traffic Light Card)':
                   • Siswa mengangkat kartu Hijau (Paham sekali), Kuning (Ragu-ragu), Merah (Belum paham sama sekali) di akhir materi. Guru langsung tahu bagian mana yang butuh pengulangan.
                    
                2. 'Exit Ticket 1 Menit':
                   • Siswa menuliskan 1 hal yang paling dipahami dan 1 hal yang masih membingungkan di secarik kertas kecil sebelum keluar kelas.
                    
                3. 'Tunjuk Jari Skala 1 - 5 (Fist to Five)':
                   • 1 jari = sangat bingung, 5 jari = sangat siap mengajari teman.
                    
                4. 'Satu Kata Rangkuman':
                   • Setiap siswa menuliskan 1 kata kunci inti materi hari ini di papan tulis bersama.
                """.trimIndent()
            }

            // 8. Olahraga & PJOK (Pendidikan Jasmani, Olahraga, dan Kesehatan)
            lower.contains("olahraga") || lower.contains("pjok") || lower.contains("jasmani") || lower.contains("senam") || lower.contains("kebugaran") -> {
                """
                🏃‍♂️ Panduan Pembelajaran PJOK & Olahraga Bermakna (Kurikulum Merdeka):
                
                Prinsip Utama: Pembelajaran PJOK bukan untuk mencetak atlet profesional semata, melainkan menumbuhkan pembudayaan gerak sepanjang hayat (Physical Literacy) yang menyenangkan (*Joyful Learning*).
                
                1. ⚖️ Diferensiasi Gerak Sesuai Kebugaran Fisik (Tiered Movement):
                   • Sediakan 3 stasiun/level tantangan: Level 1 (Gerak dasar dengan alat bantu), Level 2 (Beban tubuh standar), Level 3 (Pengembangan ketangkasan & variasi gerak cepat).
                   • Siswa bebas memilih level awal dan menantang dirinya naik level secara bertahap tanpa takut dinilai buruk.
                
                2. 🛡️ Struktur Alur Gerak yang Aman:
                   • Pemanasan Berbasis Permainan (Game-based Warm up): Hindari statis membosankan, gunakan permainan 'Tag Game' atau 'Garis Gerak'.
                   • Kegiatan Inti Terstruktur: Berikan umpan balik formatif langsung terkait teknik keselamatan.
                   • Pendinginan Reflektif (*Mindful Stretching*): Sambil meregangkan otot, ajak siswa mengecek denyut nadi dan refleksi kenyamanan tubuh.
                
                3. 🤝 Integrasi Profil Pelajar Pancasila:
                   • Tanamkan nilai Sportivitas, Kejujuran, Gotong Royong (kerjasama tim), dan Regulasi Diri (mengontrol emosi saat menang/kalah).
                """.trimIndent()
            }

            // 9. Pencegahan Tawuran, Bullying, & Disiplin Positif (Restorative Practice)
            lower.contains("tawuran") || lower.contains("bullying") || lower.contains("perundungan") || lower.contains("berkelahi") || lower.contains("kekerasan") || lower.contains("restitusi") || lower.contains("disiplin") -> {
                """
                🛡️ Strategi Pencegahan Tawuran, Bullying & Penerapan Disiplin Positif:
                
                Sesuai Permendikbudristek No. 46 Tahun 2023 tentang Pencegahan dan Penanganan Kekerasan di Lingkungan Satuan Pendidikan (PPKSP):
                
                1. 🧭 Pendekatan Segitiga Restitusi (Bukan Hukuman Fisik):
                   • Langkah 1: Menstabilkan Identitas (*'Berbuat salah itu hal manusiawi, tidak ada orang yang sempurna'*).
                   • Langkah 2: Validasi Tindakan yang Salah (*'Kamu pasti punya alasan melakukan itu, mari kita bicarakan cara yang lebih aman'*).
                   • Langkah 3: Menanyakan Keyakinan Kelas (*'Nilai atau kesepakatan apa yang ingin kita junjung bersama?'*).
                
                2. 👥 Program Teman Pembela (Upstander/Agen Perubahan):
                   • Latih siswa rumus 4T saat melihat perundungan/perselisihan:
                     - **Tahan** (jangan ikut menyoraki/merekam jadi penonton).
                     - **Tegur/Alihkan** (ajak korban menjauh dengan sopan).
                     - **Temani** (beri dukungan emosional).
                     - **Temui Guru/TPPK** (laporkan segera secara rahasia).
                
                3. ⚡ Penyaluran Energi & Ruang Ekspresi:
                   • Buka wadah ekstrakurikuler bela diri terarah, turnamen olahraga antar-kelas, dan panggung minat bakat seni secara rutin untuk mengalihkan agresivitas remaja ke prestasi positif.
                """.trimIndent()
            }

            else -> {
                """
                🎓 Panduan Pedagogik Kurikulum Merdeka:
                
                Pertanyaan Anda mengenai: '$query'
                
                Prinsip Kunci yang Dianjurkan:
                1. **Berpusat pada Peserta Didik (Student-Centered)**: Jadikan siswa sebagai subjek aktif yang mengonstruksi pengetahuannya sendiri melalui penyelidikan dan refleksi.
                2. **Fleksibilitas Kontekstual**: Sesuaikan perangkat dan metode ajar dengan kearifan lokal, potensi lingkungan sekitar satuan pendidikan, dan sarana prasarana yang tersedia.
                3. **Asesmen Berkelanjutan**: Gunakan data asesmen formatif sebagai panduan untuk memperbaiki cara mengajar di pertemuan berikutnya, bukan sekadar menghakimi nilai angka siswa.
                4. **Penguatan Karakter P3**: Selalu selipkan nilai-nilai integritas, nalar kritis, dan gotong royong dalam setiap interaksi pembelajaran.
                """.trimIndent()
            }
        }
    }
}
