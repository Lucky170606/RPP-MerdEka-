package com.example.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ai.AdvancedCurriculumEngine
import com.example.data.model.Fase
import com.example.data.model.KurikulumMerdekaReferenceData
import com.example.data.model.PromesDocument
import com.example.data.model.ProtaDocument
import com.example.data.model.TeacherProfile
import com.example.ui.theme.*
import com.example.util.DocumentExporter
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.viewmodel.ModulViewModel
import com.example.data.local.ProtaEntity
import com.example.data.local.PromesEntity
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProtaPromesScreen(
    viewModel: ModulViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val profile = remember { TeacherProfile.loadFromPreferences(context) }
    val coroutineScope = rememberCoroutineScope()

    val savedProtaList by viewModel.allProta.collectAsStateWithLifecycle(initialValue = emptyList())
    val savedPromesList by viewModel.allPromes.collectAsStateWithLifecycle(initialValue = emptyList())

    var showSavedDialog by remember { mutableStateOf(false) }

    var selectedTab by remember { mutableIntStateOf(0) } // 0: PROTA, 1: PROMES

    var selectedSubject by remember { mutableStateOf("Matematika") }
    var selectedFase by remember { mutableStateOf(Fase.FASE_B) }
    var selectedGrade by remember { mutableStateOf("Kelas 4") }
    var academicYear by remember { mutableStateOf(profile.defaultAcademicYear.ifBlank { "2024/2025" }) }
    var selectedSemester by remember { mutableStateOf("Semester 1 (Ganjil)") }

    var protaDoc by remember {
        mutableStateOf(
            AdvancedCurriculumEngine.generateProta(
                selectedSubject,
                selectedFase.code,
                selectedGrade,
                academicYear
            )
        )
    }

    var promesDoc by remember {
        mutableStateOf(
            AdvancedCurriculumEngine.generatePromes(
                selectedSubject,
                selectedFase.code,
                selectedGrade,
                selectedSemester,
                academicYear
            )
        )
    }

    fun recalculate() {
        protaDoc = AdvancedCurriculumEngine.generateProta(
            selectedSubject,
            selectedFase.code,
            selectedGrade,
            academicYear
        )
        promesDoc = AdvancedCurriculumEngine.generatePromes(
            selectedSubject,
            selectedFase.code,
            selectedGrade,
            selectedSemester,
            academicYear
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Program Tahunan & Semester",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            "Prota & Promes Kurikulum Merdeka",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                if (selectedTab == 0) {
                                    viewModel.saveProta(protaDoc)
                                    Toast.makeText(context, "Prota berhasil disimpan ke database!", Toast.LENGTH_SHORT).show()
                                } else {
                                    viewModel.savePromes(promesDoc)
                                    Toast.makeText(context, "Promes berhasil disimpan ke database!", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        modifier = Modifier.testTag("btn_save_to_db")
                    ) {
                        Icon(Icons.Default.Storage, contentDescription = "Simpan ke Database")
                    }
                    IconButton(
                        onClick = { showSavedDialog = true },
                        modifier = Modifier.testTag("btn_show_saved")
                    ) {
                        Icon(Icons.Default.FolderSpecial, contentDescription = "Daftar Tersimpan")
                    }
                    IconButton(
                        onClick = {
                            if (selectedTab == 0) {
                                DocumentExporter.printOrSaveProtaPdf(context, protaDoc)
                            } else {
                                DocumentExporter.printOrSavePromesPdf(context, promesDoc)
                            }
                        },
                        modifier = Modifier.testTag("prota_print_btn")
                    ) {
                        Icon(Icons.Default.Print, contentDescription = "Cetak PDF")
                    }
                    IconButton(
                        onClick = {
                            val uri = if (selectedTab == 0) {
                                DocumentExporter.exportProtaToWord(context, protaDoc)
                            } else {
                                DocumentExporter.exportPromesToWord(context, promesDoc)
                            }
                            if (uri != null) {
                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = "application/msword"
                                    putExtra(Intent.EXTRA_STREAM, uri)
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }
                                context.startActivity(Intent.createChooser(intent, "Bagikan Dokumen Word"))
                            } else {
                                Toast.makeText(context, "Gagal mengekspor berkas Word", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.testTag("prota_word_btn")
                    ) {
                        Icon(Icons.Default.Description, contentDescription = "Ekspor Word")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (showSavedDialog) {
            AlertDialog(
                onDismissRequest = { showSavedDialog = false },
                title = { Text(if (selectedTab == 0) "Daftar Prota Tersimpan" else "Daftar Promes Tersimpan", fontWeight = FontWeight.Bold) },
                text = {
                    Box(modifier = Modifier.heightIn(max = 300.dp).fillMaxWidth()) {
                        if (selectedTab == 0) {
                            if (savedProtaList.isEmpty()) {
                                Text("Belum ada Prota yang disimpan ke database.", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            } else {
                                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    items(savedProtaList) { item ->
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(8.dp),
                                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                        ) {
                                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                                Text(item.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                Text("${item.subject} • ${item.grade} • ${item.academicYear}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)) {
                                                    TextButton(onClick = {
                                                        viewModel.parseProtaJson(item.contentJson)?.let {
                                                            protaDoc = it
                                                            selectedSubject = it.subject
                                                            selectedGrade = it.grade
                                                            academicYear = it.academicYear
                                                            Toast.makeText(context, "Prota dimuat!", Toast.LENGTH_SHORT).show()
                                                        }
                                                        showSavedDialog = false
                                                    }) {
                                                        Text("Muat", fontSize = 12.sp)
                                                    }
                                                    TextButton(onClick = {
                                                        coroutineScope.launch {
                                                            viewModel.deleteProta(item.id)
                                                            Toast.makeText(context, "Berhasil dihapus", Toast.LENGTH_SHORT).show()
                                                        }
                                                    }) {
                                                        Text("Hapus", fontSize = 12.sp, color = MaterialTheme.colorScheme.error)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            if (savedPromesList.isEmpty()) {
                                Text("Belum ada Promes yang disimpan ke database.", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            } else {
                                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    items(savedPromesList) { item ->
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(8.dp),
                                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                        ) {
                                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                                Text(item.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                Text("${item.subject} • ${item.grade} • ${item.academicYear}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)) {
                                                    TextButton(onClick = {
                                                        viewModel.parsePromesJson(item.contentJson)?.let {
                                                            promesDoc = it
                                                            selectedSubject = it.subject
                                                            selectedGrade = it.grade
                                                            academicYear = it.academicYear
                                                            selectedSemester = it.semester
                                                            Toast.makeText(context, "Promes dimuat!", Toast.LENGTH_SHORT).show()
                                                        }
                                                        showSavedDialog = false
                                                    }) {
                                                        Text("Muat", fontSize = 12.sp)
                                                    }
                                                    TextButton(onClick = {
                                                        coroutineScope.launch {
                                                            viewModel.deletePromes(item.id)
                                                            Toast.makeText(context, "Berhasil dihapus", Toast.LENGTH_SHORT).show()
                                                        }
                                                    }) {
                                                        Text("Hapus", fontSize = 12.sp, color = MaterialTheme.colorScheme.error)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showSavedDialog = false }) {
                        Text("Tutup")
                    }
                }
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tab Selector
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("PROTA (Tahunan)", fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.DateRange, contentDescription = null) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("PROMES (Semester)", fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) }
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Control Card
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                "Parameter Pembelajaran",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontSize = 15.sp
                            )

                            // Subject Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                var subjectMenuExpanded by remember { mutableStateOf(false) }
                                OutlinedCard(
                                    onClick = { subjectMenuExpanded = true },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("Mata Pelajaran", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text(selectedSubject, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        }
                                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                    }
                                    DropdownMenu(
                                        expanded = subjectMenuExpanded,
                                        onDismissRequest = { subjectMenuExpanded = false }
                                    ) {
                                        KurikulumMerdekaReferenceData.MATA_PELAJARAN_LIST.forEach { mapel ->
                                            DropdownMenuItem(
                                                text = { Text(mapel) },
                                                onClick = {
                                                    selectedSubject = mapel
                                                    subjectMenuExpanded = false
                                                    recalculate()
                                                }
                                            )
                                        }
                                    }
                                }

                                var faseMenuExpanded by remember { mutableStateOf(false) }
                                OutlinedCard(
                                    onClick = { faseMenuExpanded = true },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("Fase & Kelas", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text("${selectedFase.code} ($selectedGrade)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        }
                                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                    }
                                    DropdownMenu(
                                        expanded = faseMenuExpanded,
                                        onDismissRequest = { faseMenuExpanded = false }
                                    ) {
                                        Fase.values().forEach { f ->
                                            DropdownMenuItem(
                                                text = { Text(f.label) },
                                                onClick = {
                                                    selectedFase = f
                                                    selectedGrade = f.grades.firstOrNull() ?: "Kelas 1"
                                                    faseMenuExpanded = false
                                                    recalculate()
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            if (selectedTab == 1) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    FilterChip(
                                        selected = selectedSemester.contains("1"),
                                        onClick = {
                                            selectedSemester = "Semester 1 (Ganjil)"
                                            recalculate()
                                        },
                                        label = { Text("Semester 1 (Juli - Des)") },
                                        modifier = Modifier.weight(1f)
                                    )
                                    FilterChip(
                                        selected = selectedSemester.contains("2"),
                                        onClick = {
                                            selectedSemester = "Semester 2 (Genap)"
                                            recalculate()
                                        },
                                        label = { Text("Semester 2 (Jan - Juni)") },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Total Beban Jam Pelajaran: ${if (selectedTab == 0) protaDoc.totalJp else promesDoc.totalJp} JP",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontSize = 13.sp
                                )
                                Button(
                                    onClick = { recalculate() },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary
                                    ),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text("Perbarui Tabel", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                // Document View
                if (selectedTab == 0) {
                    // PROTA View
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    "Daftar Alokasi PROTA (Tahun Ajaran $academicYear)",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 15.sp
                                )
                                Spacer(Modifier.height(12.dp))

                                protaDoc.items.forEach { item ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                                        ),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Surface(
                                                    color = if (item.semester.contains("1")) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer,
                                                    shape = RoundedCornerShape(4.dp)
                                                ) {
                                                    Text(
                                                        item.semester,
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (item.semester.contains("1")) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSecondaryContainer,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                                Text(
                                                    "${item.alokasiJp} JP",
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.primary,
                                                    fontSize = 13.sp
                                                )
                                            }
                                            Spacer(Modifier.height(6.dp))
                                            Text(
                                                "${item.nomor}. ${item.babMateri}",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Spacer(Modifier.height(4.dp))
                                            Text(
                                                item.capaianTujuan,
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                lineHeight = 16.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // PROMES Matrix View
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    "Distribusi Matriks Mingguan PROMES ($selectedSemester)",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 15.sp
                                )
                                Text(
                                    "Geser tabel horizontal untuk melihat distribusi bulan",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(Modifier.height(12.dp))

                                val scrollState = rememberScrollState()
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(scrollState)
                                ) {
                                    Column {
                                        // Header Row
                                        Row(
                                            modifier = Modifier
                                                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(4.dp))
                                                .padding(8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("Materi Pokok", fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.width(180.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text("JP", fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.width(40.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            promesDoc.items.firstOrNull()?.weeklyDistribution?.forEach { month ->
                                                Text(
                                                    month.bulan,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 12.sp,
                                                    modifier = Modifier.width(100.dp),
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }

                                        // Items Rows
                                        promesDoc.items.forEach { pItem ->
                                            Row(
                                                modifier = Modifier
                                                    .padding(vertical = 6.dp)
                                                    .border(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                                                    .padding(8.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    "${pItem.nomor}. ${pItem.materiPokok}",
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    modifier = Modifier.width(180.dp),
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                Text(
                                                    "${pItem.alokasiJp}",
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.primary,
                                                    fontSize = 12.sp,
                                                    modifier = Modifier.width(40.dp)
                                                )
                                                pItem.weeklyDistribution.forEach { mDist ->
                                                    val weeksStr = mDist.weeks.joinToString("-") { if (it > 0) "$it" else "0" }
                                                    Surface(
                                                        color = if (mDist.weeks.any { it > 0 }) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                                                        shape = RoundedCornerShape(4.dp),
                                                        modifier = Modifier.width(100.dp)
                                                    ) {
                                                        Text(
                                                            "[$weeksStr]",
                                                            fontSize = 11.sp,
                                                            color = if (mDist.weeks.any { it > 0 }) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                                            modifier = Modifier.padding(4.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
