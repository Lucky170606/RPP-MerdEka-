package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.example.data.model.TeacherProfile
import com.example.ui.components.AppHeader
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*
import com.example.ui.viewmodel.ModulViewModel
import com.example.ui.viewmodel.Screen
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherProfileScreen(
    viewModel: ModulViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var profile by remember { mutableStateOf(TeacherProfile.loadFromPreferences(context)) }

    var teacherName by remember { mutableStateOf(profile.teacherName) }
    var teacherNip by remember { mutableStateOf(profile.teacherNip) }
    var schoolName by remember { mutableStateOf(profile.schoolName) }
    var npsn by remember { mutableStateOf(profile.npsn) }
    var principalName by remember { mutableStateOf(profile.principalName) }
    var principalNip by remember { mutableStateOf(profile.principalNip) }
    var cityAndDate by remember { mutableStateOf(profile.cityAndDate) }
    var defaultAcademicYear by remember { mutableStateOf(profile.defaultAcademicYear) }
    var defaultSemester by remember { mutableStateOf(profile.defaultSemester) }
    var selectedLayoutMode by remember { mutableStateOf(profile.printLayoutMode) } // STANDAR, RINGKAS

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Profil & Sekolah", "Pengaturan AI", "Format Cetak", "Cadangan & Pemulihan", "Panduan & Info")
    val pagerState = rememberPagerState(pageCount = { tabs.size })

    val coroutineScope = rememberCoroutineScope()
    var pendingExportJson by remember { mutableStateOf("") }

    // Sync tabs with pager
    LaunchedEffect(pagerState.currentPage) {
        selectedTab = pagerState.currentPage
    }

    val exportDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri: Uri? ->
        uri?.let {
            try {
                context.contentResolver.openOutputStream(it)?.use { outputStream ->
                    outputStream.write(pendingExportJson.toByteArray(Charsets.UTF_8))
                }
                Toast.makeText(context, "Cadangan berhasil disimpan ke memori HP!", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Gagal menyimpan file: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val importDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            try {
                context.contentResolver.openInputStream(it)?.use { inputStream ->
                    val jsonString = inputStream.bufferedReader().use { reader -> reader.readText() }
                    coroutineScope.launch {
                        val success = viewModel.performRestore(jsonString.trim())
                        if (success) {
                            Toast.makeText(context, "Berhasil memulihkan semua data dari memori HP!", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(context, "Format file JSON tidak valid atau gagal dipulihkan!", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Gagal membaca file: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val saveProfile = {
        val newProfile = TeacherProfile(
            teacherName = teacherName.trim(),
            teacherNip = teacherNip.trim(),
            schoolName = schoolName.trim(),
            npsn = npsn.trim(),
            principalName = principalName.trim(),
            principalNip = principalNip.trim(),
            cityAndDate = cityAndDate.trim(),
            defaultAcademicYear = defaultAcademicYear.trim(),
            defaultSemester = defaultSemester.trim(),
            printLayoutMode = selectedLayoutMode
        )
        TeacherProfile.saveToPreferences(context, newProfile)
        Toast.makeText(context, "Pengaturan berhasil disimpan!", Toast.LENGTH_SHORT).show()
        viewModel.navigateTo(Screen.Home)
    }

    Scaffold(
        topBar = {
            Column {
                AppHeader(
                    title = "Pengaturan & Profil",
                    subtitle = "Kelola Identitas, Format Cetak, dan Data Aplikasi",
                    showBackButton = true,
                    onBackClick = { viewModel.navigateTo(Screen.Home) }
                )
                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary,
                    edgePadding = 16.dp
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { 
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                                selectedTab = index 
                            },
                            text = { 
                                Text(
                                    title, 
                                    fontWeight = FontWeight.SemiBold, 
                                    fontSize = 13.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                ) 
                            },
                            icon = {
                                when (index) {
                                    0 -> Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(18.dp))
                                    1 -> Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                                    2 -> Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(18.dp))
                                    3 -> Icon(Icons.Default.Backup, contentDescription = null, modifier = Modifier.size(18.dp))
                                    else -> Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(18.dp))
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
                when (page) {
                    0 -> {
                        // TAB 0: Profil & Sekolah
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        SectionHeader(title = "Data Guru Penyusun", icon = Icons.Default.Person)

                                        OutlinedTextField(
                                            value = teacherName,
                                            onValueChange = { teacherName = it },
                                            label = { Text("Nama Lengkap & Gelar Guru") },
                                            modifier = Modifier.fillMaxWidth().testTag("input_profile_teacher_name"),
                                            singleLine = true
                                        )

                                        OutlinedTextField(
                                            value = teacherNip,
                                            onValueChange = { teacherNip = it },
                                            label = { Text("NIP Guru (Kosongkan jika non-PNS/PPPK)") },
                                            modifier = Modifier.fillMaxWidth(),
                                            singleLine = true
                                        )
                                    }
                                }
                            }

                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        SectionHeader(title = "Satuan Pendidikan & Kepala Sekolah", icon = Icons.Default.School)

                                        OutlinedTextField(
                                            value = schoolName,
                                            onValueChange = { schoolName = it },
                                            label = { Text("Nama Satuan Pendidikan / Sekolah") },
                                            modifier = Modifier.fillMaxWidth().testTag("input_profile_school_name"),
                                            singleLine = true
                                        )

                                        OutlinedTextField(
                                            value = npsn,
                                            onValueChange = { npsn = it },
                                            label = { Text("Nomor Pokok Sekolah Nasional (NPSN)") },
                                            modifier = Modifier.fillMaxWidth(),
                                            singleLine = true
                                        )

                                        OutlinedTextField(
                                            value = principalName,
                                            onValueChange = { principalName = it },
                                            label = { Text("Nama Lengkap & Gelar Kepala Sekolah") },
                                            modifier = Modifier.fillMaxWidth(),
                                            singleLine = true
                                        )

                                        OutlinedTextField(
                                            value = principalNip,
                                            onValueChange = { principalNip = it },
                                            label = { Text("NIP Kepala Sekolah") },
                                            modifier = Modifier.fillMaxWidth(),
                                            singleLine = true
                                        )

                                        OutlinedTextField(
                                            value = cityAndDate,
                                            onValueChange = { cityAndDate = it },
                                            label = { Text("Titik Mangsa Penetapan (Kota, Tanggal)") },
                                            placeholder = { Text("Contoh: Jakarta, 15 Juli 2024") },
                                            modifier = Modifier.fillMaxWidth(),
                                            singleLine = true
                                        )
                                    }
                                }
                            }

                            item {
                                Button(
                                    onClick = { saveProfile() },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                        .testTag("btn_save_profile_tab0"),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                ) {
                                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Simpan Perubahan", fontWeight = FontWeight.Bold)
                                }
                            }

                            item {
                                Spacer(modifier = Modifier.height(20.dp))
                            }
                        }
                    }

                    1 -> {
                        // TAB 1: Pengaturan AI
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        SectionHeader(title = "Konfigurasi Konsultan AI", icon = Icons.Default.AutoAwesome)
                                        Text("Kelola API Key Gemini Anda secara mandiri untuk menggunakan fitur konsultan AI. Pastikan Anda memiliki API Key yang valid dari Google AI Studio.", style = MaterialTheme.typography.bodySmall)
                                        Button(
                                            onClick = { viewModel.navigateTo(Screen.Settings) },
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text("Atur Gemini API Key")
                                        }
                                    }
                                }
                            }
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        SectionHeader(title = "Tutorial: Mendapatkan API Key", icon = Icons.Default.Help)
                                        Text("1. Kunjungi https://aistudio.google.com/.", style = MaterialTheme.typography.bodySmall)
                                        Text("2. Login dengan akun Google Anda.", style = MaterialTheme.typography.bodySmall)
                                        Text("3. Klik 'Get API Key' di menu samping.", style = MaterialTheme.typography.bodySmall)
                                        Text("4. Buat kunci baru, salin, lalu tempel di tombol 'Atur Gemini API Key' di atas.", style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }
                    }
                    2 -> {
                        // TAB 2: Format Cetak
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        SectionHeader(title = "Preferensi Format Dokumen Cetak", icon = Icons.Default.Print)

                                        Text(
                                            "Pilih mode susunan cetak Modul Ajar saat diekspor ke PDF / Word:",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )

                                         Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            FilterChip(
                                                selected = selectedLayoutMode == "STANDAR",
                                                onClick = { selectedLayoutMode = "STANDAR" },
                                                label = { Text("Format Standar / Lengkap") },
                                                leadingIcon = {
                                                    if (selectedLayoutMode == "STANDAR") Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                                }
                                            )

                                            FilterChip(
                                                selected = selectedLayoutMode == "RINGKAS",
                                                onClick = { selectedLayoutMode = "RINGKAS" },
                                                label = { Text("Format Ringkas") },
                                                leadingIcon = {
                                                    if (selectedLayoutMode == "RINGKAS") Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            item {
                                Button(
                                    onClick = { saveProfile() },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                        .testTag("btn_save_profile_tab1"),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                ) {
                                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Simpan Preferensi", fontWeight = FontWeight.Bold)
                                }
                            }

                            item {
                                Spacer(modifier = Modifier.height(20.dp))
                            }
                        }
                    }

                    3 -> {
                        // TAB 3: Cadangan & Pemulihan (Backup & Restore)
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(14.dp)
                                    ) {
                                        SectionHeader(title = "Penyimpanan Berkas ke Memori HP", icon = Icons.Default.FolderOpen)
                                        Text(
                                            "Simpan file cadangan (JSON) langsung ke folder pilihan Anda di memori HP, atau pilih file cadangan dari perangkat untuk memulihkan data.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            lineHeight = 18.sp
                                        )

                                        Button(
                                            onClick = {
                                                coroutineScope.launch {
                                                    val json = viewModel.getBackupJsonString()
                                                    if (json != null) {
                                                        pendingExportJson = json
                                                        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                                                        val defaultName = "backup_rpp_merdeka_$timeStamp.json"
                                                        exportDocumentLauncher.launch(defaultName)
                                                    } else {
                                                        Toast.makeText(context, "Gagal membuat backup", Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                            },
                                            modifier = Modifier.fillMaxWidth().height(50.dp).testTag("btn_save_to_device_folder"),
                                            shape = RoundedCornerShape(10.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                        ) {
                                            Icon(Icons.Default.SaveAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                                            Spacer(Modifier.width(8.dp))
                                            Text("Pilih Folder & Simpan ke Memori HP", fontWeight = FontWeight.Bold)
                                        }

                                        OutlinedButton(
                                            onClick = {
                                                importDocumentLauncher.launch(arrayOf("application/json", "*/*"))
                                            },
                                            modifier = Modifier.fillMaxWidth().height(50.dp).testTag("btn_load_from_device_folder"),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Icon(Icons.Default.FolderShared, contentDescription = null, modifier = Modifier.size(18.dp))
                                            Spacer(Modifier.width(8.dp))
                                            Text("Pilih Berkas dari Memori HP (Pulihkan)", fontWeight = FontWeight.SemiBold)
                                        }
                                    }
                                }
                            }


                            item {
                                Spacer(modifier = Modifier.height(20.dp))
                            }
                        }
                    }

                    4 -> {
                        // TAB 4: Panduan & Info
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        SectionHeader(title = "Tentang Pengembang & Aplikasi", icon = Icons.Default.Info)

                                        Text(
                                            text = "RPP Merdeka AI — Aplikasi penyusunan perangkat ajar, modul ajar, PROTA, PROMES, ATP, dan asesmen berbasis Kurikulum Merdeka yang dirancang khusus untuk mendukung produktivitas guru profesional di Indonesia.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            lineHeight = 20.sp
                                        )

                                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(
                                                    text = "Dikembangkan oleh",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                                Text(
                                                    text = "Eka Ilaika",
                                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                            }

                                            Surface(
                                                onClick = {
                                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://instagram.com/ekailaika"))
                                                    context.startActivity(intent)
                                                },
                                                shape = RoundedCornerShape(20.dp),
                                                color = MaterialTheme.colorScheme.primaryContainer,
                                                modifier = Modifier.testTag("btn_developer_instagram")
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Public,
                                                        contentDescription = null,
                                                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                    Text(
                                                        text = "@ekailaika",
                                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            item {
                                OutlinedButton(
                                    onClick = { viewModel.navigateTo(Screen.Onboarding) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp)
                                        .testTag("btn_reopen_onboarding"),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Buka Tur Pengenalan Aplikasi (Walkthrough)", fontWeight = FontWeight.SemiBold)
                                }
                            }

                            item {
                                Spacer(modifier = Modifier.height(20.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
