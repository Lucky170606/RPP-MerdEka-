package com.example.data.ai

import com.example.data.model.P5ProjectModul
import com.example.data.model.P5TahapanItem

object OfflineP5Engine {

    fun generateP5Modul(
        temaTitle: String,
        topikProjek: String,
        fase: String,
        grade: String,
        alokasiWaktu: String,
        selectedDimensi: List<String>,
        teacherName: String,
        schoolName: String
    ): P5ProjectModul {
        val dimensiString = selectedDimensi.ifEmpty { listOf("Beriman & Berakhlak Mulia", "Bergotong Royong", "Bernalar Kritis") }

        val alurList = listOf(
            P5TahapanItem(
                tahap = "Tahap 1: Pengenalan (Eksplorasi Isu)",
                namaAktivitas = "Mengidentifikasi Isu Nyata & Membangun Kesadaran Kritis",
                alokasiJp = "6 JP",
                deskripsiLangkah = """
                1. Guru menayangkan video/gambar pemantik terkait isu '$topikProjek' di lingkungan sekitar sekolah dan rumah.
                2. Siswa berdiskusi dalam kelompok kecil untuk menginventarisasi fakta, mitos, dan persoalan yang mereka temui sehari-hari.
                3. Curah gagasan (Brainstorming) menyusun peta pikiran (mind map) tantangan dan peluang aksi.
                """.trimIndent(),
                peranGuru = "Fasilitator diskusi, memantik pertanyaan kritis, mengarahkan fokus observasi.",
                peranSiswa = "Menyimak tayangan, mencatat fakta menarik, mengemukakan pendapat awal dalam kelompok.",
                asesmenFormatif = "Rubrik keaktifan diskusi, lembar catatan tanya jawab pemantik (K-W-L Chart)."
            ),
            P5TahapanItem(
                tahap = "Tahap 2: Kontekstualisasi (Riset Lapangan)",
                namaAktivitas = "Observasi Langsung, Wawancara Komunitas, & Analisis Masalah",
                alokasiJp = "10 JP",
                deskripsiLangkah = """
                1. Siswa melakukan observasi di lingkungan sekolah/lingkungan sekitar untuk mengumpulkan data riil terkait $topikProjek.
                2. Menyusun instrumen wawancara sederhana dan mewawancarai narasumber terkait (warga sekolah/tokoh masyarakat).
                3. Mengolah data temuan ke dalam bentuk diagram atau poster pohon masalah (Problem Tree Analysis).
                """.trimIndent(),
                peranGuru = "Mendampingi kunjungan lapangan, mengawasi keselamatan siswa, memvalidasi instrumen wawancara.",
                peranSiswa = "Melakukan wawancara santun, mencatat data akurat, mengelompokkan data temuan secara kolaboratif.",
                asesmenFormatif = "Checklist lembar kerja observasi lapangan & rubrik analisis pohon masalah."
            ),
            P5TahapanItem(
                tahap = "Tahap 3: Aksi Nyata (Perancangan & Eksekusi)",
                namaAktivitas = "Perancangan Solusi Kreatif, Pembuatan Karya, & Uji Coba Lapangan",
                alokasiJp = "14 JP",
                deskripsiLangkah = """
                1. Tiap kelompok menyusun rencana aksi konkret untuk memecahkan masalah '$topikProjek' (desain prototipe, kampanye, atau produk bernilai).
                2. Pembagian tugas peran yang adil (ketua, pencatat, perancang, dokumentasi).
                3. Eksekusi pembuatan karya/produk secara gotong royong dengan memanfaatkan sumber daya yang ada.
                4. Uji coba prototipe skala kecil dan menerima umpan balik rekan sejawat (Peer Feedback).
                """.trimIndent(),
                peranGuru = "Konsultan teknis, memberikan masukan perbaikan tanpa mendikte, memantau kemajuan kelompok.",
                peranSiswa = "Bekerja sama secara aktif, membagi tugas merata, menghargai saran teman, menyelesaikan karya tepat waktu.",
                asesmenFormatif = "Jurnal refleksi berkala (Logbook harian) & rubrik gotong royong dan kreativitas."
            ),
            P5TahapanItem(
                tahap = "Tahap 4: Refleksi & Perayaan Hasil Belajar",
                namaAktivitas = "Pameran Karya (Exhibition) & Refleksi Ketercapaian Profil Pelajar Pancasila",
                alokasiJp = "6 JP",
                deskripsiLangkah = """
                1. Menyelenggarakan 'Pameran / Gelar Karya Projek' di kelas/sekolah dengan mengundang teman, guru, dan orang tua.
                2. Siswa mempresentasikan karya, proses di balik layar, serta dampak positif yang ingin dicapai.
                3. Mengisi lembar refleksi diri (Self-Assessment) mengenai perubahan sikap, kerja sama, dan pemahaman nilai Pancasila yang dialami.
                4. Penyusunan komitmen tindak lanjut keberlanjutan aksi di kehidupan sehari-hari.
                """.trimIndent(),
                peranGuru = "Apresiator, memfasilitasi jalannya pameran, memandu proses refleksi mendalam.",
                peranSiswa = "Mempresentasikan hasil karya secara percaya diri, memberikan apresiasi pada kelompok lain, mengevaluasi diri sendiri.",
                asesmenFormatif = "Rubrik asesmen sumatif projek P5 (Belum Berkembang, Mulai Berkembang, Berkembang Sesuai Harapan, Sangat Berkembang)."
            )
        )

        val rubrik = """
        MATRIKS RUBRIK PENCAPAIAN PROFIL PELAJAR PANCASILA PROJEK ($topikProjek):
        
        1. Dimensi ${dimensiString.getOrNull(0) ?: "Bergotong Royong"}:
           • Mulai Berkembang (MB): Mampu bekerjasama jika diingatkan dan masih membutuhkan bimbingan intensif dalam pembagian tugas.
           • Sedang Berkembang (SB): Menunjukkan inisiatif membantu teman kelompok dan mampu menyelesaikan tugas bagiannya sendiri.
           • Berkembang Sesuai Harapan (BSH): Secara mandiri berkoordinasi, menghargai pendapat anggota tim, dan berkontribusi aktif mencapai target bersama.
           • Sangat Berkembang (SAB): Menjadi motor penggerak kelompok, mampu memecahkan konflik dengan bijak, dan menginspirasi rekan lain.
           
        2. Dimensi ${dimensiString.getOrNull(1) ?: "Bernalar Kritis"}:
           • Mulai Berkembang (MB): Mengidentifikasi masalah sederhana berdasarkan apa yang dilihat langsung.
           • Sedang Berkembang (SB): Mampu mengaitkan sebab dan akibat dari fenomena yang diobservasi.
           • Berkembang Sesuai Harapan (BSH): Mengajukan pertanyaan analitis, menguji kebenaran informasi, dan menyimpulkan solusi logis.
           • Sangat Berkembang (SAB): Melakukan analisis mendalam multidisiplin dan memberikan rekomendasi solusi jangka panjang.
           
        3. Dimensi ${dimensiString.getOrNull(2) ?: "Kreatif"}:
           • Mulai Berkembang (MB): Meniru karya yang sudah ada dengan sedikit modifikasi.
           • Sedang Berkembang (SB): Mengembangkan ide baru berdasarkan referensi yang diberikan.
           • Berkembang Sesuai Harapan (BSH): Menghasilkan gagasan dan karya orisinal yang memiliki nilai guna bagi lingkungan.
           • Sangat Berkembang (SAB): Menghasilkan karya orisinal inovatif bernilai tinggi dan berdampak nyata bagi komunitas.
        """.trimIndent()

        val refleksi = """
        LEMBAR REFLEKSI DIRI SISWA (AKHIR PROJEK):
        1. Hal paling berharga yang saya pelajari selama projek '$topikProjek' adalah: ...
        2. Tantangan terbesar yang kelompok saya hadapi dan cara kami menyelesaikannya: ...
        3. Sikap Profil Pelajar Pancasila yang paling berkembang dalam diri saya adalah: ...
        4. Satu hal konkret yang akan terus saya lakukan setelah projek ini selesai: ...
        """.trimIndent()

        return P5ProjectModul(
            tema = temaTitle,
            title = "Projek P5: $topikProjek",
            fase = fase,
            grade = grade,
            timeAllocation = alokasiWaktu,
            targetDimensi = dimensiString,
            targetElemen = listOf("Kolaborasi", "Kepedulian", "Pemrosesan Informasi", "Menghasilkan Gagasan Orisinal"),
            deskripsiSingkat = "Projek penguatan karakter bertema '$temaTitle' yang berfokus pada eksplorasi dan aksi nyata mengenai '$topikProjek' untuk membentuk kepribadian pelajar Pancasila yang tangguh, peduli, dan berdaya cipta.",
            tujuanProjek = "Peserta didik mampu membangun kesadaran kritis terhadap isu $topikProjek, bekerja sama dalam tim lintas peran, serta menghasilkan solusi/karya nyata yang bermanfaat bagi lingkungan sekolah dan masyarakat.",
            alurTahapan = alurList,
            rubrikAsesmen = rubrik,
            lembarRefleksi = refleksi
        )
    }
}
