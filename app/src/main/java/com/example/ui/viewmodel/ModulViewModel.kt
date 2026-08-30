package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.GeminiService
import com.example.data.ai.OfflineAssessmentEngine
import com.example.data.local.AppDatabase
import com.example.data.local.ModulAjarEntity
import com.example.data.local.ModulRepository
import com.example.data.local.ProtaEntity
import com.example.data.local.PromesEntity
import com.example.data.local.AtpEntity
import com.example.data.local.ProtaDao
import com.example.data.local.PromesDao
import com.example.data.local.AtpDao
import com.example.data.model.Fase
import com.example.data.model.GeneratedModulContent
import com.example.data.model.KurikulumMerdekaReferenceData
import com.example.data.model.QuickPreset
import com.example.data.model.ProtaDocument
import com.example.data.model.PromesDocument
import com.example.data.model.AtpDocument
import com.example.data.model.AssessmentDocument
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File

sealed class Screen {
    object Onboarding : Screen()
    object Home : Screen()
    object Wizard : Screen()
    data class Editor(val modulId: Long) : Screen()
    object CPDatabase : Screen()
    object Guide : Screen()
    object P5Project : Screen()
    object AssessmentHots : Screen()
    object Consultant : Screen()
    object Settings : Screen()
    object ProfileSettings : Screen()
    object ProtaPromes : Screen()
    object Atp : Screen()
    object RaporKktp : Screen()
    object ObservationJournal : Screen()
}

sealed class GenerationState {
    object Idle : GenerationState()
    data class Generating(val stepMessage: String) : GenerationState()
    data class Success(val modulId: Long) : GenerationState()
    data class Error(val message: String) : GenerationState()
}

