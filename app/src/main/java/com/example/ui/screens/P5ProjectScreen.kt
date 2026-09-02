package com.example.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.example.data.ai.OfflineP5Engine
import com.example.data.model.*
import com.example.ui.components.AppHeader
import com.example.ui.components.BadgeChip
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*
import com.example.ui.viewmodel.ModulViewModel
import com.example.ui.viewmodel.Screen
import com.example.util.DocumentExporter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun P5ProjectScreen(
    viewModel: ModulViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTema by remember { mutableStateOf(P5ReferenceData.TEMA_P5_LIST[0]) }
    var selectedFase by remember { mutableStateOf(Fase.FASE_B) }
    var topikProjek by remember { mutableStateOf(selectedTema.contohTopik[0]) }
    var alokasiWaktu by remember { mutableStateOf("36 JP (2-3 Pekan)") }
    var selectedDimensiList by remember { mutableStateOf(selectedTema.targetDimensi.toMutableList()) }

    var generatedP5Modul by remember { mutableStateOf<P5ProjectModul?>(null) }
    var isGenerating by remember { mutableStateOf(false) }
    var showHistoryDialog by remember { mutableStateOf(false) }
    val savedP5Assessments by viewModel.allSavedP5Assessments.collectAsStateWithLifecycle(initialValue = emptyList())

    LaunchedEffect(selectedTema, selectedFase) {
        val topics = P5ReferenceData.getTopikForFase(selectedTema.id, selectedFase.code)
        if (topics.isNotEmpty()) {
            topikProjek = topics[0]
        }
    }

    Scaffold(
        topBar = {
            AppHeader(
                title = "Generator Modul Projek P5",
                subtitle = "8 Tema Resmi Projek Penguatan Profil Pelajar Pancasila",
                showBackButton = true,
                onBackClick = { viewModel.navigateTo(Screen.Home) }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding(),
            contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 64.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // AI Status Banner
            item {
                val isAiOnline = remember { com.example.data.ai.GeminiService.isAvailable(context) }
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isAiOnline) 
                            MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.35f)
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
                                    "Penyusunan modul projek P5 otomatis dengan dimensi profil pelajar Pancasila & tahapan mendalam berbasis Google Gemini AI."
                                else 
                                    "Penyusunan modul projek P5 berbasis mesin offline dengan template tema dan tahapan Kurikulum Merdeka standar.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Pemilihan Tema P5
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        SectionHeader(title = "1. Pilih Tema Projek Resmi P5", icon = Icons.Default.Category)

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(P5ReferenceData.TEMA_P5_LIST) { tema ->
                                FilterChip(
                                    selected = selectedTema.id == tema.id,
                                    onClick = {
                                        selectedTema = tema
                                        topikProjek = tema.contohTopik.firstOrNull() ?: ""
                                        selectedDimensiList = tema.targetDimensi.toMutableList()
                                    },
                                    label = { Text(tema.title) },
                                    leadingIcon = {
                                        if (selectedTema.id == tema.id) Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                    }
                                )
                            }
                        }

                        Text(
                            text = selectedTema.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Parameter Jenjang & Topik Projek
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
                        SectionHeader(title = "2. Konfigurasi Projek & Topik", icon = Icons.Default.Tune)

                        // Fase selector
                        Text("Pilih Fase & Jenjang:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(Fase.values()) { f ->
                                FilterChip(
                                    selected = selectedFase == f,
                                    onClick = { selectedFase = f },
                                    label = { Text(f.code) }
                                )
                            }
                        }

                        OutlinedTextField(
                            value = topikProjek,
                            onValueChange = { topikProjek = it },
                            label = { Text("Topik / Judul Projek Aksi") },
                            placeholder = { Text("Contoh: Pengolahan Sampah Organik Eco-Enzyme") },
                            modifier = Modifier.fillMaxWidth().testTag("input_p5_topic")
                        )

                        val suggestedTopics = remember(selectedTema, selectedFase) {
                            P5ReferenceData.getTopikForFase(selectedTema.id, selectedFase.code)
                        }

                        Text(
                            text = "Contoh Topik Sesuai ${selectedFase.code} (${selectedFase.grades.joinToString()}):",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(suggestedTopics) { topic ->
                                SuggestionChip(
                                    onClick = { topikProjek = topic },
                                    label = { Text(topic, fontSize = 11.sp) }
                                )
                            }
                        }

                        OutlinedTextField(
                            value = alokasiWaktu,
                            onValueChange = { alokasiWaktu = it },
                            label = { Text("Alokasi Waktu (JP)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Tombol Generate & Riwayat
            item {
                val isGenerating by viewModel.isGeneratingP5.collectAsStateWithLifecycle()
                val isAiOnline = remember { com.example.data.ai.GeminiService.isAvailable(context) }
                val profile = remember { TeacherProfile.loadFromPreferences(context) }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            viewModel.generateP5ModulWithAI(
                                temaTitle = selectedTema.title,
                                topikProjek = topikProjek.ifBlank { "Aksi Peduli Lingkungan & Karakter" },
                                fase = selectedFase.code,
                                grade = selectedFase.grades.first(),
                                alokasiWaktu = alokasiWaktu,
                                selectedDimensi = selectedDimensiList,
                                teacherName = profile.teacherName,
                                schoolName = profile.schoolName
                            ) { modul ->
                                generatedP5Modul = modul
                                Toast.makeText(context, "Modul Projek P5 Berhasil Disusun!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("btn_generate_p5"),
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
                            Text("Gemini AI Sedang Menyusun P5...", fontWeight = FontWeight.Bold)
                        } else {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                if (isAiOnline) "Susun Modul P5 dengan Gemini AI" else "Susun Modul P5 (Offline Engine)",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = { showHistoryDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Riwayat Modul P5 Tersimpan (${savedP5Assessments.size})", fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // Tampilan Hasil Modul P5
            generatedP5Modul?.let { p5 ->
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
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
                                Text(
                                    "Dokumen Modul P5 Lengkap",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                BadgeChip(
                                    text = p5.tema,
                                    backgroundColor = MaterialTheme.colorScheme.primary,
                                    textColor = MaterialTheme.colorScheme.onPrimary
                                )
                            }

                            Text(
                                text = p5.title,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )

                            Text(
                                text = p5.deskripsiSingkat,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                            )

                            // Action buttons: Cetak PDF, Unduh Word, Simpan DB
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { DocumentExporter.printOrSaveP5Pdf(context, p5) },
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
                                            val uri = DocumentExporter.exportP5ToWordDoc(context, p5)
                                            if (uri != null) {
                                                val intent = Intent(Intent.ACTION_VIEW).apply {
                                                    setDataAndType(uri, "application/msword")
                                                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
                                                }
                                                context.startActivity(Intent.createChooser(intent, "Buka Dokumen Modul P5"))
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
                                        viewModel.saveP5AssessmentToHistory(p5) { id ->
                                            Toast.makeText(context, "Modul P5 Disimpan ke Database Lokal (ID: $id)", Toast.LENGTH_SHORT).show()
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
                }

                // Tahapan Alur Projek Detail
                item {
                    SectionHeader(title = "Alur Pelaksanaan 4 Tahapan Projek", icon = Icons.Default.Timeline)
                }

                items(p5.alurTahapan) { tahap ->
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
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(tahap.tahap, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                BadgeChip(
                                    text = tahap.alokasiJp,
                                    backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                                    textColor = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                            Text(
                                tahap.namaAktivitas,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 13.sp
                            )

                            Text(
                                tahap.deskripsiLangkah,
                                style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

                            Text(
                                "Peran Fasilitator: ${tahap.peranGuru}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                "Asesmen Formatif: ${tahap.asesmenFormatif}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
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
            title = { Text("Riwayat Modul P5 Tersimpan") },
            text = {
                if (savedP5Assessments.isEmpty()) {
                    Text("Belum ada riwayat modul P5 yang disimpan di database lokal.")
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth().height(300.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(savedP5Assessments) { item ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(item.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("Tema: ${item.tema} | ${item.fase} (${item.grade})", fontSize = 11.sp)
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        TextButton(
                                            onClick = {
                                                try {
                                                    val parsedModul = com.squareup.moshi.Moshi.Builder()
                                                        .add(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory())
                                                        .build()
                                                        .adapter(P5ProjectModul::class.java)
                                                        .fromJson(item.jsonContent)
                                                    if (parsedModul != null) {
                                                        generatedP5Modul = parsedModul
                                                        showHistoryDialog = false
                                                        Toast.makeText(context, "Memuat modul P5 dari riwayat!", Toast.LENGTH_SHORT).show()
                                                    }
                                                } catch (e: Exception) {
                                                    Toast.makeText(context, "Gagal memuat riwayat P5", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        ) {
                                            Text("Muat")
                                        }
                                        TextButton(
                                            onClick = {
                                                viewModel.deleteP5AssessmentHistory(item.id)
                                                Toast.makeText(context, "Riwayat P5 dihapus", Toast.LENGTH_SHORT).show()
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
