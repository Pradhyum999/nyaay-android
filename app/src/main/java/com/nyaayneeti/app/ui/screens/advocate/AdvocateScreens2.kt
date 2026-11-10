package com.nyaayneeti.app.ui.screens.advocate

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nyaayneeti.app.data.model.CaseEntity
import com.nyaayneeti.app.data.model.CasePriority
import com.nyaayneeti.app.ui.components.*
import com.nyaayneeti.app.ui.theme.*
import com.nyaayneeti.app.viewmodel.AuthViewModel
import com.nyaayneeti.app.viewmodel.CaseViewModel

@Composable
fun AdvocateHomeTab(cases: List<CaseEntity>, onOpenCauseList: () -> Unit, onOpenCase: (String) -> Unit, onNewCase: () -> Unit, onOpenInvoices: () -> Unit, onOpenInquiries: () -> Unit, onOpenECourts: () -> Unit, onOpenAICounsel: () -> Unit, onOpenIpcBns: () -> Unit) {
    var urgentOnly by remember { mutableStateOf(false) }
    val filtered = if (urgentOnly) cases.filter { it.priority == CasePriority.URGENT } else cases
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
        item { FilterChip(selected = urgentOnly, onClick = { urgentOnly = !urgentOnly }, label = { Text("Urgent only") }, colors = FilterChipDefaults.filterChipColors(selectedContainerColor = RedContainer, selectedLabelColor = RedUrgent, labelColor = TextSub)) }
        if (filtered.isEmpty()) item { EmptyState(Icons.Default.Folder, "No cases", "Add a case or wait for a client to reach out.") }
        items(filtered, key = { it.id }) { c ->
            NyaayCard(onClick = { onOpenCase(c.id) }) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text(c.caseNumber, style = MaterialTheme.typography.labelSmall, color = GoldPrimary); CasePriorityBadge(c.priority) }
                Spacer(Modifier.height(6.dp))
                Text(c.title, style = MaterialTheme.typography.titleMedium)
                Text("Client: ${c.clientName}", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun AdvocateCasesTab(cases: List<CaseEntity>, onOpenCase: (String) -> Unit) {
    if (cases.isEmpty()) Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { EmptyState(Icons.Default.Folder, "No cases", "Assigned cases will appear here.") }
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

@Composable
fun CauseListScreen(vm: CaseViewModel, authVm: AuthViewModel, onBack: () -> Unit) {
    val hearings by vm.allHearings.collectAsState()
    Scaffold(topBar = { NyaayTopAppBar("Court Diary", showBackButton = true, onBackClick = onBack) }, containerColor = ObsidianBackground) { pad ->
        if (hearings.isEmpty()) Box(Modifier.fillMaxSize().padding(pad), contentAlignment = Alignment.Center) { EmptyState(Icons.Default.CalendarMonth, "No hearings", "Add hearings from a case room.") }
        else LazyColumn(Modifier.fillMaxSize().padding(pad), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(hearings, key = { it.id }) { h ->
                NyaayCard {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(h.caseNumber, style = MaterialTheme.typography.titleMedium)
                        PillBadge("ITEM #${h.itemNumber}", GoldContainer, GoldPrimary)
                    }
                    Spacer(Modifier.height(4.dp))
                    Text("${h.hearingTime} · ${h.courtNumber}", style = MaterialTheme.typography.bodyMedium, color = GoldPrimary)
                    Text("Stage: ${h.stage} · ${h.purpose}", style = MaterialTheme.typography.bodyMedium)
                    h.judgeName?.let { Text("Judge: $it", style = MaterialTheme.typography.labelSmall) }
                }
            }
        }
    }
}
