package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.ModulAjarEntity
import com.example.ui.components.AppHeader
import com.example.ui.components.BadgeChip
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*
import com.example.ui.viewmodel.ModulViewModel
import com.example.ui.viewmodel.Screen
import com.example.util.DocumentExporter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorCanvasScreen(
    modulId: Long,
    viewModel: ModulViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activeModul by viewModel.activeModul.collectAsStateWithLifecycle()
    val isEnhancing by viewModel.isEnhancingSection.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableStateOf(0) } // 0: Dokumen Siap Cetak, 1: Editor Komponen, 2: AI Assistant

    // Editable States mapped to activeModul
    var currentModulState by remember(activeModul) { mutableStateOf(activeModul) }
    var showAiDialogForSection by remember { mutableStateOf<String?>(null) }
    var customAiPrompt by remember { mutableStateOf("") }
    var showExportDialog by remember { mutableStateOf(false) }
    var showExitConfirmation by remember { mutableStateOf(false) }

    // Save helper
    fun saveChanges() {
        currentModulState?.let {
            viewModel.updateActiveModul(it.copy(updatedAt = System.currentTimeMillis()))
            Toast.makeText(context, "Modul Ajar berhasil disimpan!", Toast.LENGTH_SHORT).show()
        }
    }

    val hasUnsavedChanges = (currentModulState != activeModul)

    BackHandler(enabled = true) {
        if (hasUnsavedChanges) {
            showExitConfirmation = true
        } else {
            viewModel.navigateTo(Screen.Home)
        }
    }

    if (showExitConfirmation) {
        AlertDialog(
            onDismissRequest = { showExitConfirmation = false },
            title = { Text("Unsaved Changes") },
            text = { Text("You have unsaved changes. Do you want to save them before leaving?") },
            confirmButton = {
                TextButton(onClick = {
                    saveChanges()
                    showExitConfirmation = false
                    viewModel.navigateTo(Screen.Home)
                }) {
                    Text("Save & Exit")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showExitConfirmation = false
                    viewModel.navigateTo(Screen.Home)
                }) {
                    Text("Discard")
                }
            }
        )
    }

    if (currentModulState == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val modul = currentModulState!!

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = modul.topic,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            ),
                            maxLines = 1,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${modul.subject} • ${modul.fase} (${modul.grade})",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            BadgeChip(
                                backgroundColor = EduGreen100,
                                textColor = EduGreen600,
                                icon = Icons.Default.CheckCircle
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            saveChanges()
                            viewModel.navigateTo(Screen.Home)
                        },
                        modifier = Modifier.testTag("btn_editor_back")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { saveChanges() },
                        modifier = Modifier.testTag("btn_save_modul")
                    ) {
                        Icon(Icons.Default.Save, contentDescription = "Simpan", tint = MaterialTheme.colorScheme.primary)
                    }

                    IconButton(
                        onClick = { showExportDialog = true },
                        modifier = Modifier.testTag("btn_export_dialog")
                    ) {
                        Icon(Icons.Default.Download, contentDescription = "Ekspor / Unduh", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface, tonalElevation = 4.dp) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.MenuBook, contentDescription = null) },
                    label = { Text("Siap Cetak") },
                    modifier = Modifier.testTag("tab_preview")
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.EditNote, contentDescription = null) },
                    label = { Text("Editor Canvas") },
                    modifier = Modifier.testTag("tab_editor")
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.AutoAwesome, contentDescription = null) },
                    label = { Text("AI Assistant") },
                    modifier = Modifier.testTag("tab_ai")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
        ) {
            when (selectedTab) {
                0 -> PreviewPrintCanvasTab(
                    modul = modul,
                    onPrintPdf = { DocumentExporter.printOrSavePdf(context, modul) },
                    onExportDoc = {
                        val uri = DocumentExporter.exportToWordDoc(context, modul)
                        if (uri != null) {
                            Toast.makeText(context, "Dokumen Word berhasil dibuat!", Toast.LENGTH_SHORT).show()
                            DocumentExporter.shareModulText(context, modul)
                        }
                    },
                    onShare = { DocumentExporter.shareModulText(context, modul) },
                    onCopy = { DocumentExporter.copyToClipboard(context, modul) }
                )

                1 -> InteractiveEditorTab(
                    modul = modul,
                    onModulChange = { updated -> currentModulState = updated },
                    onRequestAiHelp = { sectionName ->
                        showAiDialogForSection = sectionName
                        customAiPrompt = ""
                    }
                )

                2 -> AiAssistantStudioTab(
                    modul = modul,
                    isEnhancing = isEnhancing,
                    onApplyAiResult = { updatedModul ->
                        currentModulState = updatedModul
                        saveChanges()
                    },
                    viewModel = viewModel
                )
            }
        }
    }

    // Export Options Modal Dialog
    if (showExportDialog) {
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.FileDownload, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Unduh & Ekspor Modul Ajar", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Pilih format dokumen resmi siap cetak yang ingin Anda unduh atau bagikan:",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    // Option 1: PDF Document
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showExportDialog = false
                                DocumentExporter.printOrSavePdf(context, modul)
                            }
                            .testTag("btn_export_pdf_option"),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Cetak / Simpan PDF", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("Format A4 resmi dengan kop, tabel, dan tanda tangan", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f))
                            }
                        }
                    }

                    // Option 2: Word Doc
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showExportDialog = false
                                val uri = DocumentExporter.exportToWordDoc(context, modul)
                                DocumentExporter.shareModulText(context, modul)
                            }
                            .testTag("btn_export_word_option"),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Description, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Dokumen Word (.doc / HTML)", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("Kompatibel dengan Microsoft Word & Google Docs", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f))
                            }
                        }
                    }

                    // Option 3: Copy Text
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showExportDialog = false
                                DocumentExporter.copyToClipboard(context, modul)
                            },
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Salin Teks Lengkap", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("Salin seluruh isi naskah ke papan klip", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f))
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showExportDialog = false }) {
                    Text("Tutup")
                }
            }
        )
    }

    // Section AI Refinement Dialog
    if (showAiDialogForSection != null) {
        val sectionName = showAiDialogForSection!!
        val currentSectionContent = when (sectionName) {
            "Kegiatan Inti" -> modul.kegiatanInti
            "Asesmen & Rubrik" -> "${modul.asesmenFormatif}\n\n${modul.rubrikPenilaian}"
            "Pembelajaran Berdiferensiasi" -> "${modul.diferensiasiKonten}\n\n${modul.diferensiasiProses}"
            "LKPD" -> modul.lkpdDanMateri
            else -> modul.tujuanPembelajaran
        }

        AlertDialog(
            onDismissRequest = { showAiDialogForSection = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Penyempurnaan AI: $sectionName", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Instruksikan AI untuk mengembangkan atau menyesuaikan bagian ini sesuai kebutuhan kelas Anda:",
                        style = MaterialTheme.typography.bodySmall
                    )

                    OutlinedTextField(
                        value = customAiPrompt,
                        onValueChange = { customAiPrompt = it },
                        label = { Text("Instruksi Tambahan untuk AI") },
                        placeholder = { Text("Contoh: Buat kegiatan lebih interaktif dengan studi kasus nyata dan media kartu.") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        shape = RoundedCornerShape(10.dp)
                    )

                    if (isEnhancing) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Sedang menyempurnakan dengan AI...", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.enhanceSectionWithAI(
                            sectionName = sectionName,
                            currentContent = currentSectionContent,
                            instruction = customAiPrompt.ifBlank { "Sempurnakan dan perkaya sesuai standar Kurikulum Merdeka" },
                            onComplete = { newContent ->
                                val updated = when (sectionName) {
                                    "Kegiatan Inti" -> modul.copy(kegiatanInti = newContent)
                                    "Asesmen & Rubrik" -> modul.copy(rubrikPenilaian = newContent)
                                    "Pembelajaran Berdiferensiasi" -> modul.copy(diferensiasiProses = newContent)
                                    "LKPD" -> modul.copy(lkpdDanMateri = newContent)
                                    else -> modul.copy(tujuanPembelajaran = newContent)
                                }
                                currentModulState = updated
                                saveChanges()
                                showAiDialogForSection = null
                            }
                        )
                    },
                    enabled = !isEnhancing,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text("Terapkan Perubahan AI")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showAiDialogForSection = null }) {
                    Text("Batal")
                }
            }
        )
    }
}

