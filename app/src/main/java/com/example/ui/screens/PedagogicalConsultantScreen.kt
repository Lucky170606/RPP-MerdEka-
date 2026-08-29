package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ai.ChatMessage
import com.example.data.ai.GeminiService
import com.example.data.ai.PedagogicalConsultantEngine
import com.example.ui.components.AppHeader
import com.example.ui.components.BadgeChip
import com.example.ui.theme.*
import com.example.ui.viewmodel.ModulViewModel
import com.example.ui.viewmodel.Screen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PedagogicalConsultantScreen(
    viewModel: ModulViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var inputQuery by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val messages = remember {
        mutableStateListOf(
            ChatMessage(
                sender = "AI",
                content = "Halo Bapak/Ibu Guru! 🌟 Saya Asisten Pedagogik Kurikulum Merdeka. Ada yang bisa saya bantu terkait strategi diferensiasi, ide ice breaking, penyusunan asesmen, atau solusi pengelolaan kelas hari ini?"
            )
        )
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val isGeminiConnected = GeminiService.isAvailable(context)

    LaunchedEffect(Unit) {
        if (isGeminiConnected) {
            snackbarHostState.showSnackbar("Gemini AI berhasil terkoneksi!")
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            AppHeader(
                title = "Ruang Konsultasi Guru AI",
                subtitle = "Konsultasi Pedagogik, Diferensiasi, & Manajemen Kelas",
                showBackButton = true,
                onBackClick = { viewModel.navigateTo(Screen.Home) },
                actions = {
                    // Status Indicator
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(if (isGeminiConnected) Color.Green else Color.Gray)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                }
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 4.dp,
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(modifier = Modifier.padding(12.dp).navigationBarsPadding()) {
                    // Quick Prompts row
                    Text("Inspirasi Pertanyaan Cepat:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        contentPadding = PaddingValues(bottom = 8.dp)
                    ) {
                        items(PedagogicalConsultantEngine.PROMPT_INSPIRATIONS) { insp ->
                            SuggestionChip(
                                onClick = {
                                    inputQuery = insp.query
                                },
                                label = { Text(insp.title, fontSize = 11.sp) },
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                    }

                    // Input & Send Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = inputQuery,
                            onValueChange = { inputQuery = it },
                            placeholder = { Text("Tanyakan ide mengajar atau kasus kelas...") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_consultant_query"),
                            shape = RoundedCornerShape(24.dp),
                            maxLines = 3,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                focusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                                unfocusedContainerColor = androidx.compose.ui.graphics.Color.Transparent
                            )
                        )

                        IconButton(
                            onClick = {
                                val query = inputQuery.trim()
                                if (query.isNotEmpty() && !isLoading) {
                                    messages.add(ChatMessage(sender = "USER", content = query))
                                    inputQuery = ""
                                    isLoading = true

                                    coroutineScope.launch {
                                        // Attempt Gemini or fallback to offline knowledge base
                                        val answer = try {
                                            if (GeminiService.isAvailable(context)) {
                                                val aiPrompt = """
                                                    Sebagai konsultan ahli Kurikulum Merdeka Kemendikbudristek, berikan panduan praktis dan terstruktur untuk pertanyaan guru berikut:
                                                    $query
                                                    
                                                    CATATAN PENTING: Gunakan format teks biasa, JANGAN gunakan format LaTeX atau simbol matematika khusus (seperti $\frac...). Jika ada rumus matematika, tuliskan dalam format teks biasa yang mudah dibaca (contoh: 1/2).
                                                """.trimIndent()
                                                GeminiService.generateText(context, aiPrompt)
                                            } else {
                                                PedagogicalConsultantEngine.answerPedagogicalQuery(query)
                                            }
                                        } catch (e: Exception) {
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar("Gagal terhubung ke Gemini. Menggunakan panduan offline.")
                                            }
                                            PedagogicalConsultantEngine.answerPedagogicalQuery(query)
                                        }

                                        messages.add(ChatMessage(sender = "AI", content = answer))
                                        isLoading = false
                                    }
                                }
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(if (inputQuery.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                                .testTag("btn_send_consultation"),
                            enabled = inputQuery.isNotBlank() && !isLoading
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                            } else {
                                Icon(Icons.Default.Send, contentDescription = "Kirim", tint = if (inputQuery.isNotBlank()) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(messages) { msg ->
                if (msg.sender == "USER") {
                    // User Message
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(18.dp, 18.dp, 4.dp, 18.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier.widthIn(max = 300.dp)
                        ) {
                            Text(
                                text = msg.content,
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                } else {
                    // AI Consultant Message
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Psychology, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(4.dp, 18.dp, 18.dp, 18.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Asisten Pedagogik AI", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 13.sp)
                                    val clipboardManager = LocalClipboardManager.current
                                    IconButton(
                                        onClick = {
                                            clipboardManager.setText(AnnotatedString(msg.content))
                                            // Optional: Show a toast? But I might not have context readily available or might not want to overcomplicate.
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = "Salin Teks", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                    }
                                    BadgeChip(text = "Kurikulum Merdeka", backgroundColor = MaterialTheme.colorScheme.secondaryContainer, textColor = MaterialTheme.colorScheme.onSecondaryContainer)
                                }
                                Text(
                                    text = msg.content,
                                    style = MaterialTheme.typography.bodySmall.copy(lineHeight = 20.sp),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            if (isLoading) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                        Text("Asisten sedang merumuskan rekomendasi pedagogis...", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}
