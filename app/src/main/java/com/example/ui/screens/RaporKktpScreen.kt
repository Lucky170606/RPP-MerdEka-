package com.example.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ai.AdvancedCurriculumEngine
import com.example.data.model.KurikulumMerdekaReferenceData
import com.example.data.model.StudentRaporEntry
import com.example.ui.theme.*
import com.example.util.DocumentExporter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RaporKktpScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current

    var selectedSubject by remember { mutableStateOf("Matematika") }
    var selectedGrade by remember { mutableStateOf("Kelas 4 SD") }

    val intervals = remember { AdvancedCurriculumEngine.DEFAULT_KKTP_INTERVALS }

    var studentList by remember {
        mutableStateOf(
            listOf(
                StudentRaporEntry(
                    namaSiswa = "Ahmad Fauzi",
                    nilaiAkhir = 92,
                    materiTinggi = "operasi hitung pecahan desimal dan persen",
                    materiRendah = "pengukuran luas bangun datar gabungan",
                    deskripsiCapaian = AdvancedCurriculumEngine.generateRaporSentence(
                        "Ahmad Fauzi", 92,
                        "operasi hitung pecahan desimal dan persen",
                        "pengukuran luas bangun datar gabungan"
                    )
                ),
                StudentRaporEntry(
                    namaSiswa = "Bunga Citra Lestari",
                    nilaiAkhir = 82,
                    materiTinggi = "penyajian dan analisis data diagram batang",
                    materiRendah = "keliling bangun datar segitiga",
                    deskripsiCapaian = AdvancedCurriculumEngine.generateRaporSentence(
                        "Bunga Citra Lestari", 82,
                        "penyajian dan analisis data diagram batang",
                        "keliling bangun datar segitiga"
                    )
                ),
                StudentRaporEntry(
                    namaSiswa = "Dimas Dananjaya",
                    nilaiAkhir = 70,
                    materiTinggi = "bilangan cacah sampai 10.000",
                    materiRendah = "pemecahan masalah pecahan senilai",
                    deskripsiCapaian = AdvancedCurriculumEngine.generateRaporSentence(
                        "Dimas Dananjaya", 70,
                        "bilangan cacah sampai 10.000",
                        "pemecahan masalah pecahan senilai"
                    )
                ),
                StudentRaporEntry(
                    namaSiswa = "Eka Putri Maharani",
                    nilaiAkhir = 95,
                    materiTinggi = "penalaran matematis dan pemodelan aljabar sederhana",
                    materiRendah = "estimasi pembulatan bilangan cacah",
                    deskripsiCapaian = AdvancedCurriculumEngine.generateRaporSentence(
                        "Eka Putri Maharani", 95,
                        "penalaran matematis dan pemodelan aljabar sederhana",
                        "estimasi pembulatan bilangan cacah"
                    )
                )
            )
        )
    }

    var showAddDialog by remember { mutableStateOf(false) }
    var newStudentName by remember { mutableStateOf("") }
    var newStudentScore by remember { mutableStateOf("80") }
    var newMateriTinggi by remember { mutableStateOf("pemahaman konsep inti") }
    var newMateriRendah by remember { mutableStateOf("penerapan studi kasus lanjutan") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "KKTP & Deskripsi Nilai Rapor",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            "Generator Kalimat e-Rapor Kurikulum Merdeka",
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
                            DocumentExporter.printOrSaveRaporPdf(
                                context,
                                selectedSubject,
                                selectedGrade,
                                studentList,
                                intervals
                            )
                        },
                        modifier = Modifier.testTag("rapor_print_btn")
                    ) {
                        Icon(Icons.Default.Print, contentDescription = "Cetak PDF")
                    }
                    IconButton(
                        onClick = {
                            val uri = DocumentExporter.exportRaporToWord(
                                context,
                                selectedSubject,
                                selectedGrade,
                                studentList,
                                intervals
                            )
                            if (uri != null) {
                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = "application/msword"
                                    putExtra(Intent.EXTRA_STREAM, uri)
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }
                                context.startActivity(Intent.createChooser(intent, "Bagikan Deskripsi Rapor"))
                            } else {
                                Toast.makeText(context, "Gagal mengekspor berkas Word", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.testTag("rapor_word_btn")
                    ) {
                        Icon(Icons.Default.Description, contentDescription = "Ekspor Word")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Tambah Siswa") },
                modifier = Modifier.testTag("add_student_fab")
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .imePadding()
                .padding(16.dp),
            contentPadding = PaddingValues(bottom = 72.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Settings Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            "Pengaturan Mata Pelajaran & Kelas",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontSize = 15.sp
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            var mapelExpanded by remember { mutableStateOf(false) }
                            OutlinedCard(
                                onClick = { mapelExpanded = true },
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Mata Pelajaran", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(selectedSubject, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                }
                                DropdownMenu(
                                    expanded = mapelExpanded,
                                    onDismissRequest = { mapelExpanded = false }
                                ) {
                                    KurikulumMerdekaReferenceData.MATA_PELAJARAN_LIST.forEach { m ->
                                        DropdownMenuItem(
                                            text = { Text(m) },
                                            onClick = {
                                                selectedSubject = m
                                                mapelExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            OutlinedTextField(
                                value = selectedGrade,
                                onValueChange = { selectedGrade = it },
                                label = { Text("Kelas") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }
                    }
                }
            }

            // KKTP Intervals Accordion
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            "Pedoman Interval Kriteria Ketercapaian (KKTP)",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 14.sp
                        )
                        Spacer(Modifier.height(8.dp))

                        intervals.forEach { inv ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = when {
                                        inv.rentang.contains("89") -> MaterialTheme.colorScheme.primaryContainer
                                        inv.rentang.contains("76") -> MaterialTheme.colorScheme.secondaryContainer
                                        inv.rentang.contains("66") -> MaterialTheme.colorScheme.tertiaryContainer
                                        else -> MaterialTheme.colorScheme.surfaceVariant
                                    },
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        inv.rentang,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = when {
                                            inv.rentang.contains("89") -> MaterialTheme.colorScheme.onPrimaryContainer
                                            inv.rentang.contains("76") -> MaterialTheme.colorScheme.onSecondaryContainer
                                            inv.rentang.contains("66") -> MaterialTheme.colorScheme.onTertiaryContainer
                                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                                        },
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        inv.predikat,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        inv.tindakLanjut,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Student List Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Daftar Deskripsi Rapor Siswa (${studentList.size})",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 15.sp
                    )
                }
            }

            // Students items
            itemsIndexed(studentList) { index, student ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "${index + 1}. ${student.namaSiswa}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            val isTuntas = student.nilaiAkhir >= 76
                            Surface(
                                color = if (isTuntas) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.tertiaryContainer,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    "Nilai: ${student.nilaiAkhir}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isTuntas) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onTertiaryContainer,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(Modifier.height(8.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    "Deskripsi Capaian e-Rapor:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    student.deskripsiCapaian,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 16.sp
                                )
                            }
                        }

                        Spacer(Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            IconButton(
                                onClick = {
                                    studentList = studentList.toMutableList().also { it.removeAt(index) }
                                }
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Hapus Siswa",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(Modifier.height(60.dp))
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Tambah Siswa & Rapor", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newStudentName,
                        onValueChange = { newStudentName = it },
                        label = { Text("Nama Lengkap Siswa") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newStudentScore,
                        onValueChange = { newStudentScore = it },
                        label = { Text("Nilai Akhir (0 - 100)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newMateriTinggi,
                        onValueChange = { newMateriTinggi = it },
                        label = { Text("Materi Capaian Tertinggi") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newMateriRendah,
                        onValueChange = { newMateriRendah = it },
                        label = { Text("Materi Perlu Bimbingan") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val score = newStudentScore.toIntOrNull() ?: 75
                        val desc = AdvancedCurriculumEngine.generateRaporSentence(
                            newStudentName,
                            score,
                            newMateriTinggi,
                            newMateriRendah
                        )
                        studentList = studentList + StudentRaporEntry(
                            namaSiswa = newStudentName.ifBlank { "Siswa Baru" },
                            nilaiAkhir = score,
                            materiTinggi = newMateriTinggi,
                            materiRendah = newMateriRendah,
                            deskripsiCapaian = desc
                        )
                        newStudentName = ""
                        showAddDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text("Simpan & Susun Deskripsi")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}
