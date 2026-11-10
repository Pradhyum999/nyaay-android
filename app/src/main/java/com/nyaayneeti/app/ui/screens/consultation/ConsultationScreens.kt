package com.nyaayneeti.app.ui.screens.consultation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nyaayneeti.app.data.model.ConsultationStatus
import com.nyaayneeti.app.data.model.UserRole
import com.nyaayneeti.app.ui.components.*
import com.nyaayneeti.app.ui.theme.*
import com.nyaayneeti.app.viewmodel.AuthViewModel
import com.nyaayneeti.app.viewmodel.ConsultationViewModel

@Composable
fun ConsultationListScreen(vm: ConsultationViewModel, authVm: AuthViewModel, onOpen: (String) -> Unit, onBack: () -> Unit) {
    val user by authVm.currentUser.collectAsState()
    val list by vm.consultations.collectAsState()
    Scaffold(topBar = { NyaayTopAppBar("Consultations", showBackButton = true, onBackClick = onBack) }, containerColor = ObsidianBackground) { pad ->
        if (list.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(pad), contentAlignment = Alignment.Center) {
                EmptyState(Icons.Default.Videocam, "No consultations", "Book a consultation to get started.")
            }
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(pad), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(list, key = { it.id }) { c ->
                    NyaayCard(onClick = { onOpen(c.id) }) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(if (user?.role?.isLawyer() == true) c.citizenName else c.lawyerName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text("${c.durationMinutes} min · ₹${c.fee.toInt()}", style = MaterialTheme.typography.bodyMedium)
                            }
                            PillBadge(c.status.name, when (c.status) { ConsultationStatus.COMPLETED -> GreenContainer; ConsultationStatus.REJECTED, ConsultationStatus.CANCELLED -> RedContainer; else -> GoldContainer }, when (c.status) { ConsultationStatus.COMPLETED -> GreenSuccess; ConsultationStatus.REJECTED, ConsultationStatus.CANCELLED -> RedUrgent; else -> GoldPrimary })
                        }
                    }
                }
            }
        }
    }
}

private fun UserRole.isLawyer(): Boolean =
    this == UserRole.INDEPENDENT_LAWYER || this == UserRole.FIRM_ADMIN || this == UserRole.FIRM_MEMBER
