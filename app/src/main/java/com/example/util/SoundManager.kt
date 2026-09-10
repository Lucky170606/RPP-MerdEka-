package com.example.util

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.sin

/**
 * Sound Effect types generated natively on-device via PCM synthesis (100% Offline, Zero Asset Size)
 */
enum class AiSfxType {
    BUTTON_TAP,
    AI_START_GENERATING,
    AI_SUCCESS,
    AI_ERROR,
    AI_GREETING,
    DELETE_ALERT
}

/**
 * Persona style for 100% Offline AI Voice Engine
 */
enum class VoicePersona(
    val displayName: String,
    val intonation: String,
    val description: String,
    val pitch: Float,
    val speechRate: Float,
    val preferDeepTone: Boolean,
    val samplePhrase: String
) {
    PENDIDIK_BIJAK(
        displayName = "Pendidik Bijak",
        intonation = "Tenang, terukur, dan instruksional",
        description = "Pelafalan artikulatif, tempo tertata rapi, dan berwibawa untuk sesi telaah materi kurikulum yang panjang.",
        pitch = 1.22f,
        speechRate = 0.92f,
        preferDeepTone = false,
        samplePhrase = "Halo bapak dan ibu guru. Mari kita telaah panduan pembelajaran ini secara tenang, jelas, dan terukur."
    ),
    BINTANG_RADIO(
        displayName = "Bintang Radio",
        intonation = "Modulatif, hangat, dan mengalir khas penyiar",
        description = "Gaya siaran profesional dengan vokal beresonansi merdu, artikulasi empuk, dan modulasi nada yang nyaman dinikmati.",
        pitch = 0.95f,
        speechRate = 0.96f,
        preferDeepTone = true,
        samplePhrase = "Halo rekan guru di seluruh nusantara! Selamat datang di sesi inspirasi pembelajaran kreatif hari ini."
    ),
    KREATOR_MEDSOS(
        displayName = "Bintang Iklan Medsos (Muda & Kreatif)",
        intonation = "Ekspresif, punchy, ceria, dan antusias kekinian",
        description = "Gaya penyampaian segar ala konten kreator media sosial, ritme lincah, hidup, dan memotivasi.",
        pitch = 1.35f,
        speechRate = 1.08f,
        preferDeepTone = false,
        samplePhrase = "Hai bapak ibu guru hebat! Yuk kita spill ide asesmen seru dan kekinian yang bikin siswa auto-semangat belajar!"
    ),
    PAK_ARIS(
        displayName = "Penyiar Podcast",
        intonation = "Santai, mendalam, dan reflektif khas podcast",
        description = "Gaya berbincang hangat, tempo mengalir santai dan dekat di telinga, sangat pas untuk eksplorasi gagasan kurikulum.",
        pitch = 0.85f,
        speechRate = 0.94f,
        preferDeepTone = true,
        samplePhrase = "Selamat datang di ruang bincang guru. Mari kita bedah gagasan dan inspirasi pembelajaran ini secara santai dan mendalam."
    ),
    ASISTEN_CERDAS(
        displayName = "Asisten Cerdas",
        intonation = "Lugas, baku, dan presisi",
        description = "Pengucapan standar bahasa Indonesia formal dan tempo efisien untuk sintesis administrasi cepat.",
        pitch = 1.18f,
        speechRate = 1.00f,
        preferDeepTone = false,
        samplePhrase = "Sistem siap. Modul dan perangkat ajar Kurikulum Merdeka dapat langsung diproses."
    ),
    SAHABAT_CERIA(
        displayName = "Sahabat Belajar",
        intonation = "Ceria, cerah, dan dinamis",
        description = "Gaya ramah penuh semangat untuk pembelajaran aktif, asesmen formatif, dan penguatan projek P5.",
        pitch = 1.38f,
        speechRate = 1.05f,
        preferDeepTone = false,
        samplePhrase = "Semangat pagi! Yuk kita rancang pembelajaran seru dan bermakna untuk siswa!"
    )
}

