package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.example.data.ai.ChatMessage
import com.example.data.ai.GeminiService
import com.example.data.ai.PedagogicalConsultantEngine
import com.example.ui.theme.*
import com.example.ui.viewmodel.ModulViewModel
import com.example.ui.viewmodel.Screen
import kotlinx.coroutines.launch
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

    // Recheck connectivity on enter
    LaunchedEffect(Unit) {
        val hasKey = GeminiService.isAvailable(context)
        if (hasKey) {
            val test = GeminiService.testConnection(context)
            isOnlineActive = test.isSuccess
        } else {
            isOnlineActive = false
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

    fun handleSend(textToSend: String) {
        val trimmed = textToSend.trim()
        if (trimmed.isEmpty() || isProcessing) return

        keyboardController?.hide()
        queryText = ""

        messages.add(ChatMessage(sender = "USER", content = trimmed))
        isProcessing = true

        coroutineScope.launch {
            if (messages.isNotEmpty()) {
                listState.animateScrollToItem(messages.size - 1)
            }
            
            // Check real-time connectivity & API Key
            val hasKey = GeminiService.isAvailable(context)
            var usedSource = "Offline Kurikulum Merdeka"
            val answer = if (hasKey) {
                try {
                    val aiPrompt = """
                        Sebagai konsultan ahli Kurikulum Merdeka Kemendikbudristek, berikan panduan praktis, terstruktur, dan aplikatif untuk pertanyaan guru berikut:
                        $trimmed
                        
                        PANDUAN FORMAT:
                        - Berikan poin-poin yang jelas dan mudah dipahami.
                        - Berikan contoh konkret di dalam kelas.
                        - Gunakan format teks rapi dan hindari rumus LaTeX.
                    """.trimIndent()

                    val result = withTimeoutOrNull(45000L) {
                        GeminiService.generateText(context, aiPrompt)
                    }

                    if (result.isNullOrBlank()) {
                        isOnlineActive = false
                        usedSource = "Offline Kurikulum Merdeka"
                        PedagogicalConsultantEngine.answerPedagogicalQuery(trimmed)
                    } else {
                        isOnlineActive = true
                        usedSource = "Gemini AI"
                        result
                    }
                } catch (e: Exception) {
                    isOnlineActive = false
                    usedSource = "Offline Kurikulum Merdeka"
                    PedagogicalConsultantEngine.answerPedagogicalQuery(trimmed)
                }
            } else {
                isOnlineActive = false
                usedSource = "Offline Kurikulum Merdeka"
                PedagogicalConsultantEngine.answerPedagogicalQuery(trimmed)
            }

            messages.add(ChatMessage(sender = "AI", content = answer, source = usedSource))
            isProcessing = false
            if (messages.isNotEmpty()) {
                listState.animateScrollToItem(messages.size - 1)
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
                            .clickable { viewModel.navigateTo(Screen.Settings) }
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

                    IconButton(onClick = { viewModel.navigateTo(Screen.Settings) }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Pengaturan API Key",
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
                        ModernAiBubble(
                            content = msg.content,
                            source = msg.source,
                            onCopy = {
                                clipboardManager.setText(AnnotatedString(msg.content))
                                Toast.makeText(context, "Jawaban disalin ke papan klip", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }

                // Thinking row
                if (isProcessing) {
                    item {
                        ModernAiThinkingRow()
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
                                "Tanyakan ide mengajar atau diferensiasi...",
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
                        keyboardActions = KeyboardActions(onSend = { handleSend(queryText) })
                    )

                    IconButton(
                        onClick = { handleSend(queryText) },
                        enabled = queryText.isNotBlank() && !isProcessing,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                if (queryText.isNotBlank() && !isProcessing) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .testTag("btn_send_consultation")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Kirim",
                            tint = if (queryText.isNotBlank() && !isProcessing) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.size(18.dp)
                        )
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
    content: String,
    source: String? = null,
    onCopy: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Top
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
                contentDescription = "AI",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start
        ) {
            Surface(
                shape = RoundedCornerShape(topStart = 4.dp, topEnd = 18.dp, bottomStart = 18.dp, bottomEnd = 18.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    RenderMarkdownBlocks(text = content)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Action Row with Source Badge and Copy Button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (!source.isNullOrBlank()) {
                    val isGemini = source.contains("Gemini", ignoreCase = true)
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isGemini) Color(0xFFDCFCE7) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
                        border = BorderStroke(1.dp, if (isGemini) Color(0xFF86EFAC) else MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = if (isGemini) Icons.Default.AutoAwesome else Icons.Default.MenuBook,
                                contentDescription = null,
                                tint = if (isGemini) Color(0xFF15803D) else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(11.dp)
                            )
                            Text(
                                text = if (isGemini) "Gemini AI" else "Kurikulum Merdeka",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.SemiBold),
                                color = if (isGemini) Color(0xFF15803D) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.clickable(onClick = onCopy)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Salin Jawaban",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Salin",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
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
fun ModernAiThinkingRow() {
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
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "AI sedang menyusun saran praktis",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(4.dp))
                Box(
                    modifier = Modifier.size(6.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha = alpha1))
                )
                Box(
                    modifier = Modifier.size(6.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha = alpha2))
                )
                Box(
                    modifier = Modifier.size(6.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha = alpha3))
                )
            }
        }
    }
}
