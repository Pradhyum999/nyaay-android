package com.nyaayneeti.app.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nyaayneeti.app.data.model.ReportStatus
import com.nyaayneeti.app.data.model.VerificationStatus
import com.nyaayneeti.app.ui.components.*
import com.nyaayneeti.app.ui.theme.*
import com.nyaayneeti.app.viewmodel.AuthViewModel
import com.nyaayneeti.app.viewmodel.ModerationViewModel
import com.nyaayneeti.app.viewmodel.VerificationViewModel

@Composable
fun AdminHomeScreen(authVm: AuthViewModel, verificationVm: VerificationViewModel, moderationVm: ModerationViewModel, onSignOut: () -> Unit) {
    val user by authVm.currentUser.collectAsState()
    val requests by verificationVm.requests.collectAsState(emptyList())
    val reports by moderationVm.reports.collectAsState(emptyList())
    var tab by remember { mutableStateOf(0) }

    Scaffold(topBar = { NyaayTopAppBar("Platform Admin", user?.name ?: "Admin", actions = { IconButton(onClick = onSignOut) { Icon(Icons.Default.ExitToApp, null, tint = TextSub) } }) }, containerColor = ObsidianBackground) { pad ->
        Column(Modifier.fillMaxSize().padding(pad)) {
            TabRow(selectedTabIndex = tab, containerColor = ObsidianSurface) {
                Tab(selected = tab == 0, onClick = { tab = 0 }, text = { Text("Verifications", color = if (tab == 0) GoldPrimary else TextSub) })
                Tab(selected = tab == 1, onClick = { tab = 1 }, text = { Text("Reports", color = if (tab == 1) GoldPrimary else TextSub) })
            }
            when (tab) {
                0 -> LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (requests.isEmpty()) item { EmptyState(Icons.Default.VerifiedUser, "No verification requests") }
                    items(requests, key = { it.id }) { r ->
                        NyaayCard {
                            Text(r.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text("${r.documentType} · ${r.maskedIdNumber ?: "—"}", style = MaterialTheme.typography.bodyMedium)
                            PillBadge(r.status.name, if (r.status == VerificationStatus.VERIFIED) GreenContainer else if (r.status == VerificationStatus.REJECTED) RedContainer else GoldContainer, if (r.status == VerificationStatus.VERIFIED) GreenSuccess else if (r.status == VerificationStatus.REJECTED) RedUrgent else GoldPrimary)
                            if (r.status == VerificationStatus.PENDING) {
                                Spacer(Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(onClick = { verificationVm.approve(r) }, colors = ButtonDefaults.buttonColors(containerColor = GreenSuccess)) { Text("Approve", fontWeight = FontWeight.Bold) }
                                    Button(onClick = { verificationVm.reject(r, "Incomplete or invalid documents") }, colors = ButtonDefaults.buttonColors(containerColor = RedContainer, contentColor = RedUrgent)) { Text("Reject", fontWeight = FontWeight.Bold) }
                                }
                            }
                        }
                    }
                }
                1 -> LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (reports.isEmpty()) item { EmptyState(Icons.Default.Flag, "No reports") }
                    items(reports, key = { it.id }) { r ->
                        NyaayCard {
                            Text("Reported: ${r.targetName}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(r.reason, style = MaterialTheme.typography.bodyMedium)
                            PillBadge(r.status.name, if (r.status == ReportStatus.REVIEWED) GreenContainer else GoldContainer, if (r.status == ReportStatus.REVIEWED) GreenSuccess else GoldPrimary)
                            if (r.status == ReportStatus.OPEN) {
                                Spacer(Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(onClick = { moderationVm.resolve(r.id, ReportStatus.REVIEWED) }, colors = ButtonDefaults.buttonColors(containerColor = GreenSuccess)) { Text("Done", fontWeight = FontWeight.Bold) }
                                    Button(onClick = { moderationVm.resolve(r.id, ReportStatus.DISMISSED) }, colors = ButtonDefaults.buttonColors(containerColor = RedContainer, contentColor = RedUrgent)) { Text("Dismiss", fontWeight = FontWeight.Bold) }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
