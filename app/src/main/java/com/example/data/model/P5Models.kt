package com.example.data.model

data class TemaP5(
    val id: String,
    val title: String,
    val description: String,
    val jenjangCocok: List<String>,
    val contohTopik: List<String>,
    val targetDimensi: List<String>
)

data class P5ProjectModul(
    val id: Long = 0,
    val tema: String,
    val title: String,
    val fase: String,
    val grade: String,
    val timeAllocation: String,
    val targetDimensi: List<String>,
    val targetElemen: List<String>,
    val deskripsiSingkat: String,
    val tujuanProjek: String,
    val alurTahapan: List<P5TahapanItem>,
    val rubrikAsesmen: String,
    val lembarRefleksi: String,
    val createdAt: Long = System.currentTimeMillis()
)

data class P5TahapanItem(
    val tahap: String, // Tahap 1: Pengenalan, Tahap 2: Kontekstualisasi, Tahap 3: Aksi, Tahap 4: Refleksi & Tindak Lanjut
    val namaAktivitas: String,
    val alokasiJp: String,
    val deskripsiLangkah: String,
    val peranGuru: String,
    val peranSiswa: String,
    val asesmenFormatif: String
)

object P5ReferenceData {
    val TEMA_P5_LIST = listOf(
        TemaP5(
            id = "gaya_hidup",
            title = "Gaya Hidup Berkelanjutan",
            description = "Memahami dampak aktivitas manusia terhadap lingkungan hidup serta membangun kesadaran aksi ramah lingkungan.",
            jenjangCocok = listOf("SD/MI", "SMP/MTs", "SMA/SMK/MA"),
            contohTopik = listOf(
                "Jejak Karbon & Pengolahan Sampah Organik (Eco-Enzyme)",
                "Sayangi Bumi: Pengurangan Sampah Plastik Sekali Pakai",
                "Kebun Hidroponik Sekolah & Hemat Air Bersih"
            ),
            targetDimensi = listOf("Beriman & Berakhlak Mulia", "Bernalar Kritis", "Bergotong Royong")
        ),
        TemaP5(
            id = "kearifan_lokal",
            title = "Kearifan Lokal",
            description = "Mengeksplorasi dan melestarikan warisan budaya, tradisi, kuliner lokal, dan nilai-nilai luhur masyarakat sekitar.",
            jenjangCocok = listOf("SD/MI", "SMP/MTs", "SMA/SMK/MA"),
            contohTopik = listOf(
                "Eksplorasi Makanan Tradisional & Jamu Herbal Nusantara",
                "Kriya Anyaman & Motif Batik Khas Daerah",
                "Permainan Tradisional sebagai Perekat Kebersamaan"
            ),
            targetDimensi = listOf("Berkebinekaan Global", "Kreatif", "Bergotong Royong")
        ),
        TemaP5(
            id = "bhinneka_tunggal_ika",
            title = "Bhinneka Tunggal Ika",
            description = "Membangun dialog antarbudaya, toleransi, empati, dan merayakan keragaman dalam bingkai persatuan bangsa.",
            jenjangCocok = listOf("SD/MI", "SMP/MTs", "SMA/SMK/MA"),
            contohTopik = listOf(
                "Harmoni dalam Keragaman: Cerita Damai di Sekolah Kita",
                "Eksplorasi Rumah Adat & Pakaian Adat Nusantara",
                "Menolak Perundungan (Stop Bullying) & Diskriminasi"
            ),
            targetDimensi = listOf("Berkebinekaan Global", "Beriman & Berakhlak Mulia", "Bergotong Royong")
        ),
        TemaP5(
            id = "bangunlah_jiwa_raga",
            title = "Bangunlah Jiwa dan Raganya",
            description = "Membina kesehatan fisik, kesejahteraan mental (well-being), kesadaran gizi seimbang, dan resiliensi diri.",
            jenjangCocok = listOf("SD/MI", "SMP/MTs", "SMA/SMK/MA"),
            contohTopik = listOf(
                "Isi Piringku: Sarapan Sehat & Gizi Seimbang",
                "Manajemen Emosi Positif & Literasi Anti-Stres",
                "Gerakan Kebugaran Jasmani & Senam Bersama"
            ),
            targetDimensi = listOf("Mandiri", "Beriman & Berakhlak Mulia", "Bernalar Kritis")
        ),
        TemaP5(
            id = "suara_demokrasi",
            title = "Suara Demokrasi",
            description = "Mempelajari sistem musyawarah, pengambilan keputusan partisipatif, hak suara, dan etika berpendapat.",
            jenjangCocok = listOf("SMP/MTs", "SMA/SMK/MA"),
            contohTopik = listOf(
                "Pemilihan Ketua OSIS yang Berintegritas dan Terbuka",
                "Musyawarah Kelas: Menyusun Kesepakatan Belajar Bersama",
                "Etika Berpendapat Santun di Media Sosial"
            ),
            targetDimensi = listOf("Berkebinekaan Global", "Bernalar Kritis", "Bergotong Royong")
        ),
        TemaP5(
            id = "rekayasa_teknologi",
            title = "Rekayasa dan Teknologi",
            description = "Melatih daya cipta rekayasa sains dan inovasi teknologi aplikatif untuk memecahkan persoalan di komunitas.",
            jenjangCocok = listOf("SD/MI", "SMP/MTs", "SMA/SMK/MA"),
            contohTopik = listOf(
                "Alat Filtrasi Air Sederhana Berbasis Bahan Alami",
                "Perangkap Nyamuk Ramah Lingkungan",
                "Penyiram Tanaman Otomatis Berbasis Tenaga Gravitasi/Sensor Sederhana"
            ),
            targetDimensi = listOf("Kreatif", "Bernalar Kritis", "Mandiri")
        ),
        TemaP5(
            id = "kewirausahaan",
            title = "Kewirausahaan",
            description = "Mengembangkan jiwa kemandirian ekonomi, kreativitas produk nilai tambah, literasi finansial, dan etika bisnis.",
            jenjangCocok = listOf("SD/MI", "SMP/MTs", "SMA/SMK/MA"),
            contohTopik = listOf(
                "Market Day: Olahan Produk Nabati Kreatif Siswa",
                "Bisnis Daur Ulang: Mengubah Sampah Menjadi Berkah",
                "Literasi Menabung & Pengelolaan Uang Saku Bijak"
            ),
            targetDimensi = listOf("Kreatif", "Mandiri", "Bergotong Royong")
        )
    )

