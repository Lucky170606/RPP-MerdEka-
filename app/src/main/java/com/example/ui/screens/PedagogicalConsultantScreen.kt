package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.ai.ChatMessage
import com.example.data.ai.ConnectionTestResult
import com.example.data.ai.GeminiService
import com.example.data.ai.PedagogicalConsultantEngine
import com.example.ui.theme.*
import com.example.ui.viewmodel.ModulViewModel
import com.example.ui.viewmodel.Screen
import com.example.util.AiSfxType
import com.example.util.ApiKeyManager
import com.example.util.SoundManager
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.util.regex.Pattern

data class QuickPromptCategory(
    val icon: ImageVector,
    val title: String,
    val prompt: String,
    val iconTint: Color,
    val iconBg: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PedagogicalConsultantScreen(
    viewModel: ModulViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    val listState = rememberLazyListState()

    var queryText by remember { mutableStateOf("") }
    var isProcessing by remember { mutableStateOf(false) }
    var activeConsultationJob by remember { mutableStateOf<Job?>(null) }

    val messages = remember {
        mutableStateListOf(
            ChatMessage(
                sender = "AI",
                content = "Halo Bapak/Ibu Guru Hebat! 👋✨\n\nSaya adalah **Konsultan Pedagogis AI**. Saya siap membantu menyusun ide pembelajaran aktif, diferensiasi konten, rubrik asesmen HOTS, hingga integrasi Profil Pelajar Pancasila.\n\nApa topik atau tantangan kelas yang ingin kita diskusikan hari ini?"
            )
        )
    }

    val snackbarHostState = remember { SnackbarHostState() }
    var isOnlineActive by remember { mutableStateOf(false) }

    // Gemini API Key BottomSheet state
    var showApiKeySheet by remember { mutableStateOf(false) }
    var sheetApiKey by remember { mutableStateOf("") }
    var isTestingSheetKey by remember { mutableStateOf(false) }
    var sheetTestResult by remember { mutableStateOf<ConnectionTestResult?>(null) }

    // Intercept back action to close sheet or return directly to Home
    BackHandler {
        if (showApiKeySheet) {
            showApiKeySheet = false
        } else {
            viewModel.navigateTo(Screen.Home)
        }
    }

    // Recheck connectivity on enter asynchronously in background
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val hasKey = GeminiService.isAvailable(context)
            if (hasKey) {
                val test = withTimeoutOrNull(4000L) {
                    GeminiService.testConnection(context)
                }
                isOnlineActive = test?.isSuccess == true
            } else {
                isOnlineActive = false
            }
        }
    }

    val starterCards = listOf(
        QuickPromptCategory(
            icon = Icons.Default.Lightbulb,
            title = "Ice Breaking Seru",
            prompt = "Berikan 3 ide ice breaking 5 menit yang seru dan relevan untuk membuka pelajaran dengan energi positif!",
            iconTint = Color(0xFFD97706),
            iconBg = Color(0xFFFEF3C7)
        ),
        QuickPromptCategory(
            icon = Icons.Default.Psychology,
            title = "Deep Learning",
            prompt = "Bagaimana menerapkan metode Deep Learning (pembelajaran mendalam) dalam Kurikulum Merdeka agar siswa lebih kritis?",
            iconTint = Color(0xFF4F46E5),
            iconBg = Color(0xFFE0E7FF)
        ),
        QuickPromptCategory(
            icon = Icons.Default.Quiz,
            title = "Pertanyaan HOTS",
            prompt = "Bagaimana teknik menyusun pertanyaan pemantik yang bisa memicu rasa ingin tahu (curiosity) siswa?",
            iconTint = Color(0xFF9333EA),
            iconBg = Color(0xFFF3E8FF)
        ),
        QuickPromptCategory(
            icon = Icons.Default.Groups,
            title = "Diferensiasi Kelas",
            prompt = "Bagaimana cara efektif mengajar di kelas yang kemampuan dasar siswanya sangat bervariasi?",
            iconTint = Color(0xFF16A34A),
            iconBg = Color(0xFFDCFCE7)
        ),
        QuickPromptCategory(
            icon = Icons.Default.AssignmentTurnedIn,
            title = "Asesmen Formatif",
            prompt = "Berikan 4 metode asesmen formatif cepat dan menyenangkan tanpa membebani guru dengan koreksi kertas!",
            iconTint = Color(0xFFEA580C),
            iconBg = Color(0xFFFFEDD5)
        )
    )

    fun handleSend(textToSend: String, retryIndex: Int? = null) {
        val trimmed = textToSend.trim()
        if (trimmed.isEmpty() || isProcessing) return

        keyboardController?.hide()
        queryText = ""

        if (retryIndex == null) {
            messages.add(ChatMessage(sender = "USER", content = trimmed))
        }
        isProcessing = true
        val soundManager = SoundManager.getInstance(context)
        soundManager.playSfx(AiSfxType.AI_START_GENERATING)

        activeConsultationJob = coroutineScope.launch {
            try {
                if (messages.isNotEmpty()) {
                    listState.animateScrollToItem(messages.size - 1)
                }

                val hasKey = GeminiService.isAvailable(context)
                var usedSource = "Offline Kurikulum Merdeka"
                var isFallback = false
                var errorReason: String? = null

                val answer = if (hasKey) {
                    try {
                        val aiPrompt = """
                            Sebagai konsultan ahli Kurikulum Merdeka Kemendikbudristek, berikan panduan praktis, terstruktur, dan aplikatif untuk pertanyaan guru berikut:
                            $trimmed
                            
                            PANDUAN FORMAT:
                            - Berikan poin-poin yang jelas dan mudah dipahami.
                            - Berikan contoh konkret di dalam kelas atau lingkungan sekolah.
                            - Gunakan format teks rapi dan hindari rumus LaTeX berlebih.
                        """.trimIndent()

                        val result = withTimeoutOrNull(60000L) {
                            GeminiService.generateText(context, aiPrompt)
                        }

                        if (result.isNullOrBlank()) {
                            isOnlineActive = false
                            usedSource = "Offline Kurikulum Merdeka"
                            isFallback = true
                            errorReason = "Waktu tunggu habis (Timeout)"
                            PedagogicalConsultantEngine.answerPedagogicalQuery(trimmed)
                        } else {
                            isOnlineActive = true
                            usedSource = "Gemini AI"
                            isFallback = false
                            result
                        }
                    } catch (ce: CancellationException) {
                        throw ce
                    } catch (e: Exception) {
                        isOnlineActive = false
                        usedSource = "Offline Kurikulum Merdeka"
                        isFallback = true
                        errorReason = e.localizedMessage ?: "Gangguan jaringan API"
                        PedagogicalConsultantEngine.answerPedagogicalQuery(trimmed)
                    }
                } else {
                    isOnlineActive = false
                    usedSource = "Offline Kurikulum Merdeka"
                    isFallback = false
                    PedagogicalConsultantEngine.answerPedagogicalQuery(trimmed)
                }

                val responseMsg = ChatMessage(
                    sender = "AI",
                    content = answer,
                    source = usedSource,
                    isFallback = isFallback,
                    originalQuery = trimmed,
                    errorReason = errorReason
                )

                soundManager.playSfx(AiSfxType.AI_SUCCESS)

                if (retryIndex != null && retryIndex in messages.indices) {
                    messages[retryIndex] = responseMsg
                } else {
                    messages.add(responseMsg)
                }
            } catch (ce: CancellationException) {
                // Dihentikan oleh pengguna secara aman tanpa blocking
                val stopMsg = ChatMessage(
                    sender = "AI",
                    content = "*(Respon dihentikan oleh pengguna)*\n\n${PedagogicalConsultantEngine.answerPedagogicalQuery(trimmed)}",
                    source = "Offline Kurikulum Merdeka",
                    isFallback = true,
                    originalQuery = trimmed,
                    errorReason = "Respon dihentikan"
                )
                if (retryIndex != null && retryIndex in messages.indices) {
                    messages[retryIndex] = stopMsg
                } else {
                    messages.add(stopMsg)
                }
            } catch (t: Throwable) {
                val fallbackAnswer = PedagogicalConsultantEngine.answerPedagogicalQuery(trimmed)
                val fallbackMsg = ChatMessage(
                    sender = "AI",
                    content = fallbackAnswer,
                    source = "Offline Kurikulum Merdeka",
                    isFallback = true,
                    originalQuery = trimmed,
                    errorReason = t.localizedMessage ?: "Sistem beralih ke offline"
                )
                if (retryIndex != null && retryIndex in messages.indices) {
                    messages[retryIndex] = fallbackMsg
                } else {
                    messages.add(fallbackMsg)
                }
            } finally {
                isProcessing = false
                activeConsultationJob = null
                if (messages.isNotEmpty()) {
                    listState.animateScrollToItem(messages.size - 1)
                }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // --- Top Bar (Fixed Header) ---
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { viewModel.navigateTo(Screen.Home) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(EduIndigo600, EduPurple500))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                sheetApiKey = ApiKeyManager.getApiKey(context) ?: ""
                                sheetTestResult = null
                                showApiKeySheet = true
                            }
                    ) {
                        Text(
                            text = "Ruang Konsultasi Guru AI",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(if (isOnlineActive) EduGreen600 else Color(0xFF94A3B8))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isOnlineActive) "Gemini AI Terhubung" else "Mode Offline Kurikulum Merdeka",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Medium),
                                color = if (isOnlineActive) EduGreen600 else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(
                        onClick = {
                            sheetApiKey = ApiKeyManager.getApiKey(context) ?: ""
                            sheetTestResult = null
                            showApiKeySheet = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Pengaturan Gemini AI",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            }
        }

        // --- Chat Messages List ---
        Box(modifier = Modifier.weight(1f)) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Topic Starter Chips
                item {
                    Column(modifier = Modifier.padding(bottom = 4.dp)) {
                        Text(
                            text = "💡 Topik Konsultasi Populer",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                        )
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            contentPadding = PaddingValues(horizontal = 2.dp)
                        ) {
                            items(starterCards) { item ->
                                ModernStarterChip(card = item) {
                                    handleSend(item.prompt)
                                }
                            }
                        }
                    }
                }

                // Chat Messages
                items(messages) { msg ->
                    if (msg.sender == "USER") {
                        ModernUserBubble(content = msg.content)
                    } else {
                        val msgIndex = messages.indexOf(msg)
                        ModernAiBubble(
                            message = msg,
                            onCopy = {
                                clipboardManager.setText(AnnotatedString(msg.content))
                                Toast.makeText(context, "Jawaban disalin ke papan klip", Toast.LENGTH_SHORT).show()
                            },
                            onRetryGemini = if (msg.isFallback && !msg.originalQuery.isNullOrBlank()) {
                                { handleSend(msg.originalQuery, msgIndex) }
                            } else null,
                            onApplyToWizard = {
                                viewModel.wizardAdditionalNotes.value = msg.content
                                viewModel.navigateTo(Screen.Wizard)
                                Toast.makeText(context, "Saran AI dimuat ke Catatan Tambahan Modul Ajar!", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }

                // Thinking row
                if (isProcessing) {
                    item {
                        val hasKey = isOnlineActive || GeminiService.isAvailable(context)
                        ModernAiThinkingRow(hasKey = hasKey)
                    }
                }
            }
        }

        // --- Bottom Input Bar (Anchored above keyboard & navigation bar) ---
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .navigationBarsPadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(26.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.8f),
                            shape = RoundedCornerShape(26.dp)
                        )
                        .padding(start = 14.dp, end = 6.dp, top = 2.dp, bottom = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = queryText,
                        onValueChange = { queryText = it },
                        placeholder = {
                            Text(
                                if (isProcessing) "Sedang merumuskan jawaban..." else "Tanyakan ide mengajar atau diferensiasi...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_consultant_query"),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                            cursorColor = MaterialTheme.colorScheme.primary
                        ),
                        maxLines = 4,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(onSend = { 
                            if (!isProcessing) handleSend(queryText) 
                        })
                    )

                    if (isProcessing) {
                        // AI Studio Style: Sleek circular button with subtle ring and centered rounded stop square
                        val infiniteTransition = rememberInfiniteTransition(label = "stop_ring")
                        val ringAlpha by infiniteTransition.animateFloat(
                            initialValue = 0.4f,
                            targetValue = 1f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(1000, easing = LinearEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "ring_glow"
                        )
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .border(
                                    1.5.dp,
                                    MaterialTheme.colorScheme.primary.copy(alpha = ringAlpha),
                                    CircleShape
                                )
                                .clickable {
                                    activeConsultationJob?.cancel()
                                    isProcessing = false
                                }
                                .testTag("btn_stop_consultation"),
                            contentAlignment = Alignment.Center
                        ) {
                            // Authentic AI Studio stop square
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(RoundedCornerShape(2.5.dp))
                                    .background(MaterialTheme.colorScheme.onSurface)
                            )
                        }
                    } else {
                        IconButton(
                            onClick = { handleSend(queryText) },
                            enabled = queryText.isNotBlank(),
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(
                                    if (queryText.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                                )
                                .testTag("btn_send_consultation")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Kirim",
                                tint = if (queryText.isNotBlank()) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    // Dialog: Pengaturan Cepat Gemini AI (Langsung tampil penuh di tengah tanpa perlu swipe)
    if (showApiKeySheet) {
        Dialog(
            onDismissRequest = { showApiKeySheet = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .wrapContentHeight()
                    .padding(vertical = 16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Column {
                                Text(
                                    text = "Pengaturan Gemini AI",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "Konsultan Pedagogik & Generator Online",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        IconButton(
                            onClick = { showApiKeySheet = false },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Tutup", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    Text(
                        text = "Hubungkan Google Gemini API Key Anda untuk konsultasi interaktif real-time dan analisis pembelajaran mendalam.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = sheetApiKey,
                        onValueChange = {
                            sheetApiKey = it
                            sheetTestResult = null
                        },
                        label = { Text("Gemini API Key") },
                        placeholder = { Text("Contoh: AIzaSy... atau AQ....") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        trailingIcon = {
                            if (sheetApiKey.isNotEmpty()) {
                                IconButton(onClick = {
                                    sheetApiKey = ""
                                    sheetTestResult = null
                                }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Hapus")
                                }
                            }
                        }
                    )

                    // Format Hint if short or unrecognized
                    if (sheetApiKey.isNotBlank()) {
                        val clean = sheetApiKey.trim()
                        val isRecognized = clean.startsWith("AQ.") || clean.startsWith("AIzaSy")
                        if (!isRecognized && clean.length < 20) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFFEF3C7),
                                border = BorderStroke(1.dp, Color(0xFFF59E0B)),
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
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        "Format Kunci: Google AI Studio biasanya diawali 'AQ.' (kunci Auth baru) atau 'AIzaSy' (kunci lama) dengan panjang sekitar 39 karakter.",
                                        fontSize = 11.sp,
                                        color = Color(0xFF78350F),
                                        lineHeight = 15.sp
                                    )
                                }
                            }
                        }
                    }

                    // Diagnostic Result Box
                    sheetTestResult?.let { res ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (res.isSuccess) Color(0xFFDCFCE7) else Color(0xFFFEE2E2),
                            border = BorderStroke(1.dp, if (res.isSuccess) Color(0xFF16A34A) else Color(0xFFDC2626)),
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
                                val clean = sheetApiKey.trim()
                                ApiKeyManager.saveApiKey(context, clean)
                                Toast.makeText(context, "API Key Disimpan & Diaktifkan!", Toast.LENGTH_SHORT).show()

                                // Trigger live test immediately
                                isTestingSheetKey = true
                                sheetTestResult = null
                                coroutineScope.launch {
                                    val result = GeminiService.testConnection(context, clean)
                                    sheetTestResult = result
                                    isTestingSheetKey = false
                                    isOnlineActive = result.isSuccess
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            enabled = !isTestingSheetKey
                        ) {
                            if (isTestingSheetKey) {
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

                        if (sheetApiKey.isNotBlank()) {
                            OutlinedButton(
                                onClick = {
                                    ApiKeyManager.clearApiKey(context)
                                    sheetApiKey = ""
                                    sheetTestResult = null
                                    isOnlineActive = false
                                    Toast.makeText(context, "API Key Dihapus (Beralih ke Offline)", Toast.LENGTH_SHORT).show()
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

                    // Guidance info
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("💡 Cara dapat API Key Gratis:", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            Text("1. Buka situs https://aistudio.google.com/ di browser HP/Laptop.", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("2. Klik 'Get API Key' > 'Create API Key', lalu salin kuncinya ke sini.", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("3. Jika tidak ada internet/API Key, aplikasi tetap aktif 100% menggunakan Bank Modul Offline Kurikulum Merdeka.", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ModernStarterChip(
    card: QuickPromptCategory,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .width(165.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(card.iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = card.icon,
                    contentDescription = null,
                    tint = card.iconTint,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = card.title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, fontSize = 13.sp),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = card.prompt,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 15.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun ModernUserBubble(content: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Surface(
            shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp, bottomStart = 18.dp, bottomEnd = 4.dp),
            color = MaterialTheme.colorScheme.primary,
            shadowElevation = 1.dp,
            modifier = Modifier.widthIn(max = 290.dp)
        ) {
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp, lineHeight = 20.sp),
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
            )
        }
    }
}

@Composable
fun ModernAiBubble(
    message: ChatMessage,
    onCopy: () -> Unit,
    onRetryGemini: (() -> Unit)? = null,
    onApplyToWizard: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val soundManager = remember { SoundManager.getInstance(context) }
    val isSpeaking by soundManager.isSpeaking.collectAsStateWithLifecycle()
    val isLoadingVoice by soundManager.isLoadingVoice.collectAsStateWithLifecycle()
    val currentUtteranceId by soundManager.currentUtteranceId.collectAsStateWithLifecycle()
    val messageUtteranceId = remember(message.content) { "msg_${message.content.hashCode()}" }
    val isThisMessageTarget = currentUtteranceId == messageUtteranceId || currentUtteranceId?.startsWith(messageUtteranceId) == true
    val isThisMessagePlaying = isSpeaking && isThisMessageTarget
    val isThisMessageLoading = isLoadingVoice && isThisMessageTarget
    val isGemini = message.source?.contains("Gemini", ignoreCase = true) == true
    val isFallback = message.isFallback

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header: AI Avatar & Name / Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(EduIndigo600, EduPurple500))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AI",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Text(
                        text = "Konsultan Pedagogis AI",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                val badgeColor = when {
                    isGemini -> Color(0xFFDCFCE7)
                    isFallback -> Color(0xFFFEF3C7)
                    else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
                }
                val badgeBorder = when {
                    isGemini -> Color(0xFF86EFAC)
                    isFallback -> Color(0xFFFDE68A)
                    else -> MaterialTheme.colorScheme.outlineVariant
                }
                val badgeText = when {
                    isGemini -> "Gemini AI"
                    isFallback -> "Offline (Fallback)"
                    else -> "Offline"
                }
                val badgeIconTint = when {
                    isGemini -> Color(0xFF15803D)
                    isFallback -> Color(0xFFB45309)
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = badgeColor,
                    border = BorderStroke(1.dp, badgeBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = if (isGemini) Icons.Default.AutoAwesome else Icons.Default.MenuBook,
                            contentDescription = null,
                            tint = badgeIconTint,
                            modifier = Modifier.size(10.dp)
                        )
                        Text(
                            text = badgeText,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.SemiBold),
                            color = badgeIconTint
                        )
                    }
                }
            }

            if (isFallback) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFFEF3C7),
                    border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFFB45309),
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Koneksi Gemini bermasalah. Menampilkan modul dari Bank Kurikulum Merdeka Offline.",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 15.sp),
                            color = Color(0xFF92400E)
                        )
                    }
                }
            }

            // Message Body Content
            RenderMarkdownBlocks(text = message.content)

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

            // Action Row: Read Aloud, Copy, Use to Wizard, Retry
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Read Aloud Button
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isThisMessagePlaying || isThisMessageLoading) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    border = if (isThisMessagePlaying || isThisMessageLoading) BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null,
                    modifier = Modifier.clickable {
                        if (isThisMessagePlaying || isThisMessageLoading) {
                            soundManager.stopSpeaking()
                        } else {
                            soundManager.setVoiceEnabled(true)
                            soundManager.playSfx(AiSfxType.BUTTON_TAP)
                            soundManager.speak(message.content, messageUtteranceId)
                        }
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isThisMessageLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(11.dp),
                                strokeWidth = 1.5.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            Icon(
                                imageVector = if (isThisMessagePlaying) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                                contentDescription = "Bacakan",
                                tint = if (isThisMessagePlaying) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(11.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = when {
                                isThisMessageLoading -> "Memuat..."
                                isThisMessagePlaying -> "Berhenti"
                                else -> "Bacakan"
                            },
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = if (isThisMessagePlaying || isThisMessageLoading) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Copy Button
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.clickable(onClick = onCopy)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Salin",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Salin",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (onApplyToWizard != null) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                        modifier = Modifier.clickable(onClick = onApplyToWizard)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Gunakan ke Modul",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                if (onRetryGemini != null) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                        modifier = Modifier.clickable(onClick = onRetryGemini)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Coba Lagi",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Coba Gemini AI",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

fun parseMarkdownInline(text: String): AnnotatedString {
    return buildAnnotatedString {
        // Match **bold**, *italic*, _italic_, `code`
        val pattern = Pattern.compile("(\\*\\*(.+?)\\*\\*)|(\\*(.+?)\\*)|(_(.+?)_)|(`(.+?)`)")
        val matcher = pattern.matcher(text)
        var lastIndex = 0

        while (matcher.find()) {
            val start = matcher.start()
            val end = matcher.end()

            if (start > lastIndex) {
                val plainText = text.substring(lastIndex, start).replace("*", "")
                append(plainText)
            }

            when {
                // **bold**
                matcher.group(2) != null -> {
                    val boldText = matcher.group(2)!!
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(boldText.replace("*", ""))
                    }
                }
                // *italic / emphasis*
                matcher.group(4) != null -> {
                    val italicText = matcher.group(4)!!
                    withStyle(SpanStyle(fontWeight = FontWeight.SemiBold, fontStyle = FontStyle.Italic)) {
                        append(italicText.replace("*", ""))
                    }
                }
                // _italic_
                matcher.group(6) != null -> {
                    val italicText = matcher.group(6)!!
                    withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                        append(italicText.replace("*", ""))
                    }
                }
                // `code`
                matcher.group(8) != null -> {
                    val codeText = matcher.group(8)!!
                    withStyle(SpanStyle(fontFamily = FontFamily.Monospace, background = Color(0x15000000))) {
                        append(" $codeText ")
                    }
                }
            }
            lastIndex = end
        }

        if (lastIndex < text.length) {
            val remaining = text.substring(lastIndex).replace("*", "")
            append(remaining)
        }
    }
}

@Composable
fun RenderMarkdownBlocks(text: String) {
    val paragraphs = text.split("\n\n")
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        paragraphs.forEach { paragraph ->
            val trimmed = paragraph.trim()
            if (trimmed.startsWith("###") || trimmed.startsWith("##") || trimmed.startsWith("#")) {
                val headingText = trimmed.replace(Regex("^#+\\s*"), "").replace("*", "")
                Text(
                    text = headingText,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontSize = 14.sp),
                    color = MaterialTheme.colorScheme.primary
                )
            } else if (trimmed.startsWith("- ") || trimmed.startsWith("* ") || trimmed.startsWith("• ")) {
                val lines = trimmed.split("\n")
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    lines.forEach { line ->
                        val cleanLine = line.replace(Regex("^[-*•]\\s*"), "")
                        Row(
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier.padding(start = 2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 7.dp, end = 8.dp)
                                    .size(5.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary)
                            )
                            Text(
                                text = parseMarkdownInline(cleanLine),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 13.sp,
                                    lineHeight = 19.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            } else {
                Text(
                    text = parseMarkdownInline(trimmed),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 13.sp,
                        lineHeight = 20.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun ModernAiThinkingRow(
    hasKey: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "dots")
    val alpha1 by infiniteTransition.animateFloat(
        initialValue = 0.2f, targetValue = 1f,
        animationSpec = infiniteRepeatable(animation = tween(600, delayMillis = 0), repeatMode = RepeatMode.Reverse),
        label = "d1"
    )
    val alpha2 by infiniteTransition.animateFloat(
        initialValue = 0.2f, targetValue = 1f,
        animationSpec = infiniteRepeatable(animation = tween(600, delayMillis = 200), repeatMode = RepeatMode.Reverse),
        label = "d2"
    )
    val alpha3 by infiniteTransition.animateFloat(
        initialValue = 0.2f, targetValue = 1f,
        animationSpec = infiniteRepeatable(animation = tween(600, delayMillis = 400), repeatMode = RepeatMode.Reverse),
        label = "d3"
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(EduIndigo600, EduPurple500))),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = if (hasKey) "Gemini AI sedang merumuskan solusi..." else "Menyiapkan panduan Kurikulum Merdeka...",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Box(
                    modifier = Modifier.size(5.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha = alpha1))
                )
                Box(
                    modifier = Modifier.size(5.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha = alpha2))
                )
                Box(
                    modifier = Modifier.size(5.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha = alpha3))
                )
            }
        }
    }
}