/**
 * 100% Offline Voice & Audio Engine for RPP Merdeka AI
 * Features:
 * 1. 100% Offline Text-to-Speech Engine with Device-Optimized Indonesian NLP & Voice Detection
 * 2. Specialized Educational Acronym Expansion (RPP, CP, TP, ATP, KKTP, P5, HOTS, dsb.)
 * 3. Natural prosody & sentence chunking for smooth offline reading without cutting off
 * 4. Procedural Native PCM Sound Effects (0 network, 0 asset size)
 */
class SoundManager private constructor(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isTtsReady = false
    private var pendingSpeech: Pair<String, String>? = null

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _isLoadingVoice = MutableStateFlow(false)
    val isLoadingVoice: StateFlow<Boolean> = _isLoadingVoice.asStateFlow()

    private val _currentUtteranceId = MutableStateFlow<String?>(null)
    val currentUtteranceId: StateFlow<String?> = _currentUtteranceId.asStateFlow()

    private val _isSoundEnabled = MutableStateFlow(true)
    val isSoundEnabled: StateFlow<Boolean> = _isSoundEnabled.asStateFlow()

    private val _isVoiceEnabled = MutableStateFlow(true)
    val isVoiceEnabled: StateFlow<Boolean> = _isVoiceEnabled.asStateFlow()

    private val _currentPersona = MutableStateFlow(VoicePersona.PENDIDIK_BIJAK)
    val currentPersona: StateFlow<VoicePersona> = _currentPersona.asStateFlow()

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    init {
        // Load user audio preferences
        val prefs = context.getSharedPreferences("rpp_audio_prefs", Context.MODE_PRIVATE)
        _isSoundEnabled.value = prefs.getBoolean("sfx_enabled", true)
        _isVoiceEnabled.value = prefs.getBoolean("voice_enabled", true)
        val personaName = prefs.getString("persona", VoicePersona.PENDIDIK_BIJAK.name)
        _currentPersona.value = try {
            VoicePersona.valueOf(personaName ?: VoicePersona.PENDIDIK_BIJAK.name)
        } catch (e: Exception) {
            VoicePersona.PENDIDIK_BIJAK
        }

        // Initialize Native Offline TTS
        try {
        try {
            tts = TextToSpeech(context.applicationContext, this)
        } catch (e: Exception) {
            // Handled gracefully
        }
        } catch (e: Exception) {
            // Handled gracefully
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val supportedLocales = listOf(
                Locale("id", "ID"),
                Locale("in", "ID"),
                Locale("id"),
                Locale("in")
            )
            var languageConfigured = false
            for (loc in supportedLocales) {
                try {
                    val result = tts?.setLanguage(loc)
                    if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                        languageConfigured = true
                        break
                    }
                } catch (_: Exception) {}
            }

            if (!languageConfigured) {
                try {
                    tts?.language = Locale("id", "ID")
                } catch (_: Exception) {}
            }

            try {
                // Use USAGE_MEDIA so audio routes to standard media volume (works seamlessly on Samsung One UI, Xiaomi, Oppo, etc.)
                val audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build()
                tts?.setAudioAttributes(audioAttributes)
            } catch (_: Exception) {}

            applyPersonaToOfflineTts(_currentPersona.value)

            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isSpeaking.value = true
                    _isLoadingVoice.value = false
                    _currentUtteranceId.value = utteranceId
                }

                override fun onDone(utteranceId: String?) {
                    if (_currentUtteranceId.value == utteranceId) {
                        _isSpeaking.value = false
                        _isLoadingVoice.value = false
                        _currentUtteranceId.value = null
                    }
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    if (_currentUtteranceId.value == utteranceId) {
                        _isSpeaking.value = false
                        _isLoadingVoice.value = false
                        _currentUtteranceId.value = null
                    }
                }

                override fun onError(utteranceId: String?, errorCode: Int) {
                    if (_currentUtteranceId.value == utteranceId) {
                        _isSpeaking.value = false
                        _isLoadingVoice.value = false
                        _currentUtteranceId.value = null
                    }
                }
            })
            isTtsReady = true

            // Play pending speech if triggered before initialization
            pendingSpeech?.let { (text, utteranceId) ->
                pendingSpeech = null
                speak(text, utteranceId)
            }
        }
    }

    fun setSoundEnabled(enabled: Boolean) {
        _isSoundEnabled.value = enabled
        context.getSharedPreferences("rpp_audio_prefs", Context.MODE_PRIVATE)
            .edit().putBoolean("sfx_enabled", enabled).apply()
    }

    fun setVoiceEnabled(enabled: Boolean) {
        _isVoiceEnabled.value = enabled
        if (!enabled) {
            stopSpeaking()
        }
        context.getSharedPreferences("rpp_audio_prefs", Context.MODE_PRIVATE)
            .edit().putBoolean("voice_enabled", enabled).apply()
    }

    fun setPersona(persona: VoicePersona) {
        _currentPersona.value = persona
        applyPersonaToOfflineTts(persona)
        context.getSharedPreferences("rpp_audio_prefs", Context.MODE_PRIVATE)
            .edit().putString("persona", persona.name).apply()
    }

    /**
     * Finds and applies the best offline voice matching persona acoustic qualities (intonation, tone, rate).
     * Automatically balances resonance and frequency based on available TTS packages.
     */
    private fun applyPersonaToOfflineTts(persona: VoicePersona) {
        var appliedPitch = persona.pitch
        var appliedRate = persona.speechRate
        var isHighClarityVoiceDetected = false

        try {
            val voices: Set<Voice>? = tts?.voices
            if (!voices.isNullOrEmpty()) {
                val idVoices = voices.filter {
                    val lang = it.locale.language.lowercase()
                    val country = it.locale.country.lowercase()
                    lang == "id" || lang == "in" || country == "id"
                }

                if (idVoices.isNotEmpty()) {
                    var targetVoice: Voice? = null

                    if (persona.preferDeepTone) {
                        targetVoice = idVoices.firstOrNull { voice ->
                            val name = voice.name.lowercase()
                            name.contains("x-idc") || name.contains("x-ide") || name.contains("x-idf") ||
                                name.contains("x-idb") || name.contains("male") || name.contains("man") ||
                                name.contains("pria") || name.contains("ardi") || name.contains("budi") ||
                                name.contains("m0") || name.contains("m1") || name.contains("#male") ||
                                voice.features?.any { it.lowercase().contains("male") } == true
                        }

                        if (targetVoice != null) {
                            appliedPitch = when (persona) {
                                VoicePersona.BINTANG_RADIO -> 0.96f
                                VoicePersona.PAK_ARIS -> 0.86f
                                else -> persona.pitch
                            }
                            appliedRate = persona.speechRate
                        } else {
                            targetVoice = idVoices.firstOrNull()
                            appliedPitch = when (persona) {
                                VoicePersona.BINTANG_RADIO -> 0.90f
                                VoicePersona.PAK_ARIS -> 0.80f
                                else -> persona.pitch
                            }
                            appliedRate = persona.speechRate
                        }
                    } else {
                        // High/bright clarity voice selection
                        targetVoice = idVoices.firstOrNull { voice ->
                            val name = voice.name.lowercase()
                            name.contains("x-dfz") || name.contains("x-ida") || name.contains("x-idd") ||
                                name.contains("female") || name.contains("woman") || name.contains("wanita") ||
                                name.contains("gadis") || name.contains("nur") ||
                                name.contains("f0") || name.contains("f1") || name.contains("#female") ||
                                voice.features?.any { it.lowercase().contains("female") } == true
                        }

                        if (targetVoice != null) {
                            isHighClarityVoiceDetected = true
                        } else {
                            targetVoice = idVoices.firstOrNull { voice ->
                                val name = voice.name.lowercase()
                                val isExplicitDeep = name.contains("x-idc") || name.contains("x-ide") || name.contains("x-idf") ||
                                    name.contains("male") || name.contains("man") || name.contains("pria") ||
                                    voice.features?.any { it.lowercase().contains("male") } == true
                                !isExplicitDeep
                            } ?: idVoices.firstOrNull()
                        }
                    }

                    if (targetVoice != null) {
                        try {
                            tts?.voice = targetVoice
                        } catch (_: Exception) {}
                    }
                }
            }
        } catch (_: Exception) {}

        // Acoustic Formant Calibration for clear & bright registers:
        if (!persona.preferDeepTone) {
            appliedPitch = if (isHighClarityVoiceDetected) {
                when (persona) {
                    VoicePersona.PENDIDIK_BIJAK -> 1.08f
                    VoicePersona.KREATOR_MEDSOS -> 1.25f
                    VoicePersona.SAHABAT_CERIA -> 1.22f
                    VoicePersona.ASISTEN_CERDAS -> 1.08f
                    else -> 1.10f
                }
            } else {
                when (persona) {
                    VoicePersona.PENDIDIK_BIJAK -> 1.24f
                    VoicePersona.KREATOR_MEDSOS -> 1.36f
                    VoicePersona.SAHABAT_CERIA -> 1.40f
                    VoicePersona.ASISTEN_CERDAS -> 1.20f
                    else -> 1.26f
                }
            }
        }

        try {
            tts?.setPitch(appliedPitch)
            tts?.setSpeechRate(appliedRate)
        } catch (_: Exception) {}
    }

    /**
     * Enhanced NLP Text Preprocessor for Natural Indonesian Educational Speech
     * - Expands educational abbreviations (RPP, CP, TP, ATP, KKTP, P5, HOTS, etc.)
     * - Cleans up markdown syntax (headers, asterisks, bullet points, table lines)
     * - Converts roman numerals and class phases to natural phrases
     * - Inserts natural pauses after list items for smooth breathing and cadence
     */
    private fun formatForIndonesianSpeech(text: String): String {
        var cleanText = injectPausesOnRepeats(text)
            // Markdown Headings and bold/italic markers
            .replace(Regex("^#+\\s*", RegexOption.MULTILINE), "")
            .replace(Regex("\\*"), "")
            .replace(Regex("\\*\\*(.*?)\\*\\*"), "$1")
            .replace(Regex("\\*(.*?)\\*"), "$1")
            .replace(Regex("_(.*?)_"), "$1")
            .replace(Regex("`{1,3}.*?`{1,3}"), "")
            .replace(Regex("\\[([^\\]]+)\\]\\([^\\)]+\\)"), "$1")

        // Automatic Phonetic Translation using TtsDictionary
        for ((term, phonetic) in TtsDictionary.termMap) {
            cleanText = cleanText.replace(Regex("(?i)\\b$term\\b"), phonetic)
        }

        // Educational Abbreviations Expansion
        return cleanText
            // LaTeX and symbols replacement for natural speech
            .replace(Regex("\\$\\s*\\\\(?:right|left|Right|Left)?arrow\\s*\\$|\\\\(?:right|left|Right|Left)?arrow|\\$\\s*\\\\to\\s*\\$|\\\\to"), " menjadi ")
            .replace(Regex("\\$\\s*\\\\(?:Leftrightarrow|leftrightarrow)\\s*\\$|\\\\(?:Leftrightarrow|leftrightarrow)"), " bolak-balik ")
            .replace(Regex("(?<=\\S)\\s*[-=]>\\s*|\\s*→\\s*|\\s*⇒\\s*"), " menjadi ")
            .replace(Regex("\\$\\s*\\\\times\\s*\\$|\\\\times|×"), " kali ")
            .replace(Regex("\\$\\s*\\\\div\\s*\\$|\\\\div|÷"), " bagi ")
            .replace(Regex("\\$\\s*\\\\pm\\s*\\$|\\\\pm|±"), " plus minus ")
            .replace(Regex("\\$"), "")
            .replace(Regex("\\bRPP\\b", RegexOption.IGNORE_CASE), "R P P")
            .replace(Regex("\\bModul Ajar\\b", RegexOption.IGNORE_CASE), "Modul Ajar")
            .replace(Regex("\\bCP\\b", RegexOption.IGNORE_CASE), "Capaian Pembelajaran")
            .replace(Regex("\\bTP\\b", RegexOption.IGNORE_CASE), "Tujuan Pembelajaran")
            .replace(Regex("\\bATP\\b", RegexOption.IGNORE_CASE), "Alur Tujuan Pembelajaran")
            .replace(Regex("\\bKKTP\\b", RegexOption.IGNORE_CASE), "Kriteria Ketercapaian Tujuan Pembelajaran")
            .replace(Regex("\\bPROTA\\b", RegexOption.IGNORE_CASE), "Program Tahunan")
            .replace(Regex("\\bPROMES\\b", RegexOption.IGNORE_CASE), "Program Semester")
            .replace(Regex("\\bP5\\b", RegexOption.IGNORE_CASE), "P 5, Projek Penguatan Profil Pelajar Pancasila")
            .replace(Regex("\\bLKPD\\b", RegexOption.IGNORE_CASE), "L K P D, Lembar Kerja Peserta Didik")
            .replace(Regex("\\bKOSP\\b", RegexOption.IGNORE_CASE), "Kurikulum Operasional Satuan Pendidikan")
            .replace(Regex("\\bPTK\\b", RegexOption.IGNORE_CASE), "Penelitian Tindakan Kelas")
            .replace(Regex("\\bHOTS\\b", RegexOption.IGNORE_CASE), "Keterampilan Berpikir Tingkat Tinggi HOTS")
            .replace(Regex("\\bLOTS\\b", RegexOption.IGNORE_CASE), "Keterampilan Berpikir Tingkat Dasar LOTS")
            .replace(Regex("\\bJP\\b", RegexOption.IGNORE_CASE), "Jam Pelajaran")
            .replace(Regex("\\bNIP\\b", RegexOption.IGNORE_CASE), "N I P")
            .replace(Regex("\\bSD\\b", RegexOption.IGNORE_CASE), "S D")
            .replace(Regex("\\bSMP\\b", RegexOption.IGNORE_CASE), "S M P")
            .replace(Regex("\\bSMA\\b", RegexOption.IGNORE_CASE), "S M A")
            .replace(Regex("\\bSMK\\b", RegexOption.IGNORE_CASE), "S M K")
            .replace(Regex("\\bAI\\b", RegexOption.IGNORE_CASE), "A I")
            // Phase and Grades
            .replace(Regex("Fase\\s+([A-F])", RegexOption.IGNORE_CASE), "Fase $1, ")
            .replace(Regex("Kelas\\s+X\\b", RegexOption.IGNORE_CASE), "Kelas 10")
            .replace(Regex("Kelas\\s+XI\\b", RegexOption.IGNORE_CASE), "Kelas 11")
            .replace(Regex("Kelas\\s+XII\\b", RegexOption.IGNORE_CASE), "Kelas 12")
            .replace(Regex("Kelas\\s+IX\\b", RegexOption.IGNORE_CASE), "Kelas 9")
            .replace(Regex("Kelas\\s+VIII\\b", RegexOption.IGNORE_CASE), "Kelas 8")
            .replace(Regex("Kelas\\s+VII\\b", RegexOption.IGNORE_CASE), "Kelas 7")
            .replace(Regex("Kelas\\s+VI\\b", RegexOption.IGNORE_CASE), "Kelas 6")
            .replace(Regex("Kelas\\s+V\\b", RegexOption.IGNORE_CASE), "Kelas 5")
            .replace(Regex("Kelas\\s+IV\\b", RegexOption.IGNORE_CASE), "Kelas 4")
            .replace(Regex("Kelas\\s+III\\b", RegexOption.IGNORE_CASE), "Kelas 3")
            .replace(Regex("Kelas\\s+II\\b", RegexOption.IGNORE_CASE), "Kelas 2")
            .replace(Regex("Kelas\\s+I\\b", RegexOption.IGNORE_CASE), "Kelas 1")
            // Lists, bullet points & numbered items (keep the number with comma pause so TTS speaks it)
            .replace(Regex("(?:^|(?<=[\\s(]))(\\d+)\\.\\s+", RegexOption.MULTILINE), "$1, ")
            .replace(Regex("(?:^|(?<=[\\s(]))([a-zA-Z])\\.\\s+", RegexOption.MULTILINE), "$1, ")
            .replace(Regex("^[-*•]\\s*", RegexOption.MULTILINE), ", ")
            .replace(Regex("[|~_=]"), " ")
    }

    /**
     * Splits long text into natural sentence chunks for uninterrupted offline TTS queue
     */
    private fun splitIntoSentenceChunks(text: String, maxChunkSize: Int = 350): List<String> {
        val sentences = text.split(Regex("(?<=[.!?\\n;])\\s+"))
        val chunks = mutableListOf<String>()
        var currentChunk = StringBuilder()

        for (sentence in sentences) {
            val trimmed = sentence.trim()
            if (trimmed.isEmpty()) continue

            if (currentChunk.length + trimmed.length > maxChunkSize && currentChunk.isNotEmpty()) {
                chunks.add(currentChunk.toString().trim())
                currentChunk = StringBuilder()
            }

            if (trimmed.length > maxChunkSize) {
                val commaParts = trimmed.split(Regex("(?<=[,])\\s+"))
                for (part in commaParts) {
                    if (currentChunk.length + part.length > maxChunkSize && currentChunk.isNotEmpty()) {
                        chunks.add(currentChunk.toString().trim())
                        currentChunk = StringBuilder()
                    }
                    currentChunk.append(part).append(" ")
                }
            } else {
                currentChunk.append(trimmed).append(" ")
            }
        }

        if (currentChunk.isNotEmpty()) {
            chunks.add(currentChunk.toString().trim())
        }

        return chunks.filter { it.isNotBlank() }
    }

    /**
     * Speak text using 100% Offline High Quality Native Speech Engine
     */
    fun speak(text: String, utteranceId: String = "ai_speech_${System.currentTimeMillis()}") {
        if (!_isVoiceEnabled.value || text.isBlank()) return

        stopSpeaking()
        val persona = _currentPersona.value
        val cleanText = formatForIndonesianSpeech(text)
        if (cleanText.isBlank()) return

        if (!isTtsReady) {
            pendingSpeech = Pair(cleanText, utteranceId)
            return
        }

        applyPersonaToOfflineTts(persona)
        _isSpeaking.value = true
        _isLoadingVoice.value = false
        _currentUtteranceId.value = utteranceId

        val audioParams = android.os.Bundle().apply {
            putInt(TextToSpeech.Engine.KEY_PARAM_STREAM, android.media.AudioManager.STREAM_MUSIC)
            putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, 1.0f)
        }

        // Simple language splitting: for now just checking for "student centered"
        // In the future this can be improved to use a list of English terms.
        val chunks = splitIntoSentenceChunks(cleanText, 350)
        if (chunks.isEmpty()) {
            tts?.speak(cleanText, TextToSpeech.QUEUE_FLUSH, audioParams, utteranceId)
        } else {
            for (i in chunks.indices) {
                val queueMode = if (i == 0) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD
                val chunkUtteranceId = if (i == chunks.lastIndex) utteranceId else "${utteranceId}_part_$i"
                tts?.speak(chunks[i], queueMode, audioParams, chunkUtteranceId)
            }
        }
    }

    fun stopSpeaking() {
        try {
            tts?.stop()
        } catch (_: Exception) {}
        _isSpeaking.value = false
        _isLoadingVoice.value = false
        _currentUtteranceId.value = null
    }

    /**
     * Synthesizes custom procedural audio in real-time (PCM AudioTrack)
     * 100% Offline, Zero Asset Size, Zero Network Latency.
     */
    fun playSfx(type: AiSfxType) {
        if (!_isSoundEnabled.value) return

        coroutineScope.launch(Dispatchers.Default) {
            try {
                when (type) {
                    AiSfxType.BUTTON_TAP -> playChirp(600.0, 900.0, 45)
                    AiSfxType.AI_START_GENERATING -> playFuturisticRamp(350.0, 1100.0, 220)
                    AiSfxType.AI_SUCCESS -> playSuccessChime()
                    AiSfxType.AI_ERROR -> playChirp(300.0, 150.0, 160)
                    AiSfxType.AI_GREETING -> playGreetingBeep()
                    AiSfxType.DELETE_ALERT -> playChirp(400.0, 200.0, 120)
                }
            } catch (_: Exception) {}
        }
    }

    private fun playChirp(startFreq: Double, endFreq: Double, durationMs: Int) {
        val sampleRate = 44100
        val numSamples = (durationMs * sampleRate) / 1000
        val buffer = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val progress = i.toDouble() / numSamples
            val currentFreq = startFreq + (endFreq - startFreq) * progress
            val envelope = sin(Math.PI * progress) // smooth fade in and out
            val sample = sin(2.0 * Math.PI * i * currentFreq / sampleRate) * envelope * 0.4
            buffer[i] = (sample * Short.MAX_VALUE).toInt().toShort()
        }

        playPcmBuffer(buffer, sampleRate)
    }

    private fun playFuturisticRamp(startFreq: Double, endFreq: Double, durationMs: Int) {
        val sampleRate = 44100
        val numSamples = (durationMs * sampleRate) / 1000
        val buffer = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val progress = i.toDouble() / numSamples
            val currentFreq = startFreq + (endFreq - startFreq) * (progress * progress)
            val envelope = if (progress < 0.1) progress / 0.1 else (1.0 - progress)
            val wave1 = sin(2.0 * Math.PI * i * currentFreq / sampleRate)
            val wave2 = sin(4.0 * Math.PI * i * currentFreq / sampleRate) * 0.25
            val sample = (wave1 + wave2) * envelope * 0.35
            buffer[i] = (sample * Short.MAX_VALUE).toInt().toShort()
        }

        playPcmBuffer(buffer, sampleRate)
    }

    private fun playSuccessChime() {
        val sampleRate = 44100
        val freqs = doubleArrayOf(523.25, 659.25, 783.99, 1046.50)
        val noteDurationMs = 80
        val noteSamples = (noteDurationMs * sampleRate) / 1000
        val totalSamples = noteSamples * freqs.size
        val buffer = ShortArray(totalSamples)

        for (n in freqs.indices) {
            val freq = freqs[n]
            val offset = n * noteSamples
            for (i in 0 until noteSamples) {
                val progress = i.toDouble() / noteSamples
                val envelope = (1.0 - progress) * (if (progress < 0.15) progress / 0.15 else 1.0)
                val sample = sin(2.0 * Math.PI * i * freq / sampleRate) * envelope * 0.45
                buffer[offset + i] = (sample * Short.MAX_VALUE).toInt().toShort()
            }
        }

        playPcmBuffer(buffer, sampleRate)
    }

    private fun playGreetingBeep() {
        val sampleRate = 44100
        val freqs = doubleArrayOf(440.0, 880.0)
        val noteDurationMs = 90
        val noteSamples = (noteDurationMs * sampleRate) / 1000
        val totalSamples = noteSamples * freqs.size
        val buffer = ShortArray(totalSamples)

        for (n in freqs.indices) {
            val freq = freqs[n]
            val offset = n * noteSamples
            for (i in 0 until noteSamples) {
                val progress = i.toDouble() / noteSamples
                val envelope = (1.0 - progress) * 0.4
                val sample = sin(2.0 * Math.PI * i * freq / sampleRate) * envelope
                buffer[offset + i] = (sample * Short.MAX_VALUE).toInt().toShort()
            }
        }

        playPcmBuffer(buffer, sampleRate)
    }

    private fun playPcmBuffer(buffer: ShortArray, sampleRate: Int) {
        try {
            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(buffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(buffer, 0, buffer.size)
            audioTrack.play()

            coroutineScope.launch(Dispatchers.Default) {
                val durationMs = (buffer.size * 1000L) / sampleRate + 50
                kotlinx.coroutines.delay(durationMs)
                audioTrack.release()
            }
        } catch (_: Exception) {}
    }

    fun release() {
        stopSpeaking()
        tts?.shutdown()
        tts = null
    }

    private fun injectPausesOnRepeats(text: String): String {
        val sentences = text.split(Regex("(?<=[.!?])"))
        val processed = mutableListOf<String>()
        val seen = mutableSetOf<String>()
        for (sentence in sentences) {
            val normalized = sentence.trim().lowercase()
            if (normalized.length > 5 && seen.contains(normalized)) {
                processed.add("... ,,, " + sentence)
            } else {
                processed.add(sentence)
                if (normalized.length > 5) seen.add(normalized)
            }
        }
        return processed.joinToString(" ")
    }

    companion object {
        @Volatile
        private var instance: SoundManager? = null

        fun getInstance(context: Context): SoundManager {
            return instance ?: synchronized(this) {
                instance ?: SoundManager(context.applicationContext).also { instance = it }
            }
        }
    }
}
