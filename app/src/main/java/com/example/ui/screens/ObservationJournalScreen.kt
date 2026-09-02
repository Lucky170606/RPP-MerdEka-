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
import com.example.data.model.JurnalObservasiItem
import com.example.data.model.KurikulumMerdekaReferenceData
import com.example.ui.theme.*
import com.example.util.DocumentExporter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ObservationJournalScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val todayDate = remember { SimpleDateFormat("dd/MM/yyyy", Locale("id", "ID")).format(Date()) }

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Jurnal Harian, 1: Penilaian Antarteman

    var jurnalList by remember {
        mutableStateOf(
            listOf(
                JurnalObservasiItem(
                    tanggal = todayDate,
                    namaSiswa = "Muhammad Rizky",
                    dimensiP3 = "Gotong Royong",
                    catatanPerilaku = "Secara sukarela membantu anggota kelompok lain yang mengalami kesulitan merakit model paru-paru buatan saat eksperimen IPAS.",
                    butirSikapPositifNegatif = "Positif (+)",
                    rencanaTindakLanjut = "Diberikan apresiasi di depan kelas dan dijadikan ketua tutor sebaya."
                ),
                JurnalObservasiItem(
                    tanggal = todayDate,
                    namaSiswa = "Siti Aisyah",
                    dimensiP3 = "Bernalar Kritis",
                    catatanPerilaku = "Mengajukan pertanyaan pembanding yang mendalam terkait dampak pencemaran air terhadap ekosistem sawah di desanya.",
                    butirSikapPositifNegatif = "Positif (+)",
                    rencanaTindakLanjut = "Diberikan pengayaan tugas investigasi mandiri lanjutan."
                ),
                JurnalObservasiItem(
                    tanggal = todayDate,
                    namaSiswa = "Budi Santoso",
                    dimensiP3 = "Mandiri",
                    catatanPerilaku = "Kurang fokus saat pengerjaan LKPD mandiri dan sering berpindah tempat mengganggu konsentrasi teman sebangku.",
                    butirSikapPositifNegatif = "Perlu Pembinaan (-)",
                    rencanaTindakLanjut = "Bimbingan reflektif personal setelah jam pelajaran selesai mengenai manajemen waktu."
                )
            )
        )
    }

    val peerQuestions = remember { AdvancedCurriculumEngine.DEFAULT_PEER_QUESTIONS }

    var showAddDialog by remember { mutableStateOf(false) }
    var inputNamaSiswa by remember { mutableStateOf("") }
    var inputDimensi by remember { mutableStateOf("Bergotong Royong") }
    var inputPerilaku by remember { mutableStateOf("") }
    var inputSikapPositif by remember { mutableStateOf(true) }
    var inputTindakLanjut by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Jurnal Observasi & Antarteman",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            "Instrumen Sikap Profil Pelajar Pancasila",
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
                            DocumentExporter.printOrSaveObservationPdf(context, jurnalList, peerQuestions)
                        },
                        modifier = Modifier.testTag("obs_print_btn")
                    ) {
                        Icon(Icons.Default.Print, contentDescription = "Cetak PDF")
                    }
                    IconButton(
                        onClick = {
                            val uri = DocumentExporter.exportObservationToWord(context, jurnalList, peerQuestions)
                            if (uri != null) {
                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = "application/msword"
                                    putExtra(Intent.EXTRA_STREAM, uri)
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }
                                context.startActivity(Intent.createChooser(intent, "Bagikan Lembar Observasi"))
                            } else {
                                Toast.makeText(context, "Gagal mengekspor berkas Word", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.testTag("obs_word_btn")
                    ) {
                        Icon(Icons.Default.Description, contentDescription = "Ekspor Word")
                    }
                }
            )
        },
        floatingActionButton = {
            if (selectedTab == 0) {
                ExtendedFloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("Catat Kejadian") },
                    modifier = Modifier.testTag("add_journal_fab")
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .imePadding()
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Jurnal Guru", fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.EditNote, contentDescription = null) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Penilaian Antarteman", fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.People, contentDescription = null) }
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                if (selectedTab == 0) {
                    // Jurnal List
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Info,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                Spacer(Modifier.width(10.dp))
                                Text(
                                    "Jurnal observasi harian mencatat perilaku menonjol (positif maupun butuh bimbingan) siswa selama proses KBM untuk asesmen autentik P3.",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }

                    itemsIndexed(jurnalList) { index, item ->
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
                                        "${index + 1}. ${item.namaSiswa}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    val isPositif = item.butirSikapPositifNegatif.contains("+")
                                    Surface(
                                        color = if (isPositif) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer,
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            item.butirSikapPositifNegatif,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isPositif) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }

                                Spacer(Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        "Dimensi P3: ${item.dimensiP3}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        item.tanggal,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Spacer(Modifier.height(8.dp))
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(
                                            "Catatan Kejadian:",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            item.catatanPerilaku,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            lineHeight = 16.sp
                                        )
                                        Spacer(Modifier.height(6.dp))
                                        Text(
                                            "Tindak Lanjut:",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.secondary
                                        )
                                        Text(
                                            item.rencanaTindakLanjut,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Spacer(Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    IconButton(
                                        onClick = {
                                            jurnalList = jurnalList.toMutableList().also { it.removeAt(index) }
                                        }
                                    ) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Hapus",
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
                } else {
                    // Peer Assessment Preview
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    "Lembar Penilaian Antarteman (Siap Cetak)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    "Digunakan peserta didik untuk saling mengevaluasi kontribusi teman dalam kerja kelompok berbasis skala Likert 1-4.",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    itemsIndexed(peerQuestions) { _, item ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        "Butir #${item.no}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Surface(
                                        color = MaterialTheme.colorScheme.secondaryContainer,
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            item.dimensiTerkait,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    item.pernyataan,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    listOf("1 (Tidak Pernah)", "2 (Kadang-kadang)", "3 (Sering)", "4 (Selalu)").forEach { opt ->
                                        Surface(
                                            color = MaterialTheme.colorScheme.surfaceVariant,
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                opt,
                                                fontSize = 9.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Button(
                            onClick = {
                                DocumentExporter.printOrSaveObservationPdf(context, jurnalList, peerQuestions)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Icon(Icons.Default.Print, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Cetak / Simpan Lembar PDF Siap Bagi")
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Catat Observasi Sikap Siswa", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = inputNamaSiswa,
                        onValueChange = { inputNamaSiswa = it },
                        label = { Text("Nama Siswa") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    var dimensiExpanded by remember { mutableStateOf(false) }
                    OutlinedCard(
                        onClick = { dimensiExpanded = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Dimensi P3", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(inputDimensi, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }
                        DropdownMenu(
                            expanded = dimensiExpanded,
                            onDismissRequest = { dimensiExpanded = false }
                        ) {
                            KurikulumMerdekaReferenceData.PROFIL_PELAJAR_PANCASILA.forEach { dim ->
                                DropdownMenuItem(
                                    text = { Text(dim.title) },
                                    onClick = {
                                        inputDimensi = dim.title
                                        dimensiExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = inputSikapPositif,
                            onClick = { inputSikapPositif = true },
                            label = { Text("Positif (+)") },
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = !inputSikapPositif,
                            onClick = { inputSikapPositif = false },
                            label = { Text("Perlu Bimbingan (-)") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    OutlinedTextField(
                        value = inputPerilaku,
                        onValueChange = { inputPerilaku = it },
                        label = { Text("Catatan Perilaku Kejadian") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = inputTindakLanjut,
                        onValueChange = { inputTindakLanjut = it },
                        label = { Text("Rencana Tindak Lanjut / Apresiasi") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        jurnalList = jurnalList + JurnalObservasiItem(
                            tanggal = todayDate,
                            namaSiswa = inputNamaSiswa.ifBlank { "Siswa" },
                            dimensiP3 = inputDimensi,
                            catatanPerilaku = inputPerilaku.ifBlank { "Menunjukkan sikap yang relevan selama KBM." },
                            butirSikapPositifNegatif = if (inputSikapPositif) "Positif (+)" else "Perlu Pembinaan (-)",
                            rencanaTindakLanjut = inputTindakLanjut.ifBlank { "Diberikan motivasi dan arahan." }
                        )
                        inputNamaSiswa = ""
                        inputPerilaku = ""
                        inputTindakLanjut = ""
                        showAddDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text("Simpan ke Jurnal")
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
