package com.nyaayneeti.app.ui.screens.student

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nyaayneeti.app.ui.components.*
import com.nyaayneeti.app.ui.screens.auth.fieldColors
import com.nyaayneeti.app.ui.theme.*
import com.nyaayneeti.app.viewmodel.InternshipViewModel

@Composable
fun InternshipDetailScreen(id: String, vm: InternshipViewModel, onBack: () -> Unit) {
    val internship by vm.selectedInternship.collectAsState()
    val feedback by vm.feedback.collectAsState()
    var coverLetter by remember { mutableStateOf("") }

    Scaffold(topBar = { NyaayTopAppBar("Internship", showBackButton = true, onBackClick = onBack) }, containerColor = ObsidianBackground) { pad ->
        Column(Modifier.fillMaxSize().padding(pad).verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            internship?.let { i ->
                NyaayCard {
                    Text(i.title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Text(i.firmName, style = MaterialTheme.typography.titleMedium, color = GoldPrimary)
                    Spacer(Modifier.height(8.dp))
                    Text("${i.location} · ${i.durationMonths} month(s)", style = MaterialTheme.typography.bodyMedium)
                    Text("Stipend: ${i.stipend}", style = MaterialTheme.typography.bodyMedium)
                    Text("Eligibility: ${i.eligibility}", style = MaterialTheme.typography.bodyMedium)
                    if (i.skills.isNotBlank()) Text("Skills: ${i.skills}", style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(8.dp))
                    Text(i.description, style = MaterialTheme.typography.bodyMedium)
                }
                OutlinedTextField(coverLetter, { coverLetter = it }, label = { Text("Cover letter") }, modifier = Modifier.fillMaxWidth().height(140.dp), colors = fieldColors())
                Button(onClick = { vm.apply(i, coverLetter) }, enabled = coverLetter.isNotBlank(), modifier = Modifier.fillMaxWidth().height(52.dp), colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary), shape = RoundedCornerShape(14.dp)) {
                    Text("Apply", color = OnPrimary, fontWeight = FontWeight.Bold)
                }
                feedback?.let { Text(it, color = GreenSuccess, style = MaterialTheme.typography.bodyMedium) }
            } ?: Text("Loading…", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
