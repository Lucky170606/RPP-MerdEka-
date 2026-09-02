package com.example.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextOverflow
import com.example.data.ai.AdvancedCurriculumEngine
import com.example.data.local.AtpEntity
import com.example.data.model.AtpDocument
import com.example.data.model.Fase
import com.example.data.model.KurikulumMerdekaReferenceData
import com.example.ui.theme.*
import com.example.util.DocumentExporter
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.viewmodel.ModulViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AtpScreen(
    viewModel: ModulViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val savedAtpList by viewModel.allAtp.collectAsStateWithLifecycle(initialValue = emptyList())
    var showSavedDialog by remember { mutableStateOf(false) }
    var showSubjectDialog by remember { mutableStateOf(false) }
    var showFaseDialog by remember { mutableStateOf(false) }

    var selectedSubject by remember { mutableStateOf("IPAS") }
    var selectedFase by remember { mutableStateOf(Fase.FASE_B) }
    var selectedGrade by remember { mutableStateOf("Kelas 4") }

    var atpDoc by remember {
        mutableStateOf(
            AdvancedCurriculumEngine.generateAtp(
                selectedSubject,
                selectedFase.code,
                selectedGrade
            )
        )
    }

    fun recalculate() {
        android.util.Log.d("AtpScreen", "recalculate called: sub=$selectedSubject, fase=${selectedFase.code}, grade=$selectedGrade")
        atpDoc = AdvancedCurriculumEngine.generateAtp(
            selectedSubject,
            selectedFase.code,
            selectedGrade
        )
        android.util.Log.d("AtpScreen", "recalculate done: totalJp=${atpDoc.totalJp}, listSize=${atpDoc.alurTujuanList.size}")
    }

    LaunchedEffect(selectedSubject, selectedFase.code, selectedGrade) {
        recalculate()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "ATP",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            "Pemetaan Elemen CP",
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
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
                                viewModel.saveAtp(atpDoc)
                                Toast.makeText(context, "ATP berhasil disimpan ke database!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.testTag("btn_save_atp_db")
                    ) {
                        Icon(Icons.Default.Storage, contentDescription = "Simpan ke Database")
                    }
                    IconButton(
                        onClick = { showSavedDialog = true },
                        modifier = Modifier.testTag("btn_show_saved_atp")
                    ) {
                        Icon(Icons.Default.FolderSpecial, contentDescription = "Daftar Tersimpan")
                    }
                    IconButton(
                        onClick = {
                            DocumentExporter.printOrSaveAtpPdf(context, atpDoc)
                        },
                        modifier = Modifier.testTag("atp_print_btn")
                    ) {
                        Icon(Icons.Default.Print, contentDescription = "Cetak PDF")
                    }
                    IconButton(
                        onClick = {
                            val uri = DocumentExporter.exportAtpToWord(context, atpDoc)
                            if (uri != null) {
                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = "application/msword"
                                    putExtra(Intent.EXTRA_STREAM, uri)
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }
                                context.startActivity(Intent.createChooser(intent, "Bagikan ATP Word"))
                            } else {
                                Toast.makeText(context, "Gagal mengekspor berkas Word", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.testTag("atp_word_btn")
                    ) {
                        Icon(Icons.Default.Description, contentDescription = "Ekspor Word")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (showSubjectDialog) {
        AlertDialog(
            onDismissRequest = { showSubjectDialog = false },
            title = { Text("Pilih Mata Pelajaran", fontWeight = FontWeight.Bold) },
            text = {
                Box(modifier = Modifier.heightIn(max = 350.dp).fillMaxWidth()) {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        val jenjang = com.example.data.model.CurriculumConstants.getJenjangByFase(selectedFase.code)
                        val filteredMapel = com.example.data.model.CurriculumConstants.MATA_PELAJARAN_MAP[jenjang] ?: emptyList()
                        
                        items(filteredMapel) { mapel ->
                            TextButton(
                                onClick = {
                                    selectedSubject = mapel
                                    showSubjectDialog = false
                                    // Explicitly trigger recalculate after state updates
                                    recalculate()
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    mapel,
                                    modifier = Modifier.fillMaxWidth(),
                                    fontWeight = if (selectedSubject == mapel) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedSubject == mapel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSubjectDialog = false }) { Text("Tutup") }
            }
        )
    }

    if (showFaseDialog) {
        AlertDialog(
            onDismissRequest = { showFaseDialog = false },
            title = { Text("Pilih Fase & Kelas", fontWeight = FontWeight.Bold) },
            text = {
                Box(modifier = Modifier.heightIn(max = 350.dp).fillMaxWidth()) {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        items(Fase.values()) { f ->
                            TextButton(
                                onClick = {
                                    selectedFase = f
                                    selectedGrade = f.grades.firstOrNull() ?: "Kelas 1"
                                    showFaseDialog = false
                                    // Explicitly trigger recalculate after state updates
                                    recalculate()
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    f.label,
                                    modifier = Modifier.fillMaxWidth(),
                                    fontWeight = if (selectedFase == f) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedFase == f) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showFaseDialog = false }) { Text("Tutup") }
            }
        )
    }

    if (showSavedDialog) {
            AlertDialog(
                onDismissRequest = { showSavedDialog = false },
                title = { Text("Daftar ATP Tersimpan", fontWeight = FontWeight.Bold) },
                text = {
                    Box(modifier = Modifier.heightIn(max = 300.dp).fillMaxWidth()) {
                        if (savedAtpList.isEmpty()) {
                            Text("Belum ada ATP yang disimpan ke database.", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        } else {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(savedAtpList) { item ->
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                    ) {
                                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Text(item.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            Text("${item.subject} • ${item.grade}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)) {
                                                TextButton(onClick = {
                                                    viewModel.parseAtpJson(item.contentJson)?.let {
                                                        atpDoc = it
                                                        selectedSubject = it.subject
                                                        selectedGrade = it.grade
                                                        Toast.makeText(context, "ATP dimuat!", Toast.LENGTH_SHORT).show()
                                                    }
                                                    showSavedDialog = false
                                                }) {
                                                    Text("Muat", fontSize = 12.sp)
                                                }
                                                TextButton(onClick = {
                                                    coroutineScope.launch {
                                                        viewModel.deleteAtp(item.id)
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
                },
                confirmButton = {
                    TextButton(onClick = { showSavedDialog = false }) {
                        Text("Tutup")
                    }
                }
            )
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .imePadding()
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
                            "Parameter Alur Pembelajaran (ATP)",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontSize = 15.sp
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedCard(
                                onClick = { showSubjectDialog = true },
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Mata Pelajaran", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(selectedSubject, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                }
                            }

                            OutlinedCard(
                                onClick = { showFaseDialog = true },
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Fase & Kelas", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("${selectedFase.code} ($selectedGrade)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Total: ${atpDoc.totalJp} JP (${atpDoc.alurTujuanList.size} Alur TP)",
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
                                Text("Muat Ulang ATP", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Rasional & Karakteristik
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            "A. Rasional & Karakteristik Mata Pelajaran",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 14.sp
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            atpDoc.rasional,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 17.sp
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Karakteristik: ${atpDoc.karakteristikMataPelajaran}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 17.sp
                        )
                    }
                }
            }

            // Alur Steps
            item {
                Text(
                    "B. Urutan Alur Tujuan Pembelajaran (ATP)",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 15.sp
                )
            }

            items(atpDoc.alurTujuanList) { step ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(12.dp)
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
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    "Alur ${step.nomorUrut} • Elemen: ${step.elemen}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Text(
                                "${step.alokasiJp} JP",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 13.sp
                            )
                        }

                        Spacer(Modifier.height(8.dp))
                        Text(
                            step.tujuanPembelajaran,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(Modifier.height(6.dp))
                        Text(
                            "Lingkup Materi: ${step.materiPokok}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(Modifier.height(6.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(
                                    "Dimensi P3: ${step.profilPancasila}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    "Indikator Ketercapaian: ${step.indikatorKetercapaian}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(Modifier.height(10.dp))
                        Button(
                            onClick = {
                                viewModel.prepareWizardForTopic(
                                    subject = selectedSubject,
                                    fase = selectedFase,
                                    grade = selectedGrade,
                                    topic = step.materiPokok.ifBlank { step.tujuanPembelajaran }
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Buat Modul Ajar / RPP dari Materi Ini", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
