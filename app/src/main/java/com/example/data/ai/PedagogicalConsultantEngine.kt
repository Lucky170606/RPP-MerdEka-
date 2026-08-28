package com.example.data.ai

data class ChatMessage(
    val sender: String, // "USER" or "AI"
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
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
        )
    )

    fun answerPedagogicalQuery(query: String): String {
        val lower = query.lowercase()
        return when {
            lower.contains("ice breaking") || lower.contains("permainan") || lower.contains("semangat") -> {
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

            lower.contains("pemantik") || lower.contains("hots") || lower.contains("kritis") -> {
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
