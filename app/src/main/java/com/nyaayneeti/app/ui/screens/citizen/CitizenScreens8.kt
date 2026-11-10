package com.nyaayneeti.app.ui.screens.citizen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nyaayneeti.app.data.matching.cleanList
import com.nyaayneeti.app.data.model.CaseEntity
import com.nyaayneeti.app.data.model.UserEntity
import com.nyaayneeti.app.ui.components.*
import com.nyaayneeti.app.ui.theme.*
import com.nyaayneeti.app.viewmodel.AuthViewModel
import com.nyaayneeti.app.viewmodel.ConsultationViewModel
import com.nyaayneeti.app.viewmodel.InquiryViewModel

@Composable
internal fun EngageAdvocateSheet(lawyer: UserEntity, vm: InquiryViewModel, cases: List<CaseEntity>, onDismiss: () -> Unit) {
    var subject by remember { mutableStateOf("Urgent Consultation regarding Legal Rights") }
    var message by remember { mutableStateOf("") }
    var attachBrief by remember { mutableStateOf(true) }
    var selectedCaseId by remember { mutableStateOf<String?>(null) }
    val feedback by vm.feedback.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { Button(onClick = { vm.engage(lawyer, subject, message, attachBrief, selectedCaseId); onDismiss() }, enabled = message.isNotBlank(), colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)) { Text("Send", color = OnPrimary, fontWeight = FontWeight.Bold) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel", color = TextSub) } },
        title = { Text("Reach out to ${lawyer.name}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (cases.isNotEmpty()) {
                    Text("Link to a case", style = MaterialTheme.typography.labelSmall, color = GoldPrimary)
                    cases.forEach { c ->
                        val sel = selectedCaseId == c.id
                        Row(Modifier.fillMaxWidth().clickable { selectedCaseId = if (sel) null else c.id }.padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = sel, onCheckedChange = { selectedCaseId = if (it) c.id else null }, colors = CheckboxDefaults.colors(checkedColor = GoldPrimary))
                            Text(c.title, style = MaterialTheme.typography.bodyMedium, maxLines = 1)
                        }
                    }
                    if (selectedCaseId != null) TextButton(onClick = { selectedCaseId = null }) { Text("Start a new matter instead", color = GoldPrimary) }
                }
                OutlinedTextField(subject, { subject = it }, label = { Text("Subject") }, singleLine = true, colors = dirFieldColors())
                OutlinedTextField(message, { message = it }, label = { Text("Briefly describe your matter") }, modifier = Modifier.height(120.dp), colors = dirFieldColors())
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = attachBrief, onCheckedChange = { attachBrief = it }, colors = CheckboxDefaults.colors(checkedColor = GoldPrimary))
                    Text("Attach my AI case brief", style = MaterialTheme.typography.labelSmall)
                }
                feedback?.let { Text(it, color = GreenSuccess, style = MaterialTheme.typography.labelSmall) }
            }
        }
    )
}

@Composable
internal fun dirFieldColors(): TextFieldColors = TextFieldDefaults.colors(focusedContainerColor = ObsidianSurface, unfocusedContainerColor = ObsidianSurface, focusedTextColor = GoldPrimary, unfocusedTextColor = TextMain, focusedIndicatorColor = GoldPrimary, unfocusedIndicatorColor = ObsidianBorder, cursorColor = GoldPrimary)

@Composable
internal fun dirChipColors(): SelectableChipColors = FilterChipDefaults.filterChipColors(selectedContainerColor = GoldContainer, selectedLabelColor = GoldPrimary, labelColor = TextSub)

@Composable
fun LawyerProfileScreen(uid: String, authVm: AuthViewModel, consVm: ConsultationViewModel, onBack: () -> Unit, onRequested: (String) -> Unit) {
    val lawyers by authVm.verifiedLawyers.collectAsState(emptyList())
    val me by authVm.currentUser.collectAsState()
    val lawyer = lawyers.find { it.uid == uid }
    var notes by remember { mutableStateOf("") }

    Scaffold(topBar = { NyaayTopAppBar("Advocate Profile", showBackButton = true, onBackClick = onBack) }, containerColor = ObsidianBackground) { pad ->
        lawyer?.let { l ->
            Column(Modifier.fillMaxSize().padding(pad).verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                NyaayCard {
                    Text(l.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    RoleBadge(l.role)
                    Spacer(Modifier.height(8.dp))
                    StarRating(l.avgRating)
                    Text("${l.avgRating} · ${l.reviewCount} reviews", style = MaterialTheme.typography.bodyMedium)
                    l.barCouncilId?.let { Text("Bar Council: $it", style = MaterialTheme.typography.bodyMedium) }
                    l.city?.let { Text("$it · ${l.experienceYears} years experience", style = MaterialTheme.typography.bodyMedium) }
                    val areas = l.specializations.cleanList()
                    if (areas.isNotEmpty()) Text("Areas: ${areas.joinToString(", ")}", style = MaterialTheme.typography.bodyMedium)
                    if (l.feeMax > 0) Text("Fees: ₹${l.feeMin.toInt()}–₹${l.feeMax.toInt()}", style = MaterialTheme.typography.bodyMedium)
                    l.bio?.let { Spacer(Modifier.height(8.dp)); Text(it, style = MaterialTheme.typography.bodyMedium) }
                }
                OutlinedTextField(notes, { notes = it }, label = { Text("What do you need help with?") }, modifier = Modifier.fillMaxWidth(), colors = dirFieldColors())
                Button(onClick = {
                    val citizen = me
                    if (citizen != null) consVm.request(citizen, l, notes, 500.0) { id -> onRequested(id) }
                }, enabled = notes.isNotBlank(), modifier = Modifier.fillMaxWidth().height(52.dp), colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary), shape = RoundedCornerShape(14.dp)) {
                    Text("Request Consultation · ₹500", color = OnPrimary, fontWeight = FontWeight.Bold)
                }
            }
        } ?: Box(Modifier.fillMaxSize().padding(pad), contentAlignment = Alignment.Center) { Text("Loading…", style = MaterialTheme.typography.bodyMedium) }
    }
}
