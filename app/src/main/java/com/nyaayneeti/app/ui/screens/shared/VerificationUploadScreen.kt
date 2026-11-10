package com.nyaayneeti.app.ui.screens.shared

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nyaayneeti.app.data.model.VerificationStatus
import com.nyaayneeti.app.ui.components.*
import com.nyaayneeti.app.ui.screens.auth.fieldColors
import com.nyaayneeti.app.ui.theme.*
import com.nyaayneeti.app.viewmodel.AuthViewModel
import com.nyaayneeti.app.viewmodel.VerificationViewModel

@Composable
fun VerificationUploadScreen(vm: VerificationViewModel, authVm: AuthViewModel, onBack: () -> Unit) {
    val user by authVm.currentUser.collectAsState()
    val feedback by vm.feedback.collectAsState()
    var docType by remember { mutableStateOf("Bar Council Card") }
    var maskedId by remember { mutableStateOf("") }

    Scaffold(topBar = { NyaayTopAppBar("Verify your profile", showBackButton = true, onBackClick = onBack) }, containerColor = ObsidianBackground) { pad ->
        Column(Modifier.fillMaxSize().padding(pad).padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            val status = user?.verificationStatus
            if (status != null) {
                NyaayCard {
                    Text("Current status", style = MaterialTheme.typography.labelSmall, color = GoldPrimary)
                    Spacer(Modifier.height(4.dp))
                    PillBadge(status.name, if (status == VerificationStatus.VERIFIED) GreenContainer else if (status == VerificationStatus.REJECTED) RedContainer else GoldContainer, if (status == VerificationStatus.VERIFIED) GreenSuccess else if (status == VerificationStatus.REJECTED) RedUrgent else GoldPrimary)
                }
            }

            Text("Document type", style = MaterialTheme.typography.titleMedium)
            listOf("Bar Council Card", "Aadhaar", "Voter ID", "Passport", "Driving License").forEach { t ->
                FilterChip(selected = docType == t, onClick = { docType = t }, label = { Text(t) }, colors = FilterChipDefaults.filterChipColors(selectedContainerColor = GoldContainer, selectedLabelColor = GoldPrimary, labelColor = TextSub))
            }
            OutlinedTextField(maskedId, { maskedId = it }, label = { Text("Document number (last 4 digits)") }, singleLine = true, modifier = Modifier.fillMaxWidth(), colors = fieldColors())
            feedback?.let { Text(it, color = GreenSuccess, style = MaterialTheme.typography.bodyMedium) }
            Button(onClick = { vm.submit(docType, maskedId) }, modifier = Modifier.fillMaxWidth().height(52.dp), colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary), shape = RoundedCornerShape(14.dp)) {
                Text("Submit for review", color = OnPrimary, fontWeight = FontWeight.Bold)
            }
        }
    }
}
