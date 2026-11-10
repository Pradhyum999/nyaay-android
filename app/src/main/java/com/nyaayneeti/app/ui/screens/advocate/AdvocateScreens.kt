package com.nyaayneeti.app.ui.screens.advocate

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nyaayneeti.app.ui.components.NyaayTopAppBar
import com.nyaayneeti.app.ui.screens.forum.ForumFeed
import com.nyaayneeti.app.ui.screens.shared.ProfileTab
import com.nyaayneeti.app.ui.theme.*
import com.nyaayneeti.app.viewmodel.*

@Composable
fun AdvocateHomeScreen(
    authVm: AuthViewModel, caseVm: CaseViewModel, forumVm: ForumViewModel,
    onOpenCase: (String) -> Unit, onOpenCauseList: () -> Unit, onOpenForumPost: (String) -> Unit, onNewPost: () -> Unit,
    onOpenNotifications: () -> Unit, onOpenSubscription: () -> Unit, onOpenConsultations: () -> Unit,
    onNewCase: () -> Unit, onOpenInvoices: () -> Unit, onOpenInquiries: () -> Unit,
    onOpenECourts: () -> Unit, onOpenAICounsel: () -> Unit, onOpenIpcBns: () -> Unit,
    onOpenVerification: () -> Unit
) {
    val user by authVm.currentUser.collectAsState()
    val cases by caseVm.cases.collectAsState()
    var tab by remember { mutableStateOf(0) }

    Scaffold(
        topBar = { NyaayTopAppBar("Advocate Workstation", user?.name ?: "Advocate", actions = { IconButton(onClick = onOpenNotifications) { Icon(Icons.Default.Notifications, null, tint = TextSub) } }) },
        bottomBar = {
            NavigationBar(containerColor = ObsidianSurface) {
                NavigationBarItem(selected = tab == 0, onClick = { tab = 0 }, icon = { Icon(Icons.Default.Home, null) }, label = { Text("Home") })
                NavigationBarItem(selected = tab == 1, onClick = { tab = 1 }, icon = { Icon(Icons.Default.Folder, null) }, label = { Text("Cases") })
                NavigationBarItem(selected = tab == 2, onClick = { tab = 2 }, icon = { Icon(Icons.Default.Forum, null) }, label = { Text("Forum") })
                NavigationBarItem(selected = tab == 3, onClick = { tab = 3 }, icon = { Icon(Icons.Default.Person, null) }, label = { Text("Profile") })
            }
        },
        containerColor = ObsidianBackground
    ) { pad ->
        Box(Modifier.fillMaxSize().padding(pad)) {
            when (tab) {
                0 -> AdvocateHomeTab(cases, onOpenCauseList, onOpenCase, onNewCase, onOpenInvoices, onOpenInquiries, onOpenECourts, onOpenAICounsel, onOpenIpcBns)
                1 -> AdvocateCasesTab(cases, onOpenCase)
                2 -> ForumFeed(forumVm, onOpenForumPost, onNewPost)
                3 -> ProfileTab(authVm, onSignOut = { authVm.signOut() }, onOpenNotifications = onOpenNotifications, onOpenConsultations = onOpenConsultations, onOpenSubscription = onOpenSubscription, onOpenVerification = onOpenVerification)
            }
        }
    }
}
