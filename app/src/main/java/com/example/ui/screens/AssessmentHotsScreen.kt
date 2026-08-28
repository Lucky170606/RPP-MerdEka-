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
import com.example.data.ai.OfflineAssessmentEngine
import com.example.data.model.AssessmentDocument
import com.example.data.model.Fase
import com.example.data.model.TeacherProfile
import com.example.ui.components.AppHeader
import com.example.ui.components.BadgeChip
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*
import com.example.ui.viewmodel.ModulViewModel
import com.example.ui.viewmodel.Screen
import com.example.util.DocumentExporter

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

    var assessmentDoc by remember { mutableStateOf<AssessmentDocument?>(null) }

    val subjects = listOf("Matematika", "IPAS", "Bahasa Indonesia", "Pendidikan Pancasila", "Informatika", "Bahasa Inggris")
    val jenisList = listOf("Asesmen Sumatif Akhir Bab", "Sumatif Tengah Semester (STS)", "Sumatif Akhir Semester (SAS)")

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
                                    onClick = { selectedSubject = sub },
                                    label = { Text(sub) }
                                )
                            }
                        }

                        Text("Fase & Jenjang:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
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
                            value = materiTopik,
                            onValueChange = { materiTopik = it },
                            label = { Text("Topik / Materi Ujian") },
                            modifier = Modifier.fillMaxWidth().testTag("input_assessment_topic")
                        )

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

                        // Jumlah Soal Slider/Selector
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Jumlah Butir Soal: $jumlahSoal Soal", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                listOf(3, 5, 8, 10).forEach { num ->
                                    FilterChip(
                                        selected = jumlahSoal == num,
                                        onClick = { jumlahSoal = num },
                                        label = { Text("$num") }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Tombol Generate Soal
            item {
                Button(
                    onClick = {
                        val profile = TeacherProfile.loadFromPreferences(context)
                        assessmentDoc = OfflineAssessmentEngine.generateAssessment(
                            subject = selectedSubject,
                            fase = selectedFase.code,
                            grade = selectedFase.grades.first(),
                            topic = materiTopik.ifBlank { "Materi Pokok" },
                            jenisAsesmen = jenisAsesmen,
                            semester = profile.defaultSemester,
                            jumlahSoal = jumlahSoal
                        )
                        Toast.makeText(context, "Kisi-Kisi & Soal HOTS Berhasil Dibuat!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("btn_generate_assessment"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(Icons.Default.AutoFixHigh, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Susun Kisi-Kisi & Soal HOTS Otomatis", fontWeight = FontWeight.Bold)
                }
            }

            // Hasil Asesmen Document
            assessmentDoc?.let { doc ->
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
                                    doc.title,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontSize = 14.sp
                                )
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
                                "Indikator: ${k.indikatorSoal}",
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
                                    text = s.stimulusText,
                                    style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
                                    modifier = Modifier.padding(8.dp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // Pertanyaan
                            Text(
                                s.pertanyaan,
                                fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            // Opsi Jawaban (if any)
                            if (s.pilihanOpsi.isNotEmpty()) {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    s.pilihanOpsi.forEach { opt ->
                                        Text(
                                            opt,
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
                                    "Kunci Jawaban: ${s.kunciJawaban}",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    fontSize = 12.sp
                                )
                                Text(
                                    "Pembahasan: ${s.pembahasanDanAlasan}",
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
}
