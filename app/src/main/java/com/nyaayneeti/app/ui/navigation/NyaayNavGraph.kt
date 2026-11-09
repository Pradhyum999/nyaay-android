package com.nyaayneeti.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.nyaayneeti.app.data.model.UserRole
import com.nyaayneeti.app.ui.screens.advocate.AdvocateHomeScreen
import com.nyaayneeti.app.ui.screens.advocate.CauseListScreen
import com.nyaayneeti.app.ui.screens.auth.LanguageSelectScreen
import com.nyaayneeti.app.ui.screens.auth.PhoneAuthScreen
import com.nyaayneeti.app.ui.screens.auth.RoleSelectScreen
import com.nyaayneeti.app.ui.screens.citizen.BuildCaseIntakeScreen
import com.nyaayneeti.app.ui.screens.citizen.CaseRoomScreen
import com.nyaayneeti.app.ui.screens.citizen.CitizenHomeScreen
import com.nyaayneeti.app.ui.screens.consultation.ConsultationScreen
import com.nyaayneeti.app.viewmodel.AIIntakeViewModel
import com.nyaayneeti.app.viewmodel.AuthViewModel
import com.nyaayneeti.app.viewmodel.CaseViewModel

sealed class Screen(val route: String) {
    object LanguageSelect : Screen("language_select")
    object RoleSelect : Screen("role_select")
    object PhoneAuth : Screen("phone_auth")
    object CitizenHome : Screen("citizen_home")
    object BuildCaseIntake : Screen("build_case_intake")
    object CaseRoom : Screen("case_room/{caseId}") {
        fun createRoute(caseId: String) = "case_room/$caseId"
    }
    object AdvocateHome : Screen("advocate_home")
    object CauseList : Screen("cause_list")
    object Consultation : Screen("consultation")
}

@Composable
fun NyaayNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    caseViewModel: CaseViewModel,
    aiViewModel: AIIntakeViewModel
) {
    val currentUser by authViewModel.currentUser.collectAsState()

    val startDestination = when {
        currentUser == null -> Screen.LanguageSelect.route
        currentUser?.role == UserRole.ADVOCATE -> Screen.AdvocateHome.route
        else -> Screen.CitizenHome.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.LanguageSelect.route) {
            LanguageSelectScreen(
                viewModel = authViewModel,
                onLanguageSelected = {
                    navController.navigate(Screen.RoleSelect.route)
                }
            )
        }

        composable(Screen.RoleSelect.route) {
            RoleSelectScreen(
                viewModel = authViewModel,
                onRoleChosen = {
                    navController.navigate(Screen.PhoneAuth.route)
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.PhoneAuth.route) {
            val role by authViewModel.selectedRole.collectAsState()
            PhoneAuthScreen(
                viewModel = authViewModel,
                onAuthSuccess = {
                    val target = if (role == UserRole.ADVOCATE) Screen.AdvocateHome.route else Screen.CitizenHome.route
                    navController.navigate(target) {
                        popUpTo(Screen.LanguageSelect.route) { inclusive = true }
                    }
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.CitizenHome.route) {
            CitizenHomeScreen(
                authViewModel = authViewModel,
                caseViewModel = caseViewModel,
                onStartIntake = {
                    navController.navigate(Screen.BuildCaseIntake.route)
                },
                onOpenCaseRoom = { caseId ->
                    navController.navigate(Screen.CaseRoom.createRoute(caseId))
                },
                onOpenConsultation = {
                    navController.navigate(Screen.Consultation.route)
                }
            )
        }

        composable(Screen.BuildCaseIntake.route) {
            BuildCaseIntakeScreen(
                aiViewModel = aiViewModel,
                authViewModel = authViewModel,
                onCaseRoomCreated = { caseId ->
                    navController.navigate(Screen.CaseRoom.createRoute(caseId)) {
                        popUpTo(Screen.CitizenHome.route)
                    }
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.CaseRoom.route) { backStackEntry ->
            val caseId = backStackEntry.arguments?.getString("caseId") ?: "case-101"
            CaseRoomScreen(
                caseId = caseId,
                caseViewModel = caseViewModel,
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.AdvocateHome.route) {
            AdvocateHomeScreen(
                authViewModel = authViewModel,
                caseViewModel = caseViewModel,
                onOpenCauseList = {
                    navController.navigate(Screen.CauseList.route)
                },
                onOpenCase = { caseId ->
                    navController.navigate(Screen.CaseRoom.createRoute(caseId))
                }
            )
        }

        composable(Screen.CauseList.route) {
            CauseListScreen(
                caseViewModel = caseViewModel,
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Consultation.route) {
            ConsultationScreen(
                onEndCall = {
                    navController.popBackStack()
                }
            )
        }
    }
}
