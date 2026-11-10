package com.nyaayneeti.app.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nyaayneeti.app.data.model.MessageType
import com.nyaayneeti.app.ui.components.NyaayTopAppBar
import com.nyaayneeti.app.ui.theme.*
import com.nyaayneeti.app.viewmodel.ChatViewModel

@Composable
fun ChatThreadScreen(convoId: String, vm: ChatViewModel, onBack: () -> Unit) {
    val me by vm.me.collectAsState()
    val peer by vm.peer.collectAsState()
    val messages by vm.messages.collectAsState()
    val draft by vm.draft.collectAsState()
    var inCall by remember { mutableStateOf(false) }

    if (inCall) {
        CallScreen(peerName = peer?.name ?: "Call", onEnd = { inCall = false })
        return
    }

    Scaffold(topBar = {
        NyaayTopAppBar(peer?.name ?: "Chat", showBackButton = true, onBackClick = onBack, actions = {
            IconButton(onClick = { vm.sendCall(); inCall = true }) { Icon(Icons.Default.Call, "Voice call", tint = GreenSuccess) }
            IconButton(onClick = { vm.sendCall(); inCall = true }) { Icon(Icons.Default.Videocam, "Video call", tint = GoldPrimary) }
        })
    }, containerColor = ObsidianBackground) { pad ->
        Column(Modifier.fillMaxSize().padding(pad)) {
            LazyColumn(Modifier.weight(1f).fillMaxWidth(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(messages, key = { it.id }) { m ->
                    val mine = m.senderUid == me?.uid
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = if (mine) Arrangement.End else Arrangement.Start) {
                        Column(
                            Modifier.clip(RoundedCornerShape(16.dp))
                                .background(if (mine) GoldPrimary else ObsidianSurfaceElevated)
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            if (m.type == MessageType.CALL) {
                                Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.Call, null, tint = if (mine) OnPrimary else TextMain, modifier = Modifier.size(16.dp)); Spacer(Modifier.width(6.dp)); Text(m.text, color = if (mine) OnPrimary else TextMain) }
                            } else {
                                Column {
                                    Text(m.text, color = if (mine) OnPrimary else TextMain)
                                    if (mine) Text(if (m.isRead) "✓✓" else "✓", color = OnPrimary.copy(alpha = if (m.isRead) 1f else 0.55f), fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }
            Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = draft, onValueChange = { vm.onDraft(it) },
                    placeholder = { Text("Message") }, singleLine = true, modifier = Modifier.weight(1f),
                    colors = TextFieldDefaults.colors(focusedContainerColor = ObsidianSurface, unfocusedContainerColor = ObsidianSurface, focusedTextColor = GoldPrimary, unfocusedTextColor = TextMain, focusedIndicatorColor = GoldPrimary, unfocusedIndicatorColor = ObsidianBorder, cursorColor = GoldPrimary)
                )
                Spacer(Modifier.width(8.dp))
                IconButton(onClick = { vm.send() }, modifier = Modifier.size(48.dp).background(GoldPrimary, CircleShape)) {
                    Icon(Icons.AutoMirrored.Filled.Send, "Send", tint = OnPrimary)
                }
            }
        }
    }
}
