package com.example.data.ai

import android.content.Context
import android.util.Log
import com.example.data.model.AssessmentDocument
import com.example.data.model.GeneratedModulContent
import com.example.data.model.KisiKisiItem
import com.example.data.model.SoalHotsItem
import com.example.data.model.P5ProjectModul
import com.example.util.ApiKeyManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

data class ConnectionTestResult(
    val isSuccess: Boolean,
    val message: String,
    val modelUsed: String? = null,
    val httpCode: Int? = null,
    val detail: String? = null
)

object GeminiService {
    private const val TAG = "GeminiService"
    
    val SUPPORTED_MODELS = mutableListOf(
        "gemini-3.5-flash",
        "gemini-3.1-flash-lite-preview",
        "gemini-flash-latest",
        "gemini-3.1-pro-preview"
    )

    var activeModel: String = "gemini-3.5-flash"

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()

    private suspend fun Call.awaitResponse(): Response = suspendCancellableCoroutine { continuation ->
        continuation.invokeOnCancellation {
            try {
                cancel()
            } catch (_: Exception) {}
        }
        enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                continuation.resume(response)
            }
            override fun onFailure(call: Call, e: IOException) {
                if (!continuation.isCancelled) {
                    continuation.resumeWithException(e)
                }
            }
        })
    }

    private fun getEndpoint(model: String): String {
        return "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent"
    }

    private fun getPrioritizedModels(): List<String> {
        val list = mutableListOf<String>()
        list.add(activeModel)
        SUPPORTED_MODELS.forEach { if (it != activeModel) list.add(it) }
        return list
    }

    fun cleanJson(rawText: String): String {
        val trimmed = rawText.trim()
            .removePrefix("```json")
            .removePrefix("```JSON")
            .removePrefix("```")
            .removeSuffix("```")
            .trim()
        val firstBrace = trimmed.indexOf('{')
        val lastBrace = trimmed.lastIndexOf('}')
        return if (firstBrace != -1 && lastBrace != -1 && lastBrace > firstBrace) {
            trimmed.substring(firstBrace, lastBrace + 1)
        } else {
            trimmed
        }
    }

    suspend fun executeWithRetryAndFallback(
        context: Context,
        promptText: String,
        isJsonResponse: Boolean = false,
        temperature: Double = 0.5
    ): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = ApiKeyManager.getApiKey(context)
        if (apiKey.isNullOrBlank()) {
            return@withContext Result.failure(java.io.IOException("API Key Gemini belum terpasang."))
        }

        val jsonRequest = JSONObject().apply {
            val contentsArray = JSONArray().apply {
                val contentObj = JSONObject().apply {
                    val partsArray = JSONArray().apply {
                        val partObj = JSONObject().apply {
                            put("text", promptText)
                        }
                        put(partObj)
                    }
                    put("parts", partsArray)
                }
                put(contentObj)
            }
            put("contents", contentsArray)

            val generationConfig = JSONObject().apply {
                put("temperature", temperature)
                put("maxOutputTokens", 8192)
                if (isJsonResponse) {
                    put("responseMimeType", "application/json")
                }
            }
            put("generationConfig", generationConfig)
        }

        val requestBody = jsonRequest.toString().toRequestBody("application/json".toMediaType())

        var lastException: Exception? = null

        for (model in getPrioritizedModels().take(2)) {
            try {
                val request = Request.Builder()
                    .url(getEndpoint(model))
                    .header("x-goog-api-key", apiKey)
                    .header("Content-Type", "application/json")
                    .post(requestBody)
                    .build()

                val response = client.newCall(request).awaitResponse()
                val responseBody = response.body?.string() ?: ""

                if (response.isSuccessful && responseBody.isNotBlank()) {
                    val parsedJson = JSONObject(responseBody)
                    val candidates = parsedJson.optJSONArray("candidates")
                    val textOutput = candidates?.optJSONObject(0)
                        ?.optJSONObject("content")
                        ?.optJSONArray("parts")
                        ?.optJSONObject(0)
                        ?.optString("text")

                    if (!textOutput.isNullOrBlank()) {
                        activeModel = model
                        return@withContext Result.success(textOutput.trim())
                    }
                } else {
                    val httpCode = response.code
                    Log.w(TAG, "Model $model returned HTTP $httpCode: $responseBody")
                    lastException = java.io.IOException("HTTP $httpCode: $responseBody")
                }
            } catch (e: Exception) {
                Log.w(TAG, "Model $model network error: ${e.message}")
                lastException = e
            }
        }

        Result.failure(lastException ?: java.io.IOException("Gagal menghubungi seluruh model Gemini AI."))
    }

    suspend fun testConnection(context: Context, customKey: String? = null): ConnectionTestResult = withContext(Dispatchers.IO) {
        val apiKey = customKey?.trim()?.takeIf { it.isNotBlank() } ?: ApiKeyManager.getApiKey(context)

        if (apiKey.isNullOrBlank()) {
            return@withContext ConnectionTestResult(
                isSuccess = false,
                message = "API Key belum diisi.",
                detail = "Silakan masukkan Google Gemini API Key Anda dari https://aistudio.google.com/"
            )
        }

        val testPayload = JSONObject().apply {
            val contentsArray = JSONArray().apply {
                val contentObj = JSONObject().apply {
                    val partsArray = JSONArray().apply {
                        val partObj = JSONObject().apply {
                            put("text", "Halo! Berikan respons 1 kata: 'Koneksi Sukses'.")
                        }
                        put(partObj)
                    }
                    put("parts", partsArray)
                }
                put(contentObj)
            }
            put("contents", contentsArray)
        }

        val requestBody = testPayload.toString().toRequestBody("application/json".toMediaType())

        var lastHttpCode: Int? = null
        var lastErrorDetail: String? = null
        var lastModelAttempted: String? = null

        for (model in getPrioritizedModels().take(2)) {
            lastModelAttempted = model
            try {
                val request = Request.Builder()
                    .url(getEndpoint(model))
                    .header("x-goog-api-key", apiKey)
                    .header("Content-Type", "application/json")
                    .post(requestBody)
                    .build()

                val response = client.newCall(request).awaitResponse()
                lastHttpCode = response.code
                val bodyString = response.body?.string() ?: ""

                if (response.isSuccessful) {
                    activeModel = model
                    val json = JSONObject(bodyString)
                    val reply = json.optJSONArray("candidates")
                        ?.optJSONObject(0)
                        ?.optJSONObject("content")
                        ?.optJSONArray("parts")
                        ?.optJSONObject(0)
                        ?.optString("text")?.trim() ?: "Terkoneksi"

                    return@withContext ConnectionTestResult(
                        isSuccess = true,
                        message = "Koneksi Berhasil! Gemini AI Aktif.",
                        modelUsed = model,
                        httpCode = response.code,
                        detail = "Respons Server: $reply"
                    )
                } else {
                    var parsedMessage = "Kode HTTP ${response.code}"
                    try {
                        val errJson = JSONObject(bodyString).optJSONObject("error")
                        val msg = errJson?.optString("message")
                        if (!msg.isNullOrBlank()) {
                            parsedMessage = msg
                        }
                    } catch (_: Exception) {
                        parsedMessage = bodyString.take(200)
                    }
                    lastErrorDetail = parsedMessage

                    // If it's pure bad API Key (API_KEY_INVALID), terminate early
                    if (response.code == 400 && (parsedMessage.contains("API_KEY_INVALID", ignoreCase = true) || parsedMessage.contains("API key not valid", ignoreCase = true))) {
                        return@withContext ConnectionTestResult(
                            isSuccess = false,
                            message = "API Key Tidak Valid",
                            modelUsed = model,
                            httpCode = response.code,
                            detail = parsedMessage
                        )
                    }
                    
                    // For 404 (model retired/not available for user) or other 400s, continue testing next models
                    Log.w(TAG, "Model $model returned HTTP ${response.code}: $parsedMessage, trying next fallback model...")
                }
            } catch (e: Exception) {
                Log.w(TAG, "Test failed on model $model: ${e.message}")
                lastErrorDetail = e.localizedMessage
            }
        }

        ConnectionTestResult(
            isSuccess = false,
            message = "Gagal terhubung ke Gemini API (Kode: $lastHttpCode).",
            modelUsed = lastModelAttempted,
            httpCode = lastHttpCode,
            detail = lastErrorDetail ?: "Periksa koneksi internet atau kuota Google AI Studio Anda."
        )
    }

    suspend fun generateModulAjarAI(
        context: Context,
        teacherName: String,
        schoolName: String,
        fase: String,
        grade: String,
        subject: String,
        topic: String,
        timeAllocation: String,
        semester: String,
        academicYear: String,
        modelName: String,
        selectedDimensi: List<String>,
        targetGayaBelajar: List<String>,
        targetKesiapan: List<String>,
        additionalNotes: String
    ): Result<GeneratedModulContent> = withContext(Dispatchers.IO) {
        val apiKey = ApiKeyManager.getApiKey(context)

        val systemPrompt = """
            Anda adalah Konsultan Ahli Kurikulum Merdeka Kemendikbudristek RI dan Pakar Pedagogik Pembelajaran Berdiferensiasi.
            Tugas Anda adalah membuat dokumen Modul Ajar (RPP Plus) Kurikulum Merdeka yang sangat lengkap, operasional, berbobot, dan siap cetak.
            
            Informasi Input:
            - Guru: $teacherName
            - Satuan Pendidikan: $schoolName
            - Jenjang / Fase / Kelas: $fase ($grade)
            - Mata Pelajaran: $subject
            - Topik / Materi: $topic
            - Alokasi Waktu: $timeAllocation
            - Semester / Tahun Ajaran: $semester / $academicYear
            - Model Pembelajaran: $modelName
            - Dimensi Profil Pelajar Pancasila: ${selectedDimensi.joinToString(", ")}
            - Gaya Belajar Fokus: ${targetGayaBelajar.joinToString(", ")}
            - Tingkat Kesiapan Siswa: ${targetKesiapan.joinToString(", ")}
            - Catatan Khusus Guru: $additionalNotes

            Harap balas HANYA dengan JSON murni tanpa markdown wrapper (jangan pakai ```json ... ```, langsung kurung kurawal JSON) dengan format key berikut:
            {
              "identitas": "String rincian kop identitas modul ajar",
              "kompetensiAwal": "String pengetahuan prasyarat",
              "profilPelajarPancasila": "String 2-4 dimensi P3 terpilih beserta keterangannya",
              "saranaPrasarana": "String media, alat peraga, sumber belajar",
              "targetPesertaDidik": "String target reguler, pencapaian tinggi, dan berkesulitan",
              "modelPembelajaran": "String model dan metode pembelajaran",
              "tujuanPembelajaran": "String 2-4 butir tujuan pembelajaran spesifik berbasis Taksonomi Bloom",
              "pemahamanBermakna": "String manfaat pembelajaran dalam kehidupan nyata",
              "pertanyaanPemantik": "String 3 pertanyaan pemantik diskusi",
              "kegiatanPendahuluan": "String langkah pendahuluan terstruktur dengan estimasi menit",
              "kegiatanInti": "String langkah inti terstruktur mengikuti sintaks model $modelName",
              "kegiatanPenutup": "String langkah penutup, refleksi 3-2-1, dan tindak lanjut",
              "diferensiasiKonten": "String diferensiasi konten (visual, auditori, kinestetik)",
              "diferensiasiProses": "String diferensiasi proses berdasarkan kesiapan belajar siswa",
              "diferensiasiProduk": "String pilihan tugas/unjuk kerja variatif sesuai minat",
              "asesmenDiagnostik": "String instrumen asesmen diagnostik kognitif & non-kognitif",
              "asesmenFormatif": "String asesmen formatif selama proses, checklist, dan observasi",
              "asesmenSumatif": "String tes tertulis / proyek sumatif",
              "rubrikPenilaian": "String tabel rubrik penilaian berjenjang (Skala 1 s.d 4)",
              "remedialDanPengayaan": "String strategi remedial dan program pengayaan",
              "lkpdDanMateri": "String draf Lembar Kerja Peserta Didik (LKPD) yang dapat dikerjakan siswa"
            }
        """.trimIndent()

        if (apiKey.isNullOrBlank()) {
            Log.w(TAG, "GEMINI_API_KEY is not configured, fallback to high-quality Offline Engine")
            val offlineResult = OfflineCurriculumEngine.generateCompleteModul(
                teacherName, schoolName, fase, grade, subject, topic, timeAllocation,
                semester, academicYear, modelName, selectedDimensi, targetGayaBelajar,
                targetKesiapan, additionalNotes
            )
            return@withContext Result.success(offlineResult)
        }

        try {
            val executionResult = executeWithRetryAndFallback(context, systemPrompt, isJsonResponse = true, temperature = 0.6)
            if (executionResult.isFailure) {
                Log.e(TAG, "All models failed, falling back to offline engine: ${executionResult.exceptionOrNull()?.message}")
                val offlineResult = OfflineCurriculumEngine.generateCompleteModul(
                    teacherName, schoolName, fase, grade, subject, topic, timeAllocation,
                    semester, academicYear, modelName, selectedDimensi, targetGayaBelajar,
                    targetKesiapan, additionalNotes
                )
                return@withContext Result.success(offlineResult)
            }

            val textOutput = executionResult.getOrThrow()
            val cleanedJsonText = cleanJson(textOutput)

            val modulJson = JSONObject(cleanedJsonText)

            val generated = GeneratedModulContent(
                identitas = modulJson.optString("identitas", "Penyusun: $teacherName | $schoolName | $subject ($fase)"),
                kompetensiAwal = modulJson.optString("kompetensiAwal", "Peserta didik memahami pengetahuan prasyarat terkait $topic."),
                profilPelajarPancasila = modulJson.optString("profilPelajarPancasila", selectedDimensi.joinToString("\n")),
                saranaPrasarana = modulJson.optString("saranaPrasarana", "Buku Teks, Proyektor, LKPD, Lembar Observasi."),
                targetPesertaDidik = modulJson.optString("targetPesertaDidik", "Peserta didik reguler, dengan pencapaian tinggi, dan peserta didik yang membutuhkan bimbingan khusus."),
                modelPembelajaran = modulJson.optString("modelPembelajaran", modelName),
                tujuanPembelajaran = modulJson.optString("tujuanPembelajaran", "1. Memahami konsep $topic\n2. Menganalisis penerapan $topic"),
                pemahamanBermakna = modulJson.optString("pemahamanBermakna", "Pemahaman mengenai $topic dapat diterapkan dalam kehidupan bermasyarakat."),
                pertanyaanPemantik = modulJson.optString("pertanyaanPemantik", "1. Mengapa penting memahami $topic?"),
                kegiatanPendahuluan = modulJson.optString("kegiatanPendahuluan", "Orientasi, Apersepsi, Motivasi (15 menit)."),
                kegiatanInti = modulJson.optString("kegiatanInti", "Kegiatan inti sintaks $modelName."),
                kegiatanPenutup = modulJson.optString("kegiatanPenutup", "Simpulan, Refleksi 3-2-1, Doa Penutup (15 menit)."),
                diferensiasiKonten = modulJson.optString("diferensiasiKonten", "Materi visual, audio, dan benda konkret."),
                diferensiasiProses = modulJson.optString("diferensiasiProses", "Bimbingan bertingkat (scaffolding)."),
                diferensiasiProduk = modulJson.optString("diferensiasiProduk", "Pilihan tugas poster, presentasi, atau laporan tertulis."),
                asesmenDiagnostik = modulJson.optString("asesmenDiagnostik", "Asesmen diagnostik awal kognitif dan non-kognitif."),
                asesmenFormatif = modulJson.optString("asesmenFormatif", "Observasi sikap dan penilaian kinerja LKPD."),
                asesmenSumatif = modulJson.optString("asesmenSumatif", "Tes tertulis dan penilaian produk proyek."),
                rubrikPenilaian = modulJson.optString("rubrikPenilaian", "Rubrik kriteria skala 1 sampai 4."),
                remedialDanPengayaan = modulJson.optString("remedialDanPengayaan", "Remedial untuk yang belum tuntas, pengayaan materi HOTS."),
                lkpdDanMateri = modulJson.optString("lkpdDanMateri", "Lembar Kerja Peserta Didik terkait $topic.")
            )

            Result.success(generated)
        } catch (e: Exception) {
            Log.e(TAG, "Error generating AI content, falling back to offline engine", e)
            val offlineResult = OfflineCurriculumEngine.generateCompleteModul(
                teacherName, schoolName, fase, grade, subject, topic, timeAllocation,
                semester, academicYear, modelName, selectedDimensi, targetGayaBelajar,
                targetKesiapan, additionalNotes
            )
            Result.success(offlineResult)
        }
    }

    suspend fun improveSectionAI(
        context: Context,
        sectionName: String,
        currentContent: String,
        instruction: String,
        topic: String,
        subject: String,
        fase: String
    ): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = ApiKeyManager.getApiKey(context)
        if (apiKey.isNullOrBlank()) {
            val enhanced = when {
                instruction.contains("soal", ignoreCase = true) || instruction.contains("asesmen", ignoreCase = true) -> {
                    """
                    $currentContent
                    
                    --- TAMBAHAN BUTIR SOAL HOTS & RUBRIK ---
                    1. (Pilihan Ganda HOTS): Berdasarkan konsep $topic, manakah analisis skenario yang paling tepat jika terjadi perubahan kondisi lingkungan?
                       A. Nilai variabel berbanding terbalik
                       B. Terjadi ekuilibrium baru secara bertahap
                       C. Tidak berpengaruh terhadap sistem
                       D. Terjadi reduksi instan
                       Kunci: B (Pembahasan: Sistem beradaptasi mencari keseimbangan baru).
                    2. (Uraian Reflektif): Jelaskan 2 contoh nyata penerapan $topic dalam mengatasi masalah di lingkungan sekitarmu!
                    """.trimIndent()
                }
                instruction.contains("diferensiasi", ignoreCase = true) -> {
                    """
                    $currentContent
                    
                    --- PENDALAMAN DIFERENSIASI GAYA BELAJAR ---
                    • Diferensiasi Gaya Belajar Visual: Disediakan bagan konsep berwarna, komik edukasi, dan slide presentasi interaktif.
                    • Diferensiasi Gaya Belajar Auditori: Penjelasan lisan lewat podcast rekaman guru dan forum diskusi terbuka.
                    • Diferensiasi Gaya Belajar Kinestetik: Simulasi peran (role-playing), eksperimen langsung, dan penyusunan kartu puzzle konsep.
                    """.trimIndent()
                }
                else -> {
                    """
                    $currentContent
                    
                    • Catatan Penguatan Tambahan: Guru memastikan setiap peserta didik terlibat aktif sesuai profil belajarnya, memberikan umpan balik konstruktif langsung, dan mendokumentasikan perkembangan capaian kompetensi secara objektif.
                    """.trimIndent()
                }
            }
            return@withContext Result.success(enhanced)
        }

        try {
            val prompt = """
                Anda adalah asisten ahli Kurikulum Merdeka Kemendikbudristek.
                Mata Pelajaran: $subject ($fase) | Topik: $topic
                Bagian Modul: $sectionName
                
                Isi Bagian Saat Ini:
                $currentContent
                
                Instruksi Guru:
                $instruction
                
                Tuliskan kembali atau kembangkan isi bagian ini secara terstruktur, profesional, dan siap cetak sesuai kaidah Kurikulum Merdeka. Tuliskan teks hasilnya secara langsung tanpa pembuka/penutup basa-basi.
            """.trimIndent()

            val result = executeWithRetryAndFallback(context, prompt, isJsonResponse = false, temperature = 0.7)
            if (result.isSuccess) {
                return@withContext result
            }

            Result.success("$currentContent\n\n[Diperbarui]: Dilengkapi strategi tambahan sesuai instruksi $instruction")
        } catch (e: Exception) {
            Log.e(TAG, "Section AI refinement error", e)
            Result.success("$currentContent\n\n[Penyempurnaan]: Telah disesuaikan dengan instruksi: $instruction")
        }
    }

    fun isAvailable(context: Context): Boolean {
        return ApiKeyManager.hasUserApiKey(context)
    }

    suspend fun generateText(context: Context, promptText: String): String = withContext(Dispatchers.IO) {
        val result = executeWithRetryAndFallback(context, promptText, isJsonResponse = false)
        result.getOrThrow()
    }

    suspend fun generateKisiKisiHots(
        context: Context,
        subject: String,
        fase: String,
        grade: String,
        topic: String,
        jenisAsesmen: String,
        semester: String,
        count: Int
    ): Result<AssessmentDocument> = withContext(Dispatchers.IO) {
        val apiKey = ApiKeyManager.getApiKey(context)
        if (apiKey.isNullOrBlank()) {
            Log.w(TAG, "GEMINI_API_KEY is not configured, fallback to high-quality OfflineAssessmentEngine")
            val offlineDoc = OfflineAssessmentEngine.generateAssessment(
                subject = subject,
                fase = fase,
                grade = grade,
                topic = topic,
                jenisAsesmen = jenisAsesmen,
                semester = semester,
                jumlahSoal = count
            )
            return@withContext Result.success(offlineDoc)
        }

        val prompt = """
            Anda adalah Pakar Asesmen Pendidikan Kurikulum Merdeka Kemendikbudristek RI dan Spesialis Butir Soal Berorientasi HOTS (Higher Order Thinking Skills - Level Kognitif C4 Menganalisis, C5 Mengevaluasi, C6 Mencipta).
            Tugas Anda adalah menyusun Dokumen Kisi-Kisi dan Bank Soal HOTS yang lengkap, operasional, berbobot ilmiah/pedagogis tinggi, dan kontekstual.

            Spesifikasi Input:
            - Mata Pelajaran: $subject
            - Fase / Kelas: $fase ($grade)
            - Semester: $semester
            - Topik / Materi Pokok: $topic
            - Jenis Asesmen: $jenisAsesmen
            - Jumlah Soal yang Disusun: $count butir

            Aturan Penyusunan Soal HOTS:
            1. Setiap soal wajib memiliki stimulus naratif/kontekstual yang jelas (misalnya: narasi fenomena alam/sosial, studi kasus, data pengamatan/tabel, atau dilema masalah kontekstual).
            2. Pertanyaan harus menuntut penalaran tingkat tinggi (analisis, evaluasi solusi, prediksi dampak, pemecahan masalah kritis).
            3. Komposisi: Mayoritas Pilihan Ganda (PG) dengan 4 opsi pilihan (A, B, C, D) yang distraktif dan logis, dan sisanya Uraian Analisis Kasus dengan rubrik penskoran.
            4. Kunci jawaban dan pembahasan rasional terperinci.
            5. DUKUNGAN RUMUS & SIMBOL (Sains/Matematika/Fisika/Kimia): Jika materi berkaitan dengan matematika, fisika, atau kimia, tuliskan rumus/persamaan secara jelas menggunakan simbol Unicode (misal: x², H₂O, Δt, √x, ±, α, β, π, ρ, λ, ∫) atau format LaTeX standar dalam kurung dolar seperti ${'$'}f(x) = ax^2 + bx + c${'$'} atau ${'$'}\text{CaCO}_3 + 2\text{HCl} \rightarrow \text{CaCl}_2 + \text{H}_2\text{O} + \text{CO}_2${'$'} agar dapat ter-render rapi dan presisi.
            6. PENTING & MUTLAK: Anda WAJIB menghasilkan TEPAT $count butir soal secara lengkap dari nomor 1 sampai $count di dalam array JSON "kisiKisi" dan "soalList" tanpa terpotong atau dikurangi!

            Harap berikan respons HANYA dalam bentuk JSON murni tanpa markdown wrapper (langsung buka kurung kurawal) dengan format struktur berikut:
            {
              "title": "Kisi-Kisi & Bank Soal Asesmen: $subject - $topic",
              "pedomanPenskoran": "Pedoman penskoran dan Kriteria Ketercapaian Tujuan Pembelajaran (KKTP)...",
              "kisiKisi": [
                {
                  "nomorUrut": 1,
                  "capaianElemen": "Elemen Capaian Pembelajaran",
                  "materiPokok": "Sub-materi terkait $topic",
                  "indikatorSoal": "Disajikan ..., peserta didik dapat ...",
                  "levelKognitif": "C4 (Menganalisis)",
                  "bentukSoal": "Pilihan Ganda (PG)",
                  "nomorSoal": 1
                }
              ],
              "soalList": [
                {
                  "nomor": 1,
                  "bentukSoal": "Pilihan Ganda",
                  "levelKognitif": "C4 (Menganalisis)",
                  "stimulusText": "Teks stimulus naratif kontekstual...",
                  "pertanyaan": "Kalimat pertanyaan HOTS...",
                  "pilihanOpsi": [
                    "A. Opsi jawaban A",
                    "B. Opsi jawaban B",
                    "C. Opsi jawaban C",
                    "D. Opsi jawaban D"
                  ],
                  "kunciJawaban": "A",
                  "pembahasanDanAlasan": "Penjelasan mengapa jawaban tersebut tepat secara konsep...",
                  "skorMaksimal": 10
                }
              ]
            }
        """.trimIndent()

        try {
            val executionResult = executeWithRetryAndFallback(context, prompt, isJsonResponse = true, temperature = 0.4)
            val textOutput = executionResult.getOrThrow()
            val cleanJsonString = cleanJson(textOutput)

            val jsonObject = JSONObject(cleanJsonString)
            val docTitle = jsonObject.optString("title", "Kisi-Kisi & Bank Soal Asesmen: $subject - $topic")
            val pedoman = jsonObject.optString(
                "pedomanPenskoran",
                """
                PEDOMAN PENSKORAN & PENILAIAN NILAI AKHIR (NA):
                1. Nilai Soal Pilihan Ganda = (Jumlah Benar / Jumlah Soal PG) x 100
                2. Nilai Soal Uraian = (Total Skor Perolehan Uraian / Total Skor Maksimal Uraian) x 100
                3. Nilai Akhir (NA) = (60% x Nilai PG) + (40% x Nilai Uraian)
                
                Kriteria Ketercapaian Tujuan Pembelajaran (KKTP):
                • 0 - 65%   : Belum Mencapai TP (Perlu Remedial di seluruh materi)
                • 66 - 75%  : Cukup Mencapai TP (Perlu Remedial di indikator yang belum tuntas)
                • 76 - 88%  : Sudah Mencapai TP (Tuntas)
                • 89 - 100% : Sangat Mahir Mencapai TP (Diberikan Pengayaan/Tantangan HOTS Lanjut)
                """.trimIndent()
            )

            val parsedKisiKisi = mutableListOf<KisiKisiItem>()
            val kisiArray = jsonObject.optJSONArray("kisiKisi") ?: JSONArray()
            for (i in 0 until kisiArray.length()) {
                val item = kisiArray.getJSONObject(i)
                parsedKisiKisi.add(
                    KisiKisiItem(
                        nomorUrut = item.optInt("nomorUrut", item.optInt("no", i + 1)),
                        capaianElemen = item.optString("capaianElemen", "Pemahaman Konseptual $subject"),
                        materiPokok = item.optString("materiPokok", topic),
                        indikatorSoal = item.optString("indikatorSoal", item.optString("indikator", "Menganalisis permasalahan terkait $topic")),
                        levelKognitif = item.optString("levelKognitif", "C4 (Menganalisis)"),
                        bentukSoal = item.optString("bentukSoal", if (i < (count * 0.6).toInt().coerceAtLeast(1)) "Pilihan Ganda (PG)" else "Uraian Analisis Kasus"),
                        nomorSoal = item.optInt("nomorSoal", item.optInt("no", i + 1))
                    )
                )
            }

            val parsedSoal = mutableListOf<SoalHotsItem>()
            val soalArray = jsonObject.optJSONArray("soalList") ?: jsonObject.optJSONArray("soalHots") ?: JSONArray()
            for (i in 0 until soalArray.length()) {
                val item = soalArray.getJSONObject(i)
                val opsiList = mutableListOf<String>()
                val optArray = item.optJSONArray("pilihanOpsi") ?: item.optJSONArray("pilihanJawaban")
                if (optArray != null) {
                    for (j in 0 until optArray.length()) {
                        opsiList.add(optArray.getString(j))
                    }
                }

                val bentuk = item.optString(
                    "bentukSoal",
                    if (opsiList.isNotEmpty()) "Pilihan Ganda" else "Uraian Analisis Kasus"
                )

                parsedSoal.add(
                    SoalHotsItem(
                        nomor = item.optInt("nomor", item.optInt("no", i + 1)),
                        bentukSoal = bentuk,
                        levelKognitif = item.optString("levelKognitif", "C4 (Menganalisis)"),
                        stimulusText = item.optString("stimulusText", "Konteks Permasalahan / Stimulus: $topic"),
                        pertanyaan = item.optString("pertanyaan", "Pertanyaan terkait penerapan materi $topic."),
                        pilihanOpsi = opsiList,
                        kunciJawaban = item.optString("kunciJawaban", "A"),
                        pembahasanDanAlasan = item.optString("pembahasanDanAlasan", "Pembahasan jawaban berdasarkan analisis konsep materi $topic."),
                        skorMaksimal = item.optInt("skorMaksimal", if (opsiList.isNotEmpty()) 10 else 20)
                    )
                )
            }

            if (parsedSoal.isNotEmpty()) {
                val doc = AssessmentDocument(
                    title = docTitle,
                    subject = subject,
                    fase = fase,
                    grade = grade,
                    semester = semester,
                    topikUjian = topic,
                    jenisAsesmen = jenisAsesmen,
                    jumlahSoal = parsedSoal.size,
                    kisiKisiList = if (parsedKisiKisi.isNotEmpty()) parsedKisiKisi else {
                        parsedSoal.mapIndexed { idx, s ->
                            KisiKisiItem(
                                nomorUrut = idx + 1,
                                capaianElemen = "Pemahaman & Penerapan $subject",
                                materiPokok = topic,
                                indikatorSoal = "Disajikan stimulus, peserta didik mampu menyelesaikan persoalan tingkat ${s.levelKognitif}",
                                levelKognitif = s.levelKognitif,
                                bentukSoal = s.bentukSoal,
                                nomorSoal = s.nomor
                            )
                        }
                    },
                    soalList = parsedSoal,
                    pedomanPenskoran = pedoman,
                    isOnlineAiGenerated = true,
                    engineName = "Gemini AI (Online - $activeModel)"
                )
                return@withContext Result.success(doc)
            } else {
                Log.w(TAG, "Empty soal list parsed from Gemini JSON, falling back to offline engine")
                val offlineDoc = OfflineAssessmentEngine.generateAssessment(
                    subject = subject,
                    fase = fase,
                    grade = grade,
                    topic = topic,
                    jenisAsesmen = jenisAsesmen,
                    semester = semester,
                    jumlahSoal = count
                )
                return@withContext Result.success(offlineDoc)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Gemini assessment generation failed: ${e.message}, falling back to offline engine", e)
            val offlineDoc = OfflineAssessmentEngine.generateAssessment(
                subject = subject,
                fase = fase,
                grade = grade,
                topic = topic,
                jenisAsesmen = jenisAsesmen,
                semester = semester,
                jumlahSoal = count
            )
            return@withContext Result.success(offlineDoc)
        }
    }

    suspend fun generateP5Modul(
        context: Context,
        temaTitle: String,
        topikProjek: String,
        fase: String,
        grade: String,
        alokasiWaktu: String,
        selectedDimensi: List<String>,
        teacherName: String,
        schoolName: String
    ): Result<P5ProjectModul> = withContext(Dispatchers.IO) {
        val apiKey = ApiKeyManager.getApiKey(context)
        if (apiKey.isNullOrBlank()) {
            val fallbackModul = OfflineP5Engine.generateP5Modul(
                temaTitle = temaTitle,
                topikProjek = topikProjek,
                fase = fase,
                grade = grade,
                alokasiWaktu = alokasiWaktu,
                selectedDimensi = selectedDimensi,
                teacherName = teacherName,
                schoolName = schoolName
            )
            return@withContext Result.success(fallbackModul)
        }

        val prompt = """
            Anda adalah Konsultan Kurikulum Merdeka & Fasilitator Projek Penguatan Profil Pelajar Pancasila (P5) ahli.
            Buatkan Modul Projek P5 yang komprehensif, mendalam, dan operasional dalam format JSON murni.

            PARAMETER PROJEK P5:
            - Tema P5 Resmi: $temaTitle
            - Topik Spesifik Projek: $topikProjek
            - Fase / Jenjang: $fase (Kelas $grade)
            - Alokasi Waktu: $alokasiWaktu
            - Target Dimensi Profil Pelajar Pancasila: ${selectedDimensi.joinToString(", ")}
            - Guru Penyusun: $teacherName
            - Satuan Pendidikan: $schoolName

            Struktur JSON wajib memiliki key berikut:
            {
              "title": "Judul Modul P5 yang Inspiratif",
              "deskripsiSingkat": "Deskripsi latar belakang dan urgensi pelaksanaan projek P5 ini.",
              "tujuanProjek": "Tujuan akhir dan kompetensi yang ingin dicapai peserta didik.",
              "targetDimensi": ["Dimensi 1", "Dimensi 2"],
              "targetElemen": ["Elemen Sub-Elemen 1", "Elemen Sub-Elemen 2"],
              "timeAllocation": "$alokasiWaktu",
              "alurTahapan": [
                {
                  "tahap": "Tahap 1: Pengenalan",
                  "namaAktivitas": "Nama kegiatan eksplorasi",
                  "alokasiJp": "12 JP",
                  "deskripsiLangkah": "Langkah detail kegiatan pengenalan...",
                  "peranGuru": "Fasilitator & motivator...",
                  "peranSiswa": "Aktif mengamati dan bertanya...",
                  "asesmenFormatif": "Observasi keaktifan"
                },
                {
                  "tahap": "Tahap 2: Kontekstualisasi",
                  "namaAktivitas": "Nama kegiatan kontekstual",
                  "alokasiJp": "16 JP",
                  "deskripsiLangkah": "Langkah detail kontekstualisasi...",
                  "peranGuru": "Pendamping analisis...",
                  "peranSiswa": "Diskusi kelompok...",
                  "asesmenFormatif": "Lembar kerja kelompok"
                },
                {
                  "tahap": "Tahap 3: Aksi Nyata",
                  "namaAktivitas": "Aksi bersama / unjuk karya",
                  "alokasiJp": "24 JP",
                  "deskripsiLangkah": "Langkah pelaksanaan aksi nyata...",
                  "peranGuru": "Mentor pendamping...",
                  "peranSiswa": "Membuat produk / aksi...",
                  "asesmenFormatif": "Rubrik penilaian unjuk kerja"
                },
                {
                  "tahap": "Tahap 4: Refleksi & Tindak Lanjut",
                  "namaAktivitas": "Evaluasi & Refleksi",
                  "alokasiJp": "8 JP",
                  "deskripsiLangkah": "Refleksi akhir dan tindak lanjut berkelanjutan...",
                  "peranGuru": "Reflektor & evaluator...",
                  "peranSiswa": "Menuliskan jurnal refleksi...",
                  "asesmenFormatif": "Jurnal refleksi siswa"
                }
              ],
              "rubrikAsesmen": "Rubrik penilaian perkembangan sub-elemen P5: Belum Berkembang (BB), Mulai Berkembang (MB), Berkembang Sesuai Harapan (BSH), Sangat Berkembang (SB).",
              "lembarRefleksi": "Pertanyaan pemantik refleksi untuk peserta didik dan fasilitator."
            }
        """.trimIndent()

        try {
            val executionResult = executeWithRetryAndFallback(context, prompt, isJsonResponse = true, temperature = 0.5)
            val textOutput = executionResult.getOrThrow()
            val cleanJsonString = cleanJson(textOutput)

            val jsonObject = JSONObject(cleanJsonString)
            val title = jsonObject.optString("title", "Modul Projek P5: $topikProjek")
            val deskripsiSingkat = jsonObject.optString("deskripsiSingkat", "Projek P5 bertema $temaTitle dengan fokus $topikProjek.")
            val tujuanProjek = jsonObject.optString("tujuanProjek", "Peserta didik mampu menginternalisasi nilai Profil Pelajar Pancasila.")
            val rubrikAsesmen = jsonObject.optString("rubrikAsesmen", "Rubrik Asesmen P5 Kurikulum Merdeka.")
            val lembarRefleksi = jsonObject.optString("lembarRefleksi", "Lembar refleksi pelaksanaan projek.")

            val dimensiArray = jsonObject.optJSONArray("targetDimensi")
            val dimensiList = mutableListOf<String>()
            if (dimensiArray != null) {
                for (i in 0 until dimensiArray.length()) {
                    dimensiList.add(dimensiArray.getString(i))
                }
            }
            if (dimensiList.isEmpty()) dimensiList.addAll(selectedDimensi)

            val elemenArray = jsonObject.optJSONArray("targetElemen")
            val elemenList = mutableListOf<String>()
            if (elemenArray != null) {
                for (i in 0 until elemenArray.length()) {
                    elemenList.add(elemenArray.getString(i))
                }
            }
            if (elemenList.isEmpty()) elemenList.addAll(listOf("Akhir kepada Alam", "Kolaborasi", "Memperoleh dan memproses informasi"))

            val alurArray = jsonObject.optJSONArray("alurTahapan")
            val alurList = mutableListOf<com.example.data.model.P5TahapanItem>()
            if (alurArray != null) {
                for (i in 0 until alurArray.length()) {
                    val item = alurArray.getJSONObject(i)
                    alurList.add(
                        com.example.data.model.P5TahapanItem(
                            tahap = item.optString("tahap", "Tahap ${i + 1}"),
                            namaAktivitas = item.optString("namaAktivitas", "Aktivitas Projek"),
                            alokasiJp = item.optString("alokasiJp", "10 JP"),
                            deskripsiLangkah = item.optString("deskripsiLangkah", "Langkah pelaksanaan..."),
                            peranGuru = item.optString("peranGuru", "Fasilitator"),
                            peranSiswa = item.optString("peranSiswa", "Peserta aktif"),
                            asesmenFormatif = item.optString("asesmenFormatif", "Observasi")
                        )
                    )
                }
            }

            if (alurList.isEmpty()) {
                val fallbackModul = OfflineP5Engine.generateP5Modul(
                    temaTitle = temaTitle,
                    topikProjek = topikProjek,
                    fase = fase,
                    grade = grade,
                    alokasiWaktu = alokasiWaktu,
                    selectedDimensi = selectedDimensi,
                    teacherName = teacherName,
                    schoolName = schoolName
                )
                return@withContext Result.success(fallbackModul)
            }

            val modul = P5ProjectModul(
                tema = temaTitle,
                title = title,
                fase = fase,
                grade = grade,
                timeAllocation = alokasiWaktu,
                targetDimensi = dimensiList,
                targetElemen = elemenList,
                deskripsiSingkat = deskripsiSingkat,
                tujuanProjek = tujuanProjek,
                alurTahapan = alurList,
                rubrikAsesmen = rubrikAsesmen,
                lembarRefleksi = lembarRefleksi
            )
            return@withContext Result.success(modul)
        } catch (e: Exception) {
            Log.e(TAG, "Gemini P5 generation failed: ${e.message}, falling back to offline engine", e)
            val fallbackModul = OfflineP5Engine.generateP5Modul(
                temaTitle = temaTitle,
                topikProjek = topikProjek,
                fase = fase,
                grade = grade,
                alokasiWaktu = alokasiWaktu,
                selectedDimensi = selectedDimensi,
                teacherName = teacherName,
                schoolName = schoolName
            )
            return@withContext Result.success(fallbackModul)
        }
    }
}
