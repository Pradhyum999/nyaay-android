package com.nyaayneeti.app.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nyaayneeti.app.AppContainer
import com.nyaayneeti.app.data.model.UserRole
import com.nyaayneeti.app.ui.screens.advocate.AdvocateHomeScreen
import com.nyaayneeti.app.ui.screens.advocate.CauseListScreen
import com.nyaayneeti.app.ui.screens.advocate.InvoicesScreen
import com.nyaayneeti.app.ui.screens.advocate.IpcToBnsScreen
import com.nyaayneeti.app.ui.screens.advocate.LawyerAICounselScreen
import com.nyaayneeti.app.ui.screens.advocate.NewCaseScreen
import com.nyaayneeti.app.ui.screens.admin.AdminHomeScreen
import com.nyaayneeti.app.ui.screens.auth.LanguageSelectScreen
import com.nyaayneeti.app.ui.screens.auth.OnboardingScreen
import com.nyaayneeti.app.ui.screens.auth.PhoneAuthScreen
import com.nyaayneeti.app.ui.screens.auth.RoleSelectScreen
import com.nyaayneeti.app.ui.screens.chat.ChatThreadScreen
import com.nyaayneeti.app.ui.screens.citizen.BuildCaseIntakeScreen
import com.nyaayneeti.app.ui.screens.citizen.CaseRoomScreen
import com.nyaayneeti.app.ui.screens.citizen.CitizenHomeScreen
import com.nyaayneeti.app.ui.screens.citizen.LawyerDiscoveryScreen
import com.nyaayneeti.app.ui.screens.citizen.LawyerProfileScreen
import com.nyaayneeti.app.ui.screens.consultation.ConsultationListScreen
import com.nyaayneeti.app.ui.screens.consultation.ConsultationRoomScreen
import com.nyaayneeti.app.ui.screens.firm.FirmHomeScreen
import com.nyaayneeti.app.ui.screens.firm.InternshipApplicationsScreen
import com.nyaayneeti.app.ui.screens.firm.NewInternshipScreen
import com.nyaayneeti.app.ui.screens.forum.ForumPostDetailScreen
import com.nyaayneeti.app.ui.screens.forum.NewPostScreen
import com.nyaayneeti.app.ui.screens.shared.NotificationsScreen
import com.nyaayneeti.app.ui.screens.shared.InquiriesScreen
import com.nyaayneeti.app.ui.screens.shared.ECourtsLookupScreen
import com.nyaayneeti.app.ui.screens.shared.PaymentsScreen
import com.nyaayneeti.app.ui.screens.shared.SubscriptionScreen
import com.nyaayneeti.app.ui.screens.shared.VerificationUploadScreen
import com.nyaayneeti.app.ui.screens.student.InternshipDetailScreen
import com.nyaayneeti.app.ui.screens.student.StudentHomeScreen
import com.nyaayneeti.app.viewmodel.*

object Routes {
    const val LanguageSelect = "language_select"
    const val RoleSelect = "role_select"
    const val PhoneAuth = "phone_auth"
    const val Onboarding = "onboarding"
    const val CitizenHome = "citizen_home"
    const val LawyerHome = "lawyer_home"
    const val FirmHome = "firm_home"
    const val StudentHome = "student_home"
    const val CaseRoom = "case_room/{caseId}"
    const val BuildCaseIntake = "build_case_intake"
    const val Lawyers = "lawyers"
    const val LawyerProfile = "lawyer_profile/{uid}"
    const val Consultation = "consultation/{id}"
    const val ConsultationList = "consultation_list"
    const val Inquiries = "inquiries"
    const val NewCase = "new_case"
    const val Invoices = "invoices"
    const val AdminHome = "admin_home"
    const val Verification = "verification"
    const val ECourts = "ecourts"
    const val LawyerAI = "lawyer_ai"
    const val IpcBns = "ipc_bns"
    const val ChatThread = "chat_thread/{convoId}/{peerUid}"
    const val ForumPost = "forum_post/{postId}"
    const val ForumNew = "forum_new"
    const val Internship = "internship/{id}"
    const val InternshipNew = "internship_new"
    const val InternshipApplications = "internship_applications/{id}"
    const val Notifications = "notifications"
    const val Subscription = "subscription"
    const val Payments = "payments"
    const val CauseList = "cause_list"

