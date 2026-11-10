package com.nyaayneeti.app.ui.screens.citizen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nyaayneeti.app.ui.components.NyaayTopAppBar
import com.nyaayneeti.app.ui.screens.chat.ChatInbox
import com.nyaayneeti.app.ui.screens.forum.ForumFeed
import com.nyaayneeti.app.ui.screens.shared.ProfileTab
import com.nyaayneeti.app.ui.theme.*
import com.nyaayneeti.app.viewmodel.*

@Composable
fun CitizenHomeScreen(
    authVm: AuthViewModel, caseVm: CaseViewModel, chatVm: ChatViewModel, forumVm: ForumViewModel,
    onOpenCase: (String) -> Unit, onStartIntake: () -> Unit,
    onOpenChat: (String, String) -> Unit, onOpenForumPost: (String) -> Unit, onNewPost: () -> Unit,
    onOpenLawyers: () -> Unit, onOpenNotifications: () -> Unit, onOpenConsultations: () -> Unit
) {
    val user by authVm.currentUser.collectAsState()
    val cases by caseVm.cases.collectAsState()
    var tab by remember { mutableStateOf(0) }

    Scaffold(
        topBar = { NyaayTopAppBar("Citizen Portal", user?.name ?: "Citizen", actions = { IconButton(onClick = onOpenNotifications) { Icon(Icons.Default.Notifications, null, tint = TextSub) } }) },
        bottomBar = {
            NavigationBar(containerColor = ObsidianSurface) {
                NavigationBarItem(selected = tab == 0, onClick = { tab = 0 }, icon = { Icon(Icons.Default.Home, null) }, label = { Text("Home") })
                NavigationBarItem(selected = tab == 1, onClick = { tab = 1 }, icon = { Icon(Icons.Default.Folder, null) }, label = { Text("Cases") })
                NavigationBarItem(selected = tab == 2, onClick = { tab = 2 }, icon = { Icon(Icons.Default.Chat, null) }, label = { Text("Chat") })
                NavigationBarItem(selected = tab == 3, onClick = { tab = 3 }, icon = { Icon(Icons.Default.Forum, null) }, label = { Text("Forum") })
                NavigationBarItem(selected = tab == 4, onClick = { tab = 4 }, icon = { Icon(Icons.Default.Person, null) }, label = { Text("Profile") })
            }
        },
        containerColor = ObsidianBackground
    ) { pad ->
        Box(Modifier.fillMaxSize().padding(pad)) {
            when (tab) {
                0 -> CitizenHomeTab(cases, onStartIntake, onOpenLawyers, onOpenCase)
                1 -> CitizenCasesTab(cases, onOpenCase)
                2 -> ChatInbox(chatVm, onOpenChat)
                3 -> ForumFeed(forumVm, onOpenForumPost, onNewPost = onNewPost)
                4 -> ProfileTab(authVm, onSignOut = { authVm.signOut() }, onOpenNotifications = onOpenNotifications, onOpenConsultations = onOpenConsultations)
            }
        }
    }
}