@OptIn(kotlinx.coroutines.FlowPreview::class, kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class ModulViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ModulRepository
    private val protaDao: ProtaDao
    private val promesDao: PromesDao
    private val atpDao: AtpDao
    private val backupManager: com.example.data.backup.BackupManager

    val allProta: Flow<List<ProtaEntity>>
    val allPromes: Flow<List<PromesEntity>>
    val allAtp: Flow<List<AtpEntity>>

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    init {
        val db = AppDatabase.getDatabase(application)
        repository = ModulRepository(db.modulAjarDao())
        protaDao = db.protaDao()
        promesDao = db.promesDao()
        atpDao = db.atpDao()
        backupManager = com.example.data.backup.BackupManager(application, db)

        allProta = protaDao.getAllProta()
        allPromes = promesDao.getAllPromes()
        allAtp = atpDao.getAllAtp()

        seedInitialSampleDataIfEmpty()
    }

    private fun seedInitialSampleDataIfEmpty() {
        viewModelScope.launch {
            repository.modulCount.firstOrNull()?.let { count ->
                if (count == 0) {
                    val sample1Content = com.example.data.ai.OfflineCurriculumEngine.generateCompleteModul(
                        teacherName = "Budi Santoso, S.Pd.",
                        schoolName = "SDN Merdeka Belajar 01",
                        fase = "Fase B",
                        grade = "Kelas 4",
                        subject = "Matematika",
                        topic = "Pecahan Senilai & Perbandingan Pecahan",
                        timeAllocation = "2 JP (2 x 35 Menit)",
                        semester = "Semester 1 (Ganjil)",
                        academicYear = "2024/2025",
                        modelName = "Problem-Based Learning (PBL)",
                        selectedDimensi = listOf("Bernalar Kritis", "Bergotong Royong", "Mandiri"),
                        targetGayaBelajar = listOf("Visual", "Kinestetik"),
                        targetKesiapan = listOf("Perlu Bimbingan", "Berkembang", "Mahir"),
                        additionalNotes = "Gunakan media manipulatif kertas lipat dan gambar kue martabak"
                    )

                    val sample1 = ModulAjarEntity(
                        title = "Matematika - Pecahan Senilai & Perbandingan Pecahan",
                        subject = "Matematika",
                        fase = "Fase B",
                        grade = "Kelas 4",
                        topic = "Pecahan Senilai & Perbandingan Pecahan",
                        timeAllocation = "2 JP (2 x 35 Menit)",
                        teacherName = "Budi Santoso, S.Pd.",
                        schoolName = "SDN Merdeka Belajar 01",
                        semester = "Semester 1 (Ganjil)",
                        academicYear = "2024/2025",
                        modelPembelajaran = "Problem-Based Learning (PBL)",
                        dimensiP3 = "Bernalar Kritis, Bergotong Royong, Mandiri",
                        capaianPembelajaran = "Peserta didik dapat membandingkan dan mengurutkan berbagai pecahan termasuk pecahan campuran, melakukan operasi penjumlahan dan pengurangan pecahan, serta melakukan operasi perkalian dan pembagian pecahan dengan bilangan asli.",
                        tujuanPembelajaran = sample1Content.tujuanPembelajaran,
                        pemahamanBermakna = sample1Content.pemahamanBermakna,
                        pertanyaanPemantik = sample1Content.pertanyaanPemantik,
                        saranaPrasarana = sample1Content.saranaPrasarana,
                        targetPesertaDidik = sample1Content.targetPesertaDidik,
                        kegiatanPendahuluan = sample1Content.kegiatanPendahuluan,
                        kegiatanInti = sample1Content.kegiatanInti,
                        kegiatanPenutup = sample1Content.kegiatanPenutup,
                        diferensiasiKonten = sample1Content.diferensiasiKonten,
                        diferensiasiProses = sample1Content.diferensiasiProses,
                        diferensiasiProduk = sample1Content.diferensiasiProduk,
                        asesmenDiagnostik = sample1Content.asesmenDiagnostik,
                        asesmenFormatif = sample1Content.asesmenFormatif,
                        asesmenSumatif = sample1Content.asesmenSumatif,
                        rubrikPenilaian = sample1Content.rubrikPenilaian,
                        remedialDanPengayaan = sample1Content.remedialDanPengayaan,
                        lkpdDanMateri = sample1Content.lkpdDanMateri,
                        isFavorite = true
                    )

                    val sample2Content = com.example.data.ai.OfflineCurriculumEngine.generateCompleteModul(
                        teacherName = "Siti Rahmawati, M.Pd.",
                        schoolName = "SD Negeri Nusantara",
                        fase = "Fase C",
                        grade = "Kelas 5",
                        subject = "IPAS",
                        topic = "Fotosintesis dan Rantai Makanan Ekosistem",
                        timeAllocation = "3 JP (3 x 35 Menit)",
                        semester = "Semester 1 (Ganjil)",
                        academicYear = "2024/2025",
                        modelName = "Project-Based Learning (PjBL)",
                        selectedDimensi = listOf("Beriman & Berakhlak Mulia", "Kreatif", "Bergotong Royong"),
                        targetGayaBelajar = listOf("Visual", "Auditori", "Kinestetik"),
                        targetKesiapan = listOf("Perlu Bimbingan", "Berkembang", "Mahir"),
                        additionalNotes = "Eksplorasi tanaman di kebun sekolah"
                    )

                    val sample2 = ModulAjarEntity(
                        title = "IPAS - Fotosintesis dan Rantai Makanan Ekosistem",
                        subject = "IPAS",
                        fase = "Fase C",
                        grade = "Kelas 5",
                        topic = "Fotosintesis dan Rantai Makanan Ekosistem",
                        timeAllocation = "3 JP (3 x 35 Menit)",
                        teacherName = "Siti Rahmawati, M.Pd.",
                        schoolName = "SD Negeri Nusantara",
                        semester = "Semester 1 (Ganjil)",
                        academicYear = "2024/2025",
                        modelPembelajaran = "Project-Based Learning (PjBL)",
                        dimensiP3 = "Beriman & Berakhlak Mulia, Kreatif, Bergotong Royong",
                        capaianPembelajaran = "Peserta didik menyelidiki bagaimana hubungan saling ketergantungan antar komponen biotik dan abiotik dapat mempengaruhi kestabilan suatu ekosistem di lingkungan sekitarnya.",
                        tujuanPembelajaran = sample2Content.tujuanPembelajaran,
                        pemahamanBermakna = sample2Content.pemahamanBermakna,
                        pertanyaanPemantik = sample2Content.pertanyaanPemantik,
                        saranaPrasarana = sample2Content.saranaPrasarana,
                        targetPesertaDidik = sample2Content.targetPesertaDidik,
                        kegiatanPendahuluan = sample2Content.kegiatanPendahuluan,
                        kegiatanInti = sample2Content.kegiatanInti,
                        kegiatanPenutup = sample2Content.kegiatanPenutup,
                        diferensiasiKonten = sample2Content.diferensiasiKonten,
                        diferensiasiProses = sample2Content.diferensiasiProses,
                        diferensiasiProduk = sample2Content.diferensiasiProduk,
                        asesmenDiagnostik = sample2Content.asesmenDiagnostik,
                        asesmenFormatif = sample2Content.asesmenFormatif,
                        asesmenSumatif = sample2Content.asesmenSumatif,
                        rubrikPenilaian = sample2Content.rubrikPenilaian,
                        remedialDanPengayaan = sample2Content.remedialDanPengayaan,
                        lkpdDanMateri = sample2Content.lkpdDanMateri,
                        isFavorite = false
                    )

                    repository.insertModul(sample1)
                    repository.insertModul(sample2)
                }
            }
        }
    }

    // Robust Backstack Navigation
    private val prefs = application.getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)
    private val isOnboardingDone = prefs.getBoolean("onboarding_completed", false)
    private val backStack = java.util.ArrayDeque<Screen>().apply {
        add(if (isOnboardingDone) Screen.Home else Screen.Onboarding)
    }
    private val _currentScreen = MutableStateFlow<Screen>(if (isOnboardingDone) Screen.Home else Screen.Onboarding)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    // Search & Filter
    val searchQuery = MutableStateFlow("")
    val selectedSubjectFilter = MutableStateFlow("Semua")
    val showOnlyFavorites = MutableStateFlow(false)

    // Data from Room
    val allModul: StateFlow<List<ModulAjarEntity>> = searchQuery
        .debounce(200)
        .flatMapLatest { query ->
            if (query.isBlank()) repository.allModul else repository.searchModul(query)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Modul for Editor
    private val _activeModul = MutableStateFlow<ModulAjarEntity?>(null)
    val activeModul: StateFlow<ModulAjarEntity?> = _activeModul.asStateFlow()

    // Wizard Form State
    val wizardTeacherName = MutableStateFlow("Budi Santoso, S.Pd.")
    val wizardSchoolName = MutableStateFlow("SDN Merdeka Belajar 01")
    val wizardFase = MutableStateFlow(Fase.FASE_B)
    val wizardGrade = MutableStateFlow("Kelas 4")
    val wizardSubject = MutableStateFlow("Matematika")
    val wizardTopic = MutableStateFlow("Mengenal Pecahan Senilai dan Operasi Hitung")
    val wizardTimeAllocation = MutableStateFlow("2 JP (2 x 35 Menit)")
    val wizardSemester = MutableStateFlow("Semester 1 (Ganjil)")
    val wizardAcademicYear = MutableStateFlow("2024/2025")
    val wizardModel = MutableStateFlow("Problem-Based Learning (PBL)")
    val wizardSelectedDimensi = MutableStateFlow<List<String>>(listOf("Bernalar Kritis", "Bergotong Royong", "Mandiri"))
    val wizardGayaBelajar = MutableStateFlow<List<String>>(listOf("Visual", "Auditori", "Kinestetik"))
    val wizardKesiapan = MutableStateFlow<List<String>>(listOf("Perlu Bimbingan", "Berkembang", "Mahir"))
    val wizardAdditionalNotes = MutableStateFlow("")

    val generationState = MutableStateFlow<GenerationState>(GenerationState.Idle)
    val isEnhancingSection = MutableStateFlow(false)
    val isGeneratingAssessment = MutableStateFlow(false)

    fun navigateTo(screen: Screen) {
        if (_currentScreen.value == screen) return
        if (screen is Screen.Home) {
            backStack.clear()
            backStack.add(Screen.Home)
        } else {
            backStack.addLast(screen)
        }
        _currentScreen.value = screen
        if (screen is Screen.Editor) {
            loadModulById(screen.modulId)
        }
    }

    fun navigateBack() {
        if (backStack.size > 1) {
            backStack.removeLast()
            val previous = backStack.lastOrNull() ?: Screen.Home
            _currentScreen.value = previous
            if (previous is Screen.Editor) {
                loadModulById(previous.modulId)
            }
        } else {
            _currentScreen.value = Screen.Home
        }
    }

    fun loadModulById(id: Long) {
        viewModelScope.launch {
            repository.getModulById(id).collect { modul ->
                _activeModul.value = modul
            }
        }
    }

    fun applyPreset(preset: QuickPreset) {
        val foundFase = Fase.values().firstOrNull { it.code == preset.fase } ?: Fase.FASE_B
        wizardFase.value = foundFase
        wizardGrade.value = preset.grade
        wizardSubject.value = preset.subject
        wizardTopic.value = preset.topic
        wizardTimeAllocation.value = preset.timeAllocation
        wizardModel.value = preset.model
        wizardSelectedDimensi.value = preset.dimensi
        navigateTo(Screen.Wizard)
    }

    fun toggleDimensi(dimensiTitle: String) {
        val current = wizardSelectedDimensi.value.toMutableList()
        if (current.contains(dimensiTitle)) {
            if (current.size > 1) current.remove(dimensiTitle)
        } else {
            current.add(dimensiTitle)
        }
        wizardSelectedDimensi.value = current
    }

    fun toggleGayaBelajar(gaya: String) {
        val current = wizardGayaBelajar.value.toMutableList()
        if (current.contains(gaya)) {
            if (current.size > 1) current.remove(gaya)
        } else {
            current.add(gaya)
        }
        wizardGayaBelajar.value = current
    }

    fun startAIGeneration() {
        viewModelScope.launch {
            generationState.value = GenerationState.Generating("Menganalisis Capaian Pembelajaran & Dimensi P3...")

            val matchedCP = KurikulumMerdekaReferenceData.findMatchingCP(
                wizardSubject.value,
                wizardFase.value.code,
                wizardTopic.value
            )

            generationState.value = GenerationState.Generating("Menyusun Kegiatan Berdiferensiasi & Sintaks ${wizardModel.value}...")

            val result = GeminiService.generateModulAjarAI(
                context = getApplication(),
                teacherName = wizardTeacherName.value,
                schoolName = wizardSchoolName.value,
                fase = wizardFase.value.code,
                grade = wizardGrade.value,
                subject = wizardSubject.value,
                topic = wizardTopic.value,
                timeAllocation = wizardTimeAllocation.value,
                semester = wizardSemester.value,
                academicYear = wizardAcademicYear.value,
                modelName = wizardModel.value,
                selectedDimensi = wizardSelectedDimensi.value,
                targetGayaBelajar = wizardGayaBelajar.value,
                targetKesiapan = wizardKesiapan.value,
                additionalNotes = wizardAdditionalNotes.value
            )

            result.onSuccess { content ->
                val entity = ModulAjarEntity(
                    title = "${wizardSubject.value} - ${wizardTopic.value}",
                    subject = wizardSubject.value,
                    fase = wizardFase.value.code,
                    grade = wizardGrade.value,
                    topic = wizardTopic.value,
                    timeAllocation = wizardTimeAllocation.value,
                    teacherName = wizardTeacherName.value,
                    schoolName = wizardSchoolName.value,
                    semester = wizardSemester.value,
                    academicYear = wizardAcademicYear.value,
                    modelPembelajaran = wizardModel.value,
                    dimensiP3 = wizardSelectedDimensi.value.joinToString(", "),
                    capaianPembelajaran = matchedCP?.capaianText ?: "Peserta didik mampu menguasai kompetensi dasar dan keterampilan analisis terkait materi ${wizardTopic.value}.",
                    tujuanPembelajaran = content.tujuanPembelajaran,
                    pemahamanBermakna = content.pemahamanBermakna,
                    pertanyaanPemantik = content.pertanyaanPemantik,
                    saranaPrasarana = content.saranaPrasarana,
                    targetPesertaDidik = content.targetPesertaDidik,
                    kegiatanPendahuluan = content.kegiatanPendahuluan,
                    kegiatanInti = content.kegiatanInti,
                    kegiatanPenutup = content.kegiatanPenutup,
                    diferensiasiKonten = content.diferensiasiKonten,
                    diferensiasiProses = content.diferensiasiProses,
                    diferensiasiProduk = content.diferensiasiProduk,
                    asesmenDiagnostik = content.asesmenDiagnostik,
                    asesmenFormatif = content.asesmenFormatif,
                    asesmenSumatif = content.asesmenSumatif,
                    rubrikPenilaian = content.rubrikPenilaian,
                    remedialDanPengayaan = content.remedialDanPengayaan,
                    lkpdDanMateri = content.lkpdDanMateri
                )

                val newId = repository.insertModul(entity)
                generationState.value = GenerationState.Success(newId)
                navigateTo(Screen.Editor(newId))
            }.onFailure { error ->
                generationState.value = GenerationState.Error("Gagal menyusun modul: ${error.localizedMessage}")
            }
        }
    }

    fun updateActiveModul(updated: ModulAjarEntity) {
        viewModelScope.launch {
            repository.updateModul(updated)
            _activeModul.value = updated
        }
    }

    fun deleteModul(id: Long) {
        viewModelScope.launch {
            repository.deleteModul(id)
            if (_currentScreen.value is Screen.Editor) {
                navigateTo(Screen.Home)
            }
        }
    }

    fun toggleFavorite(id: Long, isFavorite: Boolean) {
        viewModelScope.launch {
            repository.toggleFavorite(id, isFavorite)
        }
    }

    suspend fun getBackupJsonString(): String? {
        return backupManager.createBackupJson()
    }

    suspend fun performRestore(jsonString: String): Boolean {
        return backupManager.restoreFromBackupFile(jsonString)
    }

    fun enhanceSectionWithAI(sectionName: String, currentContent: String, instruction: String, onComplete: (String) -> Unit) {
        val currentModul = _activeModul.value ?: return
        viewModelScope.launch {
            isEnhancingSection.value = true
            val result = GeminiService.improveSectionAI(
                context = getApplication(),
                sectionName = sectionName,
                currentContent = currentContent,
                instruction = instruction,
                topic = currentModul.topic,
                subject = currentModul.subject,
                fase = currentModul.fase
            )
            isEnhancingSection.value = false
            result.onSuccess { newText ->
                onComplete(newText)
            }
        }
    }

    fun generateKisiKisiHotsWithAI(
        subject: String,
        fase: String,
        grade: String,
        topic: String,
        jenisAsesmen: String,
        semester: String,
        count: Int,
        onResult: (AssessmentDocument) -> Unit
    ) {
        viewModelScope.launch {
            isGeneratingAssessment.value = true
            val result = GeminiService.generateKisiKisiHots(
                context = getApplication(),
                subject = subject,
                fase = fase,
                grade = grade,
                topic = topic,
                jenisAsesmen = jenisAsesmen,
                semester = semester,
                count = count
            )
            isGeneratingAssessment.value = false
            result.onSuccess { doc ->
                onResult(doc)
            }.onFailure {
                val fallbackDoc = OfflineAssessmentEngine.generateAssessment(
                    subject = subject,
                    fase = fase,
                    grade = grade,
                    topic = topic,
                    jenisAsesmen = jenisAsesmen,
                    semester = semester,
                    jumlahSoal = count
                )
                onResult(fallbackDoc)
            }
        }
    }

    suspend fun exportAllModulsJson(): String {
        val moduls = repository.allModul.firstOrNull() ?: emptyList()
        val jsonArray = org.json.JSONArray()
        moduls.forEach { m ->
            val obj = org.json.JSONObject().apply {
                put("title", m.title)
                put("subject", m.subject)
                put("fase", m.fase)
                put("grade", m.grade)
                put("topic", m.topic)
                put("timeAllocation", m.timeAllocation)
                put("semester", m.semester)
                put("academicYear", m.academicYear)
                put("schoolName", m.schoolName)
                put("teacherName", m.teacherName)
                put("modelPembelajaran", m.modelPembelajaran)
                put("dimensiP3", m.dimensiP3)
                put("kompetensiAwal", m.kompetensiAwal)
                put("profilPelajarPancasila", m.profilPelajarPancasila)
                put("capaianPembelajaran", m.capaianPembelajaran)
                put("saranaPrasarana", m.saranaPrasarana)
                put("targetPesertaDidik", m.targetPesertaDidik)
                put("tujuanPembelajaran", m.tujuanPembelajaran)
                put("pemahamanBermakna", m.pemahamanBermakna)
                put("pertanyaanPemantik", m.pertanyaanPemantik)
                put("kegiatanPendahuluan", m.kegiatanPendahuluan)
                put("kegiatanInti", m.kegiatanInti)
                put("kegiatanPenutup", m.kegiatanPenutup)
                put("diferensiasiKonten", m.diferensiasiKonten)
                put("diferensiasiProses", m.diferensiasiProses)
                put("diferensiasiProduk", m.diferensiasiProduk)
                put("asesmenDiagnostik", m.asesmenDiagnostik)
                put("asesmenFormatif", m.asesmenFormatif)
                put("asesmenSumatif", m.asesmenSumatif)
                put("rubrikPenilaian", m.rubrikPenilaian)
                put("remedialDanPengayaan", m.remedialDanPengayaan)
                put("lkpdDanMateri", m.lkpdDanMateri)
            }
            jsonArray.put(obj)
        }
        return jsonArray.toString(2)
    }

    suspend fun importModulsJson(jsonString: String): Int {
        return try {
            val jsonArray = org.json.JSONArray(jsonString)
            var count = 0
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val entity = ModulAjarEntity(
                    title = obj.optString("title", "Modul Import"),
                    subject = obj.optString("subject", "Umum"),
                    fase = obj.optString("fase", "Fase B"),
                    grade = obj.optString("grade", "Kelas 4"),
                    topic = obj.optString("topic", "Materi Import"),
                    timeAllocation = obj.optString("timeAllocation", "2 JP"),
                    teacherName = obj.optString("teacherName", "Guru"),
                    schoolName = obj.optString("schoolName", "Sekolah"),
                    semester = obj.optString("semester", "Semester 1"),
                    academicYear = obj.optString("academicYear", "2024/2025"),
                    modelPembelajaran = obj.optString("modelPembelajaran", "Problem-Based Learning"),
                    dimensiP3 = obj.optString("dimensiP3", "Bernalar Kritis, Mandiri"),
                    kompetensiAwal = obj.optString("kompetensiAwal", "Peserta didik memahami pengetahuan prasyarat terkait topik."),
                    profilPelajarPancasila = obj.optString("profilPelajarPancasila", "Bernalar Kritis, Mandiri"),
                    capaianPembelajaran = obj.optString("capaianPembelajaran", "Memahami konsep materi pembelajaran."),
                    saranaPrasarana = obj.optString("saranaPrasarana", "Buku teks, proyektor, LKPD"),
                    targetPesertaDidik = obj.optString("targetPesertaDidik", "Peserta Didik Reguler (Tipikal)"),
                    tujuanPembelajaran = obj.optString("tujuanPembelajaran", ""),
                    pemahamanBermakna = obj.optString("pemahamanBermakna", ""),
                    pertanyaanPemantik = obj.optString("pertanyaanPemantik", ""),
                    kegiatanPendahuluan = obj.optString("kegiatanPendahuluan", ""),
                    kegiatanInti = obj.optString("kegiatanInti", ""),
                    kegiatanPenutup = obj.optString("kegiatanPenutup", ""),
                    diferensiasiKonten = obj.optString("diferensiasiKonten", ""),
                    diferensiasiProses = obj.optString("diferensiasiProses", ""),
                    diferensiasiProduk = obj.optString("diferensiasiProduk", ""),
                    asesmenDiagnostik = obj.optString("asesmenDiagnostik", ""),
                    asesmenFormatif = obj.optString("asesmenFormatif", ""),
                    asesmenSumatif = obj.optString("asesmenSumatif", ""),
                    rubrikPenilaian = obj.optString("rubrikPenilaian", ""),
                    remedialDanPengayaan = obj.optString("remedialDanPengayaan", ""),
                    lkpdDanMateri = obj.optString("lkpdDanMateri", "")
                )
                repository.insertModul(entity)
                count++
            }
            count
        } catch (e: Exception) {
            -1
        }
    }

    suspend fun saveProta(doc: ProtaDocument): Long {
        val json = moshi.adapter(ProtaDocument::class.java).toJson(doc)
        val entity = ProtaEntity(
            title = doc.title,
            subject = doc.subject,
            fase = doc.fase,
            grade = doc.grade,
            academicYear = doc.academicYear,
            contentJson = json,
            updatedAt = System.currentTimeMillis()
        )
        return protaDao.insertProta(entity)
    }

    suspend fun deleteProta(id: Long) {
        protaDao.deleteProtaById(id)
    }

    fun parseProtaJson(json: String): ProtaDocument? {
        return try {
            moshi.adapter(ProtaDocument::class.java).fromJson(json)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun savePromes(doc: PromesDocument): Long {
        val json = moshi.adapter(PromesDocument::class.java).toJson(doc)
        val entity = PromesEntity(
            title = doc.title,
            subject = doc.subject,
            fase = doc.fase,
            grade = doc.grade,
            semester = doc.semester,
            academicYear = doc.academicYear,
            contentJson = json,
            updatedAt = System.currentTimeMillis()
        )
        return promesDao.insertPromes(entity)
    }

    suspend fun deletePromes(id: Long) {
        promesDao.deletePromesById(id)
    }

    fun parsePromesJson(json: String): PromesDocument? {
        return try {
            moshi.adapter(PromesDocument::class.java).fromJson(json)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun saveAtp(doc: AtpDocument): Long {
        val json = moshi.adapter(AtpDocument::class.java).toJson(doc)
        val entity = AtpEntity(
            title = doc.title,
            subject = doc.subject,
            fase = doc.fase,
            grade = doc.grade,
            contentJson = json,
            updatedAt = System.currentTimeMillis()
        )
        return atpDao.insertAtp(entity)
    }

    suspend fun deleteAtp(id: Long) {
        atpDao.deleteAtpById(id)
    }

    fun parseAtpJson(json: String): AtpDocument? {
        return try {
            moshi.adapter(AtpDocument::class.java).fromJson(json)
        } catch (e: Exception) {
            null
        }
    }
}
