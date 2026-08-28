package com.example.ui.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Fase
import com.example.data.model.KurikulumMerdekaReferenceData
import com.example.ui.components.AppHeader
import com.example.ui.components.BadgeChip
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*
import com.example.ui.viewmodel.ModulViewModel
import com.example.ui.viewmodel.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReferenceDatabaseScreen(
    viewModel: ModulViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Bank CP, 1: Profil P3, 2: Model Belajar
    var selectedFaseFilter by remember { mutableStateOf<String>("Semua") }
    var dbSearchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            AppHeader(
                title = "Bank Data Kurikulum Merdeka",
                subtitle = "Standar Resmi BSKAP Kemendikbudristek",
                showBackButton = true,
                onBackClick = { viewModel.navigateTo(Screen.Home) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Tab Selector
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Capaian (CP)", fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.MenuBook, contentDescription = null) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Dimensi P3", fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.Diversity3, contentDescription = null) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Model Sintaks", fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.Psychology, contentDescription = null) }
                )
            }

            when (selectedTab) {
                0 -> {
                    // TAB 0: Capaian Pembelajaran Database
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            OutlinedTextField(
                                value = dbSearchQuery,
                                onValueChange = { dbSearchQuery = it },
                                placeholder = { Text("Cari materi, elemen, atau kata kunci CP...") },
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                singleLine = true
                            )
                        }

                        item {
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                item {
                                    FilterChip(
                                        selected = selectedFaseFilter == "Semua",
                                        onClick = { selectedFaseFilter = "Semua" },
                                        label = { Text("Semua Fase") },
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                }
                                items(Fase.values()) { f ->
                                    FilterChip(
                                        selected = selectedFaseFilter == f.code,
                                        onClick = { selectedFaseFilter = f.code },
                                        label = { Text(f.code) },
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                }
                            }
                        }

                        val filteredCPList = KurikulumMerdekaReferenceData.CP_DATABASE.filter { cp ->
                            val matchFase = selectedFaseFilter == "Semua" || cp.fase == selectedFaseFilter
                            val matchQuery = dbSearchQuery.isBlank() ||
                                    cp.subject.contains(dbSearchQuery, ignoreCase = true) ||
                                    cp.elemen.contains(dbSearchQuery, ignoreCase = true) ||
                                    cp.capaianText.contains(dbSearchQuery, ignoreCase = true)
                            matchFase && matchQuery
                        }

                        items(filteredCPList) { cp ->
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
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            BadgeChip(
                                                text = cp.fase,
                                                backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                                                textColor = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                            BadgeChip(
                                                text = cp.subject,
                                                backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                                                textColor = MaterialTheme.colorScheme.onSecondaryContainer
                                            )
                                        }
                                        BadgeChip(
                                            text = cp.elemen,
                                            backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                                            textColor = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Text(
                                        text = cp.capaianText,
                                        style = MaterialTheme.typography.bodySmall.copy(lineHeight = 20.sp),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )

                                    Button(
                                        onClick = {
                                            val foundFase = Fase.values().firstOrNull { it.code == cp.fase } ?: Fase.FASE_B
                                            viewModel.wizardFase.value = foundFase
                                            viewModel.wizardGrade.value = foundFase.grades.first()
                                            viewModel.wizardSubject.value = cp.subject
                                            viewModel.wizardTopic.value = "Penerapan ${cp.elemen}"
                                            viewModel.navigateTo(Screen.Wizard)
                                        },
                                        modifier = Modifier.align(Alignment.End),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.primary,
                                            contentColor = MaterialTheme.colorScheme.onPrimary
                                        )
                                    ) {
                                        Icon(Icons.Default.AutoFixHigh, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Gunakan untuk RPP AI", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // TAB 1: 6 Dimensi Profil Pelajar Pancasila
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text("6 Dimensi Profil Pelajar Pancasila", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                    Text(
                                        "Merupakan karakter dan kompetensi fondasi yang dibangun melalui kegiatan intrakurikuler, kokurikuler (P5), dan ekstrakurikuler.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        items(KurikulumMerdekaReferenceData.PROFIL_PELAJAR_PANCASILA) { dim ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                            ) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.primaryContainer),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                Icons.Default.Diversity3,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(dim.title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
                                    }

                                    Text(dim.description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

                                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

                                    Text("Elemen & Sub-Elemen Kunci:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                    dim.subElements.forEach { sub ->
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(sub, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // TAB 2: Model Pembelajaran & Sintaks Resmi
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(KurikulumMerdekaReferenceData.MODEL_PEMBELAJARAN_LIST) { model ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                            ) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.secondaryContainer),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                Icons.Default.Psychology,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.secondary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(model.name, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
                                            Text(model.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }

                                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

                                    Text("Sintaks / Langkah Kerja:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    model.sintaks.forEachIndexed { idx, step ->
                                        Row(modifier = Modifier.padding(vertical = 2.dp), verticalAlignment = Alignment.Top) {
                                            Box(
                                                modifier = Modifier
                                                    .size(20.dp)
                                                    .clip(CircleShape)
                                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text("${idx + 1}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                                            }
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(step, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
