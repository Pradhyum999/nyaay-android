package com.nyaayneeti.app.ui.screens.shared

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nyaayneeti.app.data.model.InquiryStatus
import com.nyaayneeti.app.data.model.UserRole
import com.nyaayneeti.app.ui.components.*
import com.nyaayneeti.app.ui.theme.*
import com.nyaayneeti.app.viewmodel.AuthViewModel
import com.nyaayneeti.app.viewmodel.InquiryViewModel

@Composable
fun InquiriesScreen(vm: InquiryViewModel, authVm: AuthViewModel, onOpenCase: (String) -> Unit, onBack: () -> Unit) {
    val user by authVm.currentUser.collectAsState()
    val isLawyer = user?.role?.isLawyer() == true
    val inquiries by (if (isLawyer) vm.forLawyer else vm.forClient).collectAsState()

    Scaffold(topBar = { NyaayTopAppBar("Inquiries", showBackButton = true, onBackClick = onBack) }, containerColor = ObsidianBackground) { pad ->
        if (inquiries.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(pad), contentAlignment = Alignment.Center) {
                EmptyState(Icons.Default.Inbox, "No inquiries", if (isLawyer) "Client inquiries will appear here." else "Your inquiries will appear here.")
            }
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(pad), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(inquiries, key = { it.id }) { i ->
                    NyaayCard {
                        Text(i.subject, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(if (isLawyer) "From: ${i.clientName}" else "To: advocate", style = MaterialTheme.typography.bodyMedium)
                        if (i.message.isNotBlank()) { Spacer(Modifier.height(4.dp)); Text(i.message, style = MaterialTheme.typography.bodyMedium, maxLines = 3) }
                        Spacer(Modifier.height(8.dp))
                        PillBadge(i.status.name, when (i.status) { InquiryStatus.ACCEPTED -> GreenContainer; InquiryStatus.DECLINED -> RedContainer; else -> GoldContainer }, when (i.status) { InquiryStatus.ACCEPTED -> GreenSuccess; InquiryStatus.DECLINED -> RedUrgent; else -> GoldPrimary })
                        if (isLawyer && i.status == InquiryStatus.NEW) {
                            Spacer(Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(onClick = { vm.accept(i.id) }, colors = ButtonDefaults.buttonColors(containerColor = GreenSuccess)) { Text("Accept", fontWeight = FontWeight.Bold) }
                                Button(onClick = { vm.decline(i.id) }, colors = ButtonDefaults.buttonColors(containerColor = RedContainer, contentColor = RedUrgent)) { Text("Decline", fontWeight = FontWeight.Bold) }
                            }
                        }
                        if (i.status == InquiryStatus.ACCEPTED && i.caseId != null) {
                            Spacer(Modifier.height(8.dp))
                            Button(onClick = { onOpenCase(i.caseId!!) }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)) { Text("Open case", color = OnPrimary, fontWeight = FontWeight.Bold) }
                        }
                    }
                }
            }
        }
    }
}

private fun UserRole.isLawyer(): Boolean =
    this == UserRole.INDEPENDENT_LAWYER || this == UserRole.FIRM_ADMIN || this == UserRole.FIRM_MEMBER
