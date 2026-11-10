package com.nyaayneeti.app.ui.screens.student

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
import com.nyaayneeti.app.ui.components.*
import com.nyaayneeti.app.ui.theme.*
import com.nyaayneeti.app.viewmodel.InternshipViewModel

@Composable
fun StudentHomeTab(vm: InternshipViewModel, onOpenInternship: (String) -> Unit) {
    val internships by vm.openInternships.collectAsState(emptyList())
    val myApps by vm.myApplications.collectAsState()
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { SectionTitle("Featured internships") }
        if (internships.isEmpty()) item { EmptyState(Icons.Default.School, "No open internships") }
        items(internships.take(3), key = { it.id }) { i -> InternshipCard(i, onOpenInternship) }
        item { SectionTitle("My applications (${myApps.size})") }
        items(myApps, key = { it.id }) { a ->
            NyaayCard {
                Text("Internship #${a.internshipId.take(4)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                PillBadge(a.status.name, if (a.status == com.nyaayneeti.app.data.model.ApplicationStatus.ACCEPTED) GreenContainer else GoldContainer, if (a.status == com.nyaayneeti.app.data.model.ApplicationStatus.ACCEPTED) GreenSuccess else GoldPrimary)
            }
        }
    }
}

@Composable
fun InternshipListTab(vm: InternshipViewModel, onOpenInternship: (String) -> Unit) {
    val internships by vm.openInternships.collectAsState(emptyList())
    if (internships.isEmpty()) Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { EmptyState(Icons.Default.School, "No open internships", "Check back soon.") }
    else LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(internships, key = { it.id }) { i -> InternshipCard(i, onOpenInternship) }
    }
}

@Composable
private fun InternshipCard(i: com.nyaayneeti.app.data.model.InternshipEntity, onOpen: (String) -> Unit) {
    NyaayCard(onClick = { onOpen(i.id) }) {
        Text(i.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(i.firmName, style = MaterialTheme.typography.bodyMedium)
        Text("${i.location} · ${i.durationMonths} mo · ${i.stipend}", style = MaterialTheme.typography.bodyMedium)
    }
}
