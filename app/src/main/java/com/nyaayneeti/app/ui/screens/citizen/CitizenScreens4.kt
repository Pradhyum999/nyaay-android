package com.nyaayneeti.app.ui.screens.citizen

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
internal fun SummaryTab(c: CaseEntity?, isLawyer: Boolean, onStage: (CaseStage) -> Unit) {
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            c?.let { case ->
                NyaayCard {
                    Text(case.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        CasePriorityBadge(case.priority)
                        CaseStatusBadge(case.status)
                    }
                    Spacer(Modifier.height(8.dp))
                    Text("Court: ${case.court}", style = MaterialTheme.typography.bodyMedium)
                    Text("Matter: ${case.matterType}", style = MaterialTheme.typography.bodyMedium)
                    Text("Legal area: ${case.legalArea}", style = MaterialTheme.typography.bodyMedium)
                    Text("Advocate: ${case.lawyerName ?: "Awaiting assignment"}", style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(8.dp))
                    SectionTitle("Stage")
                    if (isLawyer) {
                        Spacer(Modifier.height(6.dp))
                        CaseStage.values().forEach { s ->
                            FilterChip(selected = case.stage == s, onClick = { onStage(s) }, label = { Text(s.name.replace("_", " ")) }, colors = FilterChipDefaults.filterChipColors(selectedContainerColor = GoldContainer, selectedLabelColor = GoldPrimary, labelColor = TextSub))
                        }
                    } else {
                        Text(case.stage.name.replace("_", " "), style = MaterialTheme.typography.titleMedium, color = GoldPrimary)
                    }
                    if (case.summaryText.isNotBlank()) { Spacer(Modifier.height(8.dp)); Text(case.summaryText, style = MaterialTheme.typography.bodyMedium) }
                }
            }
        }
    }
}

@Composable
internal fun TimelineTab(events: List<TimelineEventEntity>) {
    if (events.isEmpty()) Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { EmptyState(Icons.Default.Timeline, "No activity yet") }
    else LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        items(events) { e ->
            NyaayCard {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(e.titleEn, style = MaterialTheme.typography.titleMedium, color = GoldPrimary)
                    Text(e.actor, style = MaterialTheme.typography.labelSmall)
                }
                Spacer(Modifier.height(4.dp))
                Text(e.descriptionEn, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
internal fun TasksTab(tasks: List<CaseTaskEntity>, onToggle: (String) -> Unit) {
    if (tasks.isEmpty()) Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { EmptyState(Icons.Default.CheckCircle, "No tasks", "Tasks from your case checklist will appear here.") }
    else LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(tasks) { t ->
            NyaayCard(onClick = { onToggle(t.id) }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = t.isCompleted, onCheckedChange = { onToggle(t.id) }, colors = CheckboxDefaults.colors(checkedColor = GoldPrimary))
                    Spacer(Modifier.width(8.dp))
                    Column(Modifier.weight(1f)) {
                        Text(t.title, style = MaterialTheme.typography.titleMedium, color = if (t.isCompleted) TextFaint else TextMain)
                        Text("For: ${t.assignedTo}", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}