    fun getTopikForFase(temaId: String, faseCode: String): List<String> {
        return when (temaId) {
            "gaya_hidup" -> when (faseCode) {
                "Fase A", "Fase B" -> listOf(
                    "Aksi Bersih Kelas & Pilah Sampah Organik/Anorganik",
                    "Tanaman Apotek Hidup di Pot Botol Bekas",
                    "Hemat Air & Listrik di Rumah dan Sekolah"
                )
                "Fase C" -> listOf(
                    "Pembuatan Pupuk Kompos & Eco-Enzyme Sekolah",
                    "Kampanye Stop Plastik Sekali Pakai di Kantin",
                    "Bank Sampah Mini Kelas & Daur Ulang Kreatif"
                )
                "Fase D" -> listOf(
                    "Analisis Jejak Karbon Sekolah & Reduksi Emisi",
                    "Pengolahan Limbah Minyak Jelantah Menjadi Lilin/Sabun",
                    "Desain Biopori & Sumur Resapan Air Hujan di Sekolah"
                )
                else -> listOf(
                    "Inovasi Zero Waste & Circular Economy di Satuan Pendidikan",
                    "Kajian Energi Terbarukan & Efisiensi Energi Kampus/Sekolah",
                    "Konservasi Lingkungan & Restorasi Ekosistem Lokal"
                )
            }
            "kearifan_lokal" -> when (faseCode) {
                "Fase A", "Fase B" -> listOf(
                    "Mengenal dan Melestarikan Permainan Tradisional Anak",
                    "Cerita Rakyat & Dongeng Legenda Nusantara",
                    "Mencicipi Jajanan Kuliner Tradisional Sehat"
                )
                "Fase C" -> listOf(
                    "Pembuatan Kerajinan Tangan dari Bahan Alam Sekitar",
                    "Dokumentasi Makanan Tradisional Khas Daerah",
                    "Pementasan Tari Tradisional Sederhana"
                )
                "Fase D" -> listOf(
                    "Eksplorasi Nilai Luhur Filosofi Rumah Adat & Arsitektur Tradisional",
                    "Kamus Mini Bahasa Daerah & Pelestarian Dialek Lokal",
                    "Pameran Pusaka Budaya & Kuliner Warisan Leluhur"
                )
                else -> listOf(
                    "Digitalisasi Budaya & Pengarsipan Naskah/Tradisi Lokal",
                    "Kewirausahaan Berbasis Kriya dan Kain Tradisional",
                    "Kajian Sosiologis & Pelestarian Ritual Adat Nusantara"
                )
            }
            "bhinneka_tunggal_ika" -> when (faseCode) {
                "Fase A", "Fase B" -> listOf(
                    "Indahnya Berbagi & Berteman dengan Siapa Saja",
                    "Pakaian Adat Nusantara & Cerita Persahabatan",
                    "Saling Menghargai Perbedaan Kegemaran di Kelas"
                )
                "Fase C" -> listOf(
                    "Festival Budaya Nusantara & Kuliner Kebangsaan",
                    "Kampanye Anti-Perundungan (Stop Bullying) di Sekolah",
                    "Pojok Toleransi & Saling Berbagi Cerita Daerah"
                )
                "Fase D" -> listOf(
                    "Membangun Dialog Lintas Budaya & Etnis di Sekolah",
                    "Pementasan Drama Kolaboratif Keberagaman Indonesia",
                    "Analisis Kasus & Solusi Mediasi Konflik Sosial Remaja"
                )
                else -> listOf(
                    "Kajian Pluralisme & Penguatan Karakter Kebangsaan",
                    "Diplomasi Budaya Antar-Pelajar Melalui Media Digital",
                    "Advokasi Inklusi Sosial & Penghargaan Hak Asasi Manusia"
                )
            }
            "bangunlah_jiwa_raga" -> when (faseCode) {
                "Fase A", "Fase B" -> listOf(
                    "Sarapan Sehat & Membawa Bekal Bergizi ke Sekolah",
                    "Gerakan Cuci Tangan & Kebersihan Diri",
                    "Permainan Aktif & Senam Ceria Bersama"
                )
                "Fase C" -> listOf(
                    "Edukasi Gizi Seimbang & Kenali Jajanan Sehat",
                    "Manajemen Waktu Istirahat & Screen Time Bijak",
                    "Kegiatan Outbound & Ketangkasan Jasmani"
                )
                "Fase D" -> listOf(
                    "Literasi Kesehatan Mental & Manajemen Stres Remaja",
                    "Kampenyekan Bahaya Perundungan Siber (Cyberbullying)",
                    "Kebugaran Jasmani Berkala & Pola Hidup Aktif"
                )
                else -> listOf(
                    "Program Peer Counseling & Pendampingan Kesehatan Mental",
                    "Edukasi Reproduksi Sehat & Pencegahan NAPZA",
                    "Ketahanan Diri, Mindfulness, & Keseimbangan Hidup (Work-Life Balance)"
                )
            }
            "suara_demokrasi" -> when (faseCode) {
                "Fase A", "Fase B" -> listOf(
                    "Musyawarah Memilih Ketua Piket & Ketua Kelompok",
                    "Belajar Berbagi Giliran & Mendengarkan Teman Bicara",
                    "Aturan Bersama di Kelas yang Menyenangkan"
                )
                "Fase C" -> listOf(
                    "Pemilihan Ketua Kelas yang Demokratis dan Jujur",
                    "Penyusunan Tata Tertib Kelas Melalui Mufakat",
                    "Simulasi Rapat Kecil Mengambil Keputusan Bersama"
                )
                "Fase D" -> listOf(
                    "Pemilihan Ketua OSIS yang Transparan dan Akuntabel",
                    "Debat Pelajar Antar-Kelas Mengenai Isu Sekolah",
                    "Etika Bermedia Sosial & Literasi Demokrasi Digital"
                )
                else -> listOf(
                    "Simulasi Parlemen Remaja & Sidang Pleno Kebijakan",
                    "Kajian Kritis Kebijakan Publik & Partisipasi Pemuda",
                    "Kampanye Pilkada/Pemilu Jurdil dan Anti Politik Uang"
                )
            }
            "rekayasa_teknologi" -> when (faseCode) {
                "Fase A", "Fase B" -> listOf(
                    "Mainan Bergerak Sederhana dari Barang Bekas",
                    "Kincir Angin Kertas & Eksplorasi Tenaga Angin",
                    "Menanam Biji dengan Sistem Sumbu Sederhana (Wick)"
                )
                "Fase C" -> listOf(
                    "Alat Penjernih Air Sederhana dari Ijuk, Kerikil, dan Pasir",
                    "Perangkap Serangga Ramah Lingkungan",
                    "Alarm Kotak Pensil Sensor Cahaya Sederhana"
                )
                "Fase D" -> listOf(
                    "Sistem Otomasi Penyiram Tanaman Berbasis Sensor Kelembaban",
                    "Pembangkit Listrik Tenaga Mikro Hidro / Surya Mini",
                    "Aplikasi Pengingat Jadwal Sekolah Berbasis Android Sederhana"
                )
                else -> listOf(
                    "Inovasi IoT (Internet of Things) untuk Pertanian Perkotaan",
                    "Robotika Edukasi Pemilah Sampah Otomatis",
                    "Pengembangan Perangkat Lunak Solusi UMKM Lokal"
                )
            }
            "kewirausahaan" -> when (faseCode) {
                "Fase A", "Fase B" -> listOf(
                    "Pameran Karya Kreatif Seni Rupa Siswa",
                    "Celengan Buatan Sendiri & Belajar Menabung Sejak Dini",
                    "Bazaar Mini Makanan Sehat Buatan Rumah"
                )
                "Fase C" -> listOf(
                    "Market Day: Olahan Makanan Tradisional & Modern",
                    "Pemanfaatan Barang Bekas Menjadi Suvenir Bernilai Jual",
                    "Pencatatan Keuangan Sederhana untuk Uang Saku"
                )
                "Fase D" -> listOf(
                    "Wirausaha Muda: Produksi & Pemasaran Digital Produk Kreatif",
                    "Bisnis Daur Ulang Fashion (Eco-Fashion / Upcycling)",
                    "Literasi Keuangan & Perencanaan Usaha Remaja"
                )
                else -> listOf(
                    "Inkubasi Startup Siswa & Business Plan Kompetitif",
                    "E-Commerce & Digital Marketing Produk Unggulan Sekolah",
                    "Kewirausahaan Sosial (Social Enterprise) Berdampak Komunitas"
                )
            }
            else -> listOf("Projek Aksi Kolaboratif Kurikulum Merdeka")
        }
    }
}
