package com.nyaayneeti.app.ui.screens.firm

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nyaayneeti.app.data.model.UserRole
import com.nyaayneeti.app.ui.components.NyaayTopAppBar
import com.nyaayneeti.app.ui.screens.advocate.AdvocateCasesTab
import com.nyaayneeti.app.ui.screens.forum.ForumFeed
import com.nyaayneeti.app.ui.screens.shared.ProfileTab
import com.nyaayneeti.app.ui.theme.*
import com.nyaayneeti.app.viewmodel.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FirmHomeScreen(
    authVm: AuthViewModel, firmVm: FirmViewModel, caseVm: CaseViewModel, internshipVm: InternshipViewModel, forumVm: ForumViewModel,
    onOpenCase: (String) -> Unit, onOpenForumPost: (String) -> Unit, onNewPost: () -> Unit,
    onOpenNotifications: () -> Unit, onOpenSubscription: () -> Unit,
    onNewInternship: () -> Unit, onOpenApplications: (String) -> Unit, onOpenConsultations: () -> Unit,
    onOpenCauseList: () -> Unit, onNewCase: () -> Unit, onOpenInvoices: () -> Unit, onOpenInquiries: () -> Unit,
    onOpenECourts: () -> Unit, onOpenAICounsel: () -> Unit, onOpenIpcBns: () -> Unit, onOpenVerification: () -> Unit
) {
    val user by authVm.currentUser.collectAsState()
    val firm by firmVm.firm.collectAsState()
    val members by firmVm.members.collectAsState()
    val cases by caseVm.cases.collectAsState()
    val internships by internshipVm.firmInternships.collectAsState()
    var tab by remember { mutableStateOf(0) }
    val isAdmin = user?.role == UserRole.FIRM_ADMIN
    val firmName = firm?.name?.takeIf { it.isNotBlank() } ?: "Firm"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(firmName, style = MaterialTheme.typography.titleMedium, color = TextMain, fontWeight = FontWeight.Bold)
                        Text(if (isAdmin) "Firm admin" else "Firm member · ${user?.name ?: ""}", style = MaterialTheme.typography.labelSmall, color = GoldPrimary)
                    }
                },
                navigationIcon = {
                    Box(
                        modifier = Modifier.padding(start = 12.dp).size(36.dp).clip(CircleShape).background(GoldPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(firmName.take(1).uppercase(), color = OnPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                },
                actions = {
                    IconButton(onClick = onOpenNotifications) { Icon(Icons.Default.Notifications, null, tint = TextSub) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ObsidianBackground),
                windowInsets = TopAppBarDefaults.windowInsets
            )
        },
        bottomBar = {
            NavigationBar(containerColor = ObsidianSurface) {
                if (isAdmin) {
                    NavigationBarItem(selected = tab == 0, onClick = { tab = 0 }, icon = { Icon(Icons.Default.Home, null) }, label = { Text("Dashboard") })
                    NavigationBarItem(selected = tab == 1, onClick = { tab = 1 }, icon = { Icon(Icons.Default.Folder, null) }, label = { Text("Cases") })
                    NavigationBarItem(selected = tab == 2, onClick = { tab = 2 }, icon = { Icon(Icons.Default.Groups, null) }, label = { Text("Firm") })
                    NavigationBarItem(selected = tab == 3, onClick = { tab = 3 }, icon = { Icon(Icons.Default.Forum, null) }, label = { Text("Forum") })
                    NavigationBarItem(selected = tab == 4, onClick = { tab = 4 }, icon = { Icon(Icons.Default.Person, null) }, label = { Text("Profile") })
                } else {
                    NavigationBarItem(selected = tab == 0, onClick = { tab = 0 }, icon = { Icon(Icons.Default.Home, null) }, label = { Text("Home") })
                    NavigationBarItem(selected = tab == 1, onClick = { tab = 1 }, icon = { Icon(Icons.Default.Forum, null) }, label = { Text("Forum") })
                    NavigationBarItem(selected = tab == 2, onClick = { tab = 2 }, icon = { Icon(Icons.Default.Person, null) }, label = { Text("Profile") })
                }
            }
        },
        containerColor = ObsidianBackground
    ) { pad ->
        Box(Modifier.fillMaxSize().padding(pad)) {
            if (isAdmin) {
                when (tab) {
                    0 -> FirmDashboardTab(members, cases, internships, onNewInternship, onOpenApplications, onOpenCase, onOpenCauseList, onNewCase, onOpenInvoices, onOpenInquiries, onOpenECourts, onOpenAICounsel, onOpenIpcBns)
                    1 -> AdvocateCasesTab(cases, onOpenCase)
                    2 -> FirmMembersTab(firmVm, members)
                    3 -> ForumFeed(forumVm, onOpenForumPost, onNewPost)
                    4 -> ProfileTab(authVm, onSignOut = { authVm.signOut() }, onOpenNotifications = onOpenNotifications, onOpenConsultations = onOpenConsultations, onOpenSubscription = onOpenSubscription, onOpenVerification = onOpenVerification)
                }
            } else {
                when (tab) {
                    0 -> MemberHomeTab(cases, onOpenCase)
                    1 -> ForumFeed(forumVm, onOpenForumPost, onNewPost)
                    2 -> ProfileTab(authVm, onSignOut = { authVm.signOut() }, onOpenNotifications = onOpenNotifications, onOpenConsultations = onOpenConsultations, onOpenSubscription = onOpenSubscription, onOpenVerification = onOpenVerification)
                }
            }
        }
    }
}
