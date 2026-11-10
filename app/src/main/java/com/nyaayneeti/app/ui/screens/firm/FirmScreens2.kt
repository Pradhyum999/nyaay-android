package com.nyaayneeti.app.ui.screens.firm

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nyaayneeti.app.data.model.*
import com.nyaayneeti.app.ui.components.*
import com.nyaayneeti.app.ui.theme.*

@Composable
fun FirmDashboardTab(
    members: List<UserEntity>, cases: List<CaseEntity>, internships: List<InternshipEntity>,
    onNewInternship: () -> Unit, onOpenApplications: (String) -> Unit, onOpenCase: (String) -> Unit,
    onOpenCauseList: () -> Unit, onNewCase: () -> Unit, onOpenInvoices: () -> Unit, onOpenInquiries: () -> Unit,
    onOpenECourts: () -> Unit, onOpenAICounsel: () -> Unit, onOpenIpcBns: () -> Unit
) {
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                NyaayCard(Modifier.weight(1f)) { Text("${members.size}", style = MaterialTheme.typography.headlineMedium, color = GoldPrimary); Text("Members", style = MaterialTheme.typography.labelSmall) }
                NyaayCard(Modifier.weight(1f)) { Text("${cases.size}", style = MaterialTheme.typography.headlineMedium, color = GoldPrimary); Text("Cases", style = MaterialTheme.typography.labelSmall) }
            }
        }
        item {
            NyaayCard(onClick = onOpenCauseList) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CalendarMonth, null, tint = GoldPrimary)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) { Text("Today's Court Diary", style = MaterialTheme.typography.titleMedium, color = GoldPrimary, fontWeight = FontWeight.Bold); Text("${cases.count { it.nextHearingDate != null }} hearings", style = MaterialTheme.typography.bodyMedium) }
                }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                NyaayCard(Modifier.weight(1f), onClick = onNewCase) { Icon(Icons.Default.Add, null, tint = GoldPrimary); Spacer(Modifier.height(4.dp)); Text("New Case", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
                NyaayCard(Modifier.weight(1f), onClick = onOpenInvoices) { Icon(Icons.Default.Receipt, null, tint = GoldPrimary); Spacer(Modifier.height(4.dp)); Text("Invoices", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
                NyaayCard(Modifier.weight(1f), onClick = onOpenInquiries) { Icon(Icons.Default.Inbox, null, tint = GoldPrimary); Spacer(Modifier.height(4.dp)); Text("Inquiries", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                NyaayCard(Modifier.weight(1f), onClick = onOpenECourts) { Icon(Icons.Default.Search, null, tint = GoldPrimary); Spacer(Modifier.height(4.dp)); Text("eCourts", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
                NyaayCard(Modifier.weight(1f), onClick = onOpenAICounsel) { Icon(Icons.Default.AutoAwesome, null, tint = GoldPrimary); Spacer(Modifier.height(4.dp)); Text("AI Counsel", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
                NyaayCard(Modifier.weight(1f), onClick = onOpenIpcBns) { Icon(Icons.Default.Balance, null, tint = GoldPrimary); Spacer(Modifier.height(4.dp)); Text("IPC→BNS", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
            }
        }
        item {
            Button(onClick = onNewInternship, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)) { Text("Post internship", color = OnPrimary, fontWeight = FontWeight.Bold) }
        }
        item { SectionTitle("Internships") }
        if (internships.isEmpty()) item { EmptyState(Icons.Default.School, "No internships", "Post your first internship.") }
        items(internships, key = { it.id }) { i ->
            NyaayCard(onClick = { onOpenApplications(i.id) }) {
                Text(i.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("${i.location} · ${i.stipend}", style = MaterialTheme.typography.bodyMedium)
            }
        }
        item { SectionTitle("Recent cases") }
        items(cases.take(5), key = { it.id }) { c ->
            NyaayCard(onClick = { onOpenCase(c.id) }) {
                Text(c.title, style = MaterialTheme.typography.titleMedium)
                Text("Advocate: ${c.lawyerName ?: "Unassigned"}", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun MemberHomeTab(cases: List<CaseEntity>, onOpenCase: (String) -> Unit) {
    if (cases.isEmpty()) Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { EmptyState(Icons.Default.Folder, "No assigned cases", "Cases assigned to you appear here.") }
    else LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(cases, key = { it.id }) { c ->
            NyaayCard(onClick = { onOpenCase(c.id) }) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text(c.caseNumber, style = MaterialTheme.typography.labelSmall, color = GoldPrimary); CasePriorityBadge(c.priority) }
                Spacer(Modifier.height(6.dp))
                Text(c.title, style = MaterialTheme.typography.titleMedium)
                Text("Stage: ${c.stage.name}", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
