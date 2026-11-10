package com.nyaayneeti.app.ui.screens.citizen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nyaayneeti.app.data.model.*
import com.nyaayneeti.app.ui.components.NyaayTopAppBar
import com.nyaayneeti.app.ui.screens.advocate.HearingFormSheet
import com.nyaayneeti.app.ui.theme.*
import com.nyaayneeti.app.viewmodel.AuthViewModel
import com.nyaayneeti.app.viewmodel.CaseAIViewModel
import com.nyaayneeti.app.viewmodel.CaseViewModel

@Composable
fun CaseRoomScreen(caseId: String, vm: CaseViewModel, caseAiVm: CaseAIViewModel, authVm: AuthViewModel, onBack: () -> Unit) {
    val user by authVm.currentUser.collectAsState()
    val c by vm.selectedCase.collectAsState()
    val timeline by vm.timeline.collectAsState()
    val tasks by vm.tasks.collectAsState()
    val notes by vm.notes.collectAsState()
    val docs by vm.documents.collectAsState()
    val hearings by vm.hearings.collectAsState()
    val invoices by vm.invoices.collectAsState()
    var tab by remember { mutableStateOf(0) }
    var showHearingForm by remember { mutableStateOf(false) }
    var showAsk by remember { mutableStateOf(false) }
    val isLawyer = user?.role?.isLawyer() == true

    val caseContext = remember(c, timeline, tasks, notes) {
        buildString {
            c?.let { append("Title: ${it.title}\nMatter: ${it.matterType}\nLegal area: ${it.legalArea}\nStage: ${it.stage.name}\nStatus: ${it.status.name}\nCourt: ${it.court}\nAdvocate: ${it.lawyerName ?: "Unassigned"}\n") }
            append("Timeline:\n")
            timeline.take(10).forEach { append("- ${it.titleEn}: ${it.descriptionEn}\n") }
            append("Tasks:\n")
            tasks.take(10).forEach { append("- ${it.title} (${if (it.isCompleted) "done" else "pending"})\n") }
            append("Notes:\n")
            notes.take(10).forEach { append("- ${it.content}\n") }
        }
    }

    Scaffold(topBar = {
        NyaayTopAppBar(c?.caseNumber ?: "Case Room", showBackButton = true, onBackClick = onBack, actions = {
            IconButton(onClick = { showAsk = true }) { Icon(Icons.Default.AutoAwesome, "Ask My Case", tint = GoldPrimary) }
            if (isLawyer) {
                IconButton(onClick = { vm.deleteCase(caseId); onBack() }) { Icon(Icons.Default.Delete, "Delete case", tint = RedUrgent) }
            }
        })
    }, containerColor = ObsidianBackground) { pad ->
        Column(Modifier.fillMaxSize().padding(pad)) {
            TabRow(selectedTabIndex = tab, containerColor = ObsidianSurface) {
                listOf("Summary", "Timeline", "Tasks", "Notes", "Docs", "Hearings", "Invoices").forEachIndexed { i, t ->
                    Tab(selected = tab == i, onClick = { tab = i }, text = { Text(t, color = if (tab == i) GoldPrimary else TextSub, fontSize = 12.sp) })
                }
            }
            when (tab) {
                0 -> SummaryTab(c, isLawyer) { s -> vm.updateStage(caseId, s) }
                1 -> TimelineTab(timeline)
                2 -> TasksTab(tasks) { id -> vm.toggleTask(caseId, id) }
                3 -> NotesTab(notes, isLawyer) { content, priv -> vm.addNote(caseId, content, priv) }
                4 -> DocsTab(docs, caseId, user?.uid ?: "", isLawyer, onAdd = { d -> vm.addDocument(caseId, d) }, onVerify = { id, status -> vm.verifyDocument(id, status) })
                5 -> HearingsTab(hearings, isLawyer) { showHearingForm = true }
                6 -> InvoicesTab(invoices)
            }
        }
    }

    if (showHearingForm) {
        HearingFormSheet(caseId = caseId, vm = vm, onDismiss = { showHearingForm = false })
    }
    if (showAsk) {
        AskMyCasePanel(caseAiVm, caseContext, onDismiss = { showAsk = false })
    }
}

internal fun UserRole.isLawyer(): Boolean =
    this == UserRole.INDEPENDENT_LAWYER || this == UserRole.FIRM_ADMIN || this == UserRole.FIRM_MEMBER
