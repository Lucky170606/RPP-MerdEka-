# RPP Merdeka AI - Generator & Manajemen Perangkat Ajar Kurikulum Merdeka & Madrasah

**RPP Merdeka AI** adalah aplikasi Android profesional berbasis **Kotlin** dan **Jetpack Compose** yang dirancang khusus untuk membantu guru, pendidik, dan tenaga kependidikan di sekolah umum (SD, SMP, SMA/SMK) maupun lingkungan Madrasah (RA, MI, MTs, MA) dalam menyusun, mengelola, dan mengekspor perangkat pembelajaran secara otomatis, akurat, dan terstruktur sesuai Kurikulum Merdeka.

---

## 🌟 Fitur Utama

1. **Generator Modul Ajar Pintar (AI & Offline Engine)**
   - Pembuatan Modul Ajar lengkap dengan langkah pembelajaran, asesmen formatif/sumatif, LKPD, dan refleksi guru.
   - Pilihan moda tatap muka, PBL, PJBL, dan diferensiasi pembelajaran.

2. **Ekosistem Perangkat Ajar Lengkap**
   - **Modul Projek P5 / P5RA**: 8 Tema resmi Profil Pelajar Pancasila & Rahmatan Lil 'Alamin.
   - **Kisi & Soal HOTS**: Generator soal tingkat kognitif C1–C6 beserta kunci jawaban.
   - **PROTA & PROMES**: Program Tahunan & Semester dengan perhitungan JP otomatis.
   - **Alur Tujuan Pembelajaran (ATP)**: Penyusunan tujuan pembelajaran per fase dengan pemetaan jam pelajaran.
   - **Kalender Akademik KBM**: Kalkulator minggu efektif, hari libur, dan pekan efektif belajar.

3. **Fitur Administrasi Guru & Penilaian (Didukung Room Database)**
   - **KKTP & Nilai Rapor**: Kalkulator interval Kriteria Ketercapaian Tujuan Pembelajaran dan generator kalimat deskripsi rapor otomatis untuk e-Rapor.
   - **Jurnal Observasi & Sikap P3**: Pencatatan jurnal harian Profil Pelajar Pancasila, catatan perilaku positif/negatif, serta lembar penilaian antarteman.

4. **Mesin Kurikulum Multi-Fase (Fase A s.d. Fase F)**
   - Pemetaan materi yang presisi untuk **Fase A, B, C (SD/MI)**, **Fase D (SMP/MTs)**, **Fase E (Kelas 10 SMA/MA)**, dan **Fase F (Kelas 11-12 SMA/MA)**.
   - Dukungan penuh untuk mata pelajaran umum (*Matematika, Bahasa Indonesia, IPAS, Fisika, Kimia, Biologi*) serta mata pelajaran khas Madrasah (*Akidah Akhlak, Fikih, Al-Qur'an Hadis, SKI, Bahasa Arab*).

5. **Penyimpanan Lokal & Backup/Restore**
   - Penyimpanan aman secara offline menggunakan **Room Database**.
   - Fitur **Backup & Restore** lengkap mencakup seluruh riwayat modul, prota, promes, atp, asesmen, KKTP, jurnal observasi, dan profil guru ke dalam berkas JSON.
   - Ekspor berkas ke format **PDF** dan **Dokumen Word (.docx)**.

---

## 🛠️ Teknologi yang Digunakan

- **Bahasa**: 100% Kotlin
- **Antarmuka (UI)**: Jetpack Compose (Material Design 3 dengan dynamic color)
- **Arsitektur**: MVVM (Model-View-ViewModel) dengan StateFlow & Coroutines
- **Database**: Room Database (SQLite lokal dengan migrasi aman)
- **Serialisasi**: Moshi Kotlin JSON
- **Ekspor Dokumen**: Android PdfDocument & Apache POI / Word XML generator

---

## 🚀 Memulai (Getting Started)

1. Buka proyek ini di **Android Studio** atau platform pengembangan AI Studio.
2. Pastikan konfigurasi SDK sesuai (compileSdk & targetSdk terbaru).
3. Jalankan aplikasi pada emulator atau perangkat Android Anda (`compile_applet`).
