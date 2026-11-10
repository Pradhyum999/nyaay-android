package com.nyaayneeti.app.ui.screens.citizen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nyaayneeti.app.ui.components.*
import com.nyaayneeti.app.ui.theme.*
import com.nyaayneeti.app.viewmodel.AIIntakeViewModel

@Composable
fun BuildCaseIntakeScreen(vm: AIIntakeViewModel, onCaseCreated: (String) -> Unit, onBack: () -> Unit) {
    val messages by vm.messages.collectAsState()
    val profile by vm.profile.collectAsState()
    val input by vm.input.collectAsState()
    val busy by vm.busy.collectAsState()

    Scaffold(topBar = { NyaayTopAppBar("Build My Case", showBackButton = true, onBackClick = onBack) }, containerColor = ObsidianBackground) { pad ->
        Column(Modifier.fillMaxSize().padding(pad)) {
            LazyColumn(Modifier.weight(1f).fillMaxWidth(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(messages) { m ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = if (m.isUser) Arrangement.End else Arrangement.Start) {
                        Column(Modifier.clip(RoundedCornerShape(16.dp)).background(if (m.isUser) GoldPrimary else ObsidianSurfaceElevated).padding(horizontal = 14.dp, vertical = 10.dp)) {
                            Text(m.message, color = if (m.isUser) OnPrimary else TextMain)
                        }
                    }
                }
                profile?.let { p ->
                    item {
                        NyaayCard {
                            SectionTitle("CASE PROFILE")
                            Spacer(Modifier.height(6.dp))
                            Text(p.legalArea, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(4.dp))
                            Text("Urgency: ${p.urgency}", style = MaterialTheme.typography.bodyMedium)
                            Text("Documents needed: ${p.documentsRequired.size}", style = MaterialTheme.typography.bodyMedium)
                            Spacer(Modifier.height(12.dp))
                            Button(onClick = { vm.createCase(onCaseCreated) }, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary), shape = RoundedCornerShape(14.dp)) {
                                Text("Create Case Room", color = OnPrimary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                if (busy) item { Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) { CircularProgressIndicator(Modifier.size(20.dp), color = GoldPrimary) } }
            }
            Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(input, { vm.onInput(it) }, placeholder = { Text("Describe your issue…") }, modifier = Modifier.weight(1f), colors = TextFieldDefaults.colors(focusedContainerColor = ObsidianSurface, unfocusedContainerColor = ObsidianSurface, focusedTextColor = GoldPrimary, unfocusedTextColor = TextMain, focusedIndicatorColor = GoldPrimary, unfocusedIndicatorColor = ObsidianBorder, cursorColor = GoldPrimary))
                Spacer(Modifier.width(8.dp))
                IconButton(onClick = { vm.send() }, enabled = !busy) { Icon(Icons.Default.Send, "Send", tint = GoldPrimary) }
            }
        }
    }
}
