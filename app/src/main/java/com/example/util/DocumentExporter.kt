package com.example.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.print.PrintAttributes
import android.print.PrintManager
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.data.local.ModulAjarEntity
import com.example.data.model.*
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DocumentExporter {

    fun generateHtmlDocument(modul: ModulAjarEntity, profile: TeacherProfile? = null): String {
        val teacher = profile?.teacherName ?: modul.teacherName
        val school = profile?.schoolName ?: modul.schoolName
        val teacherNip = profile?.teacherNip ?: "-"
        val principal = profile?.principalName ?: "Dra. Hj. Siti Rohmah, M.Pd."
        val principalNip = profile?.principalNip ?: "-"
        val cityDate = profile?.cityAndDate ?: "Jakarta, ${SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID")).format(Date())}"
        val isCompact = profile?.printLayoutMode == "RINGKAS"

        return """
            <!DOCTYPE html>
            <html lang="id">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Modul Ajar - ${modul.title}</title>
                <style>
                    body {
                        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                        line-height: ${if (isCompact) "1.4" else "1.6"};
                        color: #1a202c;
                        background: #ffffff;
                        margin: 0;
                        padding: ${if (isCompact) "12px" else "24px"};
                    }
                    .header-box {
                        border-bottom: 3px double #1e3a8a;
                        padding-bottom: 12px;
                        margin-bottom: 18px;
                        text-align: center;
                    }
                    .header-box h1 {
                        font-size: ${if (isCompact) "16pt" else "19pt"};
                        color: #1e3a8a;
                        margin: 0 0 4px 0;
                        text-transform: uppercase;
                        letter-spacing: 0.5px;
                    }
                    .header-box h2 {
                        font-size: ${if (isCompact) "12pt" else "14pt"};
                        color: #0d9488;
                        margin: 0 0 6px 0;
                    }
                    .header-box p {
                        font-size: 9.5pt;
                        color: #4b5563;
                        margin: 0;
                    }
                    .section-title {
                        font-size: ${if (isCompact) "11pt" else "12.5pt"};
                        font-weight: bold;
                        color: #1e3a8a;
                        background-color: #f1f5f9;
                        padding: ${if (isCompact) "5px 10px" else "8px 12px"};
                        border-left: 5px solid #1e3a8a;
                        margin-top: ${if (isCompact) "14px" else "20px"};
                        margin-bottom: 10px;
                        border-radius: 4px;
                    }
                    .subsection-title {
                        font-size: 10.5pt;
                        font-weight: 600;
                        color: #0f766e;
                        margin-top: 10px;
                        margin-bottom: 4px;
                    }
                    table.info-table {
                        width: 100%;
                        border-collapse: collapse;
                        margin-bottom: 14px;
                    }
                    table.info-table td {
                        padding: ${if (isCompact) "4px 8px" else "6px 10px"};
                        border-bottom: 1px solid #e2e8f0;
                        font-size: 9.5pt;
                    }
                    table.info-table td.label {
                        width: 28%;
                        font-weight: 600;
                        color: #334155;
                        background: #f8fafc;
                    }
                    .badge {
                        display: inline-block;
                        padding: 2px 7px;
                        background: #e0f2fe;
                        color: #0369a1;
                        border-radius: 10px;
                        font-size: 8pt;
                        font-weight: 600;
                        margin-right: 4px;
                        margin-bottom: 3px;
                    }
                    .content-block {
                        font-size: 9.5pt;
                        white-space: pre-line;
                        margin-bottom: 10px;
                        text-align: justify;
                    }
                    .footer-signature {
                        margin-top: 30px;
                        width: 100%;
                        page-break-inside: avoid;
                    }
                    .footer-signature td {
                        width: 50%;
                        text-align: center;
                        vertical-align: top;
                        font-size: 9.5pt;
                    }
                    @media print {
                        body { padding: 8mm; }
                        .no-print { display: none; }
                        .section-title { background-color: #f1f5f9 !important; -webkit-print-color-adjust: exact; }
                    }
                </style>
            </head>
            <body>
                <div class="header-box">
                    <h1>MODUL AJAR KURIKULUM MERDEKA ${if (isCompact) "(FORMAT RINGKAS)" else ""}</h1>
                    <h2>${modul.subject.uppercase(Locale.ROOT)} - ${modul.topic}</h2>
                    <p>Satuan Pendidikan: ${school} | Penyusun: ${teacher}</p>
                </div>

                <div class="section-title">A. INFORMASI UMUM</div>
                <table class="info-table">
                    <tr><td class="label">Nama Penyusun</td><td>${teacher} (NIP. $teacherNip)</td></tr>
                    <tr><td class="label">Satuan Pendidikan</td><td>${school}</td></tr>
                    <tr><td class="label">Fase / Kelas</td><td>${modul.fase} / ${modul.grade}</td></tr>
                    <tr><td class="label">Mata Pelajaran</td><td>${modul.subject}</td></tr>
                    <tr><td class="label">Materi Pokok / Topik</td><td>${modul.topic}</td></tr>
                    <tr><td class="label">Alokasi Waktu</td><td>${modul.timeAllocation}</td></tr>
                    <tr><td class="label">Semester / Tahun Ajaran</td><td>${modul.semester} / ${modul.academicYear}</td></tr>
                    <tr><td class="label">Model Pembelajaran</td><td>${modul.modelPembelajaran}</td></tr>
                    <tr>
                        <td class="label">Profil Pelajar Pancasila</td>
                        <td>
                            ${modul.dimensiP3.split(",").joinToString("") { "<span class='badge'>${it.trim()}</span>" }}
                            <div class="content-block" style="margin-top: 4px;">${modul.profilPelajarPancasila}</div>
                        </td>
                    </tr>
                    <tr><td class="label">Sarana & Prasarana</td><td>${modul.saranaPrasarana}</td></tr>
                    <tr><td class="label">Target Peserta Didik</td><td>${modul.targetPesertaDidik}</td></tr>
                </table>

                <div class="section-title">B. KOMPONEN INTI</div>
                <div class="subsection-title">1. Capaian & Tujuan Pembelajaran</div>
                <div class="content-block"><strong>Capaian Pembelajaran (CP):</strong><br>${modul.capaianPembelajaran}</div>
                <div class="content-block"><strong>Tujuan Pembelajaran (TP):</strong><br>${modul.tujuanPembelajaran}</div>

                <div class="subsection-title">2. Pemahaman Bermakna & Pertanyaan Pemantik</div>
                <div class="content-block"><strong>Pemahaman Bermakna:</strong><br>${modul.pemahamanBermakna}</div>
                <div class="content-block"><strong>Pertanyaan Pemantik:</strong><br>${modul.pertanyaanPemantik}</div>

                <div class="subsection-title">3. Kegiatan Pembelajaran Terstruktur</div>
                <div class="content-block"><strong>a. Kegiatan Pendahuluan:</strong><br>${modul.kegiatanPendahuluan}</div>
                <div class="content-block"><strong>b. Kegiatan Inti (${modul.modelPembelajaran}):</strong><br>${modul.kegiatanInti}</div>
                <div class="content-block"><strong>c. Kegiatan Penutup:</strong><br>${modul.kegiatanPenutup}</div>

                <div class="section-title">C. PEMBELAJARAN BERDIFERENSIASI</div>
                <div class="content-block"><strong>1. Diferensiasi Konten:</strong><br>${modul.diferensiasiKonten}</div>
                <div class="content-block"><strong>2. Diferensiasi Proses:</strong><br>${modul.diferensiasiProses}</div>
                <div class="content-block"><strong>3. Diferensiasi Produk:</strong><br>${modul.diferensiasiProduk}</div>

                <div class="section-title">D. ASESMEN & RUBRIK PENILAIAN</div>
                <div class="content-block"><strong>1. Asesmen Diagnostik (Awal):</strong><br>${modul.asesmenDiagnostik}</div>
                <div class="content-block"><strong>2. Asesmen Formatif (Proses & Sikap P3):</strong><br>${modul.asesmenFormatif}</div>
                <div class="content-block"><strong>3. Asesmen Sumatif (Akhir):</strong><br>${modul.asesmenSumatif}</div>
                
                <div class="subsection-title">4. Rubrik Penilaian Kriteria</div>
                <div class="content-block" style="background:#f8fafc; padding:10px; border:1px dashed #cbd5e1; border-radius:6px;">
                    ${modul.rubrikPenilaian}
                </div>

                ${if (!isCompact) """
                <div class="section-title">E. REMEDIAL, PENGAYAAN & REFLEKSI</div>
                <div class="content-block">${modul.remedialDanPengayaan}</div>

                <div class="section-title">F. LAMPIRAN (LKPD & BAHAN BACAAN)</div>
                <div class="content-block" style="background:#f8fafc; padding:12px; border:1px solid #e2e8f0; border-radius:6px;">
                    ${modul.lkpdDanMateri}
                </div>
                """ else ""}

                <table class="footer-signature">
                    <tr>
                        <td>
                            Mengetahui,<br>
                            Kepala Sekolah ${school}<br><br><br><br>
                            <strong>$principal</strong><br>
                            NIP. $principalNip
                        </td>
                        <td>
                            $cityDate<br>
                            Guru Mata Pelajaran<br><br><br><br>
                            <strong>$teacher</strong><br>
                            NIP. $teacherNip
                        </td>
                    </tr>
                </table>
            </body>
            </html>
        """.trimIndent()
    }

    fun generatePlainTextDocument(modul: ModulAjarEntity, profile: TeacherProfile? = null): String {
        val teacher = profile?.teacherName ?: modul.teacherName
        val school = profile?.schoolName ?: modul.schoolName
        return """
============================================================
              MODUL AJAR KURIKULUM MERDEKA
============================================================
Mata Pelajaran : ${modul.subject}
Fase / Kelas   : ${modul.fase} (${modul.grade})
Materi / Topik : ${modul.topic}
Alokasi Waktu  : ${modul.timeAllocation}
Penyusun       : $teacher
Satuan Sekolah : $school
Semester / TP  : ${modul.semester} / ${modul.academicYear}
Model Belajar  : ${modul.modelPembelajaran}
Dimensi P3     : ${modul.dimensiP3}

------------------------------------------------------------
A. INFORMASI UMUM
------------------------------------------------------------
• Kompetensi Awal:
${modul.kompetensiAwal}

• Profil Pelajar Pancasila:
${modul.profilPelajarPancasila}

• Sarana & Prasarana:
${modul.saranaPrasarana}

• Target Peserta Didik:
${modul.targetPesertaDidik}

------------------------------------------------------------
B. KOMPONEN INTI
------------------------------------------------------------
1. Capaian Pembelajaran (CP):
${modul.capaianPembelajaran}

2. Tujuan Pembelajaran (TP):
${modul.tujuanPembelajaran}

3. Pemahaman Bermakna:
${modul.pemahamanBermakna}

4. Pertanyaan Pemantik:
${modul.pertanyaanPemantik}

5. Kegiatan Pembelajaran:
a. Pendahuluan:
${modul.kegiatanPendahuluan}

b. Kegiatan Inti (${modul.modelPembelajaran}):
${modul.kegiatanInti}

c. Kegiatan Penutup:
${modul.kegiatanPenutup}

------------------------------------------------------------
C. PEMBELAJARAN BERDIFERENSIASI
------------------------------------------------------------
• Diferensiasi Konten:
${modul.diferensiasiKonten}

• Diferensiasi Proses:
${modul.diferensiasiProses}

• Diferensiasi Produk:
${modul.diferensiasiProduk}

------------------------------------------------------------
D. ASESMEN & RUBRIK PENILAIAN
------------------------------------------------------------
• Asesmen Diagnostik:
${modul.asesmenDiagnostik}

• Asesmen Formatif:
${modul.asesmenFormatif}

• Asesmen Sumatif:
${modul.asesmenSumatif}

• Rubrik Penilaian:
${modul.rubrikPenilaian}

------------------------------------------------------------
E. REMEDIAL & PENGAYAAN
------------------------------------------------------------
${modul.remedialDanPengayaan}

------------------------------------------------------------
F. LEMBAR KERJA PESERTA DIDIK (LKPD) & LAMPIRAN
------------------------------------------------------------
${modul.lkpdDanMateri}

============================================================
        """.trimIndent()
    }

    // P5 Document HTML
    fun generateP5HtmlDocument(p5Modul: P5ProjectModul, profile: TeacherProfile? = null): String {
        val teacher = profile?.teacherName ?: "Guru Pembimbing P5"
        val school = profile?.schoolName ?: "Satuan Pendidikan"
        val principal = profile?.principalName ?: "Kepala Sekolah"
        val principalNip = profile?.principalNip ?: "-"
        val teacherNip = profile?.teacherNip ?: "-"
        val cityDate = profile?.cityAndDate ?: "Jakarta, ${SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID")).format(Date())}"

        val tahapanHtml = p5Modul.alurTahapan.joinToString("<hr style='border:0; border-top:1px dashed #cbd5e1; margin:16px 0;'>") { tahap ->
            """
            <div style="margin-bottom:12px;">
                <h3 style="color:#0f766e; margin:0 0 4px 0; font-size:11pt;">${tahap.tahap} (${tahap.alokasiJp})</h3>
                <p style="font-weight:bold; color:#1e293b; margin:0 0 6px 0;">Aktivitas: ${tahap.namaAktivitas}</p>
                <div class="content-block">${tahap.deskripsiLangkah}</div>
                <div style="font-size:9pt; background:#f8fafc; padding:8px; border-radius:4px;">
                    <strong>Peran Fasilitator/Guru:</strong> ${tahap.peranGuru}<br>
                    <strong>Peran Siswa:</strong> ${tahap.peranSiswa}<br>
                    <strong>Asesmen Formatif:</strong> ${tahap.asesmenFormatif}
                </div>
            </div>
            """
        }

        return """
            <!DOCTYPE html>
            <html lang="id">
            <head>
                <meta charset="UTF-8">
                <title>${p5Modul.title}</title>
                <style>
                    body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; line-height: 1.5; color: #1a202c; padding: 20px; }
                    .header-box { border-bottom: 3px double #1e3a8a; padding-bottom: 12px; margin-bottom: 16px; text-align: center; }
                    .header-box h1 { font-size: 18pt; color: #1e3a8a; margin: 0 0 4px 0; }
                    .header-box h2 { font-size: 13pt; color: #0d9488; margin: 0 0 6px 0; }
                    .section-title { font-size: 12pt; font-weight: bold; color: #1e3a8a; background-color: #f1f5f9; padding: 6px 10px; border-left: 5px solid #1e3a8a; margin-top: 18px; margin-bottom: 10px; }
                    .badge { display: inline-block; padding: 3px 8px; background: #e0f2fe; color: #0369a1; border-radius: 10px; font-size: 8.5pt; font-weight: 600; margin-right: 4px; margin-bottom: 4px; }
                    .content-block { font-size: 9.5pt; white-space: pre-line; margin-bottom: 10px; text-align: justify; }
                    table.footer-signature { margin-top: 30px; width: 100%; page-break-inside: avoid; }
                    table.footer-signature td { width: 50%; text-align: center; vertical-align: top; font-size: 9.5pt; }
                </style>
            </head>
            <body>
                <div class="header-box">
                    <h1>MODUL PROJEK PENGUATAN PROFIL PELAJAR PANCASILA (P5)</h1>
                    <h2>TEMA: ${p5Modul.tema.uppercase(Locale.ROOT)}</h2>
                    <p>Satuan Pendidikan: $school | Fase: ${p5Modul.fase} (${p5Modul.grade}) | Alokasi: ${p5Modul.timeAllocation}</p>
                </div>

                <div class="section-title">A. INFORMASI UMUM PROJEK</div>
                <div class="content-block"><strong>Judul Projek:</strong> ${p5Modul.title}</div>
                <div class="content-block"><strong>Target Dimensi Profil Pelajar Pancasila:</strong><br>
                    ${p5Modul.targetDimensi.joinToString("") { "<span class='badge'>$it</span>" }}
                </div>
                <div class="content-block"><strong>Deskripsi Singkat:</strong><br>${p5Modul.deskripsiSingkat}</div>
                <div class="content-block"><strong>Tujuan & Sasaran Projek:</strong><br>${p5Modul.tujuanProjek}</div>

                <div class="section-title">B. ALUR & TAHAPAN PELAKSANAAN PROJEK</div>
                $tahapanHtml

                <div class="section-title">C. RUBRIK ASESMEN P5 (PERKEMBANGAN SUB-ELEMEN)</div>
                <div class="content-block" style="background:#f8fafc; padding:10px; border:1px dashed #cbd5e1; border-radius:6px;">
                    ${p5Modul.rubrikAsesmen}
                </div>

                <div class="section-title">D. LEMBAR REFLEKSI SISWA & TINDAK LANJUT</div>
                <div class="content-block" style="background:#f8fafc; padding:10px; border:1px solid #e2e8f0; border-radius:6px;">
                    ${p5Modul.lembarRefleksi}
                </div>

                <table class="footer-signature">
                    <tr>
                        <td>
                            Mengetahui,<br>
                            Kepala Sekolah $school<br><br><br><br>
                            <strong>$principal</strong><br>
                            NIP. $principalNip
                        </td>
                        <td>
                            $cityDate<br>
                            Koordinator Projek P5<br><br><br><br>
                            <strong>$teacher</strong><br>
                            NIP. $teacherNip
                        </td>
                    </tr>
                </table>
            </body>
            </html>
        """.trimIndent()
    }

    // Assessment Document HTML
    fun generateAssessmentHtmlDocument(doc: AssessmentDocument, profile: TeacherProfile? = null): String {
        val teacher = profile?.teacherName ?: "Guru Mata Pelajaran"
        val school = profile?.schoolName ?: "Satuan Pendidikan"
        val principal = profile?.principalName ?: "Kepala Sekolah"
        val principalNip = profile?.principalNip ?: "-"
        val teacherNip = profile?.teacherNip ?: "-"
        val cityDate = profile?.cityAndDate ?: "Jakarta, ${SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID")).format(Date())}"

        val kisiTableRows = doc.kisiKisiList.joinToString("") { k ->
            """
            <tr>
                <td style="text-align:center;">${k.nomorUrut}</td>
                <td>${k.capaianElemen}</td>
                <td>${k.materiPokok}</td>
                <td>${k.indikatorSoal}</td>
                <td style="text-align:center;"><strong>${k.levelKognitif}</strong></td>
                <td>${k.bentukSoal}</td>
                <td style="text-align:center;">${k.nomorSoal}</td>
            </tr>
            """
        }

        val soalRows = doc.soalList.joinToString("<hr style='border:0; border-top:1px dashed #cbd5e1; margin:18px 0;'>") { s ->
            val opsiHtml = if (s.pilihanOpsi.isNotEmpty()) {
                "<div style='margin: 8px 0 8px 14px; font-size:9.5pt;'>" +
                        s.pilihanOpsi.joinToString("<br>") +
                        "</div>"
            } else ""

            """
            <div style="page-break-inside:avoid; margin-bottom:14px;">
                <p style="font-weight:bold; color:#1e3a8a; margin:0 0 4px 0;">Soal Nomor ${s.nomor} [${s.bentukSoal} | Level: ${s.levelKognitif}] - Skor: ${s.skorMaksimal}</p>
                <div style="background:#f8fafc; padding:8px 12px; border-left:3px solid #0d9488; font-size:9.5pt; font-style:italic; margin-bottom:6px; white-space:pre-line;">
                    ${s.stimulusText}
                </div>
                <div style="font-size:9.5pt; margin-bottom:6px; font-weight:600; white-space:pre-line;">
                    ${s.pertanyaan}
                </div>
                $opsiHtml
                <div style="background:#f1f5f9; padding:6px 10px; border-radius:4px; font-size:9pt; margin-top:6px;">
                    <strong>Kunci Jawaban:</strong> ${s.kunciJawaban}<br>
                    <strong>Pembahasan / Pedoman Penskoran:</strong> ${s.pembahasanDanAlasan}
                </div>
            </div>
            """
        }

        return """
            <!DOCTYPE html>
            <html lang="id">
            <head>
                <meta charset="UTF-8">
                <title>${doc.title}</title>
                <style>
                    body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; line-height: 1.5; color: #1a202c; padding: 20px; }
                    .header-box { border-bottom: 3px double #1e3a8a; padding-bottom: 12px; margin-bottom: 16px; text-align: center; }
                    .header-box h1 { font-size: 17pt; color: #1e3a8a; margin: 0 0 4px 0; }
                    .header-box h2 { font-size: 13pt; color: #0d9488; margin: 0 0 6px 0; }
                    .section-title { font-size: 12pt; font-weight: bold; color: #1e3a8a; background-color: #f1f5f9; padding: 6px 10px; border-left: 5px solid #1e3a8a; margin-top: 18px; margin-bottom: 10px; }
                    table.kisi-table { width: 100%; border-collapse: collapse; margin-bottom: 16px; font-size: 9pt; }
                    table.kisi-table th, table.kisi-table td { border: 1px solid #cbd5e1; padding: 6px 8px; vertical-align: top; }
                    table.kisi-table th { background-color: #e2e8f0; color: #1e293b; font-weight: bold; text-align: center; }
                    table.footer-signature { margin-top: 30px; width: 100%; page-break-inside: avoid; }
                    table.footer-signature td { width: 50%; text-align: center; vertical-align: top; font-size: 9.5pt; }
                </style>
            </head>
            <body>
                <div class="header-box">
                    <h1>KISI-KISI & BANK SOAL ASESMEN KURIKULUM MERDEKA</h1>
                    <h2>MATA PELAJARAN: ${doc.subject.uppercase(Locale.ROOT)} (${doc.topikUjian})</h2>
                    <p>Satuan Pendidikan: $school | Fase: ${doc.fase} (${doc.grade}) | ${doc.jenisAsesmen}</p>
                </div>

                <div class="section-title">A. TABEL KISI-KISI PENULISAN SOAL ASESMEN</div>
                <table class="kisi-table">
                    <thead>
                        <tr>
                            <th style="width:5%;">No</th>
                            <th style="width:20%;">Elemen / CP</th>
                            <th style="width:20%;">Materi Pokok</th>
                            <th style="width:30%;">Indikator Soal</th>
                            <th style="width:10%;">Level</th>
                            <th style="width:10%;">Bentuk</th>
                            <th style="width:5%;">No Soal</th>
                        </tr>
                    </thead>
                    <tbody>
                        $kisiTableRows
                    </tbody>
                </table>

                <div class="section-title">B. KARTU SOAL HOTS & PEDOMAN KUNCI JAWABAN</div>
                $soalRows

                <div class="section-title">C. PEDOMAN PENSKORAN & KRITERIA KETERCAPAIAN (KKTP)</div>
                <div style="font-size:9.5pt; white-space:pre-line; background:#f8fafc; padding:10px; border:1px solid #e2e8f0; border-radius:6px;">
                    ${doc.pedomanPenskoran}
                </div>

                <table class="footer-signature">
                    <tr>
                        <td>
                            Mengetahui,<br>
                            Kepala Sekolah $school<br><br><br><br>
                            <strong>$principal</strong><br>
                            NIP. $principalNip
                        </td>
                        <td>
                            $cityDate<br>
                            Guru Pengampu Mata Pelajaran<br><br><br><br>
                            <strong>$teacher</strong><br>
                            NIP. $teacherNip
                        </td>
                    </tr>
                </table>
            </body>
            </html>
        """.trimIndent()
    }

    // Print & Export Methods
    fun printOrSavePdf(context: Context, modul: ModulAjarEntity) {
        val profile = TeacherProfile.loadFromPreferences(context)
        val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager ?: return
        val webView = WebView(context)
        val htmlContent = generateHtmlDocument(modul, profile)

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?) = false

            override fun onPageFinished(view: WebView?, url: String?) {
                val printAdapter = webView.createPrintDocumentAdapter("Modul_Ajar_${modul.topic.replace(" ", "_")}")
                val printAttributes = PrintAttributes.Builder()
                    .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                    .setResolution(PrintAttributes.Resolution("id_rpp", "RPP Print", 300, 300))
                    .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                    .build()

                printManager.print("Modul_Ajar_${modul.subject}_${modul.grade}", printAdapter, printAttributes)
            }
        }
        webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
    }

    fun printOrSaveP5Pdf(context: Context, p5Modul: P5ProjectModul) {
        val profile = TeacherProfile.loadFromPreferences(context)
        val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager ?: return
        val webView = WebView(context)
        val htmlContent = generateP5HtmlDocument(p5Modul, profile)

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?) = false
            override fun onPageFinished(view: WebView?, url: String?) {
                val printAdapter = webView.createPrintDocumentAdapter("Modul_P5_${p5Modul.tema.replace(" ", "_")}")
                val printAttributes = PrintAttributes.Builder()
                    .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                    .setResolution(PrintAttributes.Resolution("id_p5", "P5 Print", 300, 300))
                    .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                    .build()
                printManager.print("Modul_P5_${p5Modul.tema}", printAdapter, printAttributes)
            }
        }
        webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
    }

    fun printOrSaveAssessmentPdf(context: Context, doc: AssessmentDocument) {
        val profile = TeacherProfile.loadFromPreferences(context)
        val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager ?: return
        val webView = WebView(context)
        val htmlContent = generateAssessmentHtmlDocument(doc, profile)

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?) = false
            override fun onPageFinished(view: WebView?, url: String?) {
                val printAdapter = webView.createPrintDocumentAdapter("Kisi_Soal_${doc.subject.replace(" ", "_")}")
                val printAttributes = PrintAttributes.Builder()
                    .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                    .setResolution(PrintAttributes.Resolution("id_soal", "Soal Print", 300, 300))
                    .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                    .build()
                printManager.print("Kisi_Soal_${doc.subject}", printAdapter, printAttributes)
            }
        }
        webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
    }

    fun exportToWordDoc(context: Context, modul: ModulAjarEntity): Uri? {
        val profile = TeacherProfile.loadFromPreferences(context)
        try {
            val html = generateHtmlDocument(modul, profile)
            val fileName = "Modul_Ajar_${modul.topic.replace("[^a-zA-Z0-9]".toRegex(), "_")}.doc"
            val file = File(context.cacheDir, fileName)
            FileOutputStream(file).use { out ->
                out.write(html.toByteArray(Charsets.UTF_8))
            }
            return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun exportP5ToWordDoc(context: Context, p5Modul: P5ProjectModul): Uri? {
        val profile = TeacherProfile.loadFromPreferences(context)
        try {
            val html = generateP5HtmlDocument(p5Modul, profile)
            val fileName = "Modul_P5_${p5Modul.title.replace("[^a-zA-Z0-9]".toRegex(), "_")}.doc"
            val file = File(context.cacheDir, fileName)
            FileOutputStream(file).use { out ->
                out.write(html.toByteArray(Charsets.UTF_8))
            }
            return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun exportAssessmentToWordDoc(context: Context, doc: AssessmentDocument): Uri? {
        val profile = TeacherProfile.loadFromPreferences(context)
        try {
            val html = generateAssessmentHtmlDocument(doc, profile)
            val fileName = "Kisi_Soal_${doc.subject.replace("[^a-zA-Z0-9]".toRegex(), "_")}.doc"
            val file = File(context.cacheDir, fileName)
            FileOutputStream(file).use { out ->
                out.write(html.toByteArray(Charsets.UTF_8))
            }
            return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun shareModulText(context: Context, modul: ModulAjarEntity) {
        val profile = TeacherProfile.loadFromPreferences(context)
        val text = generatePlainTextDocument(modul, profile)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Modul Ajar ${modul.subject} - ${modul.topic}")
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(Intent.createChooser(intent, "Bagikan Modul Ajar Kurikulum Merdeka"))
    }

    fun copyToClipboard(context: Context, modul: ModulAjarEntity) {
        val profile = TeacherProfile.loadFromPreferences(context)
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Modul Ajar", generatePlainTextDocument(modul, profile))
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Modul Ajar berhasil disalin ke Clipboard!", Toast.LENGTH_SHORT).show()
    }

    // ==========================================
    // PROTA (PROGRAM TAHUNAN) HTML & EXPORT
    // ==========================================
    fun generateProtaHtml(prota: ProtaDocument, profile: TeacherProfile? = null): String {
        val teacher = profile?.teacherName ?: "Guru Pengampu"
        val school = profile?.schoolName ?: "Satuan Pendidikan"
        val principal = profile?.principalName ?: "Kepala Sekolah"
        val principalNip = profile?.principalNip ?: "-"
        val teacherNip = profile?.teacherNip ?: "-"
        val cityDate = profile?.cityAndDate ?: "Jakarta, ${SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID")).format(Date())}"

        val rows = prota.items.joinToString("") { item ->
            """
            <tr>
                <td style="text-align:center;">${item.nomor}</td>
                <td style="text-align:center;">${item.semester}</td>
                <td><strong>${item.babMateri}</strong></td>
                <td>${item.capaianTujuan}</td>
                <td style="text-align:center; font-weight:bold;">${item.alokasiJp} JP</td>
            </tr>
            """
        }

        return """
            <!DOCTYPE html>
            <html lang="id">
            <head>
                <meta charset="UTF-8">
                <title>${prota.title}</title>
                <style>
                    body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; line-height: 1.5; color: #1a202c; padding: 20px; }
                    .header-box { border-bottom: 3px double #1e3a8a; padding-bottom: 12px; margin-bottom: 16px; text-align: center; }
                    .header-box h1 { font-size: 17pt; color: #1e3a8a; margin: 0 0 4px 0; }
                    .header-box h2 { font-size: 13pt; color: #0d9488; margin: 0 0 6px 0; }
                    table.data-table { width: 100%; border-collapse: collapse; margin-bottom: 16px; font-size: 9.5pt; }
                    table.data-table th, table.data-table td { border: 1px solid #cbd5e1; padding: 6px 8px; vertical-align: middle; }
                    table.data-table th { background-color: #e2e8f0; color: #1e293b; font-weight: bold; text-align: center; }
                    table.footer-signature { margin-top: 30px; width: 100%; page-break-inside: avoid; }
                    table.footer-signature td { width: 50%; text-align: center; vertical-align: top; font-size: 9.5pt; }
                </style>
            </head>
            <body>
                <div class="header-box">
                    <h1>PROGRAM TAHUNAN (PROTA) KURIKULUM MERDEKA</h1>
                    <h2>MATA PELAJARAN: ${prota.subject.uppercase(Locale.ROOT)} (${prota.fase} - ${prota.grade})</h2>
                    <p>Satuan Pendidikan: $school | Tahun Ajaran: ${prota.academicYear} | Total Alokasi: ${prota.totalJp} JP</p>
                </div>

                <table class="data-table">
                    <thead>
                        <tr>
                            <th style="width:5%;">No</th>
                            <th style="width:18%;">Semester</th>
                            <th style="width:25%;">Materi Pokok / Lingkup Materi</th>
                            <th style="width:42%;">Tujuan Pembelajaran</th>
                            <th style="width:10%;">Alokasi JP</th>
                        </tr>
                    </thead>
                    <tbody>
                        $rows
                        <tr style="background:#f1f5f9; font-weight:bold;">
                            <td colspan="4" style="text-align:right;">TOTAL ALOKASI JAM PELAJARAN (1 TAHUN AJARAN):</td>
                            <td style="text-align:center; color:#1e3a8a;">${prota.totalJp} JP</td>
                        </tr>
                    </tbody>
                </table>

                <table class="footer-signature">
                    <tr>
                        <td>
                            Mengetahui,<br>
                            Kepala Sekolah $school<br><br><br><br>
                            <strong>$principal</strong><br>
                            NIP. $principalNip
                        </td>
                        <td>
                            $cityDate<br>
                            Guru Mata Pelajaran<br><br><br><br>
                            <strong>$teacher</strong><br>
                            NIP. $teacherNip
                        </td>
                    </tr>
                </table>
            </body>
            </html>
        """.trimIndent()
    }

    fun printOrSaveProtaPdf(context: Context, prota: ProtaDocument) {
        val profile = TeacherProfile.loadFromPreferences(context)
        val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager ?: return
        val webView = WebView(context)
        val htmlContent = generateProtaHtml(prota, profile)

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?) = false
            override fun onPageFinished(view: WebView?, url: String?) {
                val printAdapter = webView.createPrintDocumentAdapter("PROTA_${prota.subject.replace(" ", "_")}")
                val printAttributes = PrintAttributes.Builder()
                    .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                    .setResolution(PrintAttributes.Resolution("id_prota", "Prota Print", 300, 300))
                    .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                    .build()
                printManager.print("PROTA_${prota.subject}", printAdapter, printAttributes)
            }
        }
        webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
    }

    fun exportProtaToWord(context: Context, prota: ProtaDocument): Uri? {
        val profile = TeacherProfile.loadFromPreferences(context)
        try {
            val html = generateProtaHtml(prota, profile)
            val fileName = "PROTA_${prota.subject.replace("[^a-zA-Z0-9]".toRegex(), "_")}.doc"
            val file = File(context.cacheDir, fileName)
            FileOutputStream(file).use { out -> out.write(html.toByteArray(Charsets.UTF_8)) }
            return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    // ==========================================
    // PROMES (PROGRAM SEMESTER) HTML & EXPORT
    // ==========================================
    fun generatePromesHtml(promes: PromesDocument, profile: TeacherProfile? = null): String {
        val teacher = profile?.teacherName ?: "Guru Pengampu"
        val school = profile?.schoolName ?: "Satuan Pendidikan"
        val principal = profile?.principalName ?: "Kepala Sekolah"
        val principalNip = profile?.principalNip ?: "-"
        val teacherNip = profile?.teacherNip ?: "-"
        val cityDate = profile?.cityAndDate ?: "Jakarta, ${SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID")).format(Date())}"

        val months = promes.items.firstOrNull()?.weeklyDistribution?.map { it.bulan } ?: emptyList()
        val monthHeaders = months.joinToString("") { "<th colspan='4'>$it</th>" }
        val weekSubHeaders = months.joinToString("") { "<th>1</th><th>2</th><th>3</th><th>4</th>" }

        val rows = promes.items.joinToString("") { item ->
            val weeksCols = item.weeklyDistribution.joinToString("") { monthMatrix ->
                monthMatrix.weeks.joinToString("") { jp ->
                    if (jp > 0) "<td style='text-align:center; font-weight:bold; background:#dbeafe;'>$jp</td>"
                    else "<td style='text-align:center; color:#cbd5e1;'>-</td>"
                }
            }

            """
            <tr>
                <td style="text-align:center;">${item.nomor}</td>
                <td><strong>${item.materiPokok}</strong></td>
                <td>${item.tujuanPembelajaran}</td>
                <td style="text-align:center; font-weight:bold;">${item.alokasiJp}</td>
                $weeksCols
            </tr>
            """
        }

        return """
            <!DOCTYPE html>
            <html lang="id">
            <head>
                <meta charset="UTF-8">
                <title>${promes.title}</title>
                <style>
                    body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; line-height: 1.4; color: #1a202c; padding: 16px; }
                    .header-box { border-bottom: 3px double #1e3a8a; padding-bottom: 10px; margin-bottom: 14px; text-align: center; }
                    .header-box h1 { font-size: 16pt; color: #1e3a8a; margin: 0 0 4px 0; }
                    .header-box h2 { font-size: 12pt; color: #0d9488; margin: 0 0 4px 0; }
                    table.data-table { width: 100%; border-collapse: collapse; margin-bottom: 16px; font-size: 8.5pt; }
                    table.data-table th, table.data-table td { border: 1px solid #cbd5e1; padding: 4px 5px; vertical-align: middle; }
                    table.data-table th { background-color: #e2e8f0; color: #1e293b; font-weight: bold; text-align: center; }
                    table.footer-signature { margin-top: 24px; width: 100%; page-break-inside: avoid; }
                    table.footer-signature td { width: 50%; text-align: center; vertical-align: top; font-size: 9pt; }
                </style>
            </head>
            <body>
                <div class="header-box">
                    <h1>PROGRAM SEMESTER (PROMES) KURIKULUM MERDEKA</h1>
                    <h2>MATA PELAJARAN: ${promes.subject.uppercase(Locale.ROOT)} (${promes.fase} - ${promes.grade})</h2>
                    <p>Satuan Pendidikan: $school | ${promes.semester} - Tahun Ajaran: ${promes.academicYear} | Alokasi: ${promes.totalJp} JP</p>
                </div>

                <table class="data-table">
                    <thead>
                        <tr>
                            <th rowspan="2" style="width:3%;">No</th>
                            <th rowspan="2" style="width:20%;">Materi Pokok</th>
                            <th rowspan="2" style="width:30%;">Tujuan Pembelajaran</th>
                            <th rowspan="2" style="width:6%;">Alokasi JP</th>
                            $monthHeaders
                        </tr>
                        <tr>
                            $weekSubHeaders
                        </tr>
                    </thead>
                    <tbody>
                        $rows
                    </tbody>
                </table>

                <table class="footer-signature">
                    <tr>
                        <td>
                            Mengetahui,<br>
                            Kepala Sekolah $school<br><br><br><br>
                            <strong>$principal</strong><br>
                            NIP. $principalNip
                        </td>
                        <td>
                            $cityDate<br>
                            Guru Mata Pelajaran<br><br><br><br>
                            <strong>$teacher</strong><br>
                            NIP. $teacherNip
                        </td>
                    </tr>
                </table>
            </body>
            </html>
        """.trimIndent()
    }

    fun printOrSavePromesPdf(context: Context, promes: PromesDocument) {
        val profile = TeacherProfile.loadFromPreferences(context)
        val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager ?: return
        val webView = WebView(context)
        val htmlContent = generatePromesHtml(promes, profile)

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?) = false
            override fun onPageFinished(view: WebView?, url: String?) {
                val printAdapter = webView.createPrintDocumentAdapter("PROMES_${promes.subject.replace(" ", "_")}")
                val printAttributes = PrintAttributes.Builder()
                    .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                    .setResolution(PrintAttributes.Resolution("id_promes", "Promes Print", 300, 300))
                    .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                    .build()
                printManager.print("PROMES_${promes.subject}", printAdapter, printAttributes)
            }
        }
        webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
    }

    fun exportPromesToWord(context: Context, promes: PromesDocument): Uri? {
        val profile = TeacherProfile.loadFromPreferences(context)
        try {
            val html = generatePromesHtml(promes, profile)
            val fileName = "PROMES_${promes.subject.replace("[^a-zA-Z0-9]".toRegex(), "_")}.doc"
            val file = File(context.cacheDir, fileName)
            FileOutputStream(file).use { out -> out.write(html.toByteArray(Charsets.UTF_8)) }
            return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    // ==========================================
    // ATP (ALUR TUJUAN PEMBELAJARAN) HTML & EXPORT
    // ==========================================
    fun generateAtpHtml(atp: AtpDocument, profile: TeacherProfile? = null): String {
        val teacher = profile?.teacherName ?: "Guru Pengampu"
        val school = profile?.schoolName ?: "Satuan Pendidikan"
        val principal = profile?.principalName ?: "Kepala Sekolah"
        val principalNip = profile?.principalNip ?: "-"
        val teacherNip = profile?.teacherNip ?: "-"
        val cityDate = profile?.cityAndDate ?: "Jakarta, ${SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID")).format(Date())}"

        val rows = atp.alurTujuanList.joinToString("") { step ->
            """
            <tr>
                <td style="text-align:center; font-weight:bold;">${step.nomorUrut}</td>
                <td><strong>${step.elemen}</strong></td>
                <td>${step.capaianPembelajaran}</td>
                <td><strong>${step.tujuanPembelajaran}</strong><br><span style="font-size:8.5pt; color:#475569;">Materi: ${step.materiPokok}</span></td>
                <td style="text-align:center; font-weight:bold;">${step.alokasiJp} JP</td>
                <td style="font-size:8.5pt;">${step.profilPancasila}</td>
                <td style="font-size:8.5pt;">${step.indikatorKetercapaian}</td>
            </tr>
            """
        }

        return """
            <!DOCTYPE html>
            <html lang="id">
            <head>
                <meta charset="UTF-8">
                <title>${atp.title}</title>
                <style>
                    body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; line-height: 1.5; color: #1a202c; padding: 20px; }
                    .header-box { border-bottom: 3px double #1e3a8a; padding-bottom: 12px; margin-bottom: 16px; text-align: center; }
                    .header-box h1 { font-size: 17pt; color: #1e3a8a; margin: 0 0 4px 0; }
                    .header-box h2 { font-size: 13pt; color: #0d9488; margin: 0 0 6px 0; }
                    .section-title { font-size: 11pt; font-weight: bold; color: #1e3a8a; background-color: #f1f5f9; padding: 6px 10px; border-left: 5px solid #1e3a8a; margin-top: 14px; margin-bottom: 8px; }
                    table.data-table { width: 100%; border-collapse: collapse; margin-bottom: 16px; font-size: 9pt; }
                    table.data-table th, table.data-table td { border: 1px solid #cbd5e1; padding: 6px 8px; vertical-align: top; }
                    table.data-table th { background-color: #e2e8f0; color: #1e293b; font-weight: bold; text-align: center; }
                    table.footer-signature { margin-top: 30px; width: 100%; page-break-inside: avoid; }
                    table.footer-signature td { width: 50%; text-align: center; vertical-align: top; font-size: 9.5pt; }
                </style>
            </head>
            <body>
                <div class="header-box">
                    <h1>ALUR TUJUAN PEMBELAJARAN (ATP) KURIKULUM MERDEKA</h1>
                    <h2>MATA PELAJARAN: ${atp.subject.uppercase(Locale.ROOT)} (${atp.fase} - ${atp.grade})</h2>
                    <p>Satuan Pendidikan: $school | Alokasi Total: ${atp.totalJp} JP</p>
                </div>

                <div class="section-title">A. RASIONAL & KARAKTERISTIK MATA PELAJARAN</div>
                <div style="font-size:9.5pt; text-align:justify; margin-bottom:10px;">${atp.rasional}</div>
                <div style="font-size:9.5pt; text-align:justify; margin-bottom:14px;"><strong>Karakteristik:</strong> ${atp.karakteristikMataPelajaran}</div>

                <div class="section-title">B. BAGAN ALUR TUJUAN PEMBELAJARAN (ATP)</div>
                <table class="data-table">
                    <thead>
                        <tr>
                            <th style="width:5%;">Alur</th>
                            <th style="width:14%;">Elemen</th>
                            <th style="width:23%;">Capaian Pembelajaran (CP)</th>
                            <th style="width:25%;">Tujuan Pembelajaran (TP) & Materi</th>
                            <th style="width:8%;">Alokasi</th>
                            <th style="width:12%;">Dimensi P3</th>
                            <th style="width:13%;">Indikator Ketercapaian</th>
                        </tr>
                    </thead>
                    <tbody>
                        $rows
                    </tbody>
                </table>

                <table class="footer-signature">
                    <tr>
                        <td>
                            Mengetahui,<br>
                            Kepala Sekolah $school<br><br><br><br>
                            <strong>$principal</strong><br>
                            NIP. $principalNip
                        </td>
                        <td>
                            $cityDate<br>
                            Guru Pengampu Mata Pelajaran<br><br><br><br>
                            <strong>$teacher</strong><br>
                            NIP. $teacherNip
                        </td>
                    </tr>
                </table>
            </body>
            </html>
        """.trimIndent()
    }

    fun printOrSaveAtpPdf(context: Context, atp: AtpDocument) {
        val profile = TeacherProfile.loadFromPreferences(context)
        val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager ?: return
        val webView = WebView(context)
        val htmlContent = generateAtpHtml(atp, profile)

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?) = false
            override fun onPageFinished(view: WebView?, url: String?) {
                val printAdapter = webView.createPrintDocumentAdapter("ATP_${atp.subject.replace(" ", "_")}")
                val printAttributes = PrintAttributes.Builder()
                    .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                    .setResolution(PrintAttributes.Resolution("id_atp", "ATP Print", 300, 300))
                    .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                    .build()
                printManager.print("ATP_${atp.subject}", printAdapter, printAttributes)
            }
        }
        webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
    }

    fun exportAtpToWord(context: Context, atp: AtpDocument): Uri? {
        val profile = TeacherProfile.loadFromPreferences(context)
        try {
            val html = generateAtpHtml(atp, profile)
            val fileName = "ATP_${atp.subject.replace("[^a-zA-Z0-9]".toRegex(), "_")}.doc"
            val file = File(context.cacheDir, fileName)
            FileOutputStream(file).use { out -> out.write(html.toByteArray(Charsets.UTF_8)) }
            return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    // ==========================================
    // RAPOR & KKTP DESKRIPSI HTML & EXPORT
    // ==========================================
    fun generateRaporHtml(
        subject: String,
        grade: String,
        students: List<StudentRaporEntry>,
        intervals: List<KktpInterval>,
        profile: TeacherProfile? = null
    ): String {
        val teacher = profile?.teacherName ?: "Guru Pengampu"
        val school = profile?.schoolName ?: "Satuan Pendidikan"
        val principal = profile?.principalName ?: "Kepala Sekolah"
        val principalNip = profile?.principalNip ?: "-"
        val teacherNip = profile?.teacherNip ?: "-"
        val cityDate = profile?.cityAndDate ?: "Jakarta, ${SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID")).format(Date())}"

        val intervalRows = intervals.joinToString("") { inv ->
            """
            <tr>
                <td style="text-align:center; font-weight:bold;">${inv.rentang}</td>
                <td style="font-weight:bold; color:#1e3a8a;">${inv.predikat}</td>
                <td>${inv.deskripsiKriteria}</td>
                <td>${inv.tindakLanjut}</td>
            </tr>
            """
        }

        val studentRows = students.mapIndexed { idx, s ->
            """
            <tr>
                <td style="text-align:center;">${idx + 1}</td>
                <td><strong>${s.namaSiswa}</strong></td>
                <td style="text-align:center; font-weight:bold; font-size:11pt; color:#1e3a8a;">${s.nilaiAkhir}</td>
                <td style="text-align:justify;">${s.deskripsiCapaian}</td>
            </tr>
            """
        }.joinToString("")

        return """
            <!DOCTYPE html>
            <html lang="id">
            <head>
                <meta charset="UTF-8">
                <title>Rekap Nilai & Deskripsi Rapor - $subject</title>
                <style>
                    body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; line-height: 1.5; color: #1a202c; padding: 20px; }
                    .header-box { border-bottom: 3px double #1e3a8a; padding-bottom: 12px; margin-bottom: 16px; text-align: center; }
                    .header-box h1 { font-size: 16pt; color: #1e3a8a; margin: 0 0 4px 0; }
                    .header-box h2 { font-size: 12.5pt; color: #0d9488; margin: 0 0 4px 0; }
                    .section-title { font-size: 11pt; font-weight: bold; color: #1e3a8a; background-color: #f1f5f9; padding: 5px 10px; border-left: 5px solid #1e3a8a; margin-top: 14px; margin-bottom: 8px; }
                    table.data-table { width: 100%; border-collapse: collapse; margin-bottom: 16px; font-size: 9.5pt; }
                    table.data-table th, table.data-table td { border: 1px solid #cbd5e1; padding: 6px 8px; vertical-align: top; }
                    table.data-table th { background-color: #e2e8f0; color: #1e293b; font-weight: bold; text-align: center; }
                    table.footer-signature { margin-top: 30px; width: 100%; page-break-inside: avoid; }
                    table.footer-signature td { width: 50%; text-align: center; vertical-align: top; font-size: 9.5pt; }
                </style>
            </head>
            <body>
                <div class="header-box">
                    <h1>REKAPITULASI CAPAIAN KOMPETENSI & DESKRIPSI RAPOR</h1>
                    <h2>MATA PELAJARAN: ${subject.uppercase(Locale.ROOT)} ($grade)</h2>
                    <p>Satuan Pendidikan: $school | Standar Kurikulum Merdeka Kemendikbudristek</p>
                </div>

                <div class="section-title">A. KRITERIA KETERCAPAIAN TUJUAN PEMBELAJARAN (KKTP)</div>
                <table class="data-table">
                    <thead>
                        <tr>
                            <th style="width:15%;">Interval Nilai</th>
                            <th style="width:25%;">Kategori Ketercapaian</th>
                            <th style="width:35%;">Deskripsi Kriteria</th>
                            <th style="width:25%;">Rekomendasi Tindak Lanjut</th>
                        </tr>
                    </thead>
                    <tbody>
                        $intervalRows
                    </tbody>
                </table>

                <div class="section-title">B. DAFTAR NILAI AKHIR & DESKRIPSI CAPAIAN KOMPETENSI SISWA</div>
                <table class="data-table">
                    <thead>
                        <tr>
                            <th style="width:5%;">No</th>
                            <th style="width:22%;">Nama Peserta Didik</th>
                            <th style="width:10%;">Nilai Akhir</th>
                            <th style="width:63%;">Deskripsi Rapor Resmi (Capaian Kompetensi)</th>
                        </tr>
                    </thead>
                    <tbody>
                        $studentRows
                    </tbody>
                </table>

                <table class="footer-signature">
                    <tr>
                        <td>
                            Mengetahui,<br>
                            Kepala Sekolah $school<br><br><br><br>
                            <strong>$principal</strong><br>
                            NIP. $principalNip
                        </td>
                        <td>
                            $cityDate<br>
                            Guru Mata Pelajaran<br><br><br><br>
                            <strong>$teacher</strong><br>
                            NIP. $teacherNip
                        </td>
                    </tr>
                </table>
            </body>
            </html>
        """.trimIndent()
    }

    fun printOrSaveRaporPdf(context: Context, subject: String, grade: String, students: List<StudentRaporEntry>, intervals: List<KktpInterval>) {
        val profile = TeacherProfile.loadFromPreferences(context)
        val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager ?: return
        val webView = WebView(context)
        val htmlContent = generateRaporHtml(subject, grade, students, intervals, profile)

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?) = false
            override fun onPageFinished(view: WebView?, url: String?) {
                val printAdapter = webView.createPrintDocumentAdapter("Deskripsi_Rapor_${subject.replace(" ", "_")}")
                val printAttributes = PrintAttributes.Builder()
                    .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                    .setResolution(PrintAttributes.Resolution("id_rapor", "Rapor Print", 300, 300))
                    .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                    .build()
                printManager.print("Deskripsi_Rapor_$subject", printAdapter, printAttributes)
            }
        }
        webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
    }

    fun exportRaporToWord(context: Context, subject: String, grade: String, students: List<StudentRaporEntry>, intervals: List<KktpInterval>): Uri? {
        val profile = TeacherProfile.loadFromPreferences(context)
        try {
            val html = generateRaporHtml(subject, grade, students, intervals, profile)
            val fileName = "Deskripsi_Rapor_${subject.replace("[^a-zA-Z0-9]".toRegex(), "_")}.doc"
            val file = File(context.cacheDir, fileName)
            FileOutputStream(file).use { out -> out.write(html.toByteArray(Charsets.UTF_8)) }
            return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    // ==========================================
    // JURNAL OBSERVASI & PENILAIAN ANTARTEMAN
    // ==========================================
    fun generateObservationAndPeerHtml(
        jurnalList: List<JurnalObservasiItem>,
        peerQuestions: List<PeerAssessmentQuestion>,
        profile: TeacherProfile? = null
    ): String {
        val teacher = profile?.teacherName ?: "Guru Pengampu"
        val school = profile?.schoolName ?: "Satuan Pendidikan"
        val principal = profile?.principalName ?: "Kepala Sekolah"
        val principalNip = profile?.principalNip ?: "-"
        val teacherNip = profile?.teacherNip ?: "-"
        val cityDate = profile?.cityAndDate ?: "Jakarta, ${SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID")).format(Date())}"

        val jurnalRows = jurnalList.mapIndexed { idx, j ->
            """
            <tr>
                <td style="text-align:center;">${idx + 1}</td>
                <td style="text-align:center;">${j.tanggal}</td>
                <td><strong>${j.namaSiswa}</strong></td>
                <td>${j.dimensiP3}</td>
                <td>${j.catatanPerilaku}</td>
                <td style="text-align:center;"><strong>${j.butirSikapPositifNegatif}</strong></td>
                <td>${j.rencanaTindakLanjut}</td>
            </tr>
            """
        }.joinToString("")

        val peerRows = peerQuestions.map { p ->
            """
            <tr>
                <td style="text-align:center;">${p.no}</td>
                <td>${p.pernyataan}</td>
                <td style="text-align:center; font-size:8.5pt;">${p.dimensiTerkait}</td>
                <td style="text-align:center;"></td>
                <td style="text-align:center;"></td>
                <td style="text-align:center;"></td>
                <td style="text-align:center;"></td>
            </tr>
            """
        }.joinToString("")

        return """
            <!DOCTYPE html>
            <html lang="id">
            <head>
                <meta charset="UTF-8">
                <title>Instrumen Jurnal Observasi & Penilaian Antarteman</title>
                <style>
                    body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; line-height: 1.5; color: #1a202c; padding: 20px; }
                    .header-box { border-bottom: 3px double #1e3a8a; padding-bottom: 12px; margin-bottom: 16px; text-align: center; }
                    .header-box h1 { font-size: 16pt; color: #1e3a8a; margin: 0 0 4px 0; }
                    .header-box h2 { font-size: 12pt; color: #0d9488; margin: 0 0 4px 0; }
                    .section-title { font-size: 11pt; font-weight: bold; color: #1e3a8a; background-color: #f1f5f9; padding: 6px 10px; border-left: 5px solid #1e3a8a; margin-top: 16px; margin-bottom: 8px; }
                    table.data-table { width: 100%; border-collapse: collapse; margin-bottom: 16px; font-size: 9pt; }
                    table.data-table th, table.data-table td { border: 1px solid #cbd5e1; padding: 6px 8px; vertical-align: top; }
                    table.data-table th { background-color: #e2e8f0; color: #1e293b; font-weight: bold; text-align: center; }
                    table.footer-signature { margin-top: 30px; width: 100%; page-break-inside: avoid; }
                    table.footer-signature td { width: 50%; text-align: center; vertical-align: top; font-size: 9.5pt; }
                </style>
            </head>
            <body>
                <div class="header-box">
                    <h1>INSTRUMEN JURNAL OBSERVASI & PENILAIAN SIKAP P3</h1>
                    <h2>PROFIL PELAJAR PANCASILA - KURIKULUM MERDEKA</h2>
                    <p>Satuan Pendidikan: $school | Tahun Ajaran: ${profile?.defaultAcademicYear ?: "2024/2025"}</p>
                </div>

                <div class="section-title">A. JURNAL HARIAN OBSERVASI SIKAP & ANEKDOT GURU</div>
                <table class="data-table">
                    <thead>
                        <tr>
                            <th style="width:5%;">No</th>
                            <th style="width:12%;">Tanggal</th>
                            <th style="width:18%;">Nama Siswa</th>
                            <th style="width:15%;">Dimensi P3</th>
                            <th style="width:25%;">Catatan Perilaku Kejadian</th>
                            <th style="width:10%;">Nilai Sikap</th>
                            <th style="width:15%;">Tindak Lanjut / Apresiasi</th>
                        </tr>
                    </thead>
                    <tbody>
                        $jurnalRows
                    </tbody>
                </table>

                <div class="section-title" style="page-break-before:always;">B. LEMBAR PENILAIAN ANTARTEMAN (PEER-ASSESSMENT) SAAT DISKUSI KELOMPOK</div>
                <div style="font-size:9.5pt; margin-bottom:8px;">
                    Nama Siswa Penilai: __________________________ &nbsp;&nbsp;&nbsp;&nbsp; Nama Teman yang Dinilai: __________________________<br>
                    Kelas / Kelompok: __________________________ &nbsp;&nbsp;&nbsp;&nbsp; Materi / Topik: __________________________
                </div>

                <table class="data-table">
                    <thead>
                        <tr>
                            <th rowspan="2" style="width:5%;">No</th>
                            <th rowspan="2" style="width:50%;">Pernyataan Butir Sikap Kolaboratif</th>
                            <th rowspan="2" style="width:21%;">Dimensi Terkait</th>
                            <th colspan="4" style="width:24%;">Skala Penilaian</th>
                        </tr>
                        <tr>
                            <th>4 (Selalu)</th>
                            <th>3 (Sering)</th>
                            <th>2 (Kadang)</th>
                            <th>1 (Tidak)</th>
                        </tr>
                    </thead>
                    <tbody>
                        $peerRows
                    </tbody>
                </table>

                <table class="footer-signature">
                    <tr>
                        <td>
                            Mengetahui,<br>
                            Kepala Sekolah $school<br><br><br><br>
                            <strong>$principal</strong><br>
                            NIP. $principalNip
                        </td>
                        <td>
                            $cityDate<br>
                            Guru Mata Pelajaran / Wali Kelas<br><br><br><br>
                            <strong>$teacher</strong><br>
                            NIP. $teacherNip
                        </td>
                    </tr>
                </table>
            </body>
            </html>
        """.trimIndent()
    }

    fun printOrSaveObservationPdf(context: Context, jurnalList: List<JurnalObservasiItem>, peerQuestions: List<PeerAssessmentQuestion>) {
        val profile = TeacherProfile.loadFromPreferences(context)
        val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager ?: return
        val webView = WebView(context)
        val htmlContent = generateObservationAndPeerHtml(jurnalList, peerQuestions, profile)

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?) = false
            override fun onPageFinished(view: WebView?, url: String?) {
                val printAdapter = webView.createPrintDocumentAdapter("Jurnal_Observasi_P3")
                val printAttributes = PrintAttributes.Builder()
                    .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                    .setResolution(PrintAttributes.Resolution("id_obs", "Obs Print", 300, 300))
                    .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                    .build()
                printManager.print("Jurnal_Observasi_P3", printAdapter, printAttributes)
            }
        }
        webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
    }

    fun exportObservationToWord(context: Context, jurnalList: List<JurnalObservasiItem>, peerQuestions: List<PeerAssessmentQuestion>): Uri? {
        val profile = TeacherProfile.loadFromPreferences(context)
        try {
            val html = generateObservationAndPeerHtml(jurnalList, peerQuestions, profile)
            val fileName = "Jurnal_Observasi_P3.doc"
            val file = File(context.cacheDir, fileName)
            FileOutputStream(file).use { out -> out.write(html.toByteArray(Charsets.UTF_8)) }
            return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}

