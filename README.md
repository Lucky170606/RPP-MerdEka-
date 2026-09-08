# RPP Merdeka AI - Asisten Cerdas Perangkat Ajar Kurikulum Merdeka

Aplikasi Android modern untuk membantu guru, pendidik, dan praktisi pendidikan menyusun perangkat ajar **Kurikulum Merdeka** (Kemendikbudristek & Kemenag RI) secara otomatis, komprehensif, dan siap cetak.

---

## 🚀 Apa Saja yang Baru di Versi Ini? (Changelog Terbaru)

1. **Integrasi Penuh Kurikulum Madrasah (Kemenag RI) Semua Jenjang:**
   - **Madrasah Ibtidaiyah (MI - Fase A, B, C):** Al-Qur'an Hadis, Akidah Akhlak, Fikih, SKI, Bahasa Arab.
   - **Madrasah Tsanawiyah (MTs - Fase D):** Al-Qur'an Hadis, Akidah Akhlak, Fikih, SKI, Bahasa Arab.
   - **Madrasah Aliyah (MA - Fase E & F):** Al-Qur'an Hadis, Akidah Akhlak, Fikih, SKI, Bahasa Arab, serta peminatan keagamaan khusus (*Ushul Fikih, Ilmu Tafsir, Ilmu Hadis*).
   - Bank Capaian Pembelajaran (CP) dan Alur Tujuan Pembelajaran (ATP) resmi Kemenag.

2. **Peningkatan Batas Waktu AI (Timeout 360 Detik):**
   - Waktu tunggu (timeout) maksimal koneksi AI dinaikkan hingga **360 detik** (6 menit) untuk mendukung pembuatan paket soal HOTS berkapasitas besar dan modul pembelajaran mendalam tanpa terputus.
   - Auto-fallback cerdas ke **Mesin Asesmen & Kurikulum Offline** jika kuota internet atau jaringan bermasalah.

3. **Dukungan Simbol Matematika, Sains & Teks Arab:**
   - Dukungan penuh format Unicode untuk rumus matematika (pangkat, akar, pecahan, integral), notasi kimia/fisika, serta aksara Arab berharakat untuk ayat Al-Qur'an dan Hadis.

4. **Pembaruan Sistem Suara Guru AI (Text-to-Speech) & SFX:**
   - Kompatibilitas penuh dengan perangkat Samsung/One UI dan seluruh merk Android melalui audio routing `STREAM_MUSIC` / `USAGE_MEDIA`.
   - Penyempurnaan karakter suara pria (*Pak Guru Aris*) berbasis Google TTS dan modul bariton.

5. **Pencadangan & Pemulihan Basis Data (Backup & Restore JSON):**
   - Fitur simpan dan pulihkan cadangan terpadu (Modul Ajar, PROTA, PROMES, ATP, Soal HOTS, Modul P5, dan Profil Guru/Sekolah) langsung dari/ke memori internal HP.

6. **Perbaikan Tampilan Antarmuka (UI/UX):**
   - Tampilan profil guru dan pengaturan suara lebih rapi, modern, dan responsif.
   - Penambahan template cepat 1-klik (*Quick Presets*) untuk mapel umum dan madrasah.

---

## 🌟 Fitur Utama Aplikasi

### 1. 📝 Generator Modul Ajar / RPP Otomatis
Membuat draf Modul Ajar lengkap dengan Capaian Pembelajaran (CP), Tujuan Pembelajaran (TP), Profil Pelajar Pancasila / Rahmatan Lil 'Alamin, Diferensiasi, Langkah Kegiatan, dan Rubrik Asesmen.
* **Cara Menggunakan:**
  1. Pada halaman **Beranda**, pilih menu **"Buat Modul Ajar Baru"**.
  2. Pilih Jenjang & Fase (Fase A s.d. Fase F), Kelas, dan Mata Pelajaran (Sekolah Umum & Madrasah).
  3. Pilih Model Pembelajaran (misal: *Deep Learning*, *PBL*, *PjBL*, *Inquiry*, *Discovery*, dll.).
  4. Tentukan fokus diferensiasi (Konten/Proses/Produk) dan jenis asesmen.
  5. Klik **"Generate Modul Ajar"**.

---

### 2. 💬 Ruang Konsultasi Guru AI (Pedagogical Consultant)
Asisten tanya-jawab interaktif untuk konsultasi strategi mengajar, ide ice breaking, penanganan kelas, pemecahan masalah belajar siswa, hingga konsep pedagogis.
* Dilengkapi kartu topik cepat (*Deep Learning, Ice Breaking, Diferensiasi, dll.*).
* Didukung pembacaan suara AI (TTS) interaktif.

---

### 3. 🎯 Generator Asesmen & Bank Soal HOTS
Membuat stimulus kasus kontekstual, instrumen penilaian, dan bank soal *Higher Order Thinking Skills* (C4 Analisis, C5 Evaluasi, C6 Kreasi) lengkap dengan kisi-kisi dan kunci jawaban.

---

### 4. 📅 Alur Tujuan Pembelajaran (ATP), Prota & Promes
Alat bantu pemetaan Program Tahunan (Prota), Program Semester (Promes), dan distribusi alokasi minggu efektif semester ganjil/genap secara otomatis.

---

### 5. 🌱 Modul Projek P5 (Penguatan Profil Pelajar Pancasila & PPRA)
Perancang panduan projek kokurikuler berbasis tema resmi Kemendikbudristek dan nilai Rahmatan Lil 'Alamin Kemenag dengan alur 4 tahap (*Pengenalan, Kontekstualisasi, Aksi Nyata, Refleksi*).

---

### 6. 💾 Cadangan & Pemulihan Data (Backup & Restore)
Menjaga keamanan data perangkat ajar guru tanpa khawatir hilang saat berganti gawai.
* **Cara Ekspor Cadangan:** Buka tab **"Cadangan & Pemulihan"** di menu Profil $\rightarrow$ Klik **"Pilih Folder & Simpan ke Memori HP"**.
* **Cara Pulihkan Data:** Klik **"Pilih Berkas dari Memori HP (Pulihkan)"** $\rightarrow$ Pilih file `.json` cadangan Anda.

---

## 📱 Ringkasan Navigasi Cepat

* **Beranda (Home):** Dashboard ringkasan dokumen, pintasan generator modul, dan akses cepat perangkat ajar.
* **Modul Saya:** Daftar seluruh Modul Ajar dan RPP yang tersimpan secara lokal di perangkat (Room Database).
* **Konsultasi AI:** Asisten chat pedagogis interaktif dengan suara AI.
* **Profil Guru & Pengaturan:** Pengaturan profil tetap (Nama, NIP, Nama Sekolah/Madrasah, NPSN, Kepala Sekolah, Tahun Ajaran), gaya tema, suara TTS, dan manajemen backup.
