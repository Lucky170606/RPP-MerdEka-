package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Fase
import com.example.data.model.KurikulumMerdekaReferenceData
import com.example.ui.components.AppHeader
import com.example.ui.components.BadgeChip
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*
import com.example.ui.viewmodel.GenerationState
import com.example.ui.viewmodel.ModulViewModel
import com.example.ui.viewmodel.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneratorWizardScreen(
    viewModel: ModulViewModel,
    modifier: Modifier = Modifier
) {
    var currentStep by remember { mutableStateOf(1) } // Step 1 to 4

    val teacherName by viewModel.wizardTeacherName.collectAsStateWithLifecycle()
    val schoolName by viewModel.wizardSchoolName.collectAsStateWithLifecycle()
    val selectedFase by viewModel.wizardFase.collectAsStateWithLifecycle()
    val selectedGrade by viewModel.wizardGrade.collectAsStateWithLifecycle()
    val selectedSubject by viewModel.wizardSubject.collectAsStateWithLifecycle()
    val topic by viewModel.wizardTopic.collectAsStateWithLifecycle()
    val timeAllocation by viewModel.wizardTimeAllocation.collectAsStateWithLifecycle()
    val semester by viewModel.wizardSemester.collectAsStateWithLifecycle()
    val academicYear by viewModel.wizardAcademicYear.collectAsStateWithLifecycle()
    val selectedModel by viewModel.wizardModel.collectAsStateWithLifecycle()
    val selectedDimensi by viewModel.wizardSelectedDimensi.collectAsStateWithLifecycle()
    val selectedGayaBelajar by viewModel.wizardGayaBelajar.collectAsStateWithLifecycle()
    val selectedKesiapan by viewModel.wizardKesiapan.collectAsStateWithLifecycle()
    val additionalNotes by viewModel.wizardAdditionalNotes.collectAsStateWithLifecycle()

    val generationState by viewModel.generationState.collectAsStateWithLifecycle()

    val matchedCP = remember(selectedSubject, selectedFase, topic) {
        KurikulumMerdekaReferenceData.findMatchingCP(selectedSubject, selectedFase.code, topic)
    }

    BackHandler {
        if (currentStep > 1) {
            currentStep--
        } else {
            viewModel.navigateTo(Screen.Home)
        }
    }

    Scaffold(
        topBar = {
            AppHeader(
                title = "Generator RPP & Modul Ajar",
                subtitle = "Langkah $currentStep dari 4: ${
                    when (currentStep) {
                        1 -> "Identitas & Fase"
                        2 -> "CP, TP & Dimensi P3"
                        3 -> "Diferensiasi & Asesmen"
                        else -> "Konfirmasi & Generate AI"
                    }
                }",
                showBackButton = true,
                onBackClick = {
                    if (currentStep > 1) currentStep-- else viewModel.navigateTo(Screen.Home)
                }
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 6.dp,
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (currentStep > 1) {
                        OutlinedButton(
                            onClick = { currentStep-- },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("btn_wizard_prev")
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Sebelumnya")
                        }
                    } else {
                        Spacer(modifier = Modifier.width(1.dp))
                    }

                    if (currentStep < 4) {
                        Button(
                            onClick = { currentStep++ },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            modifier = Modifier.testTag("btn_wizard_next")
                        ) {
                            Text("Lanjut ke Langkah ${currentStep + 1}")
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    } else {
                        Button(
                            onClick = { viewModel.startAIGeneration() },
                            enabled = generationState !is GenerationState.Generating,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            modifier = Modifier.testTag("btn_wizard_generate")
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Generate Modul Ajar AI", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Stepper Progress Indicators
            item {
                StepIndicatorRow(currentStep = currentStep)
            }

            // STEP 1: Identitas & Fase
            if (currentStep == 1) {
                item {
                    Text(
                        text = "1. Data Dasar & Identitas Pembelajaran",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Masukkan jenjang fase, mata pelajaran, topik materi pokok, serta identitas guru dan satuan pendidikan.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Fase Selector
                item {
                    Text("Pilih Fase & Jenjang:", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(Fase.values()) { f ->
                            val isSelected = selectedFase == f
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    viewModel.wizardFase.value = f
                                    viewModel.wizardGrade.value = f.grades.first()
                                },
                                label = { Text(f.code) },
                                leadingIcon = {
                                    if (isSelected) Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                                },
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                    }
                }

                // Grade / Kelas Selector
                item {
                    Text("Pilih Kelas (${selectedFase.jenjang}):", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        selectedFase.grades.forEach { g ->
                            val isSelected = selectedGrade == g
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.wizardGrade.value = g },
                                label = { Text(g) },
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                    }
                }

                // Mata Pelajaran Selector
                item {
                    Text("Mata Pelajaran:", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(6.dp))
                    val subjects = remember(selectedFase) { KurikulumMerdekaReferenceData.getSubjectsForFase(selectedFase.code) }
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(subjects) { subjectName ->
                            val isSelected = selectedSubject == subjectName
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.wizardSubject.value = subjectName },
                                label = { Text(subjectName) },
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                    }
                }

                // Topik / Materi
                item {
                    OutlinedTextField(
                        value = topic,
                        onValueChange = { viewModel.wizardTopic.value = it },
                        label = { Text("Topik / Materi Pokok *") },
                        placeholder = { Text("Misal: Mengenal Pecahan Senilai dengan Benda Konkret") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_topic"),
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                // Alokasi Waktu
                item {
                    OutlinedTextField(
                        value = timeAllocation,
                        onValueChange = { viewModel.wizardTimeAllocation.value = it },
                        label = { Text("Alokasi Waktu *") },
                        placeholder = { Text("Misal: 2 JP (2 x 35 Menit)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_time_allocation"),
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                // Guru & Sekolah
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = teacherName,
                            onValueChange = { viewModel.wizardTeacherName.value = it },
                            label = { Text("Nama Guru") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        OutlinedTextField(
                            value = schoolName,
                            onValueChange = { viewModel.wizardSchoolName.value = it },
                            label = { Text("Satuan Pendidikan") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }

                // Semester & TP
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = semester,
                            onValueChange = { viewModel.wizardSemester.value = it },
                            label = { Text("Semester") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        OutlinedTextField(
                            value = academicYear,
                            onValueChange = { viewModel.wizardAcademicYear.value = it },
                            label = { Text("Tahun Ajaran") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
            }

            // STEP 2: Database CP & Profil Pelajar Pancasila
            if (currentStep == 2) {
                item {
                    Text(
                        text = "2. Capaian Pembelajaran (CP) & Profil Pancasila",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Sistem secara otomatis mencocokkan Capaian Pembelajaran (CP) resmi dari BSKAP Kemendikbudristek berdasarkan input Fase dan Mata Pelajaran.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Matched CP Card from Internal Database
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
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
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.AutoFixHigh,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "CP Terdeteksi Otomatis (${matchedCP?.elemen ?: "Elemen Standar"})",
                                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                BadgeChip(
                                    text = "Resmi BSKAP",
                                    backgroundColor = MaterialTheme.colorScheme.primary,
                                    textColor = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                            Text(
                                text = matchedCP?.capaianText ?: "Peserta didik menunjukkan pemahaman konsep, keterampilan eksplorasi dan penalaran ilmiah terkait topik $topic secara mandiri dan kritis.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }

                // 6 Dimensi Profil Pelajar Pancasila Selection
                item {
                    SectionHeader(
                        title = "Pilih Dimensi Profil Pelajar Pancasila (P3):",
                        icon = Icons.Default.Diversity3
                    )
                    Text(
                        text = "Pilih minimal 2-3 dimensi yang akan dikembangkan dalam modul ini:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }

                items(KurikulumMerdekaReferenceData.PROFIL_PELAJAR_PANCASILA) { dimension ->
                    val isSelected = selectedDimensi.any { it.contains(dimension.title.substringBefore(",").trim()) || dimension.title.contains(it) }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { viewModel.toggleDimensi(dimension.title.substringBefore(",").trim()) },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = isSelected,
                                onCheckedChange = { viewModel.toggleDimensi(dimension.title.substringBefore(",").trim()) },
                                colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = dimension.title,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = dimension.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 2
                                )
                            }
                        }
                    }
                }

                // Model Pembelajaran Picker
                item {
                    SectionHeader(
                        title = "Pilih Model Pembelajaran:",
                        icon = Icons.Default.Psychology
                    )
                }

                items(KurikulumMerdekaReferenceData.MODEL_PEMBELAJARAN_LIST) { model ->
                    val isSelected = selectedModel.contains(model.name.substringBefore("(").trim())
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { viewModel.wizardModel.value = model.name },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { viewModel.wizardModel.value = model.name },
                                colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.secondary)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = model.name,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = model.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // STEP 3: Pembelajaran Berdiferensiasi & Asesmen
            if (currentStep == 3) {
                item {
                    Text(
                        text = "3. Modul Pembelajaran Berdiferensiasi & Asesmen",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Atur preferensi diferensiasi kegiatan belajar (Konten, Proses, Produk) dan struktur instrumen penilaian.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Gaya Belajar Target (Visual, Auditori, Kinestetik)
                item {
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Hearing,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Fokus Gaya Belajar Siswa (Diferensiasi Konten)",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                            Text(
                                text = "Pilih gaya belajar yang ingin difasilitasi dalam modul ajar:",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf("Visual", "Auditori", "Kinestetik").forEach { gaya ->
                                    val isSelected = selectedGayaBelajar.contains(gaya)
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { viewModel.toggleGayaBelajar(gaya) },
                                        label = { Text(gaya) },
                                        leadingIcon = {
                                            if (isSelected) Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                                        },
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Tingkat Kesiapan Siswa (Diferensiasi Proses)
                item {
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Leaderboard,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Tingkat Kesiapan Belajar Siswa (Diferensiasi Proses)",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                            Text(
                                text = "Level bimbingan bertingkat (Scaffolding):",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                BadgeChip(
                                    text = "• Perlu Bimbingan: Pendampingan intensif & benda konkret",
                                    backgroundColor = MaterialTheme.colorScheme.errorContainer,
                                    textColor = MaterialTheme.colorScheme.onErrorContainer
                                )
                                BadgeChip(
                                    text = "• Berkembang / Cukup: Panduan semi-terstruktur & kerja kelompok",
                                    backgroundColor = MaterialTheme.colorScheme.tertiaryContainer,
                                    textColor = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                                BadgeChip(
                                    text = "• Mahir / Lanjut: Tantangan studi kasus HOTS mandiri & tutor sebaya",
                                    backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                                    textColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }

                // Asesmen & Rubrik Generator Checklist
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.FactCheck,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Instrumen Asesmen yang Dibuat Otomatis",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                            Text(
                                text = "✔ Asesmen Diagnostik (Kognitif & Non-Kognitif Awal)\n✔ Asesmen Formatif (Observasi Sikap P3 & Checklist Unjuk Kerja)\n✔ Asesmen Sumatif (Pilihan Ganda HOTS & Soal Uraian)\n✔ Rubrik Kriteria Penilaian Skala 1 s.d. 4",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }

                // Catatan Khusus Guru
                item {
                    OutlinedTextField(
                        value = additionalNotes,
                        onValueChange = { viewModel.wizardAdditionalNotes.value = it },
                        label = { Text("Instruksi / Catatan Khusus Guru (Opsional)") },
                        placeholder = { Text("Misal: Sertakan ice breaking edukasi, fokuskan pada pemanfaatan alat peraga lokal...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        shape = RoundedCornerShape(10.dp),
                        maxLines = 4
                    )
                }
            }

            // STEP 4: Ringkasan & Generasi AI
            if (currentStep == 4) {
                item {
                    Text(
                        text = "4. Ringkasan Parameter Modul Ajar",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Periksa kembali konfigurasi modul ajar Anda sebelum AI menyusun draf lengkap.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            SummaryItemRow("Mata Pelajaran", selectedSubject)
                            SummaryItemRow("Fase & Kelas", "${selectedFase.code} (${selectedGrade})")
                            SummaryItemRow("Topik Materi", topic)
                            SummaryItemRow("Alokasi Waktu", timeAllocation)
                            SummaryItemRow("Model Belajar", selectedModel.substringBefore("(").trim())
                            SummaryItemRow("Dimensi P3", selectedDimensi.joinToString(", "))
                            SummaryItemRow("Gaya Belajar", selectedGayaBelajar.joinToString(", "))
                            SummaryItemRow("Satuan Pendidikan", "$schoolName ($teacherName)")
                        }
                    }
                }

                // Loading State Animation
                if (generationState is GenerationState.Generating) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                                Text(
                                    text = (generationState as GenerationState.Generating).stepMessage,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                                Text(
                                    text = "Menyusun draf resmi lengkap, kegiatan inti, rubrik asesmen, dan LKPD...",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }

                if (generationState is GenerationState.Error) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = (generationState as GenerationState.Error).message,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun StepIndicatorRow(currentStep: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..4) {
            val isActive = currentStep >= i
            val isCurrent = currentStep == i

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                isCurrent -> MaterialTheme.colorScheme.primary
                                isActive -> MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isActive && !isCurrent) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    } else {
                        Text(
                            text = "$i",
                            color = if (isActive) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }

                if (i < 4) {
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(3.dp)
                            .background(
                                if (currentStep > i) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            )
                    )
                }
            }
        }
    }
}

@Composable
fun SummaryItemRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.35f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(0.65f),
            textAlign = androidx.compose.ui.text.style.TextAlign.End
        )
    }
}
