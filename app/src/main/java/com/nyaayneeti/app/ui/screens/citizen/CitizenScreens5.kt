package com.nyaayneeti.app.ui.screens.citizen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nyaayneeti.app.data.model.CaseNoteEntity
import com.nyaayneeti.app.data.model.DocumentEntity
import com.nyaayneeti.app.data.model.DocumentStatus
import com.nyaayneeti.app.ui.components.*
import com.nyaayneeti.app.ui.theme.*
import java.util.UUID

@Composable
internal fun NotesTab(notes: List<CaseNoteEntity>, isLawyer: Boolean, onAdd: (String, Boolean) -> Unit) {
    var text by remember { mutableStateOf("") }
    var priv by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(text, { text = it }, placeholder = { Text("Add a note…") }, modifier = Modifier.weight(1f), colors = TextFieldDefaults.colors(focusedContainerColor = ObsidianSurface, unfocusedContainerColor = ObsidianSurface, focusedTextColor = GoldPrimary, unfocusedTextColor = TextMain, focusedIndicatorColor = GoldPrimary, unfocusedIndicatorColor = ObsidianBorder, cursorColor = GoldPrimary))
            Spacer(Modifier.width(8.dp))
            IconButton(onClick = { if (text.isNotBlank()) { onAdd(text, priv); text = "" } }) { Icon(Icons.Default.Send, "Add", tint = GoldPrimary) }
        }
        if (isLawyer) Row(verticalAlignment = Alignment.CenterVertically) { Checkbox(checked = priv, onCheckedChange = { priv = it }, colors = CheckboxDefaults.colors(checkedColor = GoldPrimary)); Text("Private", style = MaterialTheme.typography.labelSmall) }
        Spacer(Modifier.height(8.dp))
        LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(notes.filter { !it.isPrivate || isLawyer }) { n ->
                NyaayCard {
                    Text(n.authorName, style = MaterialTheme.typography.titleMedium, color = GoldPrimary)
                    Spacer(Modifier.height(4.dp))
                    Text(n.content, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
internal fun DocsTab(docs: List<DocumentEntity>, caseId: String, ownerUid: String, isLawyer: Boolean, onAdd: (DocumentEntity) -> Unit, onVerify: (String, DocumentStatus) -> Unit) {
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            onAdd(DocumentEntity(id = UUID.randomUUID().toString(), caseId = caseId, ownerUid = ownerUid, name = it.lastPathSegment ?: "Document", uri = it.toString(), mimeType = "application/octet-stream", sizeBytes = 0, category = "other"))
        }
    }
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Button(onClick = { launcher.launch("*/*") }, colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)) { Text("Upload document", color = ObsidianBackground, fontWeight = FontWeight.Bold) }
        Spacer(Modifier.height(8.dp))
        if (docs.isEmpty()) Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) { EmptyState(Icons.Default.Folder, "No documents", "Uploaded documents appear here.") }
        else LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(docs) { d ->
                NyaayCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Description, null, tint = GoldPrimary)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(d.name, style = MaterialTheme.typography.titleMedium)
                            PillBadge(d.status.name, if (d.status == DocumentStatus.VERIFIED) GreenContainer else GoldContainer, if (d.status == DocumentStatus.VERIFIED) GreenSuccess else GoldPrimary)
                        }
                        if (isLawyer) {
                            TextButton(onClick = { onVerify(d.id, if (d.status == DocumentStatus.VERIFIED) DocumentStatus.PENDING else DocumentStatus.VERIFIED) }) {
                                Text(if (d.status == DocumentStatus.VERIFIED) "Unverify" else "Verify", color = if (d.status == DocumentStatus.VERIFIED) TextSub else GreenSuccess)
                            }
                        }
                    }
                }
            }
        }
    }
}
