package com.nyaayneeti.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.nyaayneeti.app.data.repository.AuthRepository
import com.nyaayneeti.app.data.repository.CaseRepository
import com.nyaayneeti.app.data.repository.GeminiIntakeRepository
import com.nyaayneeti.app.ui.navigation.NyaayNavGraph
import com.nyaayneeti.app.ui.theme.NyaayTheme
import com.nyaayneeti.app.viewmodel.AIIntakeViewModel
import com.nyaayneeti.app.viewmodel.AuthViewModel
import com.nyaayneeti.app.viewmodel.CaseViewModel

class MainActivity : ComponentActivity() {

    // Repositories
    private val authRepository = AuthRepository()
    private val caseRepository = CaseRepository()
    private val geminiRepository = GeminiIntakeRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        // Apply edge-to-edge before setContent per Google Android Edge-to-Edge Skill
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // ViewModels
        val authViewModel = AuthViewModel(authRepository)
        val caseViewModel = CaseViewModel(caseRepository)
        val aiViewModel = AIIntakeViewModel(geminiRepository, caseRepository)

        setContent {
            NyaayTheme {
                val navController = rememberNavController()
                NyaayNavGraph(
                    navController = navController,
                    authViewModel = authViewModel,
                    caseViewModel = caseViewModel,
                    aiViewModel = aiViewModel
                )
            }
        }
    }
}
