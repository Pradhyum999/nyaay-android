package com.nyaayneeti.app.ui.screens.citizen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nyaayneeti.app.data.model.CaseFile
import com.nyaayneeti.app.ui.components.*
import com.nyaayneeti.app.ui.theme.*
import com.nyaayneeti.app.viewmodel.AIIntakeViewModel
import com.nyaayneeti.app.viewmodel.AuthViewModel
import com.nyaayneeti.app.viewmodel.CaseViewModel

@Composable
fun CitizenHomeScreen(
    authViewModel: AuthViewModel,
    caseViewModel: CaseViewModel,
    onStartIntake: () -> Unit,
    onOpenCaseRoom: (String) -> Unit,
    onOpenConsultation: () -> Unit
) {
    val user by authViewModel.currentUser.collectAsState()
    val cases by caseViewModel.cases.collectAsState()

    Scaffold(
        topBar = {
            NyaayTopAppBar(
                title = "Citizen Portal",
                subtitle = user?.name ?: "Citizen",
                actions = {
                    IconButton(onClick = { authViewModel.signOut() }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Sign Out", tint = TextSub)
                    }
                }
            )
        },
        containerColor = ObsidianBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                // AI Intake Banner
                Surface(
                    color = GoldContainer,
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth().clickable { onStartIntake() }
                ) {
                    Row(
                        modifier = Modifier.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(44.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = GoldPrimary
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = ObsidianBackground)
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Build My Case",
                                style = MaterialTheme.typography.titleMedium,
                                color = GoldPrimary
                            )
                            Text(
                                text = "AI-powered structured intake & dossier generator",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = GoldPrimary)
                    }
                }
            }

            item {
                Text(
                    text = "My Active Case Rooms",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextMain
                )
            }

            if (cases.isEmpty()) {
                item {
                    NyaayCard {
                        Text(
                            text = "No cases active yet. Start with 'Build My Case' to create your first legal matter room.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSub
                        )
                    }
                }
            } else {
                items(cases) { c ->
                    CaseItemCard(
                        caseFile = c,
                        onClick = { onOpenCaseRoom(c.id) },
                        onCallAdvocate = onOpenConsultation
                    )
                }
            }
        }
    }
}

