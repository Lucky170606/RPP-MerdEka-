package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.AcademicCalendarEntity
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*
import com.example.ui.viewmodel.ModulViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AcademicCalendarScreen(
    viewModel: ModulViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val profile = com.example.data.model.TeacherProfile.loadFromPreferences(context)
    var selectedYear by remember { mutableStateOf(profile.defaultAcademicYear.ifBlank { "2024/2025" }) }
    var selectedSemester by remember { mutableStateOf(profile.defaultSemester.ifBlank { "Semester 1 (Ganjil)" }) }

    val calendarWeeksFlow = remember(selectedYear, selectedSemester) {
        viewModel.getCalendarWeeks(selectedYear, selectedSemester)
    }
    val calendarWeeks by calendarWeeksFlow.collectAsStateWithLifecycle(initialValue = emptyList())

    var editingWeek by remember { mutableStateOf<AcademicCalendarEntity?>(null) }

    // Auto-init if empty
    LaunchedEffect(calendarWeeks) {
        if (calendarWeeks.isEmpty()) {
            viewModel.initDefaultCalendar(selectedYear, selectedSemester)
        }
    }

    // Statistics calculations
    val totalWeeks = calendarWeeks.size
    val effectiveWeeks = calendarWeeks.count { it.status == "EFFECTIVE" }
    val totalEffectiveJP = calendarWeeks.filter { it.status == "EFFECTIVE" }.sumOf { it.hours }
    val holidayWeeks = calendarWeeks.count { it.status == "HOLIDAY" }
    val examWeeks = calendarWeeks.count { it.status == "EXAM" }
    val activityWeeks = calendarWeeks.count { it.status == "ACTIVITY" }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Kalender Akademik & Hari Efektif", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("Perhitungan Minggu & Jam Efektif KBM", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("btn_back")) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            scope.launch {
                                viewModel.initDefaultCalendar(selectedYear, selectedSemester)
                                Toast.makeText(context, "Kalender berhasil direset ke standar!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.testTag("btn_reset_calendar")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reset Kalender")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            scope.launch {
                                viewModel.initDefaultCalendar(selectedYear, selectedSemester)
                                Toast.makeText(context, "Kalender digenerate ulang!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.AutoFixHigh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Reset Standar", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    Button(
                        onClick = {
                            Toast.makeText(
                                context,
                                "Sukses! $effectiveWeeks Minggu Efektif ($totalEffectiveJP JP) disinkronkan ke Prota & Promes.",
                                Toast.LENGTH_LONG
                            ).show()
                            onNavigateBack()
                        },
                        modifier = Modifier
                            .weight(1.5f)
                            .height(48.dp)
                            .testTag("btn_sync_calendar"),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Sinkronkan ke Prota", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Filter Selectors: Academic Year & Semester
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("Pengaturan Periode Akademik", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Year dropdown / options
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Tahun Ajaran", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    listOf("2024/2025", "2025/2026").forEach { yr ->
                                        FilterChip(
                                            selected = selectedYear == yr,
                                            onClick = { selectedYear = yr },
                                            label = { Text(yr, fontSize = 11.sp) },
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                    }
                                }
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Semester 1 (Ganjil)", "Semester 2 (Genap)").forEach { sem ->
                                FilterChip(
                                    selected = selectedSemester == sem,
                                    onClick = { selectedSemester = sem },
                                    label = { Text(sem, fontSize = 12.sp) },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }

            // Summary Stats Cards
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SectionHeader(title = "Rekapitulasi Jam Efektif", icon = Icons.Default.Analytics)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text("Minggu Efektif", fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                                Text("$effectiveWeeks / $totalWeeks Minggu", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                                Text("Total Jam: $totalEffectiveJP JP", fontSize = 11.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
                            }
                        }

                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text("Libur & Khusus", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSecondaryContainer)
                                Text("${holidayWeeks + activityWeeks} Minggu", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSecondaryContainer)
                                Text("Ujian: $examWeeks Pekan", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f))
                            }
                        }
                    }
                }
            }

            // Weeks List grouped by Month
            item {
                SectionHeader(title = "Rincian Kalender Per Minggu", icon = Icons.Default.DateRange)
            }

            if (calendarWeeks.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            } else {
                items(calendarWeeks, key = { it.id }) { week ->
                    CalendarWeekItem(
                        week = week,
                        onEditClick = { editingWeek = week },
                        onQuickToggle = { newStatus ->
                            scope.launch {
                                val newHours = if (newStatus == "EFFECTIVE") 4 else 0
                                viewModel.saveCalendarWeek(week.copy(status = newStatus, hours = newHours))
                            }
                        }
                    )
                }
            }
        }
    }

    // Edit Dialog
    if (editingWeek != null) {
        var currentStatus by remember { mutableStateOf(editingWeek!!.status) }
        var currentHours by remember { mutableStateOf(editingWeek!!.hours.toString()) }
        var currentDesc by remember { mutableStateOf(editingWeek!!.description) }

        AlertDialog(
            onDismissRequest = { editingWeek = null },
            title = { Text("Edit ${editingWeek!!.weekLabel} (${editingWeek!!.monthName})") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Status Pekan:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf(
                            "EFFECTIVE" to "Efektif",
                            "EXAM" to "Ujian",
                            "HOLIDAY" to "Libur",
                            "ACTIVITY" to "Khusus"
                        ).forEach { (st, label) ->
                            FilterChip(
                                selected = currentStatus == st,
                                onClick = {
                                    currentStatus = st
                                    if (st == "EFFECTIVE" && currentHours == "0") currentHours = "4"
                                    if (st != "EFFECTIVE") currentHours = "0"
                                },
                                label = { Text(label, fontSize = 11.sp) },
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                    }

                    if (currentStatus == "EFFECTIVE") {
                        OutlinedTextField(
                            value = currentHours,
                            onValueChange = { currentHours = it },
                            label = { Text("Alokasi Jam Pelajaran (JP)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    OutlinedTextField(
                        value = currentDesc,
                        onValueChange = { currentDesc = it },
                        label = { Text("Keterangan Kegiatan") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val hoursInt = currentHours.toIntOrNull() ?: if (currentStatus == "EFFECTIVE") 4 else 0
                        scope.launch {
                            viewModel.saveCalendarWeek(
                                editingWeek!!.copy(
                                    status = currentStatus,
                                    hours = hoursInt,
                                    description = currentDesc
                                )
                            )
                            editingWeek = null
                            Toast.makeText(context, "Pekan diperbarui!", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("Simpan")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingWeek = null }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
fun CalendarWeekItem(
    week: AcademicCalendarEntity,
    onEditClick: () -> Unit,
    onQuickToggle: (String) -> Unit
) {
    val (statusBg, statusText, statusColor) = when (week.status) {
        "EFFECTIVE" -> Triple(MaterialTheme.colorScheme.primaryContainer, "Efektif KBM (${week.hours} JP)", MaterialTheme.colorScheme.primary)
        "EXAM" -> Triple(MaterialTheme.colorScheme.secondaryContainer, "Pekan Ujian", MaterialTheme.colorScheme.secondary)
        "HOLIDAY" -> Triple(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.6f), "Libur Sekolah", MaterialTheme.colorScheme.error)
        else -> Triple(MaterialTheme.colorScheme.tertiaryContainer, "Kegiatan Khusus", MaterialTheme.colorScheme.tertiary)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onEditClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(statusBg),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "W${week.weekNumber}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = statusColor
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${week.weekLabel} • ${week.monthName}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = statusBg
                        ) {
                            Text(
                                text = statusText,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = statusColor,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Text(
                        text = week.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }

            IconButton(onClick = onEditClick) {
                Icon(Icons.Default.Edit, contentDescription = "Edit Pekan", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
            }
        }
    }
}
