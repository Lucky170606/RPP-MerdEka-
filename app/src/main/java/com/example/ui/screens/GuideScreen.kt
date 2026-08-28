package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AppHeader
import com.example.ui.components.BadgeChip
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*
import com.example.ui.viewmodel.ModulViewModel
import com.example.ui.viewmodel.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuideScreen(
    viewModel: ModulViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Scaffold(
        topBar = {
            AppHeader(
                title = "Panduan Kurikulum Merdeka",
                subtitle = "Prinsip Penyusunan Modul Ajar & Diferensiasi",
                showBackButton = true,
                onBackClick = { viewModel.navigateTo(Screen.Home) }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Guide 1: Pembelajaran Berdiferensiasi
            item {
                GuideCard(
                    title = "1. Prinsip Pembelajaran Berdiferensiasi",
                    icon = Icons.Default.Tune,
                    badge = "Pedagogik Inti",
                    content = """
                    Pembelajaran Berdiferensiasi adalah pendekatan pembelajaran yang mengakomodasi keragaman kebutuhan belajar, minat, dan profil kesiapan peserta didik.
                    
                    Tiga Pilar Diferensiasi:
                    • Diferensiasi Konten: Menyesuaikan materi/sumber belajar (teks, diagram, video animasi, benda manipulatif konkret) sesuai modalitas visual, auditori, dan kinestetik.
                    • Diferensiasi Proses: Menyesuaikan cara belajar dan tingkat pendampingan (scaffolding). Siswa yang perlu bimbingan mendapat instruksi terstruktur intensif, sementara siswa mahir diberikan tantangan eksplorasi mandiri.
                    • Diferensiasi Produk: Memberikan variasi pilihan bentuk unjuk kerja hasil belajar siswa (infografis, presentasi lisan, laporan ringkas, atau video).
                    """.trimIndent()
                )
            }

            // Guide 2: Asesmen Kurikulum Merdeka
            item {
                GuideCard(
                    title = "2. Prinsip Asesmen Autentik",
                    icon = Icons.Default.FactCheck,
                    badge = "Asesmen & Rubrik",
                    content = """
                    Dalam Kurikulum Merdeka, asesmen berfokus pada perbaikan proses belajar (Assessment for/as Learning), bukan sekadar penilaian akhir (Assessment of Learning).
                    
                    Jenis Asesmen Utama:
                    1. Asesmen Diagnostik (Awal): Memetakan kesiapan kognitif dan kondisi non-kognitif siswa sebelum materi diajarkan.
                    2. Asesmen Formatif (Proses): Observasi keaktifan diskusi, pengamatan sikap Profil Pelajar Pancasila, kuis exit ticket, dan penilaian proses pengerjaan LKPD.
                    3. Asesmen Sumatif (Akhir): Pengukuran ketercapaian Tujuan Pembelajaran pada akhir unit / semester menggunakan instrumen soal kontekstual HOTS dan proyek.
                    """.trimIndent()
                )
            }

            // Guide 3: Sistematika Modul Ajar
            item {
                GuideCard(
                    title = "3. Sistematika Komponen Modul Ajar",
                    icon = Icons.Default.Description,
                    badge = "Standar Dokumen",
                    content = """
                    Komponen Modul Ajar (RPP Plus) Kurikulum Merdeka terdiri atas 3 bagian utama:
                    
                    A. Informasi Umum:
                       - Identitas modul, kompetensi awal, profil pelajar pancasila, sarana prasarana, target peserta didik, dan model pembelajaran.
                    
                    B. Komponen Inti:
                       - Capaian Pembelajaran (CP) & Tujuan Pembelajaran (TP).
                       - Pemahaman bermakna & pertanyaan pemantik.
                       - Kegiatan pembelajaran (Pendahuluan, Inti sesuai sintaks model, Penutup reflektif).
                       - Asesmen (Diagnostik, Formatif, Sumatif) & Rubrik Kriteria.
                       - Pengayaan & Remedial.
                    
                    C. Lampiran:
                       - Lembar Kerja Peserta Didik (LKPD).
                       - Bahan bacaan guru & siswa.
                       - Glosarium dan daftar pustaka.
                    """.trimIndent()
                )
            }

            // Guide 4: Kendali Penuh Guru
            item {
                GuideCard(
                    title = "4. Peran Guru & Validasi Mandiri",
                    icon = Icons.Default.PersonSearch,
                    badge = "Etika Guru",
                    content = """
                    AI berperan sebagai asisten cerdas untuk menghemat waktu administrasi guru. Guru tetap memegang kendali penuh untuk:
                    • Memvalidasi kesesuaian materi dengan kondisi lokal satuan pendidikan.
                    • Menyesuaikan karakteristik peserta didik nyata di kelas.
                    • Melakukan penyuntingan langsung melalui fitur 'Editor Canvas'.
                    """.trimIndent()
                )
            }


            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun GuideCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    badge: String,
    content: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f, fill = false),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                BadgeChip(
                    text = badge,
                    backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                    textColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
