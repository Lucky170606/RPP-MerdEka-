package com.example.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.ai.GeminiService
import com.example.data.ai.OfflineAssessmentEngine
import com.example.data.model.AssessmentDocument
import com.example.data.model.Fase
import com.example.data.model.KurikulumMerdekaReferenceData
import com.example.data.model.TeacherProfile
import com.example.ui.components.AppHeader
import com.example.ui.components.BadgeChip
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*
import com.example.ui.viewmodel.ModulViewModel
import com.example.ui.viewmodel.Screen
import com.example.util.DocumentExporter
import com.example.util.MathSymbolFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssessmentHotsScreen(
    viewModel: ModulViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedSubject by remember { mutableStateOf("Matematika") }
    var selectedFase by remember { mutableStateOf(Fase.FASE_B) }
    var materiTopik by remember { mutableStateOf("Pecahan Senilai & Perbandingan") }
    var jenisAsesmen by remember { mutableStateOf("Asesmen Sumatif Akhir Bab") }
    var jumlahSoal by remember { mutableStateOf(5) }
    var pgRatioPercent by remember { mutableStateOf(60f) }
    var showHistoryDialog by remember { mutableStateOf(false) }

    val savedAssessments by viewModel.allSavedAssessments.collectAsStateWithLifecycle(initialValue = emptyList())
    var assessmentDoc by remember { mutableStateOf<AssessmentDocument?>(null) }
    val isAiOnline = remember { GeminiService.isAvailable(context) }

    val subjects = remember(selectedFase) { KurikulumMerdekaReferenceData.getSubjectsForFase(selectedFase.code) }
    val jenisList = KurikulumMerdekaReferenceData.JENIS_ASESMEN_LIST
    val availableTopics = remember(selectedSubject, selectedFase) { KurikulumMerdekaReferenceData.getTopicsForSubjectAndFase(selectedSubject, selectedFase.code) }
    var expandedTopic by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            AppHeader(
                title = "Bank Soal & Kisi-Kisi HOTS",
                subtitle = "Standar Penulisan Butir Soal C1 - C6 Kurikulum Merdeka",
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
            // Indikator Status AI (Online vs Offline)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("ai_status_indicator_card"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isAiOnline) 
                            MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.45f)
                        else 
                            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f)
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isAiOnline) MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f)
                        else MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isAiOnline) MaterialTheme.colorScheme.tertiary
                                    else MaterialTheme.colorScheme.secondary
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isAiOnline) Icons.Default.AutoAwesome else Icons.Default.Storage,
                                contentDescription = null,
                                tint = if (isAiOnline) MaterialTheme.colorScheme.onTertiary else MaterialTheme.colorScheme.onSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = if (isAiOnline) "AI Online Aktif (Gemini AI)" else "Mode Offline Standar Kurikulum",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isAiOnline) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                BadgeChip(
                                    text = if (isAiOnline) "ONLINE" else "OFFLINE",
                                    backgroundColor = if (isAiOnline) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.secondary,
                                    textColor = if (isAiOnline) MaterialTheme.colorScheme.onTertiary else MaterialTheme.colorScheme.onSecondary
                                )
                            }
                            Text(
                                text = if (isAiOnline) 
                                    "Penyusunan butir soal HOTS otomatis dengan stimulus kontekstual & penalaran mendalam berbasis Google Gemini AI."
                                else 
                                    "Penyusunan instrumen soal berbasis bank kisi-kisi dan formula kurikulum lokal tanpa koneksi internet.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Form Parameter Asesmen
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SectionHeader(title = "Parameter Kisi-Kisi & Asesmen", icon = Icons.Default.FactCheck)

                        Text("Mata Pelajaran:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(subjects) { sub ->
                                FilterChip(
                                    selected = selectedSubject == sub,
                                    onClick = { selectedSubject = sub; materiTopik = "" },
                                    label = { Text(sub) }
                                )
                            }
                        }

                        Text("Fase & Jenjang:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(Fase.values()) { f ->
                                FilterChip(
                                    selected = selectedFase == f,
                                    onClick = { selectedFase = f; materiTopik = "" },
                                    label = { Text(f.code) }
                                )
                            }
                        }

                        // Topik Dropdown
                        val showDropdown = availableTopics.isNotEmpty()
                        ExposedDropdownMenuBox(
                            expanded = if (showDropdown) expandedTopic else false,
                            onExpandedChange = { if (showDropdown) expandedTopic = !expandedTopic }
                        ) {
                            OutlinedTextField(
                                value = materiTopik,
                                onValueChange = { materiTopik = it },
                                label = { Text("Topik / Materi Ujian") },
                                trailingIcon = if (showDropdown) { { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTopic) } } else null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                                    .testTag("input_assessment_topic")
                            )
                            if (showDropdown) {
                                ExposedDropdownMenu(
                                    expanded = expandedTopic,
                                    onDismissRequest = { expandedTopic = false }
                                ) {
                                    availableTopics.forEach { topic ->
                                        DropdownMenuItem(
                                            text = { Text(topic) },
                                            onClick = {
                                                materiTopik = topic
                                                expandedTopic = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        Text("Jenis Asesmen:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(jenisList) { j ->
                                FilterChip(
                                    selected = jenisAsesmen == j,
                                    onClick = { jenisAsesmen = j },
                                    label = { Text(j) }
                                )
                            }
                        }

                        // Jumlah Soal Slider
                        Text("Jumlah Butir Soal: ${jumlahSoal.toInt()} Soal", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        Slider(
                            value = jumlahSoal.toFloat(),
                            onValueChange = { jumlahSoal = it.toInt() },
                            valueRange = 5f..20f,
                            steps = 3,
                            modifier = Modifier.fillMaxWidth().testTag("slider_jumlah_soal")
                        )

                        // Rasio Pilihan Ganda (PG vs Uraian)
                        Text("Rasio Pilihan Ganda: ${pgRatioPercent.toInt()}% PG / ${(100 - pgRatioPercent).toInt()}% Uraian", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        Slider(
                            value = pgRatioPercent,
                            onValueChange = { pgRatioPercent = it },
                            valueRange = 40f..80f,
                            steps = 3,
                            modifier = Modifier.fillMaxWidth().testTag("slider_pg_ratio")
                        )
                    }
                }
            }

            // Tombol Generate Soal & Riwayat
            item {
                val isGenerating by viewModel.isGeneratingAssessment.collectAsStateWithLifecycle()
                val profile = remember { TeacherProfile.loadFromPreferences(context) }
                val targetGrade = selectedFase.grades.firstOrNull() ?: "Kelas X"
                val targetSemester = profile.defaultSemester.ifBlank { "Semester 1 (Ganjil)" }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            viewModel.generateKisiKisiHotsWithAI(
                                subject = selectedSubject,
                                fase = selectedFase.code,
                                grade = targetGrade,
                                topic = materiTopik.ifBlank { "Materi Pokok Esensial" },
                                jenisAsesmen = jenisAsesmen,
                                semester = targetSemester,
                                count = jumlahSoal.toInt(),
                                pgRatioPercent = pgRatioPercent.toInt()
                            ) { doc ->
                                assessmentDoc = doc
                                val sourceMsg = if (doc.isOnlineAiGenerated) "Gemini AI" else "Offline Engine"
                                Toast.makeText(context, "Kisi-Kisi & Soal HOTS Berhasil Disusun via $sourceMsg!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("btn_generate_assessment"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        enabled = !isGenerating
                    ) {
                        if (isGenerating) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(22.dp),
                                strokeWidth = 2.5.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                if (isAiOnline) "Gemini AI Menyusun Kisi-Kisi & Soal HOTS..." else "Engine Menyusun Kisi-Kisi & Soal HOTS...",
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Icon(
                                imageVector = if (isAiOnline) Icons.Default.AutoAwesome else Icons.Default.AutoFixHigh,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                if (isAiOnline) "Susun Kisi-Kisi & Soal HOTS (Gemini AI)" else "Susun Kisi-Kisi & Soal HOTS (Offline Engine)",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = { showHistoryDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Riwayat Asesmen Tersimpan (${savedAssessments.size})", fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // Hasil Asesmen Document
            assessmentDoc?.let { doc ->
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().testTag("result_assessment_card"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Header Banner Asal Sumber Generasi (AI Online vs Offline Engine)
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (doc.isOnlineAiGenerated)
                                        MaterialTheme.colorScheme.tertiaryContainer
                                    else
                                        MaterialTheme.colorScheme.secondaryContainer
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        imageVector = if (doc.isOnlineAiGenerated) Icons.Default.AutoAwesome else Icons.Default.Storage,
                                        contentDescription = null,
                                        tint = if (doc.isOnlineAiGenerated) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onSecondaryContainer,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = if (doc.isOnlineAiGenerated) "Hasil Generasi: Google Gemini AI (Online)" else "Hasil Generasi: Standar Kurikulum Merdeka (Offline Engine)",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = if (doc.isOnlineAiGenerated) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                        Text(
                                            text = "Mesin: ${doc.engineName}",
                                            fontSize = 11.sp,
                                            color = if (doc.isOnlineAiGenerated) MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                                        )
                                    }
                                    BadgeChip(
                                        text = if (doc.isOnlineAiGenerated) "AI ONLINE" else "OFFLINE",
                                        backgroundColor = if (doc.isOnlineAiGenerated) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.secondary,
                                        textColor = if (doc.isOnlineAiGenerated) MaterialTheme.colorScheme.onTertiary else MaterialTheme.colorScheme.onSecondary
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    doc.title,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 14.sp,
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                BadgeChip(
                                    text = "${doc.jumlahSoal} Soal",
                                    backgroundColor = MaterialTheme.colorScheme.primary,
                                    textColor = MaterialTheme.colorScheme.onPrimary
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { DocumentExporter.printOrSaveAssessmentPdf(context, doc) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary
                                    )
                                ) {
                                    Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Cetak PDF", fontSize = 12.sp)
                                }

                                OutlinedButton(
                                    onClick = {
                                        val uri = DocumentExporter.exportAssessmentToWordDoc(context, doc)
                                        if (uri != null) {
                                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                                setDataAndType(uri, "application/msword")
                                                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
                                            }
                                            context.startActivity(Intent.createChooser(intent, "Buka Kisi-Kisi Soal Word"))
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Unduh Word", fontSize = 12.sp)
                                }
                            }

                            OutlinedButton(
                                onClick = {
                                    viewModel.saveAssessmentToHistory(doc) { id ->
                                        Toast.makeText(context, "Berhasil Disimpan ke Database Lokal (ID: $id)", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Simpan ke Riwayat (Room DB)", fontSize = 12.sp)
                            }
                        }
                    }
                }

                // Tabel Kisi-kisi
                item {
                    SectionHeader(title = "Tabel Kisi-Kisi Soal (${doc.kisiKisiList.size} Butir)", icon = Icons.Default.TableChart)
                }

                items(doc.kisiKisiList) { k ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Soal No. ${k.nomorSoal}",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    BadgeChip(
                                        text = k.levelKognitif,
                                        backgroundColor = MaterialTheme.colorScheme.tertiaryContainer,
                                        textColor = MaterialTheme.colorScheme.onTertiaryContainer
                                    )
                                    BadgeChip(
                                        text = k.bentukSoal,
                                        backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                                        textColor = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                }
                            }
                            Text(
                                "Indikator: ${MathSymbolFormatter.formatMathText(k.indikatorSoal)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Kartu Soal HOTS & Pembahasan
                item {
                    SectionHeader(title = "Kartu Soal HOTS & Kunci Jawaban", icon = Icons.Default.Quiz)
                }

                items(doc.soalList) { s ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "No. ${s.nomor} [${s.bentukSoal}]",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    "Skor: ${s.skorMaksimal}",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }

                            // Stimulus
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = MathSymbolFormatter.formatMathText(s.stimulusText),
                                    style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
                                    modifier = Modifier.padding(8.dp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // Pertanyaan
                            Text(
                                MathSymbolFormatter.formatMathText(s.pertanyaan),
                                fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            // Opsi Jawaban (if any)
                            if (s.pilihanOpsi.isNotEmpty()) {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    s.pilihanOpsi.forEach { opt ->
                                        Text(
                                            MathSymbolFormatter.formatMathText(opt),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

                            // Kunci & Pembahasan
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                                        RoundedCornerShape(6.dp)
                                    )
                                    .padding(8.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    "Kunci Jawaban: ${MathSymbolFormatter.formatMathText(s.kunciJawaban)}",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    fontSize = 12.sp
                                )
                                Text(
                                    "Pembahasan: ${MathSymbolFormatter.formatMathText(s.pembahasanDanAlasan)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    if (showHistoryDialog) {
        AlertDialog(
            onDismissRequest = { showHistoryDialog = false },
            title = { Text("Riwayat Asesmen Tersimpan") },
            text = {
                if (savedAssessments.isEmpty()) {
                    Text("Belum ada riwayat asesmen yang disimpan di database lokal.")
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth().height(300.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(savedAssessments) { item ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(item.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("Topik: ${item.topikUjian} | ${item.jumlahSoal} Soal", fontSize = 11.sp)
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        TextButton(
                                            onClick = {
                                                try {
                                                    val parsedDoc = com.squareup.moshi.Moshi.Builder()
                                                        .add(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory())
                                                        .build()
                                                        .adapter(AssessmentDocument::class.java)
                                                        .fromJson(item.jsonContent)
                                                    if (parsedDoc != null) {
                                                        assessmentDoc = parsedDoc
                                                        showHistoryDialog = false
                                                        Toast.makeText(context, "Memuat asesmen dari riwayat!", Toast.LENGTH_SHORT).show()
                                                    }
                                                } catch (e: Exception) {
                                                    Toast.makeText(context, "Gagal memuat riwayat", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        ) {
                                            Text("Muat")
                                        }
                                        TextButton(
                                            onClick = {
                                                viewModel.deleteAssessmentHistory(item.id)
                                                Toast.makeText(context, "Riwayat dihapus", Toast.LENGTH_SHORT).show()
                                            }
                                        ) {
                                            Text("Hapus", color = MaterialTheme.colorScheme.error)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showHistoryDialog = false }) {
                    Text("Tutup")
                }
            }
        )
    }
}
