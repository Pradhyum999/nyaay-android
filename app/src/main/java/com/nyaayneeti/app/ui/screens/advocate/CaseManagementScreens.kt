package com.nyaayneeti.app.ui.screens.advocate

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nyaayneeti.app.data.model.CasePriority
import com.nyaayneeti.app.data.model.CaseStage
import com.nyaayneeti.app.ui.components.NyaayTopAppBar
import com.nyaayneeti.app.ui.screens.auth.fieldColors
import com.nyaayneeti.app.ui.theme.*
import com.nyaayneeti.app.viewmodel.CaseViewModel

@Composable
fun NewCaseScreen(vm: CaseViewModel, onCreated: (String) -> Unit, onBack: () -> Unit) {
    var caseNumber by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var clientName by remember { mutableStateOf("") }
    var clientPhone by remember { mutableStateOf("") }
    var court by remember { mutableStateOf("District & Sessions Court") }
    var matterType by remember { mutableStateOf("") }
    var legalArea by remember { mutableStateOf("") }
    var stage by remember { mutableStateOf(CaseStage.ADMISSION) }
    var priority by remember { mutableStateOf(CasePriority.NORMAL) }
    val feedback by vm.feedback.collectAsState()

    Scaffold(topBar = { NyaayTopAppBar("New Case", showBackButton = true, onBackClick = onBack) }, containerColor = ObsidianBackground) { pad ->
        Column(Modifier.fillMaxSize().padding(pad).verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(caseNumber, { caseNumber = it }, label = { Text("Case / CNR number *") }, singleLine = true, modifier = Modifier.fillMaxWidth(), colors = fieldColors())
            OutlinedTextField(title, { title = it }, label = { Text("Title *") }, singleLine = true, modifier = Modifier.fillMaxWidth(), colors = fieldColors())
            OutlinedTextField(clientName, { clientName = it }, label = { Text("Client name") }, singleLine = true, modifier = Modifier.fillMaxWidth(), colors = fieldColors())
            OutlinedTextField(clientPhone, { clientPhone = it }, label = { Text("Client phone") }, singleLine = true, modifier = Modifier.fillMaxWidth(), colors = fieldColors())
            OutlinedTextField(court, { court = it }, label = { Text("Court") }, singleLine = true, modifier = Modifier.fillMaxWidth(), colors = fieldColors())
            OutlinedTextField(matterType, { matterType = it }, label = { Text("Matter type") }, singleLine = true, modifier = Modifier.fillMaxWidth(), colors = fieldColors())
            OutlinedTextField(legalArea, { legalArea = it }, label = { Text("Legal area") }, singleLine = true, modifier = Modifier.fillMaxWidth(), colors = fieldColors())

            Text("Stage", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.horizontalScroll(rememberScrollState())) {
                CaseStage.values().forEach { s ->
                    FilterChip(selected = stage == s, onClick = { stage = s }, label = { Text(s.name.replace("_", " ")) }, colors = FilterChipDefaults.filterChipColors(selectedContainerColor = GoldContainer, selectedLabelColor = GoldPrimary, labelColor = TextSub))
                }
            }

            Text("Priority", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                CasePriority.values().forEach { p ->
                    FilterChip(selected = priority == p, onClick = { priority = p }, label = { Text(p.name) }, colors = FilterChipDefaults.filterChipColors(selectedContainerColor = if (p == CasePriority.URGENT) RedContainer else GoldContainer, selectedLabelColor = if (p == CasePriority.URGENT) RedUrgent else GoldPrimary, labelColor = TextSub))
                }
            }

            feedback?.let { Text(it, color = RedUrgent, style = MaterialTheme.typography.bodyMedium) }

            Button(onClick = { vm.addCase(caseNumber, title, clientName, clientPhone, court, matterType, legalArea, stage, priority, onCreated) }, enabled = caseNumber.isNotBlank() && title.isNotBlank(), modifier = Modifier.fillMaxWidth().height(52.dp), colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary), shape = RoundedCornerShape(14.dp)) {
                Text("Create Case", color = OnPrimary, fontWeight = FontWeight.Bold)
            }
        }
    }
}