// -------------------------------------------------------------
// TAB 1: Preview Format Siap Cetak (Paper Document Canvas)
// -------------------------------------------------------------
@Composable
fun PreviewPrintCanvasTab(
    modul: ModulAjarEntity,
    onPrintPdf: () -> Unit,
    onExportDoc: () -> Unit,
    onShare: () -> Unit,
    onCopy: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Quick Action Bar
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Format Siap Cetak (A4 Resmi)",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Button(
                            onClick = onPrintPdf,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("btn_canvas_print")
                        ) {
                            Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Cetak / PDF", fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = onShare,
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        // Paper Container
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, EduSlate200, RoundedCornerShape(8.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Kop Dokumen Resmi
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "MODUL AJAR KURIKULUM MERDEKA",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = EduNavy700,
                                fontSize = 18.sp
                            )
                        )
                        Text(
                            text = "${modul.subject.uppercase(Locale.ROOT)} - ${modul.topic}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = EduTeal700,
                                fontSize = 14.sp
                            )
                        )
                        Text(
                            text = "${modul.schoolName} | TP: ${modul.academicYear}",
                            style = MaterialTheme.typography.bodySmall,
                            color = EduSlate500
                        )
                        Divider(
                            color = EduNavy700,
                            thickness = 2.dp,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    // A. INFORMASI UMUM
                    PrintSectionBlock(
                        title = "A. INFORMASI UMUM",
                        content = """
                        • Nama Penyusun       : ${modul.teacherName}
                        • Satuan Pendidikan   : ${modul.schoolName}
                        • Fase / Kelas        : ${modul.fase} / ${modul.grade}
                        • Mata Pelajaran      : ${modul.subject}
                        • Alokasi Waktu       : ${modul.timeAllocation}
                        • Model Pembelajaran  : ${modul.modelPembelajaran}
                        • Target Siswa        : ${modul.targetPesertaDidik}
                        • Sarana & Prasarana  : ${modul.saranaPrasarana}
                        """.trimIndent()
                    )

                    // Dimensi P3
                    PrintSubsectionBlock(
                        subtitle = "Dimensi Profil Pelajar Pancasila",
                        content = modul.profilPelajarPancasila
                    )

                    // B. KOMPONEN INTI
                    PrintSectionBlock(
                        title = "B. KOMPONEN INTI",
                        content = ""
                    )

                    PrintSubsectionBlock(
                        subtitle = "1. Capaian & Tujuan Pembelajaran",
                        content = "Capaian Pembelajaran (CP):\n${modul.capaianPembelajaran}\n\nTujuan Pembelajaran (TP):\n${modul.tujuanPembelajaran}"
                    )

                    PrintSubsectionBlock(
                        subtitle = "2. Pemahaman Bermakna & Pertanyaan Pemantik",
                        content = "Pemahaman Bermakna:\n${modul.pemahamanBermakna}\n\nPertanyaan Pemantik:\n${modul.pertanyaanPemantik}"
                    )

                    PrintSubsectionBlock(
                        subtitle = "3. Kegiatan Pembelajaran Terstruktur",
                        content = "a. Kegiatan Pendahuluan:\n${modul.kegiatanPendahuluan}\n\nb. Kegiatan Inti (${modul.modelPembelajaran}):\n${modul.kegiatanInti}\n\nc. Kegiatan Penutup:\n${modul.kegiatanPenutup}"
                    )

                    // C. PEMBELAJARAN BERDIFERENSIASI
                    PrintSectionBlock(
                        title = "C. PEMBELAJARAN BERDIFERENSIASI",
                        content = "1. Diferensiasi Konten:\n${modul.diferensiasiKonten}\n\n2. Diferensiasi Proses (Scaffolding):\n${modul.diferensiasiProses}\n\n3. Diferensiasi Produk:\n${modul.diferensiasiProduk}"
                    )

                    // D. ASESMEN & RUBRIK PENILAIAN
                    PrintSectionBlock(
                        title = "D. ASESMEN & RUBRIK PENILAIAN",
                        content = "1. Asesmen Diagnostik:\n${modul.asesmenDiagnostik}\n\n2. Asesmen Formatif & Observasi Sikap P3:\n${modul.asesmenFormatif}\n\n3. Asesmen Sumatif:\n${modul.asesmenSumatif}\n\n4. Rubrik Kriteria Penilaian:\n${modul.rubrikPenilaian}"
                    )

                    // E. REMEDIAL & PENGAYAAN
                    PrintSectionBlock(
                        title = "E. REMEDIAL, PENGAYAAN & REFLEKSI",
                        content = modul.remedialDanPengayaan
                    )

                    // F. LAMPIRAN (LKPD)
                    PrintSectionBlock(
                        title = "F. LEMBAR KERJA PESERTA DIDIK (LKPD) & LAMPIRAN",
                        content = modul.lkpdDanMateri
                    )

                    // Signature Block
                    Divider(color = EduSlate200, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Mengetahui,", style = MaterialTheme.typography.bodySmall)
                            Text("Kepala Sekolah", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(40.dp))
                            Text("( ______________________ )", style = MaterialTheme.typography.bodySmall)
                            Text("NIP. .............................", style = MaterialTheme.typography.labelSmall)
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Dibuat di Sekolah,", style = MaterialTheme.typography.bodySmall)
                            Text("Guru Mata Pelajaran", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(40.dp))
                            Text(modul.teacherName, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                            Text("NIP. .............................", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

// -------------------------------------------------------------
// TAB 2: Interactive Section-by-Section Editor (Canvas Kerja Guru)
// -------------------------------------------------------------
@Composable
fun InteractiveEditorTab(
    modul: ModulAjarEntity,
    onModulChange: (ModulAjarEntity) -> Unit,
    onRequestAiHelp: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.TouchApp, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Area Kerja Guru: Anda memegang kendali penuh untuk menyunting setiap kalimat, menambah butir soal, atau menyempurnakan langkah pembelajaran.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        // Section 1: Informasi Dasar
        item {
            EditableSectionCard(
                title = "1. Informasi Umum & Identitas",
                icon = Icons.Default.School,
                onAiHelpClick = null
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = modul.topic,
                        onValueChange = { onModulChange(modul.copy(topic = it)) },
                        label = { Text("Topik / Materi Pokok") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = modul.timeAllocation,
                            onValueChange = { onModulChange(modul.copy(timeAllocation = it)) },
                            label = { Text("Alokasi Waktu") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        OutlinedTextField(
                            value = modul.modelPembelajaran,
                            onValueChange = { onModulChange(modul.copy(modelPembelajaran = it)) },
                            label = { Text("Model") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                    OutlinedTextField(
                        value = modul.saranaPrasarana,
                        onValueChange = { onModulChange(modul.copy(saranaPrasarana = it)) },
                        label = { Text("Sarana & Prasarana") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }
        }

        // Section 2: Tujuan Pembelajaran & Profil Pelajar Pancasila
        item {
            EditableSectionCard(
                title = "2. Capaian & Tujuan Pembelajaran (TP)",
                icon = Icons.Default.Flag,
                onAiHelpClick = { onRequestAiHelp("Tujuan Pembelajaran") }
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = modul.capaianPembelajaran,
                        onValueChange = { onModulChange(modul.copy(capaianPembelajaran = it)) },
                        label = { Text("Capaian Pembelajaran (CP Resmi)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        maxLines = 4
                    )
                    OutlinedTextField(
                        value = modul.tujuanPembelajaran,
                        onValueChange = { onModulChange(modul.copy(tujuanPembelajaran = it)) },
                        label = { Text("Tujuan Pembelajaran (TP & ATP)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        maxLines = 5
                    )
                    OutlinedTextField(
                        value = modul.profilPelajarPancasila,
                        onValueChange = { onModulChange(modul.copy(profilPelajarPancasila = it)) },
                        label = { Text("Profil Pelajar Pancasila (P3)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        maxLines = 4
                    )
                }
            }
        }

        // Section 3: Pemantik & Pemahaman Bermakna
        item {
            EditableSectionCard(
                title = "3. Pemahaman Bermakna & Pemantik",
                icon = Icons.Default.Lightbulb,
                onAiHelpClick = { onRequestAiHelp("Pemantik") }
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = modul.pemahamanBermakna,
                        onValueChange = { onModulChange(modul.copy(pemahamanBermakna = it)) },
                        label = { Text("Pemahaman Bermakna") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        maxLines = 4
                    )
                    OutlinedTextField(
                        value = modul.pertanyaanPemantik,
                        onValueChange = { onModulChange(modul.copy(pertanyaanPemantik = it)) },
                        label = { Text("Pertanyaan Pemantik") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        maxLines = 4
                    )
                }
            }
        }

        // Section 4: Langkah Pembelajaran (Pendahuluan, Inti, Penutup)
        item {
            EditableSectionCard(
                title = "4. Kegiatan Pembelajaran Terstruktur",
                icon = Icons.Default.Timeline,
                onAiHelpClick = { onRequestAiHelp("Kegiatan Inti") }
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = modul.kegiatanPendahuluan,
                        onValueChange = { onModulChange(modul.copy(kegiatanPendahuluan = it)) },
                        label = { Text("a. Kegiatan Pendahuluan (Orientasi, Apersepsi, Motivasi)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        maxLines = 6
                    )
                    OutlinedTextField(
                        value = modul.kegiatanInti,
                        onValueChange = { onModulChange(modul.copy(kegiatanInti = it)) },
                        label = { Text("b. Kegiatan Inti (Sintaks ${modul.modelPembelajaran})") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        maxLines = 12
                    )
                    OutlinedTextField(
                        value = modul.kegiatanPenutup,
                        onValueChange = { onModulChange(modul.copy(kegiatanPenutup = it)) },
                        label = { Text("c. Kegiatan Penutup (Refleksi 3-2-1 & Doa)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        maxLines = 6
                    )
                }
            }
        }

        // Section 5: Pembelajaran Berdiferensiasi
        item {
            EditableSectionCard(
                title = "5. Pembelajaran Berdiferensiasi",
                icon = Icons.Default.Tune,
                onAiHelpClick = { onRequestAiHelp("Pembelajaran Berdiferensiasi") }
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = modul.diferensiasiKonten,
                        onValueChange = { onModulChange(modul.copy(diferensiasiKonten = it)) },
                        label = { Text("Diferensiasi Konten (Visual, Audio, Kinestetik)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        maxLines = 5
                    )
                    OutlinedTextField(
                        value = modul.diferensiasiProses,
                        onValueChange = { onModulChange(modul.copy(diferensiasiProses = it)) },
                        label = { Text("Diferensiasi Proses (Bimbingan Tingkat / Scaffolding)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        maxLines = 5
                    )
                    OutlinedTextField(
                        value = modul.diferensiasiProduk,
                        onValueChange = { onModulChange(modul.copy(diferensiasiProduk = it)) },
                        label = { Text("Diferensiasi Produk (Pilihan Unjuk Kerja)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        maxLines = 4
                    )
                }
            }
        }

        // Section 6: Asesmen & Rubrik
        item {
            EditableSectionCard(
                title = "6. Asesmen & Rubrik Penilaian",
                icon = Icons.Default.FactCheck,
                onAiHelpClick = { onRequestAiHelp("Asesmen & Rubrik") }
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = modul.asesmenDiagnostik,
                        onValueChange = { onModulChange(modul.copy(asesmenDiagnostik = it)) },
                        label = { Text("Asesmen Diagnostik (Awal)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        maxLines = 4
                    )
                    OutlinedTextField(
                        value = modul.asesmenFormatif,
                        onValueChange = { onModulChange(modul.copy(asesmenFormatif = it)) },
                        label = { Text("Asesmen Formatif (Proses & Observasi Sikap)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        maxLines = 5
                    )
                    OutlinedTextField(
                        value = modul.asesmenSumatif,
                        onValueChange = { onModulChange(modul.copy(asesmenSumatif = it)) },
                        label = { Text("Asesmen Sumatif (Akhir)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        maxLines = 5
                    )
                    OutlinedTextField(
                        value = modul.rubrikPenilaian,
                        onValueChange = { onModulChange(modul.copy(rubrikPenilaian = it)) },
                        label = { Text("Rubrik Penilaian Kriteria (Skala 1 s.d 4)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        maxLines = 8
                    )
                }
            }
        }

        // Section 7: LKPD & Lampiran
        item {
            EditableSectionCard(
                title = "7. Lembar Kerja Peserta Didik (LKPD)",
                icon = Icons.Default.Assignment,
                onAiHelpClick = { onRequestAiHelp("LKPD") }
            ) {
                OutlinedTextField(
                    value = modul.lkpdDanMateri,
                    onValueChange = { onModulChange(modul.copy(lkpdDanMateri = it)) },
                    label = { Text("Naskah Lembar Kerja Siswa (LKPD)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    maxLines = 10
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

// -------------------------------------------------------------
// TAB 3: AI Assistant Studio (One-Tap Pedagogical Enhancements)
// -------------------------------------------------------------
@Composable
fun AiAssistantStudioTab(
    modul: ModulAjarEntity,
    isEnhancing: Boolean,
    onApplyAiResult: (ModulAjarEntity) -> Unit,
    viewModel: ModulViewModel
) {
    val context = LocalContext.current
    var customInstructionText by remember { mutableStateOf("") }
    var selectedEnhancementTarget by remember { mutableStateOf("Kegiatan Inti") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
            ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("AI Pedagogic Assistant", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Text(
                            "Pilih perintah cepat di bawah ini untuk menyempurnakan modul secara otomatis.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }

        item {
            SectionHeader(
                title = "Perintah Cepat AI (1-Tap Enhancement):",
                icon = Icons.Default.Bolt
            )
        }

        // Quick AI Action 1: Diferensiasi
        item {
            AiActionTile(
                title = "Tingkatkan Diferensiasi Gaya Belajar",
                description = "Tambahkan strategi instruksi khusus untuk siswa visual, audio, dan kinestetik pada kegiatan inti.",
                icon = Icons.Default.Tune,
                isLoading = isEnhancing,
                onClick = {
                    viewModel.enhanceSectionWithAI(
                        sectionName = "Pembelajaran Berdiferensiasi",
                        currentContent = modul.diferensiasiProses,
                        instruction = "Perdalam diferensiasi proses dan konten dengan panduan terperinci untuk 3 gaya belajar (visual, auditori, kinestetik) serta tingkat kesiapan siswa dasar hingga mahir.",
                        onComplete = { result ->
                            onApplyAiResult(modul.copy(diferensiasiProses = result))
                            Toast.makeText(context, "Diferensiasi berhasil diperbarui!", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            )
        }

        // Quick AI Action 2: Soal HOTS & Asesmen
        item {
            AiActionTile(
                title = "Buat 5 Butir Soal HOTS + Kunci Jawaban",
                description = "Lengkapi instrumen asesmen sumatif dengan 5 butir soal pemecahan masalah kontekstual.",
                icon = Icons.Default.Quiz,
                isLoading = isEnhancing,
                onClick = {
                    viewModel.enhanceSectionWithAI(
                        sectionName = "Asesmen Sumatif",
                        currentContent = modul.asesmenSumatif,
                        instruction = "Tambahkan 5 butir soal pilihan ganda HOTS lengkap dengan opsi A-D, kunci jawaban, dan pembahasan kontekstual terkait topik ${modul.topic}.",
                        onComplete = { result ->
                            onApplyAiResult(modul.copy(asesmenSumatif = result))
                            Toast.makeText(context, "Soal HOTS berhasil ditambahkan ke Asesmen!", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            )
        }

        // Quick AI Action 3: Rubrik Terperinci
        item {
            AiActionTile(
                title = "Perluas Rubrik Penilaian Skala 1-4",
                description = "Lengkapi indikator rubrik penilaian kinerja unjuk kerja dan sikap P3 secara detail.",
                icon = Icons.Default.TableChart,
                isLoading = isEnhancing,
                onClick = {
                    viewModel.enhanceSectionWithAI(
                        sectionName = "Rubrik Penilaian",
                        currentContent = modul.rubrikPenilaian,
                        instruction = "Kembangkan rubrik penilaian 4 skala (Perlu Bimbingan, Cukup, Baik, Sangat Baik) dengan kriteria pemahaman materi, keterampilan kolaborasi, dan presentasi produk.",
                        onComplete = { result ->
                            onApplyAiResult(modul.copy(rubrikPenilaian = result))
                            Toast.makeText(context, "Rubrik Penilaian berhasil diperluas!", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            )
        }

        // Quick AI Action 4: LKPD Interaktif
        item {
            AiActionTile(
                title = "Kembangkan Lembar Kerja Siswa (LKPD)",
                description = "Rancang aktivitas eksplorasi penemuan yang menarik untuk dikerjakan dalam kelompok.",
                icon = Icons.Default.AssignmentTurnedIn,
                isLoading = isEnhancing,
                onClick = {
                    viewModel.enhanceSectionWithAI(
                        sectionName = "LKPD",
                        currentContent = modul.lkpdDanMateri,
                        instruction = "Buat naskah Lembar Kerja Peserta Didik (LKPD) yang interaktif, memiliki petunjuk belajar yang ramah anak, langkah pengamatan praktis, dan kolom kesimpulan.",
                        onComplete = { result ->
                            onApplyAiResult(modul.copy(lkpdDanMateri = result))
                            Toast.makeText(context, "LKPD berhasil diperbarui!", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            )
        }

        // Custom Prompt Box
        item {
            SectionHeader(
                title = "Perintah Bebas untuk AI:",
                icon = Icons.Default.ChatBubbleOutline
            )
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Target Bagian Modul:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("Kegiatan Inti", "Asesmen", "LKPD", "Diferensiasi").forEach { target ->
                            FilterChip(
                                selected = selectedEnhancementTarget == target,
                                onClick = { selectedEnhancementTarget = target },
                                label = { Text(target) },
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                    }

                    OutlinedTextField(
                        value = customInstructionText,
                        onValueChange = { customInstructionText = it },
                        placeholder = { Text("Tulis instruksi khusus, misal: 'Tambahkan permainan edukasi kartu kosakata'...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(90.dp),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Button(
                        onClick = {
                            val currentText = when (selectedEnhancementTarget) {
                                "Kegiatan Inti" -> modul.kegiatanInti
                                "Asesmen" -> modul.asesmenFormatif
                                "LKPD" -> modul.lkpdDanMateri
                                else -> modul.diferensiasiProses
                            }
                            viewModel.enhanceSectionWithAI(
                                sectionName = selectedEnhancementTarget,
                                currentContent = currentText,
                                instruction = customInstructionText.ifBlank { "Tingkatkan kualitas modul" },
                                onComplete = { result ->
                                    val updated = when (selectedEnhancementTarget) {
                                        "Kegiatan Inti" -> modul.copy(kegiatanInti = result)
                                        "Asesmen" -> modul.copy(asesmenFormatif = result)
                                        "LKPD" -> modul.copy(lkpdDanMateri = result)
                                        else -> modul.copy(diferensiasiProses = result)
                                    }
                                    onApplyAiResult(updated)
                                    customInstructionText = ""
                                    Toast.makeText(context, "$selectedEnhancementTarget berhasil diperbarui!", Toast.LENGTH_SHORT).show()
                                }
                            )
                        },
                        enabled = !isEnhancing,
                        modifier = Modifier.align(Alignment.End),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Kirim Perintah AI")
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

// -------------------------------------------------------------
// Helper UI Components
// -------------------------------------------------------------
@Composable
fun PrintSectionBlock(title: String, content: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(EduSlate100, RoundedCornerShape(4.dp))
                .border(1.dp, EduSlate200, RoundedCornerShape(4.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = EduNavy700
            )
        }
        if (content.isNotBlank()) {
            Text(
                text = content,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, lineHeight = 20.sp),
                color = EduSlate900,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }
    }
}

@Composable
fun PrintSubsectionBlock(subtitle: String, content: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = subtitle,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = EduTeal700
        )
        Text(
            text = content,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, lineHeight = 20.sp),
            color = EduSlate900,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
    }
}

@Composable
fun EditableSectionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onAiHelpClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (onAiHelpClick != null) {
                    FilledTonalButton(
                        onClick = onAiHelpClick,
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("AI Assist", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            content()
        }
    }
}

@Composable
fun AiActionTile(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isLoading: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(enabled = !isLoading, onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                Text(text = description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
