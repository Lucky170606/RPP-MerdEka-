package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.AssessmentHotsScreen
import com.example.ui.screens.AtpScreen
import com.example.ui.screens.EditorCanvasScreen
import com.example.ui.screens.GeneratorWizardScreen
import com.example.ui.screens.GuideScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ObservationJournalScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.P5ProjectScreen
import com.example.ui.screens.PedagogicalConsultantScreen
import com.example.ui.screens.ProtaPromesScreen
import com.example.ui.screens.RaporKktpScreen
import com.example.ui.screens.ReferenceDatabaseScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.TeacherProfileScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.ModulViewModel
import com.example.ui.viewmodel.Screen

class MainActivity : ComponentActivity() {
    private val viewModel: ModulViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()

                    // BackHandler global yang cerdas: hanya untuk layar yang tidak punya back handler sendiri
                    BackHandler(enabled = currentScreen !is Screen.Home && currentScreen !is Screen.Onboarding && currentScreen !is Screen.Editor) {
                        viewModel.navigateTo(Screen.Home)
                    }
                    
                    when (val screen = currentScreen) {
                        is Screen.Onboarding -> OnboardingScreen(onFinished = { viewModel.navigateTo(Screen.Home) })
                        is Screen.Home -> HomeScreen(viewModel = viewModel)
                        is Screen.Wizard -> GeneratorWizardScreen(viewModel = viewModel)
                        is Screen.Editor -> EditorCanvasScreen(modulId = screen.modulId, viewModel = viewModel)
                        is Screen.CPDatabase -> ReferenceDatabaseScreen(viewModel = viewModel)
                        is Screen.Guide -> GuideScreen(viewModel = viewModel)
                        is Screen.P5Project -> P5ProjectScreen(viewModel = viewModel)
                        is Screen.AssessmentHots -> AssessmentHotsScreen(viewModel = viewModel)
                        is Screen.Consultant -> PedagogicalConsultantScreen(viewModel = viewModel)
                        is Screen.ProfileSettings -> TeacherProfileScreen(viewModel = viewModel)
                        is Screen.ProtaPromes -> ProtaPromesScreen(viewModel = viewModel, onNavigateBack = { viewModel.navigateTo(Screen.Home) })
                        is Screen.Atp -> AtpScreen(viewModel = viewModel, onNavigateBack = { viewModel.navigateTo(Screen.Home) })
                        is Screen.RaporKktp -> RaporKktpScreen(onNavigateBack = { viewModel.navigateTo(Screen.Home) })
                        is Screen.ObservationJournal -> ObservationJournalScreen(onNavigateBack = { viewModel.navigateTo(Screen.Home) })
                        is Screen.Settings -> SettingsScreen(onNavigateBack = { viewModel.navigateTo(Screen.Home) })
                    }
                }
            }
        }
    }
}
