package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ai.ConnectionTestResult
import com.example.data.ai.GeminiService
import com.example.util.ApiKeyManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var apiKey by remember { mutableStateOf(ApiKeyManager.getApiKey(context) ?: "") }
    var isTestingConnection by remember { mutableStateOf(false) }
    var testResult by remember { mutableStateOf<ConnectionTestResult?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pengaturan Gemini AI", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
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
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                            Column {
                                Text(
                                    text = "Konfigurasi Google Gemini AI",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "Konsultan Pedagogik & Generator Modul Online",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                        Text(
                            text = "Masukkan Google Gemini API Key Anda untuk menghubungkan fitur konsultasi interaktif secara real-time dan analisis kurikulum mendalam.",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp, lineHeight = 18.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        OutlinedTextField(
                            value = apiKey,
                            onValueChange = {
                                apiKey = it
                                testResult = null
                            },
                            label = { Text("Gemini API Key") },
                            placeholder = { Text("Contoh: AIzaSy... atau AQ....") },
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

                        // Format Hint if needed
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
                                            "Format Kunci: Google AI Studio biasanya berawalan 'AQ.' (kunci Auth baru) atau 'AIzaSy' (kunci lama) dengan panjang sekitar 39 karakter.",
                                            fontSize = 11.sp,
                                            color = Color(0xFF78350F),
                                            lineHeight = 15.sp
                                        )
                                    }
                                }
                            }
                        }

                        // Test Result Diagnostic Badge
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
                                            text = "Model Aktif: ${res.modelUsed}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (res.isSuccess) Color(0xFF166534) else Color(0xFF991B1B)
                                        )
                                    }
                                }
                            }
                        }

                        // Action Buttons
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
                                    Text("Simpan & Uji Koneksi AI", fontWeight = FontWeight.Bold, fontSize = 13.sp)
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

                        // Step-by-step guidance
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("💡 Panduan Mendapatkan API Key Gratis:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("1. Kunjungi situs https://aistudio.google.com/ pada browser Anda.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("2. Masuk dengan Akun Google Anda, lalu klik tombol 'Get API key'.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("3. Buat kunci baru, salin teks kunci tersebut dan tempelkan pada kolom di atas.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("4. Klik 'Simpan & Uji Koneksi AI' untuk memverifikasi kesiapan AI.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                                Text("🛡️ Mode Offline Kurikulum Merdeka:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                                Text("Aplikasi ini dapat digunakan 100% tanpa internet dan tanpa API Key dengan memanfaatkan Bank Data Kurikulum Merdeka bawaan.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
    }
}
