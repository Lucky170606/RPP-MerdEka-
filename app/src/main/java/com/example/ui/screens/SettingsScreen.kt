package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.ai.ConnectionTestResult
import com.example.data.ai.GeminiService
import com.example.util.ApiKeyManager
import com.example.util.AppThemeStyle
import com.example.util.ThemeManager
import com.example.util.ThemeMode
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var apiKey by remember { mutableStateOf(ApiKeyManager.getApiKey(context) ?: "") }
    val currentThemeStyle by ThemeManager.currentTheme.collectAsStateWithLifecycle()
    val currentThemeMode by ThemeManager.currentMode.collectAsStateWithLifecycle()
    var isTestingConnection by remember { mutableStateOf(false) }
    var testResult by remember { mutableStateOf<ConnectionTestResult?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pengaturan Aplikasi & AI", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Section 0: Mode Layar (Terang / Gelap)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.SettingsBrightness, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Text(
                                text = "Mode Warna Layar",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        Text(
                            text = "Pilih tampilan terang putih bersih atau mode gelap malam hari:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ThemeMode.values().forEach { mode ->
                                val isModeSelected = currentThemeMode == mode
                                OutlinedButton(
                                    onClick = {
                                        ThemeManager.setMode(context, mode)
                                        val label = when(mode) {
                                            ThemeMode.LIGHT -> "Mode Terang Aktif"
                                            ThemeMode.DARK -> "Mode Gelap Aktif"
                                            ThemeMode.SYSTEM -> "Mengikuti Pengaturan HP"
                                        }
                                        Toast.makeText(context, label, Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("btn_settings_mode_${mode.id}"),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = if (isModeSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                                        contentColor = if (isModeSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    border = androidx.compose.foundation.BorderStroke(
                                        width = if (isModeSelected) 1.5.dp else 1.dp,
                                        color = if (isModeSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                    ),
                                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp)
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(
                                            imageVector = when(mode) {
                                                ThemeMode.LIGHT -> Icons.Default.WbSunny
                                                ThemeMode.DARK -> Icons.Default.NightsStay
                                                ThemeMode.SYSTEM -> Icons.Default.SettingsBrightness
                                            },
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = when(mode) {
                                                ThemeMode.LIGHT -> "Terang"
                                                ThemeMode.DARK -> "Gelap"
                                                ThemeMode.SYSTEM -> "Ikuti HP"
                                            },
                                            fontSize = 11.sp,
                                            fontWeight = if (isModeSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Section 1: Template Tampilan UI
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Palette, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Text(
                                text = "Tema & Gaya Desain UI",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        Text(
                            text = "Pilih kombinasi warna dan suasana visual aplikasi yang paling nyaman di mata Anda:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        AppThemeStyle.values().forEach { themeOption ->
                            val isSelected = currentThemeStyle == themeOption

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        ThemeManager.setTheme(context, themeOption)
                                        Toast.makeText(context, "Tema beralih ke: ${themeOption.title}", Toast.LENGTH_SHORT).show()
                                    },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
                                ),
                                border = androidx.compose.foundation.BorderStroke(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy((-6).dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(20.dp)
                                                .clip(CircleShape)
                                                .background(themeOption.primaryPreview)
                                                .border(1.dp, Color.White, CircleShape)
                                        )
                                        Box(
                                            modifier = Modifier
                                                .size(20.dp)
                                                .clip(CircleShape)
                                                .background(themeOption.secondaryPreview)
                                                .border(1.dp, Color.White, CircleShape)
                                        )
                                        Box(
                                            modifier = Modifier
                                                .size(20.dp)
                                                .clip(CircleShape)
                                                .background(themeOption.accentPreview)
                                                .border(1.dp, Color.White, CircleShape)
                                        )
                                    }

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = themeOption.title,
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = themeOption.subtitle,
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    RadioButton(
                                        selected = isSelected,
                                        onClick = {
                                            ThemeManager.setTheme(context, themeOption)
                                            Toast.makeText(context, "Tema beralih ke: ${themeOption.title}", Toast.LENGTH_SHORT).show()
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Section 2: Gemini API Key & Live Test
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Text(
                                text = "Konfigurasi Gemini AI",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        Text(
                            text = "Masukkan Google Gemini API Key Anda untuk menghubungkan fitur Konsultan AI dan pembuatan modul online secara real-time.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        OutlinedTextField(
                            value = apiKey,
                            onValueChange = { 
                                apiKey = it
                                testResult = null
                            },
                            label = { Text("Gemini API Key") },
                            placeholder = { Text("AIzaSy...") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            trailingIcon = {
                                if (apiKey.isNotEmpty()) {
                                    IconButton(onClick = { 
                                        apiKey = ""
                                        testResult = null
                                    }) {
                                        Icon(Icons.Default.Clear, contentDescription = "Hapus")
                                    }
                                }
                            }
                        )

                        // API Key format hint
                        if (apiKey.isNotBlank()) {
                            val clean = apiKey.trim()
                            val isRecognized = clean.startsWith("AQ.") || clean.startsWith("AIzaSy")
                            if (!isRecognized && clean.length < 20) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFFEF3C7),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF59E0B)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.Top,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Info,
                                            contentDescription = null,
                                            tint = Color(0xFFD97706),
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Text(
                                            "Format Google AI Studio: Gemini API Key resmi diawali dengan 'AQ.' (kunci Auth baru) atau 'AIzaSy' (kunci lama).",
                                            fontSize = 11.sp,
                                            color = Color(0xFF78350F),
                                            lineHeight = 15.sp
                                        )
                                    }
                                }
                            }
                        }

                        // Status / Diagnostic card if tested
                        testResult?.let { res ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (res.isSuccess) Color(0xFFDCFCE7) else Color(0xFFFEE2E2),
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (res.isSuccess) Color(0xFF16A34A) else Color(0xFFDC2626)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Icon(
                                            imageVector = if (res.isSuccess) Icons.Default.CheckCircle else Icons.Default.ErrorOutline,
                                            contentDescription = null,
                                            tint = if (res.isSuccess) Color(0xFF15803D) else Color(0xFFB91C1C),
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            text = res.message,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = if (res.isSuccess) Color(0xFF15803D) else Color(0xFFB91C1C)
                                        )
                                    }
                                    if (!res.detail.isNullOrBlank()) {
                                        Text(
                                            text = res.detail,
                                            fontSize = 11.sp,
                                            color = if (res.isSuccess) Color(0xFF166534) else Color(0xFF991B1B)
                                        )
                                    }
                                    if (res.modelUsed != null) {
                                        Text(
                                            text = "Model Terhubung: ${res.modelUsed}",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = if (res.isSuccess) Color(0xFF166534) else Color(0xFF991B1B)
                                        )
                                    }
                                }
                            }
                        }
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    val clean = apiKey.trim()
                                    ApiKeyManager.saveApiKey(context, clean)
                                    Toast.makeText(context, "API Key Disimpan!", Toast.LENGTH_SHORT).show()
                                    
                                    // Trigger test immediately
                                    isTestingConnection = true
                                    testResult = null
                                    coroutineScope.launch {
                                        val result = GeminiService.testConnection(context, clean)
                                        testResult = result
                                        isTestingConnection = false
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                enabled = !isTestingConnection
                            ) {
                                if (isTestingConnection) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Menguji...", fontSize = 13.sp)
                                } else {
                                    Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Simpan & Uji AI", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }

                            if (apiKey.isNotBlank()) {
                                OutlinedButton(
                                    onClick = {
                                        ApiKeyManager.clearApiKey(context)
                                        apiKey = ""
                                        testResult = null
                                        Toast.makeText(context, "API Key Dihapus", Toast.LENGTH_SHORT).show()
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = MaterialTheme.colorScheme.error
                                    )
                                ) {
                                    Icon(Icons.Default.DeleteOutline, contentDescription = "Hapus", modifier = Modifier.size(18.dp))
                                }
                            }
                        }

                        // Guidance note
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("💡 Panduan API Key Google AI Studio:", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                Text("1. Buka https://aistudio.google.com/ pada browser Anda.", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("2. Klik 'Get API Key' lalu buat kunci gratis baru.", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("3. Salin kunci (diawali 'AIzaSy...') dan tempelkan pada kolom di atas.", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("4. Aplikasi juga dapat beroperasi 100% secara offline tanpa API Key menggunakan Offline Engine Kurikulum Merdeka.", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
        }
    }
}
