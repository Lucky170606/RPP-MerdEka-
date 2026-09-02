package com.example.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.ModulAjarEntity
import com.example.data.model.KurikulumMerdekaReferenceData
import com.example.data.model.QuickPreset
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
fun HomeScreen(
    viewModel: ModulViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var backPressedTime by remember { mutableLongStateOf(0L) }
    
    BackHandler {
        if (System.currentTimeMillis() - backPressedTime < 2000) {
            (context as? android.app.Activity)?.finish()
        } else {
            backPressedTime = System.currentTimeMillis()
            Toast.makeText(context, "Tekan sekali lagi untuk keluar", Toast.LENGTH_SHORT).show()
        }
    }
    
    val modulList by viewModel.allModul.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedSubject by viewModel.selectedSubjectFilter.collectAsStateWithLifecycle()
    val selectedSemester by viewModel.selectedSemesterFilter.collectAsStateWithLifecycle()
    val selectedAcademicYear by viewModel.selectedAcademicYearFilter.collectAsStateWithLifecycle()
    val selectedFase by viewModel.selectedFaseFilter.collectAsStateWithLifecycle()
    val showOnlyFavorites by viewModel.showOnlyFavorites.collectAsStateWithLifecycle()
    val isFolderMode by viewModel.isFolderGroupingMode.collectAsStateWithLifecycle()

    var modulToDelete by remember { mutableStateOf<ModulAjarEntity?>(null) }
    var expandedFolders by remember { mutableStateOf<Set<String>>(emptySet()) }

    // Dynamic Lists from Database + Standard
    val availableYears by remember(modulList) {
        derivedStateOf {
            val baseYears = listOf("2024/2025", "2025/2026", "2023/2024")
            val dbYears = modulList.map { it.academicYear }.filter { it.isNotBlank() }
            listOf("Semua") + (baseYears + dbYears).distinct()
        }
    }

    val availableSubjects by remember(modulList) {
        derivedStateOf {
            val baseSubjects = listOf("Matematika", "IPAS", "Bahasa Indonesia", "Bahasa Inggris", "Pendidikan Pancasila", "Informatika", "PJOK", "Seni Rupa", "PAI")
            val dbSubjects = modulList.map { it.subject }.filter { it.isNotBlank() }
            listOf("Semua") + (baseSubjects + dbSubjects).distinct()
        }
    }

    val availableSemesters = listOf("Semua", "Semester 1 (Ganjil)", "Semester 2 (Genap)")
    val availableFases = listOf("Semua", "Fase A", "Fase B", "Fase C", "Fase D", "Fase E", "Fase F")

    val isAnyFilterActive = selectedSubject != "Semua" || selectedSemester != "Semua" || selectedAcademicYear != "Semua" || selectedFase != "Semua" || showOnlyFavorites || searchQuery.isNotBlank()

    val filteredModul by remember(modulList, selectedSubject, selectedSemester, selectedAcademicYear, selectedFase, showOnlyFavorites, searchQuery) {
        derivedStateOf {
            modulList.filter { modul ->
                val matchSubject = selectedSubject == "Semua" || modul.subject.equals(selectedSubject, ignoreCase = true)
                val matchSemester = when (selectedSemester) {
                    "Semua" -> true
                    "Semester 1 (Ganjil)" -> modul.semester.contains("1", ignoreCase = true) || modul.semester.contains("Ganjil", ignoreCase = true)
                    "Semester 2 (Genap)" -> modul.semester.contains("2", ignoreCase = true) || modul.semester.contains("Genap", ignoreCase = true)
                    else -> modul.semester.contains(selectedSemester, ignoreCase = true)
                }
                val matchYear = selectedAcademicYear == "Semua" || modul.academicYear.equals(selectedAcademicYear, ignoreCase = true)
                val matchFase = selectedFase == "Semua" || modul.fase.equals(selectedFase, ignoreCase = true)
                val matchFav = !showOnlyFavorites || modul.isFavorite
                matchSubject && matchSemester && matchYear && matchFase && matchFav
            }
        }
    }

    // Grouping by Folder: Academic Year & Semester
    val groupedByFolder by remember(filteredModul) {
        derivedStateOf {
            filteredModul.groupBy { modul ->
                val year = modul.academicYear.ifBlank { "2024/2025" }
                val sem = if (modul.semester.contains("1") || modul.semester.contains("Ganjil", ignoreCase = true)) "Semester 1 (Ganjil)"
                          else if (modul.semester.contains("2") || modul.semester.contains("Genap", ignoreCase = true)) "Semester 2 (Genap)"
                          else modul.semester.ifBlank { "Semester 1 (Ganjil)" }
                "Tahun Ajaran $year • $sem"
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "RPP Merdeka AI",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "Generator Otomatis",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                },
                actions = {
                    Row {
                        IconButton(
                            onClick = { viewModel.navigateTo(Screen.Consultant) },
                            modifier = Modifier.testTag("btn_nav_consultant")
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Psychology,
                                contentDescription = "Konsultasi",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        IconButton(
                            onClick = { viewModel.navigateTo(Screen.ProfileSettings) },
                            modifier = Modifier.testTag("btn_nav_profile")
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Settings,
                                contentDescription = "Profil",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.navigateTo(Screen.Wizard) },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )
                },
                text = { Text("Buat RPP", fontWeight = FontWeight.Bold) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("fab_create_rpp")
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Banner
            item {
                HeroBannerCard(
                    onCreateClick = { viewModel.navigateTo(Screen.Wizard) },
                    onExploreCPClick = { viewModel.navigateTo(Screen.CPDatabase) }
                )
            }

            // Quick Feature Tools Grid / Row
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SectionHeader(
                        title = "Ekosistem Perangkat Ajar Lengkap",
                        icon = Icons.Default.Apps
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Card P5
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.navigateTo(Screen.P5Project) }
                                .testTag("card_shortcut_p5"),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Diversity3, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                }
                                Text("Modul Projek P5", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                                Text("8 Tema Resmi Profil Pelajar Pancasila", style = MaterialTheme.typography.bodySmall, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
                            }
                        }

                        // Card Bank Soal HOTS
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.navigateTo(Screen.AssessmentHots) }
                                .testTag("card_shortcut_assessment"),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.secondaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Quiz, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(18.dp))
                                }
                                Text("Kisi & Soal HOTS", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                                Text("Generator Soal C1-C6 & Kunci Jawaban", style = MaterialTheme.typography.bodySmall, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Card Konsultasi Guru
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.navigateTo(Screen.Consultant) }
                                .testTag("card_shortcut_consultant"),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.tertiaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Psychology, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(18.dp))
                                }
                                Text("Ruang Konsultasi AI", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                                Text("Tanya Jawab Pedagogik & Ice Breaking", style = MaterialTheme.typography.bodySmall, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
                            }
                        }

                        // Card PROTA & PROMES
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.navigateTo(Screen.ProtaPromes) }
                                .testTag("card_shortcut_prota_promes"),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                }
                                Text("PROTA & PROMES", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                                Text("Program Tahunan & Semester Mingguan", style = MaterialTheme.typography.bodySmall, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Card Alur Tujuan Pembelajaran (ATP)
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.navigateTo(Screen.Atp) }
                                .testTag("card_shortcut_atp"),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.secondaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.AccountTree, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(18.dp))
                                }
                                Text("Bagan Dokumen ATP", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                                Text("Alur Tujuan Pembelajaran & Alokasi JP", style = MaterialTheme.typography.bodySmall, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
                            }
                        }

                        // Card KKTP & Deskripsi e-Rapor
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.navigateTo(Screen.RaporKktp) }
                                .testTag("card_shortcut_rapor"),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.tertiaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Grading, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(18.dp))
                                }
                                Text("KKTP & Nilai Rapor", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                                Text("Generator Kalimat Capaian e-Rapor", style = MaterialTheme.typography.bodySmall, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Card Jurnal Observasi & Antarteman
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.navigateTo(Screen.ObservationJournal) }
                                .testTag("card_shortcut_obs"),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.FactCheck, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                }
                                Text("Observasi & Sikap P3", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                                Text("Jurnal Anekdot & Penilaian Antarteman", style = MaterialTheme.typography.bodySmall, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
                            }
                        }

                        // Card Profil & Pengaturan
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.navigateTo(Screen.ProfileSettings) }
                                .testTag("card_shortcut_profile"),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.surfaceVariant),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Settings, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                                }
                                Text("Profil & Kop Surat", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                                Text("Data Sekolah, NIP & Pengesahan", style = MaterialTheme.typography.bodySmall, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
                            }
                        }
                    }
                }
            }

            // Quick Inspiration Presets
            item {
                Column {
                    SectionHeader(
                        title = "Inspirasi Cepat (1-Tap Preset)",
                        icon = Icons.Default.FlashOn
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        items(KurikulumMerdekaReferenceData.QUICK_PRESETS) { preset ->
                            QuickPresetCard(
                                preset = preset,
                                onClick = { viewModel.applyPreset(preset) }
                            )
                        }
                    }
                }
            }

            // Search Bar & Multi-Dimensional Filter Section
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.searchQuery.value = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_search_rpp"),
                        placeholder = { Text("Cari judul modul, topik, mata pelajaran...") },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = "Cari")
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.searchQuery.value = "" }) {
                                    Icon(Icons.Default.Close, contentDescription = "Hapus teks")
                                }
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    // Filter Category 1: Tahun Ajaran Filter
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary)
                            Text("Tahun Ajaran:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                        }
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            items(availableYears) { yr ->
                                FilterChip(
                                    selected = selectedAcademicYear == yr,
                                    onClick = { viewModel.selectedAcademicYearFilter.value = yr },
                                    label = { Text(if (yr == "Semua") "Semua Tahun" else yr, fontSize = 12.sp) },
                                    shape = RoundedCornerShape(8.dp)
                                )
                            }
                        }
                    }

                    // Filter Category 2: Tag Semester Filter
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.Bookmark, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.secondary)
                            Text("Tag Semester:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                        }
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            items(availableSemesters) { sem ->
                                FilterChip(
                                    selected = selectedSemester == sem,
                                    onClick = { viewModel.selectedSemesterFilter.value = sem },
                                    label = { Text(sem, fontSize = 12.sp) },
                                    leadingIcon = if (selectedSemester == sem) {
                                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                                    } else null,
                                    shape = RoundedCornerShape(8.dp)
                                )
                            }
                        }
                    }

                    // Filter Category 3: Mata Pelajaran & Favorit Chips
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.tertiary)
                                Text("Mata Pelajaran & Kategori:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                            }
                            if (isAnyFilterActive) {
                                TextButton(
                                    onClick = { viewModel.resetFilters() },
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text("Reset Filter", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            item {
                                FilterChip(
                                    selected = showOnlyFavorites,
                                    onClick = { viewModel.showOnlyFavorites.value = !showOnlyFavorites },
                                    label = { Text("Favorit", fontSize = 12.sp) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = if (showOnlyFavorites) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp),
                                            tint = if (showOnlyFavorites) EduAmber600 else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    },
                                    shape = RoundedCornerShape(8.dp)
                                )
                            }
                            items(availableSubjects) { sub ->
                                FilterChip(
                                    selected = selectedSubject == sub,
                                    onClick = { viewModel.selectedSubjectFilter.value = sub },
                                    label = { Text(if (sub == "Semua") "Semua Mapel" else sub, fontSize = 12.sp) },
                                    shape = RoundedCornerShape(8.dp)
                                )
                            }
                        }
                    }

                    // Filter Category 4: Fase (Jenjang)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.School, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary)
                            Text("Jenjang / Fase:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                        }
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            items(availableFases) { fase ->
                                FilterChip(
                                    selected = selectedFase == fase,
                                    onClick = { viewModel.selectedFaseFilter.value = fase },
                                    label = { Text(if (fase == "Semua") "Semua Fase" else fase, fontSize = 12.sp) },
                                    shape = RoundedCornerShape(8.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Modul List Header with View Toggle
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        SectionHeader(
                            title = if (isFolderMode) "Arsip Folder (${groupedByFolder.size})" else "Koleksi Modul (${filteredModul.size})",
                            icon = if (isFolderMode) Icons.Default.FolderSpecial else Icons.Default.LibraryBooks
                        )
                    }

                    // Compact View Mode Toggle (Daftar / Folder)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            border = androidx.compose.foundation.BorderStroke(0.8.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        ) {
                            Row(
                                modifier = Modifier.padding(2.dp),
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = if (!isFolderMode) MaterialTheme.colorScheme.primary else Color.Transparent,
                                    modifier = Modifier.clickable { viewModel.isFolderGroupingMode.value = false }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.ViewList,
                                            contentDescription = "Daftar",
                                            modifier = Modifier.size(13.dp),
                                            tint = if (!isFolderMode) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            "Daftar",
                                            fontSize = 11.sp,
                                            fontWeight = if (!isFolderMode) FontWeight.Bold else FontWeight.Normal,
                                            color = if (!isFolderMode) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = if (isFolderMode) MaterialTheme.colorScheme.primary else Color.Transparent,
                                    modifier = Modifier.clickable { viewModel.isFolderGroupingMode.value = true }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Folder,
                                            contentDescription = "Folder",
                                            modifier = Modifier.size(13.dp),
                                            tint = if (isFolderMode) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            "Folder",
                                            fontSize = 11.sp,
                                            fontWeight = if (isFolderMode) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isFolderMode) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (filteredModul.isEmpty()) {
                item {
                    EmptyModulCard(
                        isSearching = searchQuery.isNotEmpty() || isAnyFilterActive,
                        onCreateClick = { viewModel.navigateTo(Screen.Wizard) }
                    )
                }
            } else if (isFolderMode) {
                // FOLDER GROUPING MODE
                items(groupedByFolder.keys.toList(), key = { it }) { folderName ->
                    val modulsInFolder = groupedByFolder[folderName] ?: emptyList()
                    val isExpanded = expandedFolders.contains(folderName) || expandedFolders.isEmpty()

                    FolderArchiveCard(
                        folderName = folderName,
                        modulCount = modulsInFolder.size,
                        isExpanded = isExpanded,
                        onToggleExpand = {
                            expandedFolders = if (isExpanded) {
                                expandedFolders - folderName
                            } else {
                                expandedFolders + folderName
                            }
                        },
                        moduls = modulsInFolder,
                        onOpenModul = { viewModel.navigateTo(Screen.Editor(it.id)) },
                        onPrintModul = { DocumentExporter.printOrSavePdf(context, it) },
                        onShareModul = { DocumentExporter.shareModulText(context, it) },
                        onToggleFavorite = { viewModel.toggleFavorite(it.id, it.isFavorite) },
                        onDeleteModul = { modulToDelete = it }
                    )
                }
            } else {
                // FLAT LIST VIEW
                items(filteredModul, key = { it.id }) { modul ->
                    ModulItemCard(
                        modul = modul,
                        onOpenClick = { viewModel.navigateTo(Screen.Editor(modul.id)) },
                        onPrintClick = { DocumentExporter.printOrSavePdf(context, modul) },
                        onShareClick = { DocumentExporter.shareModulText(context, modul) },
                        onToggleFavorite = { viewModel.toggleFavorite(modul.id, modul.isFavorite) },
                        onDeleteClick = { modulToDelete = modul }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }

    // Delete Confirmation Dialog
    if (modulToDelete != null) {
        AlertDialog(
            onDismissRequest = { modulToDelete = null },
            title = { Text("Hapus Modul Ajar?", fontWeight = FontWeight.Bold) },
            text = {
                Text("Modul Ajar \"${modulToDelete?.title}\" akan dihapus secara permanen dari perangkat.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        modulToDelete?.let { viewModel.deleteModul(it.id) }
                        modulToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EduRed600),
                    modifier = Modifier.testTag("btn_confirm_delete")
                ) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { modulToDelete = null }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
fun HeroBannerCard(
    onCreateClick: () -> Unit,
    onExploreCPClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.secondary
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    BadgeChip(
                        text = "Kurikulum Merdeka 2024/2025",
                        backgroundColor = Color(0x33FFFFFF),
                        textColor = Color.White,
                        icon = Icons.Default.Verified
                    )
                }

                Text(
                    text = "Susun Modul Ajar & RPP Berdiferensiasi dalam Hitungan Detik",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 20.sp,
                        lineHeight = 26.sp
                    )
                )

                Text(
                    text = "Dilengkapi pencocokan otomatis CP resmi BSKAP, 6 Dimensi Profil Pelajar Pancasila, Asesmen & Rubrik, serta Teks Editor Terintegrasi.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFFE2E8F0),
                        lineHeight = 20.sp
                    )
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(top = 6.dp)
                ) {
                    Button(
                        onClick = onCreateClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiary,
                            contentColor = MaterialTheme.colorScheme.onTertiary
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("btn_hero_create")
                    ) {
                        Icon(Icons.Default.AutoFixHigh, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Buat Sekarang", fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onExploreCPClick,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = Brush.horizontalGradient(listOf(Color.White, Color.White))
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Bank CP & P3")
                    }
                }
            }
        }
    }
}

@Composable
fun QuickPresetCard(
    preset: QuickPreset,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(260.dp)
            .clickable(onClick = onClick)
            .testTag("preset_${preset.fase}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                BadgeChip(
                    text = preset.fase,
                    backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                    textColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
                BadgeChip(
                    text = preset.grade,
                    backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                    textColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

            Text(
                text = preset.title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = preset.topic,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = null,
                    tint = EduAmber600,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = preset.model.substringBefore("(").trim(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun FolderArchiveCard(
    folderName: String,
    modulCount: Int,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    moduls: List<ModulAjarEntity>,
    onOpenModul: (ModulAjarEntity) -> Unit,
    onPrintModul: (ModulAjarEntity) -> Unit,
    onShareModul: (ModulAjarEntity) -> Unit,
    onToggleFavorite: (ModulAjarEntity) -> Unit,
    onDeleteModul: (ModulAjarEntity) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Folder Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggleExpand)
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.FolderOpen else Icons.Default.Folder,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Column {
                        Text(
                            text = folderName,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "$modulCount Modul Tersimpan",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    BadgeChip(
                        text = "$modulCount Modul",
                        backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                        textColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    IconButton(
                        onClick = onToggleExpand,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = if (isExpanded) "Tutup Folder" else "Buka Folder",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Expanded Folder Content
            if (isExpanded) {
                Divider(
                    modifier = Modifier.padding(vertical = 10.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    moduls.forEach { modul ->
                        ModulItemCard(
                            modul = modul,
                            onOpenClick = { onOpenModul(modul) },
                            onPrintClick = { onPrintModul(modul) },
                            onShareClick = { onShareModul(modul) },
                            onToggleFavorite = { onToggleFavorite(modul) },
                            onDeleteClick = { onDeleteModul(modul) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ModulItemCard(
    modul: ModulAjarEntity,
    onOpenClick: () -> Unit,
    onPrintClick: () -> Unit,
    onShareClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val semesterLabel = if (modul.semester.contains("1") || modul.semester.contains("Ganjil", ignoreCase = true)) "Sem. Ganjil"
                        else if (modul.semester.contains("2") || modul.semester.contains("Genap", ignoreCase = true)) "Sem. Genap"
                        else modul.semester.ifBlank { "Sem. Ganjil" }
    val yearLabel = modul.academicYear.ifBlank { "2024/2025" }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
            .clickable(onClick = onOpenClick)
            .testTag("modul_card_${modul.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header: Badges & Favorite
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    BadgeChip(
                        text = "$semesterLabel • $yearLabel",
                        backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                        textColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        icon = Icons.Default.CalendarToday
                    )
                    BadgeChip(
                        text = modul.subject,
                        backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                        textColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    BadgeChip(
                        text = modul.grade,
                        backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                        textColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }

                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (modul.isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                        contentDescription = "Favorit",
                        tint = if (modul.isFavorite) EduAmber600 else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Topic / Title
            Text(
                text = modul.topic,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            // Info rows: Guru & Sekolah, Model Pembelajaran
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = modul.schoolName.ifBlank { "Sekolah" },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = modul.timeAllocation,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Profil Pelajar Pancasila Preview
            if (modul.dimensiP3.isNotBlank()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Diversity3,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "P3: ${modul.dimensiP3}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), thickness = 0.8.dp)

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID")).format(Date(modul.updatedAt)),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = onPrintClick,
                        modifier = Modifier.size(36.dp).testTag("btn_print_${modul.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Print,
                            contentDescription = "Cetak PDF",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onShareClick,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Share,
                            contentDescription = "Bagikan",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier.size(36.dp).testTag("btn_delete_${modul.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Hapus",
                            tint = EduRed600,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    FilledTonalButton(
                        onClick = onOpenClick,
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("btn_open_canvas_${modul.id}")
                    ) {
                        Icon(Icons.Default.EditNote, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Buka Editor", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyModulCard(
    isSearching: Boolean,
    onCreateClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isSearching) Icons.Default.SearchOff else Icons.Default.LibraryBooks,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            }

            Text(
                text = if (isSearching) "Modul Tidak Ditemukan" else "Belum Ada Modul Ajar Tersimpan",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = if (isSearching)
                    "Coba ubah kata kunci pencarian atau ganti filter mata pelajaran."
                else
                    "Mulai buat Modul Ajar Kurikulum Merdeka pertama Anda secara otomatis menggunakan kecerdasan buatan AI.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            if (!isSearching) {
                Spacer(modifier = Modifier.height(4.dp))
                Button(
                    onClick = onCreateClick,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Buat Modul Ajar Baru")
                }
            }
        }
    }
}
