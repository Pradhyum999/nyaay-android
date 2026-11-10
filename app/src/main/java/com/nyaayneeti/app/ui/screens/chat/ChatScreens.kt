package com.nyaayneeti.app.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nyaayneeti.app.ui.components.*
import com.nyaayneeti.app.ui.theme.*
import com.nyaayneeti.app.viewmodel.ChatViewModel

@Composable
fun ChatInbox(vm: ChatViewModel, onOpen: (String, String) -> Unit) {
    val me by vm.me.collectAsState()
    val conversations by vm.conversations.collectAsState()
    val names by vm.nameMap.collectAsState()

    if (conversations.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            EmptyState(Icons.Default.Chat, "No chats yet", "Message your advocate or client to start a conversation.")
        }
    } else {
        LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(conversations, key = { it.id }) { c ->
                val other = if (me?.uid == c.userA) c.userB else c.userA
                val unread = c.lastSenderUid != null && c.lastSenderUid != me?.uid
                NyaayCard(onClick = { onOpen(c.id, other) }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(Modifier.size(48.dp), shape = CircleShape, color = GoldContainer) {
                            Box(contentAlignment = Alignment.Center) { Text("⚖", fontSize = 20.sp) }
                        }
                        Spacer(Modifier.width(14.dp))
                        Column(Modifier.weight(1f)) {
                            Text(names[other] ?: "Chat", style = MaterialTheme.typography.titleMedium, fontWeight = if (unread) FontWeight.Bold else FontWeight.Normal)
                            Text(c.lastMessage, style = MaterialTheme.typography.bodyMedium, maxLines = 1)
                        }
                        if (unread) { Box(Modifier.size(10.dp).background(GoldPrimary, CircleShape)) }
                    }
                }
            }
        }
    }
}
