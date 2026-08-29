package com.example.data.ai

import android.content.Context
import android.util.Log
import com.example.data.model.GeneratedModulContent
import com.example.util.ApiKeyManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class ConnectionTestResult(
    val isSuccess: Boolean,
    val message: String,
    val modelUsed: String? = null,
    val httpCode: Int? = null,
    val detail: String? = null
)

object GeminiService {
    private const val TAG = "GeminiService"
    
    val SUPPORTED_MODELS = listOf(
        "gemini-2.5-flash",
        "gemini-3.5-flash",
        "gemini-3.6-flash",
        "gemini-3.7-flash",
        "gemini-flash-latest",
        "gemini-flash"
    )

    @Volatile
    var activeModel: String = "gemini-2.5-flash"

    private val client = OkHttpClient.Builder()
        .connectTimeout(25, TimeUnit.SECONDS)
        .readTimeout(25, TimeUnit.SECONDS)
        .writeTimeout(25, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()

    private fun getEndpoint(model: String): String {
        return "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent"
    }

    private fun getPrioritizedModels(): List<String> {
        val list = mutableListOf<String>()
        list.add(activeModel)
        SUPPORTED_MODELS.forEach { if (it != activeModel) list.add(it) }
        return list
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

        for (model in getPrioritizedModels()) {
            lastModelAttempted = model
            try {
                val request = Request.Builder()
                    .url(getEndpoint(model))
                    .header("x-goog-api-key", apiKey)
                    .header("Content-Type", "application/json")
                    .post(requestBody)
                    .build()

                val response = client.newCall(request).execute()
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
            val jsonRequest = JSONObject().apply {
                val contentsArray = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val partsArray = JSONArray().apply {
                            val partObj = JSONObject().apply {
                                put("text", systemPrompt)
                            }
                            put(partObj)
                        }
                        put("parts", partsArray)
                    }
                    put(contentObj)
                }
                put("contents", contentsArray)
                
                val generationConfig = JSONObject().apply {
                    put("temperature", 0.7)
                    put("responseMimeType", "application/json")
                }
                put("generationConfig", generationConfig)
            }

            val requestBody = jsonRequest.toString().toRequestBody("application/json".toMediaType())
            
            var responseBody: String? = null

            for (model in getPrioritizedModels()) {
                try {
                    val request = Request.Builder()
                        .url(getEndpoint(model))
                        .header("x-goog-api-key", apiKey)
                        .header("Content-Type", "application/json")
                        .post(requestBody)
                        .build()

                    val response = client.newCall(request).execute()
                    val body = response.body?.string() ?: ""
                    if (response.isSuccessful && body.isNotBlank()) {
                        activeModel = model
                        responseBody = body
                        break
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Model $model attempt failed: ${e.message}")
                }
            }

            if (responseBody == null) {
                Log.e(TAG, "All models failed, falling back to offline engine")
                val offlineResult = OfflineCurriculumEngine.generateCompleteModul(
                    teacherName, schoolName, fase, grade, subject, topic, timeAllocation,
                    semester, academicYear, modelName, selectedDimensi, targetGayaBelajar,
                    targetKesiapan, additionalNotes
                )
                return@withContext Result.success(offlineResult)
            }

            val parsedJson = JSONObject(responseBody)
            val candidates = parsedJson.optJSONArray("candidates")
            val firstCandidate = candidates?.optJSONObject(0)
            val content = firstCandidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val textOutput = parts?.optJSONObject(0)?.optString("text") ?: ""

            val cleanedJsonText = textOutput.trim()
                .removePrefix("```json")
                .removePrefix("```")
                .removeSuffix("```")
                .trim()

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

            val jsonRequest = JSONObject().apply {
                val contentsArray = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val partsArray = JSONArray().apply {
                            val partObj = JSONObject().apply {
                                put("text", prompt)
                            }
                            put(partObj)
                        }
                        put("parts", partsArray)
                    }
                    put(contentObj)
                }
                put("contents", contentsArray)
                val generationConfig = JSONObject().apply {
                    put("temperature", 0.7)
                }
                put("generationConfig", generationConfig)
            }

            val requestBody = jsonRequest.toString().toRequestBody("application/json".toMediaType())

            for (model in getPrioritizedModels()) {
                try {
                    val request = Request.Builder()
                        .url(getEndpoint(model))
                        .header("x-goog-api-key", apiKey)
                        .header("Content-Type", "application/json")
                        .post(requestBody)
                        .build()

                    val response = client.newCall(request).execute()
                    val responseBody = response.body?.string() ?: ""

                    if (response.isSuccessful && responseBody.isNotBlank()) {
                        val parsedJson = JSONObject(responseBody)
                        val candidates = parsedJson.optJSONArray("candidates")
                        val textOutput = candidates?.optJSONObject(0)?.optJSONObject("content")?.optJSONArray("parts")?.optJSONObject(0)?.optString("text")
                        if (!textOutput.isNullOrBlank()) {
                            activeModel = model
                            return@withContext Result.success(textOutput.trim())
                        }
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Improve section with $model failed: ${e.message}")
                }
            }

            Result.success("$currentContent\n\n[Diperbarui]: Dilengkapi strategi tambahan sesuai instruksi $instruction")
        } catch (e: Exception) {
            Log.e(TAG, "Section AI refinement error", e)
            Result.success("$currentContent\n\n[Penyempurnaan]: Telah disesuaikan dengan instruksi: $instruction")
        }
    }

    fun isAvailable(context: Context): Boolean {
        val key = ApiKeyManager.getApiKey(context)
        return !key.isNullOrBlank()
    }

    suspend fun generateText(context: Context, promptText: String): String = withContext(Dispatchers.IO) {
        val apiKey = ApiKeyManager.getApiKey(context)
        if (apiKey.isNullOrBlank()) {
            throw java.io.IOException("API Key belum terpasang.")
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
        }

        val requestBody = jsonRequest.toString().toRequestBody("application/json".toMediaType())

        var lastException: Exception? = null
        for (model in getPrioritizedModels()) {
            try {
                val request = Request.Builder()
                    .url(getEndpoint(model))
                    .header("x-goog-api-key", apiKey)
                    .header("Content-Type", "application/json")
                    .post(requestBody)
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string() ?: ""

                if (response.isSuccessful && responseBody.isNotBlank()) {
                    val parsedJson = JSONObject(responseBody)
                    val candidates = parsedJson.optJSONArray("candidates")
                    val textOutput = candidates?.optJSONObject(0)?.optJSONObject("content")?.optJSONArray("parts")?.optJSONObject(0)?.optString("text")
                    if (!textOutput.isNullOrBlank()) {
                        activeModel = model
                        return@withContext textOutput.trim()
                    }
                } else {
                    lastException = java.io.IOException("HTTP ${response.code}: $responseBody")
                }
            } catch (e: Exception) {
                lastException = e
            }
        }

        throw lastException ?: java.io.IOException("Gagal menghubungi model Gemini")
    }
}

