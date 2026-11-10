package com.nyaayneeti.app.ui.screens.advocate

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nyaayneeti.app.data.ai.IpcToBns
import com.nyaayneeti.app.ui.components.*
import com.nyaayneeti.app.ui.screens.auth.fieldColors
import com.nyaayneeti.app.ui.theme.*
import com.nyaayneeti.app.viewmodel.CaseAIViewModel

@Composable
fun LawyerAICounselScreen(vm: CaseAIViewModel, onBack: () -> Unit) {
    var question by remember { mutableStateOf("") }
    val answer by vm.answer.collectAsState()
    val busy by vm.busy.collectAsState()

    Scaffold(topBar = { NyaayTopAppBar("NYAAY Counsel (AI)", showBackButton = true, onBackClick = onBack) }, containerColor = ObsidianBackground) { pad ->
        Column(Modifier.fillMaxSize().padding(pad).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(question, { question = it }, label = { Text("Ask a legal research question") }, modifier = Modifier.fillMaxWidth().height(140.dp), colors = fieldColors())
            Button(onClick = { vm.counsel(question) }, enabled = question.isNotBlank() && !busy, modifier = Modifier.fillMaxWidth().height(52.dp), colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary), shape = RoundedCornerShape(14.dp)) {
                if (busy) CircularProgressIndicator(Modifier.size(20.dp), color = OnPrimary) else Text("Ask", color = OnPrimary, fontWeight = FontWeight.Bold)
            }
            answer?.let {
                Text("Answer", style = MaterialTheme.typography.labelSmall, color = GoldPrimary)
                Text(it, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun IpcToBnsScreen(onBack: () -> Unit) {
    var section by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<String?>(null) }

    Scaffold(topBar = { NyaayTopAppBar("IPC → BNS Converter", showBackButton = true, onBackClick = onBack) }, containerColor = ObsidianBackground) { pad ->
        Column(Modifier.fillMaxSize().padding(pad).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(section, { section = it }, label = { Text("IPC section (e.g. 420, 498A)") }, singleLine = true, modifier = Modifier.fillMaxWidth(), colors = fieldColors())
            Button(onClick = { result = IpcToBns.lookup(section) }, enabled = section.isNotBlank(), modifier = Modifier.fillMaxWidth().height(48.dp), colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)) { Text("Convert", color = OnPrimary, fontWeight = FontWeight.Bold) }
            result?.let { Text(it, style = MaterialTheme.typography.titleMedium, color = GoldPrimary) }
            Spacer(Modifier.height(4.dp))
            Text("Common mappings", style = MaterialTheme.typography.labelSmall, color = TextSub)
            LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                items(IpcToBns.allEntries()) { (ipc, bns) ->
                    NyaayCard { Text("IPC $ipc  →  BNS $bns", style = MaterialTheme.typography.bodyMedium) }
                }
            }
        }
    }
}
