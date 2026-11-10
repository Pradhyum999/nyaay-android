package com.nyaayneeti.app.ui.screens.firm

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nyaayneeti.app.data.model.FirmRole
import com.nyaayneeti.app.data.model.UserEntity
import com.nyaayneeti.app.ui.components.*
import com.nyaayneeti.app.ui.theme.*
import com.nyaayneeti.app.viewmodel.FirmViewModel

@Composable
fun FirmMembersTab(vm: FirmViewModel, members: List<UserEntity>) {
    var showInvite by remember { mutableStateOf(false) }
    val msg by vm.message.collectAsState()

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Button(onClick = { showInvite = true }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)) { Text("+ Invite member", color = OnPrimary, fontWeight = FontWeight.Bold) }
        Spacer(Modifier.height(12.dp))
        msg?.let { Text(it, color = GreenSuccess, style = MaterialTheme.typography.bodyMedium); Spacer(Modifier.height(8.dp)) }
        LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(members, key = { it.uid }) { m ->
                NyaayCard {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(m.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            PillBadge(m.firmRole?.name ?: "MEMBER", GoldContainer, GoldPrimary)
                        }
                        if (m.firmRole != FirmRole.OWNER) {
                            TextButton(onClick = { vm.removeMember(m.uid) }) { Text("Remove", color = RedUrgent) }
                        }
                    }
                }
            }
        }
    }

    if (showInvite) {
        val email by vm.inviteEmail.collectAsState()
        val role by vm.inviteRole.collectAsState()
        AlertDialog(
            onDismissRequest = { showInvite = false },
            confirmButton = { TextButton(onClick = { vm.invite(); showInvite = false }) { Text("Send invite", color = GoldPrimary) } },
            dismissButton = { TextButton(onClick = { showInvite = false }) { Text("Cancel", color = TextSub) } },
            title = { Text("Invite member by email") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(email, { vm.onInviteEmail(it) }, label = { Text("Email address") }, singleLine = true, colors = TextFieldDefaults.colors(focusedContainerColor = ObsidianSurface, unfocusedContainerColor = ObsidianSurface, focusedTextColor = GoldPrimary, unfocusedTextColor = TextMain, focusedIndicatorColor = GoldPrimary, unfocusedIndicatorColor = ObsidianBorder))
                    Text("The member will receive an invitation and fill their own details.", style = MaterialTheme.typography.labelSmall, color = TextSub)
                    FirmRole.values().filter { it != FirmRole.OWNER }.forEach { r ->
                        FilterChip(selected = role == r, onClick = { vm.selectInviteRole(r) }, label = { Text(r.name.replace("_", " ")) }, colors = FilterChipDefaults.filterChipColors(selectedContainerColor = GoldContainer, selectedLabelColor = GoldPrimary, labelColor = TextSub))
                    }
                }
            }
        )
    }
}
