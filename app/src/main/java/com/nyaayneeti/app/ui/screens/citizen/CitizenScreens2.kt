package com.nyaayneeti.app.ui.screens.citizen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nyaayneeti.app.data.model.CaseEntity
import com.nyaayneeti.app.ui.components.*
import com.nyaayneeti.app.ui.theme.*

@Composable
fun CitizenHomeTab(cases: List<CaseEntity>, onStartIntake: () -> Unit, onOpenLawyers: () -> Unit, onOpenCase: (String) -> Unit) {
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            NyaayCard(onClick = onStartIntake) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(Modifier.size(44.dp), shape = RoundedCornerShape(12.dp), color = GoldPrimary) {
                        Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.AutoAwesome, null, tint = OnPrimary) }
                    }
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Build My Case", style = MaterialTheme.typography.titleMedium, color = GoldPrimary, fontWeight = FontWeight.Bold)
                        Text("AI intake to structure your matter", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
        item {
            NyaayCard(onClick = onOpenLawyers) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Search, null, tint = GoldPrimary)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Find an Advocate", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text("Verified lawyers near you", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
        item { SectionTitle("Your active cases") }
        if (cases.isEmpty()) {
            item { EmptyState(Icons.Default.Folder, "No cases yet", "Use Build My Case to create your first case.") }
        } else {
            items(cases.take(5)) { c ->
                NyaayCard(onClick = { onOpenCase(c.id) }) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(c.caseNumber, style = MaterialTheme.typography.labelSmall, color = GoldPrimary)
                        CasePriorityBadge(c.priority)
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(c.title, style = MaterialTheme.typography.titleMedium)
                    Text(c.lawyerName?.let { "Advocate: $it" } ?: "Awaiting assignment", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
fun CitizenCasesTab(cases: List<CaseEntity>, onOpenCase: (String) -> Unit) {
    if (cases.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            EmptyState(Icons.Default.Folder, "No cases", "Your cases will appear here.")
        }
    } else {
        LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(cases, key = { it.id }) { c ->
                NyaayCard(onClick = { onOpenCase(c.id) }) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(c.caseNumber, style = MaterialTheme.typography.labelSmall, color = GoldPrimary)
                        CasePriorityBadge(c.priority)
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(c.title, style = MaterialTheme.typography.titleMedium)
                    Text("Stage: ${c.stage.name}", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
