package com.nyaayneeti.app.ui.screens.shared

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nyaayneeti.app.ui.components.NyaayTopAppBar
import com.nyaayneeti.app.ui.screens.auth.fieldColors
import com.nyaayneeti.app.ui.theme.*
import com.nyaayneeti.app.viewmodel.ECourtsViewModel

@Composable
fun ECourtsLookupScreen(vm: ECourtsViewModel, onBack: () -> Unit) {
    var cnr by remember { mutableStateOf("") }
    val result by vm.result.collectAsState()
    val error by vm.error.collectAsState()
    val busy by vm.busy.collectAsState()

    Scaffold(topBar = { NyaayTopAppBar("eCourts Case Lookup", showBackButton = true, onBackClick = onBack) }, containerColor = ObsidianBackground) { pad ->
        Column(Modifier.fillMaxSize().padding(pad).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(cnr, { cnr = it }, label = { Text("Enter CNR number") }, singleLine = true, modifier = Modifier.fillMaxWidth(), colors = fieldColors())
            Button(onClick = { vm.lookup(cnr) }, enabled = cnr.isNotBlank() && !busy, modifier = Modifier.fillMaxWidth().height(52.dp), colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary), shape = RoundedCornerShape(14.dp)) {
                if (busy) CircularProgressIndicator(Modifier.size(20.dp), color = OnPrimary) else Text("Look up", color = OnPrimary, fontWeight = FontWeight.Bold)
            }
            error?.let { Text(it, color = RedUrgent, style = MaterialTheme.typography.bodyMedium) }
            result?.let {
                Text("Result", style = MaterialTheme.typography.labelSmall, color = GoldPrimary)
                Text(it, style = MaterialTheme.typography.bodyMedium)
            }
            if (result == null && error == null) {
                Text("Paste a 16-digit CNR to fetch live case status from eCourts.", style = MaterialTheme.typography.bodyMedium, color = TextSub)
            }
        }
    }
}
