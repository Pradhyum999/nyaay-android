package com.nyaayneeti.app.ui.screens.advocate

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nyaayneeti.app.data.model.CasePriority
import com.nyaayneeti.app.data.model.HearingItem
import com.nyaayneeti.app.ui.components.*
import com.nyaayneeti.app.ui.theme.*
import com.nyaayneeti.app.viewmodel.AuthViewModel
import com.nyaayneeti.app.viewmodel.CaseViewModel

@Composable
fun AdvocateHomeScreen(
    authViewModel: AuthViewModel,
    caseViewModel: CaseViewModel,
    onOpenCauseList: () -> Unit,
    onOpenCase: (String) -> Unit
) {
    val user by authViewModel.currentUser.collectAsState()
    val cases by caseViewModel.cases.collectAsState()
    val hearings by caseViewModel.hearings.collectAsState()

    var showOnlyUrgent by remember { mutableStateOf(false) }

    val filteredCases = if (showOnlyUrgent) {
        cases.filter { it.priority == CasePriority.URGENT }
    } else {
        cases
    }

    Scaffold(
        topBar = {
            NyaayTopAppBar(
                title = "Advocate Workstation",
                subtitle = user?.name ?: "Advocate",
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
                // Today's Cause List Quick Banner
                NyaayCard(onClick = onOpenCauseList) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                modifier = Modifier.size(44.dp),
                                shape = RoundedCornerShape(12.dp),
                                color = GoldContainer
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = GoldPrimary)
                                }
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text("Today's Court Diary", style = MaterialTheme.typography.titleMedium, color = GoldPrimary)
                                Text("${hearings.size} Hearings scheduled today", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = GoldPrimary)
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Active Cases (${filteredCases.size})",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextMain
                    )
                    FilterChip(
                        selected = showOnlyUrgent,
                        onClick = { showOnlyUrgent = !showOnlyUrgent },
                        label = { Text("Urgent Only", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = RedContainer,
                            selectedLabelColor = RedUrgent
                        )
                    )
                }
            }

            items(filteredCases) { c ->
                NyaayCard(onClick = { onOpenCase(c.id) }) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(c.caseNumber, style = MaterialTheme.typography.labelSmall, color = GoldPrimary)
                        CasePriorityBadge(c.priority)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(c.title, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Client: ${c.clientName} (${c.clientPhone})", style = MaterialTheme.typography.bodyMedium)
                    Text("Stage: ${c.stage}", style = MaterialTheme.typography.bodyMedium)
                    c.nextHearingDate?.let {
                        Text("Next Hearing: $it", style = MaterialTheme.typography.bodyMedium, color = GoldPrimary)
                    }
                }
            }
        }
    }
}

@Composable
fun CauseListScreen(
    caseViewModel: CaseViewModel,
    onBack: () -> Unit
) {
    val hearings by caseViewModel.hearings.collectAsState()

    Scaffold(
        topBar = {
            NyaayTopAppBar(
                title = "Today's Cause List",
                subtitle = "District & Sessions Court",
                showBackButton = true,
                onBackClick = onBack
            )
        },
        containerColor = ObsidianBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(hearings) { h ->
                HearingCard(hearing = h)
            }
        }
    }
}

@Composable
fun HearingCard(hearing: HearingItem) {
    NyaayCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = GoldPrimary,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "ITEM #${hearing.itemNumber}",
                    color = ObsidianBackground,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
            Text(
                text = "${hearing.hearingTime} • ${hearing.courtNumber}",
                style = MaterialTheme.typography.labelSmall,
                color = GoldPrimary
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(hearing.caseNumber, style = MaterialTheme.typography.titleMedium, color = TextMain)
        Spacer(modifier = Modifier.height(4.dp))
        Text("Stage: ${hearing.stage}", style = MaterialTheme.typography.bodyMedium)
        Text("Purpose: ${hearing.purpose}", style = MaterialTheme.typography.bodyMedium, color = TextSub)
    }
}
