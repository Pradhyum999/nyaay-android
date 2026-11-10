package com.nyaayneeti.app.ui.screens.student

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
fun StudentHomeScreen(
    authVm: AuthViewModel, internshipVm: InternshipViewModel, forumVm: ForumViewModel,
    onOpenInternship: (String) -> Unit, onOpenForumPost: (String) -> Unit, onNewPost: () -> Unit,
    onOpenNotifications: () -> Unit
) {
    val user by authVm.currentUser.collectAsState()
    var tab by remember { mutableStateOf(0) }

    Scaffold(
        topBar = { NyaayTopAppBar("Student Hub", user?.name ?: "Student", actions = { IconButton(onClick = onOpenNotifications) { Icon(Icons.Default.Notifications, null, tint = TextSub) } }) },
        bottomBar = {
            NavigationBar(containerColor = ObsidianSurface) {
                NavigationBarItem(selected = tab == 0, onClick = { tab = 0 }, icon = { Icon(Icons.Default.Home, null) }, label = { Text("Home") })
                NavigationBarItem(selected = tab == 1, onClick = { tab = 1 }, icon = { Icon(Icons.Default.School, null) }, label = { Text("Internships") })
                NavigationBarItem(selected = tab == 2, onClick = { tab = 2 }, icon = { Icon(Icons.Default.Forum, null) }, label = { Text("Forum") })
                NavigationBarItem(selected = tab == 3, onClick = { tab = 3 }, icon = { Icon(Icons.Default.Person, null) }, label = { Text("Profile") })
            }
        },
        containerColor = ObsidianBackground
    ) { pad ->
        Box(Modifier.fillMaxSize().padding(pad)) {
            when (tab) {
                0 -> StudentHomeTab(internshipVm, onOpenInternship)
                1 -> InternshipListTab(internshipVm, onOpenInternship)
                2 -> ForumFeed(forumVm, onOpenForumPost, onNewPost)
                3 -> ProfileTab(authVm, onSignOut = { authVm.signOut() }, onOpenNotifications = onOpenNotifications)
            }
        }
    }
}
