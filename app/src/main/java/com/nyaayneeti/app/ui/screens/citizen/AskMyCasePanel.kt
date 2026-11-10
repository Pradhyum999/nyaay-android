package com.nyaayneeti.app.ui.screens.citizen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nyaayneeti.app.ui.theme.*
import com.nyaayneeti.app.viewmodel.CaseAIViewModel

@Composable
fun AskMyCasePanel(vm: CaseAIViewModel, caseContext: String, onDismiss: () -> Unit) {
    var question by remember { mutableStateOf("") }
    val answer by vm.answer.collectAsState()
    val busy by vm.busy.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { TextButton(onClick = onDismiss) { Text("Close", color = GoldPrimary) } },
        title = { Text("Ask My Case") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Ask a question grounded on this case's record.", style = MaterialTheme.typography.labelSmall, color = TextSub)
                OutlinedTextField(question, { question = it }, label = { Text("Question") }, colors = dirFieldColors())
                Button(onClick = { vm.askMyCase(caseContext, question) }, enabled = question.isNotBlank() && !busy) {
                    Text(if (busy) "Thinking…" else "Ask", fontWeight = FontWeight.Bold)
                }
                answer?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }
            }
        }
    )
}
