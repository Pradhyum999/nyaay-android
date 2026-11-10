package com.nyaayneeti.app.ui.screens.firm

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nyaayneeti.app.data.model.ApplicationStatus
import com.nyaayneeti.app.ui.components.*
import com.nyaayneeti.app.ui.screens.auth.fieldColors
import com.nyaayneeti.app.ui.theme.*
import com.nyaayneeti.app.viewmodel.FirmViewModel
import com.nyaayneeti.app.viewmodel.InternshipViewModel

@Composable
fun NewInternshipScreen(vm: InternshipViewModel, firmVm: FirmViewModel, onDone: () -> Unit, onBack: () -> Unit) {
    val firm by firmVm.firm.collectAsState()
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var stipend by remember { mutableStateOf("Unpaid") }
    var skills by remember { mutableStateOf("") }

    Scaffold(topBar = { NyaayTopAppBar("Post internship", showBackButton = true, onBackClick = onBack) }, containerColor = ObsidianBackground) { pad ->
        Column(Modifier.fillMaxSize().padding(pad).verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(title, { title = it }, label = { Text("Title") }, singleLine = true, modifier = Modifier.fillMaxWidth(), colors = fieldColors())
            OutlinedTextField(desc, { desc = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth().height(140.dp), colors = fieldColors())
            OutlinedTextField(location, { location = it }, label = { Text("Location (e.g. Pune / Remote)") }, singleLine = true, modifier = Modifier.fillMaxWidth(), colors = fieldColors())
            OutlinedTextField(stipend, { stipend = it }, label = { Text("Stipend") }, singleLine = true, modifier = Modifier.fillMaxWidth(), colors = fieldColors())
            OutlinedTextField(skills, { skills = it }, label = { Text("Skills (comma separated)") }, singleLine = true, modifier = Modifier.fillMaxWidth(), colors = fieldColors())
            Button(onClick = { vm.post(firm?.name ?: "Firm", title, desc, location, 1, stipend, skills, "Law student"); onDone() }, enabled = title.isNotBlank() && location.isNotBlank(), modifier = Modifier.fillMaxWidth().height(52.dp), colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary), shape = RoundedCornerShape(14.dp)) {
                Text("Post", color = OnPrimary, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun InternshipApplicationsScreen(vm: InternshipViewModel, onBack: () -> Unit) {
    val apps by vm.applicationsForSelected.collectAsState()
    val internship by vm.selectedInternship.collectAsState()
    Scaffold(topBar = { NyaayTopAppBar(internship?.title ?: "Applications", showBackButton = true, onBackClick = onBack) }, containerColor = ObsidianBackground) { pad ->
        if (apps.isEmpty()) Box(Modifier.fillMaxSize().padding(pad), contentAlignment = Alignment.Center) { EmptyState(Icons.Default.People, "No applications yet") }
        else Column(Modifier.fillMaxSize().padding(pad).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            apps.forEach { a ->
                NyaayCard {
                    Text(a.studentName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Text(a.coverLetter, style = MaterialTheme.typography.bodyMedium, maxLines = 3)
                    Spacer(Modifier.height(8.dp))
                    PillBadge(a.status.name, GoldContainer, GoldPrimary)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        ApplicationStatus.values().forEach { s ->
                            FilterChip(selected = a.status == s, onClick = { vm.setStatus(internship?.id ?: "", a.id, s, a.studentUid) }, label = { Text(s.name) }, colors = FilterChipDefaults.filterChipColors(selectedContainerColor = GoldContainer, selectedLabelColor = GoldPrimary, labelColor = TextSub))
                        }
                    }
                }
            }
        }
    }
}
