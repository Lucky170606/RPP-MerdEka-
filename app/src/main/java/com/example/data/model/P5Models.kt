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
}