    fun caseRoom(caseId: String) = "case_room/$caseId"
    fun lawyerProfile(uid: String) = "lawyer_profile/$uid"
    fun consultation(id: String) = "consultation/$id"
    fun chatThread(convoId: String, peerUid: String) = "chat_thread/$convoId/$peerUid"
    fun forumPost(postId: String) = "forum_post/$postId"
    fun internship(id: String) = "internship/$id"
    fun internshipApplications(id: String) = "internship_applications/$id"
}

private fun homeFor(role: UserRole): String = when (role) {
    UserRole.CITIZEN -> Routes.CitizenHome
    UserRole.INDEPENDENT_LAWYER -> Routes.LawyerHome
    UserRole.FIRM_ADMIN, UserRole.FIRM_MEMBER -> Routes.FirmHome
    UserRole.STUDENT -> Routes.StudentHome
    UserRole.PLATFORM_ADMIN -> Routes.AdminHome
}

@Composable
fun NyaayNavGraph(container: AppContainer) {
    val navController = rememberNavController()
    val factory = NyaayVMFactory(container)

    val authVm: AuthViewModel = viewModel(factory = factory)
    val sessionLoaded by authVm.sessionLoaded.collectAsState()
    val currentUser by authVm.currentUser.collectAsState()

    if (!sessionLoaded) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = androidx.compose.ui.graphics.Color(0xFFF59E0B))
        }
        return
    }

    val startDestination = when {
        currentUser == null -> Routes.LanguageSelect
        !currentUser!!.onboardingCompleted -> Routes.RoleSelect
        else -> homeFor(currentUser!!.role)
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Routes.LanguageSelect) {
            LanguageSelectScreen(vm = authVm, onDone = { navController.navigate(Routes.PhoneAuth) })
        }
        composable(Routes.PhoneAuth) {
            PhoneAuthScreen(vm = authVm, onDone = {
                val u = authVm.currentUser.value
                if (u != null && !u.onboardingCompleted) navController.navigate(Routes.RoleSelect) {
                    popUpTo(Routes.LanguageSelect) { inclusive = true }
                } else {
                    navController.navigate(homeFor(u?.role ?: UserRole.CITIZEN)) {
                        popUpTo(Routes.LanguageSelect) { inclusive = true }
                    }
                }
            }, onBack = { navController.popBackStack() })
        }
        composable(Routes.RoleSelect) {
            LaunchedEffect(Unit) {
                authVm.currentUser.value?.let { authVm.selectRole(it.role) }
            }
            RoleSelectScreen(vm = authVm, onDone = {
                authVm.applyRole { navController.navigate(Routes.Onboarding) }
            }, onBack = { navController.popBackStack() })
        }
        composable(Routes.Onboarding) {
            val role by authVm.role.collectAsState()
            val obVm: OnboardingViewModel = viewModel(factory = factory)
            OnboardingScreen(vm = obVm, role = role, onDone = {
                navController.navigate(homeFor(role)) { popUpTo(Routes.Onboarding) { inclusive = true } }
            })
        }
        composable(Routes.CitizenHome) {
            val caseVm: CaseViewModel = viewModel(factory = factory)
            val chatVm: ChatViewModel = viewModel(factory = factory)
            val forumVm: ForumViewModel = viewModel(factory = factory)
            CitizenHomeScreen(
                authVm = authVm, caseVm = caseVm, chatVm = chatVm, forumVm = forumVm,
                onOpenCase = { navController.navigate(Routes.caseRoom(it)) },
                onStartIntake = { navController.navigate(Routes.BuildCaseIntake) },
                onOpenChat = { c, p -> navController.navigate(Routes.chatThread(c, p)) },
                onOpenForumPost = { navController.navigate(Routes.forumPost(it)) },
                onNewPost = { navController.navigate(Routes.ForumNew) },
                onOpenLawyers = { navController.navigate(Routes.Lawyers) },
                onOpenNotifications = { navController.navigate(Routes.Notifications) },
                onOpenConsultations = { navController.navigate(Routes.ConsultationList) }
            )
        }
        composable(Routes.LawyerHome) {
            val caseVm: CaseViewModel = viewModel(factory = factory)
            val forumVm: ForumViewModel = viewModel(factory = factory)
            AdvocateHomeScreen(
                authVm = authVm, caseVm = caseVm, forumVm = forumVm,
                onOpenCase = { navController.navigate(Routes.caseRoom(it)) },
                onOpenCauseList = { navController.navigate(Routes.CauseList) },
                onOpenForumPost = { navController.navigate(Routes.forumPost(it)) },
                onNewPost = { navController.navigate(Routes.ForumNew) },
                onOpenNotifications = { navController.navigate(Routes.Notifications) },
                onOpenSubscription = { navController.navigate(Routes.Subscription) },
                onOpenConsultations = { navController.navigate(Routes.ConsultationList) },
                onNewCase = { navController.navigate(Routes.NewCase) },
                onOpenInvoices = { navController.navigate(Routes.Invoices) },
                onOpenInquiries = { navController.navigate(Routes.Inquiries) },
                onOpenECourts = { navController.navigate(Routes.ECourts) },
                onOpenAICounsel = { navController.navigate(Routes.LawyerAI) },
                onOpenIpcBns = { navController.navigate(Routes.IpcBns) },
                onOpenVerification = { navController.navigate(Routes.Verification) }
            )
        }
        composable(Routes.FirmHome) {
            val firmVm: FirmViewModel = viewModel(factory = factory)
            val caseVm: CaseViewModel = viewModel(factory = factory)
            val internshipVm: InternshipViewModel = viewModel(factory = factory)
            val forumVm: ForumViewModel = viewModel(factory = factory)
            FirmHomeScreen(
                authVm = authVm, firmVm = firmVm, caseVm = caseVm, internshipVm = internshipVm, forumVm = forumVm,
                onOpenCase = { navController.navigate(Routes.caseRoom(it)) },
                onOpenForumPost = { navController.navigate(Routes.forumPost(it)) },
                onNewPost = { navController.navigate(Routes.ForumNew) },
                onOpenNotifications = { navController.navigate(Routes.Notifications) },
                onOpenSubscription = { navController.navigate(Routes.Subscription) },
                onNewInternship = { navController.navigate(Routes.InternshipNew) },
                onOpenApplications = { navController.navigate(Routes.internshipApplications(it)) },
                onOpenConsultations = { navController.navigate(Routes.ConsultationList) },
                onOpenCauseList = { navController.navigate(Routes.CauseList) },
                onNewCase = { navController.navigate(Routes.NewCase) },
                onOpenInvoices = { navController.navigate(Routes.Invoices) },
                onOpenInquiries = { navController.navigate(Routes.Inquiries) },
                onOpenECourts = { navController.navigate(Routes.ECourts) },
                onOpenAICounsel = { navController.navigate(Routes.LawyerAI) },
                onOpenIpcBns = { navController.navigate(Routes.IpcBns) },
                onOpenVerification = { navController.navigate(Routes.Verification) }
            )
        }
        composable(Routes.StudentHome) {
            val internshipVm: InternshipViewModel = viewModel(factory = factory)
            val forumVm: ForumViewModel = viewModel(factory = factory)
            StudentHomeScreen(
                authVm = authVm, internshipVm = internshipVm, forumVm = forumVm,
                onOpenInternship = { navController.navigate(Routes.internship(it)) },
                onOpenForumPost = { navController.navigate(Routes.forumPost(it)) },
                onNewPost = { navController.navigate(Routes.ForumNew) },
                onOpenNotifications = { navController.navigate(Routes.Notifications) }
            )
        }
        composable(Routes.CaseRoom) { back ->
            val caseId = back.arguments?.getString("caseId") ?: return@composable
            val caseVm: CaseViewModel = viewModel(factory = factory)
            val caseAiVm: CaseAIViewModel = viewModel(factory = factory)
            LaunchedEffect(caseId) { caseVm.select(caseId) }
            CaseRoomScreen(caseId = caseId, vm = caseVm, caseAiVm = caseAiVm, authVm = authVm, onBack = { navController.popBackStack() })
        }
        composable(Routes.BuildCaseIntake) {
            val aiVm: AIIntakeViewModel = viewModel(factory = factory)
            BuildCaseIntakeScreen(vm = aiVm, onCaseCreated = { navController.navigate(Routes.caseRoom(it)) { popUpTo(Routes.CitizenHome) } }, onBack = { navController.popBackStack() })
        }
        composable(Routes.Lawyers) {
            val inquiryVm: InquiryViewModel = viewModel(factory = factory)
            LawyerDiscoveryScreen(vm = authVm, inquiryVm = inquiryVm, onOpenLawyer = { navController.navigate(Routes.lawyerProfile(it)) }, onBack = { navController.popBackStack() })
        }
        composable(Routes.LawyerProfile) { back ->
            val uid = back.arguments?.getString("uid") ?: return@composable
            val consVm: ConsultationViewModel = viewModel(factory = factory)
            LawyerProfileScreen(uid = uid, authVm = authVm, consVm = consVm, onBack = { navController.popBackStack() }, onRequested = { navController.navigate(Routes.consultation(it)) })
        }
        composable(Routes.Consultation) { back ->
            val id = back.arguments?.getString("id") ?: return@composable
            val consVm: ConsultationViewModel = viewModel(factory = factory)
            LaunchedEffect(id) { consVm.select(id) }
            ConsultationRoomScreen(id = id, vm = consVm, authVm = authVm, onBack = { navController.popBackStack() })
        }
        composable(Routes.ConsultationList) {
            val consVm: ConsultationViewModel = viewModel(factory = factory)
            ConsultationListScreen(vm = consVm, authVm = authVm, onOpen = { navController.navigate(Routes.consultation(it)) }, onBack = { navController.popBackStack() })
        }
        composable(Routes.ChatThread) { back ->
            val convoId = back.arguments?.getString("convoId") ?: return@composable
            val peerUid = back.arguments?.getString("peerUid") ?: return@composable
            val chatVm: ChatViewModel = viewModel(factory = factory)
            LaunchedEffect(convoId, peerUid) { chatVm.open(convoId, peerUid) }
            ChatThreadScreen(convoId = convoId, vm = chatVm, onBack = { navController.popBackStack() })
        }
        composable(Routes.ForumPost) { back ->
            val postId = back.arguments?.getString("postId") ?: return@composable
            val forumVm: ForumViewModel = viewModel(factory = factory)
            LaunchedEffect(postId) { forumVm.select(postId) }
            ForumPostDetailScreen(vm = forumVm, onBack = { navController.popBackStack() })
        }
        composable(Routes.ForumNew) {
            val forumVm: ForumViewModel = viewModel(factory = factory)
            NewPostScreen(vm = forumVm, onDone = { navController.popBackStack() }, onBack = { navController.popBackStack() })
        }
        composable(Routes.Internship) { back ->
            val id = back.arguments?.getString("id") ?: return@composable
            val internshipVm: InternshipViewModel = viewModel(factory = factory)
            LaunchedEffect(id) { internshipVm.select(id) }
            InternshipDetailScreen(id = id, vm = internshipVm, onBack = { navController.popBackStack() })
        }
        composable(Routes.InternshipNew) {
            val internshipVm: InternshipViewModel = viewModel(factory = factory)
            val firmVm: FirmViewModel = viewModel(factory = factory)
            NewInternshipScreen(vm = internshipVm, firmVm = firmVm, onDone = { navController.popBackStack() }, onBack = { navController.popBackStack() })
        }
        composable(Routes.InternshipApplications) { back ->
            val id = back.arguments?.getString("id") ?: return@composable
            val internshipVm: InternshipViewModel = viewModel(factory = factory)
            LaunchedEffect(id) { internshipVm.select(id) }
            InternshipApplicationsScreen(vm = internshipVm, onBack = { navController.popBackStack() })
        }
        composable(Routes.Notifications) {
            val notifVm: NotificationViewModel = viewModel(factory = factory)
            NotificationsScreen(vm = notifVm, onNavigate = { link ->
                val me = authVm.currentUser.value?.uid
                when {
                    link.startsWith("case_room/") -> navController.navigate(Routes.caseRoom(link.removePrefix("case_room/")))
                    link.startsWith("consultation/") -> navController.navigate(Routes.consultation(link.removePrefix("consultation/")))
                    link.startsWith("chat/") && me != null -> {
                        val cid = link.removePrefix("chat/")
                        val peer = cid.split("|").firstOrNull { it != me } ?: return@NotificationsScreen
                        navController.navigate(Routes.chatThread(cid, peer))
                    }
                    link == "inquiries" -> navController.navigate(Routes.Inquiries)
                    link == "verification" -> navController.navigate(Routes.Verification)
                }
            }, onBack = { navController.popBackStack() })
        }
        composable(Routes.Subscription) {
            val billingVm: BillingViewModel = viewModel(factory = factory)
            SubscriptionScreen(vm = billingVm, onBack = { navController.popBackStack() })
        }
        composable(Routes.Payments) {
            val billingVm: BillingViewModel = viewModel(factory = factory)
            PaymentsScreen(vm = billingVm, onBack = { navController.popBackStack() })
        }
        composable(Routes.CauseList) {
            val caseVm: CaseViewModel = viewModel(factory = factory)
            CauseListScreen(vm = caseVm, authVm = authVm, onBack = { navController.popBackStack() })
        }
        composable(Routes.Inquiries) {
            val inquiryVm: InquiryViewModel = viewModel(factory = factory)
            InquiriesScreen(vm = inquiryVm, authVm = authVm, onOpenCase = { navController.navigate(Routes.caseRoom(it)) }, onBack = { navController.popBackStack() })
        }
        composable(Routes.NewCase) {
            val caseVm: CaseViewModel = viewModel(factory = factory)
            NewCaseScreen(vm = caseVm, onCreated = { navController.navigate(Routes.caseRoom(it)) }, onBack = { navController.popBackStack() })
        }
        composable(Routes.Invoices) {
            val caseVm: CaseViewModel = viewModel(factory = factory)
            InvoicesScreen(vm = caseVm, onBack = { navController.popBackStack() })
        }
        composable(Routes.AdminHome) {
            val verificationVm: VerificationViewModel = viewModel(factory = factory)
            val moderationVm: ModerationViewModel = viewModel(factory = factory)
            AdminHomeScreen(authVm = authVm, verificationVm = verificationVm, moderationVm = moderationVm, onSignOut = { authVm.signOut() })
        }
        composable(Routes.Verification) {
            val verificationVm: VerificationViewModel = viewModel(factory = factory)
            VerificationUploadScreen(vm = verificationVm, authVm = authVm, onBack = { navController.popBackStack() })
        }
        composable(Routes.ECourts) {
            val ecourtsVm: ECourtsViewModel = viewModel(factory = factory)
            ECourtsLookupScreen(vm = ecourtsVm, onBack = { navController.popBackStack() })
        }
        composable(Routes.LawyerAI) {
            val caseAiVm: CaseAIViewModel = viewModel(factory = factory)
            LawyerAICounselScreen(vm = caseAiVm, onBack = { navController.popBackStack() })
        }
        composable(Routes.IpcBns) {
            IpcToBnsScreen(onBack = { navController.popBackStack() })
        }

    }
}