@Composable
fun CaseItemCard(
    caseFile: CaseFile,
    onClick: () -> Unit,
    onCallAdvocate: () -> Unit
) {
    NyaayCard(onClick = onClick) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = caseFile.caseNumber,
                style = MaterialTheme.typography.labelSmall,
                color = GoldPrimary
            )
            CasePriorityBadge(caseFile.priority)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = caseFile.title,
            style = MaterialTheme.typography.titleMedium,
            color = TextMain
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Court: ${caseFile.court}",
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "Stage: ${caseFile.stage}",
            style = MaterialTheme.typography.bodyMedium
        )
        caseFile.nextHearingDate?.let {
            Text(
                text = "Next Hearing: $it",
                style = MaterialTheme.typography.bodyMedium,
                color = GoldPrimary
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Counsel: ${caseFile.lawyerName ?: "Assigning…"}",
                style = MaterialTheme.typography.labelSmall,
                color = TextSub
            )
            Button(
                onClick = onCallAdvocate,
                colors = ButtonDefaults.buttonColors(containerColor = ObsidianSurfaceElevated),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(Icons.Default.Phone, contentDescription = null, tint = GreenSuccess, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Consult", color = GreenSuccess, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun BuildCaseIntakeScreen(
    aiViewModel: AIIntakeViewModel,
    authViewModel: AuthViewModel,
    onCaseRoomCreated: (String) -> Unit,
    onBack: () -> Unit
) {
    val messages by aiViewModel.messages.collectAsState()
    val profile by aiViewModel.currentProfile.collectAsState()
    val isGenerating by aiViewModel.isGenerating.collectAsState()
    val inputText by aiViewModel.inputText.collectAsState()
    val user by authViewModel.currentUser.collectAsState()

    Scaffold(
        topBar = {
            NyaayTopAppBar(
                title = "Build My Case",
                subtitle = "Nyay AI Intake Engine",
                showBackButton = true,
                onBackClick = onBack
            )
        },
        containerColor = ObsidianBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                profile?.let { p ->
                    item {
                        Surface(
                            color = ObsidianSurface,
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary),
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "CASE PROFILE GENERATED",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = GoldPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${(p.confidenceScore * 100).toInt()}% Confidence",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = GreenSuccess
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = p.matterType,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = TextMain
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = p.summaryText,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Required Documents: ${p.documentsRequired.joinToString(", ")}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = GoldPrimary
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                Button(
                                    onClick = {
                                        aiViewModel.createCaseRoom(
                                            clientName = user?.name ?: "Client",
                                            clientPhone = user?.phone ?: "+919999999999",
                                            onCaseCreated = onCaseRoomCreated
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.Folder, contentDescription = null, tint = ObsidianBackground)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "Create Case Room & Match Lawyer",
                                        color = ObsidianBackground,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                items(messages) { msg ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (msg.isUser) Arrangement.End else Arrangement.Start
                    ) {
                        Surface(
                            color = if (msg.isUser) GoldPrimary else ObsidianSurface,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.widthIn(max = 300.dp)
                        ) {
                            Text(
                                text = msg.message,
                                color = if (msg.isUser) ObsidianBackground else TextMain,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(14.dp)
                            )
                        }
                    }
                }

                if (isGenerating) {
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = GoldPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Analyzing matter and drafting legal dossier…", color = TextSub, fontSize = 12.sp)
                        }
                    }
                }
            }

            // Input Bar
            Surface(
                color = ObsidianSurface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = inputText,
                        onValueChange = { aiViewModel.onInputChange(it) },
                        placeholder = { Text("Explain your case or legal dispute…", color = TextFaint, fontSize = 14.sp) },
                        modifier = Modifier.weight(1f),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = TextMain,
                            unfocusedTextColor = TextMain,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )
                    IconButton(
                        onClick = { aiViewModel.sendUserMessage() },
                        enabled = inputText.isNotBlank() && !isGenerating
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = if (inputText.isNotBlank()) GoldPrimary else TextFaint
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CaseRoomScreen(
    caseId: String,
    caseViewModel: CaseViewModel,
    onBack: () -> Unit
) {
    val cases by caseViewModel.cases.collectAsState()
    val timelineMap by caseViewModel.timeline.collectAsState()
    val tasksMap by caseViewModel.tasks.collectAsState()

    val currentCase = cases.find { it.id == caseId } ?: cases.firstOrNull()
    val timeline = timelineMap[caseId] ?: emptyList()
    val tasks = tasksMap[caseId] ?: emptyList()

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Overview", "Timeline", "Tasks")

    Scaffold(
        topBar = {
            NyaayTopAppBar(
                title = currentCase?.caseNumber ?: "Case Room",
                subtitle = currentCase?.matterType,
                showBackButton = true,
                onBackClick = onBack
            )
        },
        containerColor = ObsidianBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = ObsidianBackground,
                contentColor = GoldPrimary
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title, fontWeight = FontWeight.SemiBold) }
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                when (selectedTab) {
                    0 -> {
                        item {
                            NyaayCard {
                                Text(text = "MATTER SUMMARY", style = MaterialTheme.typography.labelSmall, color = GoldPrimary)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = currentCase?.title ?: "", style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = "Court: ${currentCase?.court}", style = MaterialTheme.typography.bodyMedium)
                                Text(text = "Assigned Counsel: ${currentCase?.lawyerName ?: "Assigning"}", style = MaterialTheme.typography.bodyMedium)
                                Text(text = "Stage: ${currentCase?.stage}", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                    1 -> {
                        items(timeline) { event ->
                            NyaayCard {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(event.titleEn, style = MaterialTheme.typography.titleMedium, color = GoldPrimary)
                                    Text(event.eventDate, style = MaterialTheme.typography.labelSmall, color = TextSub)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(event.descriptionEn, style = MaterialTheme.typography.bodyMedium)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Actor: ${event.actor}", style = MaterialTheme.typography.labelSmall, color = TextFaint)
                            }
                        }
                    }
                    2 -> {
                        items(tasks) { task ->
                            NyaayCard(onClick = { caseViewModel.toggleTask(caseId, task.id) }) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = task.isCompleted,
                                        onCheckedChange = { caseViewModel.toggleTask(caseId, task.id) },
                                        colors = CheckboxDefaults.colors(checkedColor = GoldPrimary)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = task.titleEn,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = if (task.isCompleted) TextFaint else TextMain
                                        )
                                        task.dueDate?.let {
                                            Text(text = "Due: $it", style = MaterialTheme.typography.labelSmall, color = RedUrgent)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
