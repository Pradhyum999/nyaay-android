package com.nyaayneeti.app.ui.screens.advocate

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nyaayneeti.app.ui.screens.auth.fieldColors
import com.nyaayneeti.app.ui.theme.*
import com.nyaayneeti.app.viewmodel.CaseViewModel

@Composable
internal fun NewInvoiceSheet(vm: CaseViewModel, onDismiss: () -> Unit) {
    var caseNumber by remember { mutableStateOf("") }
    var clientName by remember { mutableStateOf("") }
    var appearance by remember { mutableStateOf("") }
    var drafting by remember { mutableStateOf("") }
    var clerkage by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { Button(onClick = { vm.addInvoice(null, caseNumber, clientName, appearance.toDoubleOrNull() ?: 0.0, drafting.toDoubleOrNull() ?: 0.0, clerkage.toDoubleOrNull() ?: 0.0, null); onDismiss() }, colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)) { Text("Save", color = OnPrimary, fontWeight = FontWeight.Bold) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel", color = TextSub) } },
        title = { Text("New invoice") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(caseNumber, { caseNumber = it }, label = { Text("Case number") }, singleLine = true, colors = fieldColors())
                OutlinedTextField(clientName, { clientName = it }, label = { Text("Client name") }, singleLine = true, colors = fieldColors())
                OutlinedTextField(appearance, { appearance = it }, label = { Text("Appearance fee") }, singleLine = true, colors = fieldColors())
                OutlinedTextField(drafting, { drafting = it }, label = { Text("Drafting fee") }, singleLine = true, colors = fieldColors())
                OutlinedTextField(clerkage, { clerkage = it }, label = { Text("Clerkage & misc") }, singleLine = true, colors = fieldColors())
            }
        }
    )
}

@Composable
fun HearingFormSheet(caseId: String, vm: CaseViewModel, onDismiss: () -> Unit) {
    var court by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("10:30 AM") }
    var purpose by remember { mutableStateOf("") }
    var judge by remember { mutableStateOf("") }
    val feedback by vm.feedback.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { Button(onClick = { vm.addHearingFull(caseId, court, (1..50).random(), System.currentTimeMillis(), time, purpose, judge); if (feedback == null) onDismiss() }, colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)) { Text("Add", color = OnPrimary, fontWeight = FontWeight.Bold) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel", color = TextSub) } },
        title = { Text("Add hearing") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(court, { court = it }, label = { Text("Court room / hall") }, singleLine = true, colors = fieldColors())
                OutlinedTextField(time, { time = it }, label = { Text("Time (e.g. 10:30 AM)") }, singleLine = true, colors = fieldColors())
                OutlinedTextField(purpose, { purpose = it }, label = { Text("Purpose") }, singleLine = true, colors = fieldColors())
                OutlinedTextField(judge, { judge = it }, label = { Text("Judge (optional)") }, singleLine = true, colors = fieldColors())
                feedback?.let { Text(it, color = RedUrgent, style = MaterialTheme.typography.bodyMedium) }
            }
        }
    )
}
