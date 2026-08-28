package com.example.data.ai

import com.example.data.model.GeneratedModulContent
import com.example.data.model.KurikulumMerdekaReferenceData

object OfflineCurriculumEngine {

    fun generateCompleteModul(
        teacherName: String,
        schoolName: String,
        fase: String,
        grade: String,
        subject: String,
        topic: String,
        timeAllocation: String,
        semester: String,
        academicYear: String,
        modelName: String,
        selectedDimensi: List<String>,
        targetGayaBelajar: List<String>,
        targetKesiapan: List<String>,
        additionalNotes: String
    ): GeneratedModulContent {
        val matchedCP = KurikulumMerdekaReferenceData.findMatchingCP(subject, fase, topic)
        val cpText = matchedCP?.capaianText ?: "Peserta didik mampu memahami konsep dasar, menganalisis hubungan sebab-akibat, serta mengaplikasikan pengetahuan $topic dalam pemecahan masalah nyata."
        val elemen = matchedCP?.elemen ?: "Pemahaman Konsep & Keterampilan Proses"
        
        val tp1 = "Peserta didik dapat mengidentifikasi konsep dasar dan karakteristik utama terkait $topic dengan tepat dan runtut."
        val tp2 = "Peserta didik mampu menganalisis keterkaitan $topic dengan fenomena kehidupan sehari-hari melalui diskusi kelompok yang kolaboratif."
        val tp3 = "Peserta didik dapat menyajikan karya / hasil penyelidikan mengenai solusi terkait $topic secara kreatif dan mandiri."

        val tujuanFormatted = """
            1. $tp1
            2. $tp2
            3. $tp3
        """.trimIndent()

        val dimensiText = if (selectedDimensi.isNotEmpty()) {
            selectedDimensi.mapIndexed { idx, dim ->
                "${idx + 1}. $dim: Mengembangkan sikap bernalar logis, kerjasama dalam tim, dan tanggung jawab terhadap tugas belajar."
            }.joinToString("\n")
        } else {
            "1. Bernalar Kritis: Menganalisis informasi dan memproses gagasan secara logis.\n2. Bergotong Royong: Berkolaborasi aktif dalam diskusi kelompok.\n3. Mandiri: Mengelola tugas dan waktu belajar secara mandiri."
        }

        val sintaks = KurikulumMerdekaReferenceData.MODEL_PEMBELAJARAN_LIST
            .firstOrNull { it.name.contains(modelName, ignoreCase = true) || modelName.contains(it.name, ignoreCase = true) }
            ?.sintaks ?: listOf(
                "1. Orientasi peserta didik pada masalah kontekstual",
                "2. Pengorganisasian kelompok belajar",
                "3. Penyelidikan mandiri dan kelompok",
                "4. Penyajian dan diskusi hasil karya",
                "5. Evaluasi dan refleksi proses pemecahan masalah"
            )

        val pendahuluan = """
            1. Orientasi (5 Menit):
               - Guru membuka pembelajaran dengan salam hangat, doa bersama, dan mengecek kehadiran peserta didik.
               - Guru mengkondisikan kelas agar nyaman dan siap belajar (yel-yel semangat / ice breaking ringan).
            2. Apersepsi (5 Menit):
               - Guru mengaitkan materi sebelumnya dengan topik hari ini: "$topic".
               - Guru menanyakan pengalaman sehari-hari siswa terkait materi.
            3. Motivasi & Penyampaian Tujuan (5 Menit):
               - Guru menyampaikan tujuan pembelajaran dan manfaat mempelajari $topic dalam kehidupan nyata.
               - Guru menjelaskan alur kegiatan pembelajaran dan kriteria penilaian yang akan dilakukan.
        """.trimIndent()

        val inti = StringBuilder().apply {
            appendLine("Kegiatan Inti dengan Model ${modelName}:")
            appendLine()
            sintaks.forEach { step ->
                appendLine("• $step:")
                when {
                    step.contains("Orientasi", ignoreCase = true) || step.contains("Rangsangan", ignoreCase = true) -> {
                        appendLine("   - Guru menyajikan video/gambar/studi kasus kontekstual mengenai \"$topic\".")
                        appendLine("   - Peserta didik mengamati dan mengajukan pertanyaan awal terkait fenomena yang disajikan.")
                        appendLine("   - Guru menstimulasi rasa ingin tahu siswa dengan pertanyaan pemantik.")
                    }
                    step.contains("Organisasi", ignoreCase = true) || step.contains("Identifikasi", ignoreCase = true) || step.contains("Rencana", ignoreCase = true) -> {
                        appendLine("   - Peserta didik dibagi ke dalam kelompok heterogen beranggotakan 4-5 siswa.")
                        appendLine("   - Guru membagikan Lembar Kerja Peserta Didik (LKPD) yang dirancang berdiferensiasi.")
                        appendLine("   - Setiap kelompok mendiskusikan pembagian peran dan merumuskan masalah yang akan diselesaikan.")
                    }
                    step.contains("Penyelidikan", ignoreCase = true) || step.contains("Pengumpulan", ignoreCase = true) || step.contains("Monitor", ignoreCase = true) -> {
                        appendLine("   - Peserta didik melakukan eksplorasi data, membaca referensi, atau melakukan percobaan sederhana.")
                        appendLine("   - Guru berkeliling memberikan bimbingan bertingkat (scaffolding) bagi kelompok yang memerlukan bantuan.")
                        appendLine("   - Peserta didik berdiskusi aktif memverifikasi temuan mereka dengan teori yang ada.")
                    }
                    step.contains("Karya", ignoreCase = true) || step.contains("Pengolahan", ignoreCase = true) || step.contains("Uji", ignoreCase = true) -> {
                        appendLine("   - Kelompok menyusun laporan hasil analisis/proyek dalam bentuk pilihan (poster/mind map/slide/tulisan).")
                        appendLine("   - Setiap kelompok secara bergantian mempresentasikan hasil diskusinya di depan kelas.")
                        appendLine("   - Kelompok lain memberikan tanggapan, apresiasi, dan pertanyaan konstruktif.")
                    }
                    else -> {
                        appendLine("   - Bersama guru, peserta didik menyimpulkan poin-poin penting mengenai konsep $topic.")
                        appendLine("   - Guru memberikan klarifikasi, penguatan konsep, dan meluruskan miskonsepsi yang muncul.")
                    }
                }
                appendLine()
            }
        }.toString().trim()

        val penutup = """
            1. Simpulan & Rangkuman (5 Menit):
               - Guru membimbing peserta didik merumuskan kesimpulan menyeluruh tentang "$topic".
            2. Refleksi Bersama (5 Menit):
               - Peserta didik menjawab lembar refleksi 3-2-1 (3 hal baru yang dipelajari, 2 hal menarik, 1 pertanyaan yang masih tersisa).
               - Guru memberikan apresiasi atas partisipasi aktif seluruh siswa.
            3. Tindak Lanjut & Doa (5 Menit):
               - Guru memberikan arahan tugas pengayaan mandiri dan materi pertemuan berikutnya.
               - Pembelajaran ditutup dengan doa bersama dan salam.
        """.trimIndent()

        val difKonten = """
            • Siswa Visual: Disediakan infografis, diagram warna, video animasi pembelajaran, dan kartu gambar konsep $topic.
            • Siswa Auditori: Disediakan rekaman audio podcast penjelasan, diskusi lisan terbimbing, dan tanya jawab interaktif.
            • Siswa Kinestetik: Disediakan alat peraga manipulatif konkret, kartu sortir konsep fisik, dan simulasi gerak/studi langsung.
        """.trimIndent()

        val difProses = """
            • Kelompok Perlu Bimbingan (Level Dasar): Guru memberikan pendampingan langsung intensif (scaffolding terstruktur) dengan lembar kerja bertahap.
            • Kelompok Berkembang (Level Menengah): Diberikan panduan tugas semi-terstruktur dengan pemantauan berkala dan diskusi kelompok sebaya.
            • Kelompok Mahir (Level Lanjut): Diberikan studi kasus kompleks tingkat tinggi (HOTS), memecahkan skenario alternatif, dan berperan sebagai tutor sebaya.
        """.trimIndent()

        val difProduk = """
            Peserta didik diberikan kebebasan memilih bentuk unjuk kerja / produk akhir sesuai minat:
            1. Infografis / Poster Digital atau Manual tentang $topic.
            2. Video rekaman singkat / presentasi lisan / podcast penjelasan.
            3. Rangkuman terstruktur / Mind Map / Laporan tertulis.
        """.trimIndent()

        val asesmenDiag = """
            1. Asesmen Non-Kognitif: Menanyakan kesiapan emosional, gaya belajar yang disukai, dan motivasi belajar hari ini.
            2. Asesmen Kognitif Awal: Kuis pemantik 3 soal singkat pilihan ganda atau pertanyaan lisan untuk memetakan pemahaman prasyarat mengenai $topic.
        """.trimIndent()

        val asesmenForm = """
            1. Observasi Sikap Profil Pelajar Pancasila: Mengamati keaktifan kerjasama, bernalar kritis saat diskusi, dan integritas.
            2. Penilaian Kinerja Proses: Checklist keaktifan dalam menyelesaikan LKPD dan keterlibatan dalam kelompok.
            3. Kuis Formatif Formatif Formatif (Exit Ticket): 2 soal esai singkat di akhir pembelajaran untuk mengecek ketercapaian tujuan.
        """.trimIndent()

        val asesmenSumatif = """
            Tes Tertulis Akhir / Penugasan Proyek Modul:
            • 5 Soal Pilihan Ganda Berbobot HOTS terkait konsep $topic.
            • 2 Soal Uraian Pemecahan Masalah Kontekstual.
            • Lembar Penilaian Produk Proyek / Portofolio LKPD.
        """.trimIndent()

        val rubrik = """
            | Kriteria Penilaian | Perlu Bimbingan (1) | Cukup (2) | Baik (3) | Sangat Baik (4) |
            | --- | --- | --- | --- | --- |
            | Pemahaman Konsep $topic | Belum mampu mengidentifikasi konsep dasar, perlu bimbingan penuh. | Mampu menyebutkan sebagian konsep dasar namun belum runtut. | Mampu menjelaskan konsep secara tepat dengan bahasa sendiri. | Menguasai konsep secara mendalam dan mampu mengaitkan dengan contoh baru. |
            | Analisis & Pemecahan Masalah | Belum mampu menemukan solusi masalah pada LKPD. | Menemukan solusi dengan bantuan teman sekelompok. | Mampu menganalisis masalah dan merumuskan solusi secara logis. | Menganalisis secara kritis, menghasilkan solusi inovatif dan detail. |
            | Kolaborasi & Sikap P3 | Pasif dalam kelompok dan perlu dorongan terus-menerus. | Terkadang berpartisipasi jika diminta. | Aktif berdiskusi, menghargai pendapat, dan berbagi tugas. | Menjadi inisiator kelompok, sangat solutif dan memotivasi rekan lain. |
            | Kualitas Produk Akhir | Produk belum selesai atau tidak sesuai petunjuk. | Produk selesai namun informasi kurang lengkap. | Produk rapi, informasi jelas, dan tepat waktu. | Produk sangat kreatif, estetis, komprehensif, dan melampaui ekspektasi. |
        """.trimIndent()

        val lkpd = """
            LEMBAR KERJA PESERTA DIDIK (LKPD)
            Mata Pelajaran: $subject | Fase/Kelas: $fase ($grade)
            Topik: $topic | Alokasi Waktu: $timeAllocation
            Nama Kelompok: _________________ Anggota: 1) ___ 2) ___ 3) ___ 4) ___

            A. Petunjuk Belajar:
               1. Berdoalah sebelum memulai kegiatan belajar.
               2. Baca materi pengantar dan cermati masalah yang disajikan pada lembar ini.
               3. Diskusikan bersama anggota kelompokmu dan bagi tugas secara adil.

            B. Aktivitas Penyelidikan:
               1. Analisislah fenomena terkait $topic yang disajikan oleh guru!
               2. Tuliskan 3 pertanyaan utama yang ingin kalian temukan jawabannya!
               3. Kumpulkan data dan fakta pendukung melalui bahan ajar / percobaan.
               4. Rumuskan solusi terbaik dan buat simpulan kelompok!

            C. Ruang Jawaban & Kesimpulan:
               [Area pengerjaan peserta didik...]
        """.trimIndent()

        val remedial = """
            • Remedial: Diberikan kepada peserta didik yang belum mencapai Kriteria Ketercapaian Tujuan Pembelajaran (KKTP). Bimbingan khusus secara individu / kelompok kecil melalui pengulangan materi esensial dengan media konkret sederhana.
            • Pengayaan: Diberikan kepada peserta didik yang telah mencapai KKTP. Diberikan materi pendalaman tingkat lanjut (HOTS) berupa penugasan analisis studi kasus lanjutan atau menjadi tutor sebaya bagi temannya.
        """.trimIndent()

        return GeneratedModulContent(
            identitas = "Penyusun: $teacherName | Satuan Pendidikan: $schoolName | Fase/Kelas: $fase ($grade) | Mapel: $subject | Topik: $topic | Semester: $semester | Alokasi Waktu: $timeAllocation | TP: $academicYear",
            kompetensiAwal = "Peserta didik telah memiliki pengetahuan dasar awal tentang lingkungan sekitar dan konsep pengantar sebelum mempelajari materi pokok $topic.",
            profilPelajarPancasila = dimensiText,
            saranaPrasarana = "Buku Teks Kurikulum Merdeka, Laptop/Proyektor, Lembar LKPD, Papan Tulis, Alat Tulis, Video Pembelajaran, dan Alat Peraga Konkret.",
            targetPesertaDidik = "Peserta didik reguler / tipikal (umum), tidak ada kesulitan dalam mencerna materi ajar; serta peserta didik dengan pencapaian tinggi dan yang memerlukan bimbingan bertingkat.",
            modelPembelajaran = "$modelName dengan metode Diskusi Kelompok, Tanya Jawab Interaktif, Penyelidikan Ilmiah, Penugasan LKPD, dan Presentasi.",
            tujuanPembelajaran = tujuanFormatted,
            pemahamanBermakna = "Memahami konsep $topic membantu peserta didik mengenali keteraturan alam/masyarakat serta mampu mengambil keputusan bijak dalam kehidupan sehari-hari.",
            pertanyaanPemantik = "1. Mengapa penting bagi kita untuk mempelajari $topic?\n2. Bagaimana fenomena $topic dapat kita temukan di sekitar lingkungan kita setiap hari?\n3. Apa yang terjadi jika kita tidak memahami konsep tersebut?",
            kegiatanPendahuluan = pendahuluan,
            kegiatanInti = inti,
            kegiatanPenutup = penutup,
            diferensiasiKonten = difKonten,
            diferensiasiProses = difProses,
            diferensiasiProduk = difProduk,
            asesmenDiagnostik = asesmenDiag,
            asesmenFormatif = asesmenForm,
            asesmenSumatif = asesmenSumatif,
            rubrikPenilaian = rubrik,
            remedialDanPengayaan = remedial,
            lkpdDanMateri = lkpd
        )
    }
}
