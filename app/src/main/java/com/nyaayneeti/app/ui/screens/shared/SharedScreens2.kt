package com.nyaayneeti.app.ui.screens.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.sp
import com.nyaayneeti.app.data.model.*
import com.nyaayneeti.app.ui.components.*
import com.nyaayneeti.app.ui.theme.*
import com.nyaayneeti.app.viewmodel.AuthViewModel
import com.nyaayneeti.app.viewmodel.BillingViewModel

@Composable
fun PaymentsScreen(vm: BillingViewModel, onBack: () -> Unit) {
    val payments by vm.payments.collectAsState()
    Scaffold(topBar = { NyaayTopAppBar("Payments", showBackButton = true, onBackClick = onBack) }, containerColor = ObsidianBackground) { pad ->
        if (payments.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(pad), contentAlignment = Alignment.Center) {
                EmptyState(Icons.Default.Payments, "No payments yet", "Consultation and subscription payments will show here.")
            }
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(pad), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(payments, key = { it.id }) { p ->
                    NyaayCard {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(p.purpose.replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.titleMedium)
                                Text(p.status.name, style = MaterialTheme.typography.labelSmall)
                            }
                            Text("₹${p.amount.toInt()}", color = GreenSuccess, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileTab(
    authVm: AuthViewModel,
    onSignOut: () -> Unit,
    onOpenNotifications: () -> Unit,
    onOpenConsultations: (() -> Unit)? = null,
    onOpenSubscription: (() -> Unit)? = null,
    onOpenPayments: (() -> Unit)? = null,
    onOpenVerification: (() -> Unit)? = null
) {
    val user by authVm.currentUser.collectAsState()
    val themeMode by authVm.themeMode.collectAsState()
    val u = user
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        NyaayCard {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(Modifier.size(56.dp), shape = CircleShape, color = GoldContainer) {
                    Box(contentAlignment = Alignment.Center) { Text("⚖", fontSize = 24.sp) }
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(user?.name ?: "User", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    RoleBadge(user?.role ?: UserRole.CITIZEN)
                    user?.phone?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }
                }
            }
        }
        if (u?.role?.isLawyer() == true) {
            NyaayCard {
                Text("Verification", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                PillBadge(u.verificationStatus.name, if (u.verificationStatus == VerificationStatus.VERIFIED) GreenContainer else GoldContainer, if (u.verificationStatus == VerificationStatus.VERIFIED) GreenSuccess else GoldPrimary)
                u.barCouncilId?.let { Spacer(Modifier.height(4.dp)); Text("Bar Council: $it", style = MaterialTheme.typography.bodyMedium) }
            }
        }
        onOpenConsultations?.let { cb ->
            NyaayCard(onClick = cb) { Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.Videocam, null, tint = GoldPrimary); Spacer(Modifier.width(12.dp)); Text("My Consultations", style = MaterialTheme.typography.titleMedium) } }
        }
        onOpenNotifications?.let { cb ->
            NyaayCard(onClick = cb) { Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.Notifications, null, tint = GoldPrimary); Spacer(Modifier.width(12.dp)); Text("Notifications", style = MaterialTheme.typography.titleMedium) } }
        }
        onOpenVerification?.let { cb ->
            NyaayCard(onClick = cb) { Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.VerifiedUser, null, tint = GoldPrimary); Spacer(Modifier.width(12.dp)); Text("Verify my profile", style = MaterialTheme.typography.titleMedium) } }
        }
        onOpenSubscription?.let { cb ->
            NyaayCard(onClick = cb) { Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.WorkspacePremium, null, tint = GoldPrimary); Spacer(Modifier.width(12.dp)); Text("Subscription", style = MaterialTheme.typography.titleMedium) } }
        }
        onOpenPayments?.let { cb ->
            NyaayCard(onClick = cb) { Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.Payments, null, tint = GoldPrimary); Spacer(Modifier.width(12.dp)); Text("Payments", style = MaterialTheme.typography.titleMedium) } }
        }
        NyaayCard {
            Text("Appearance", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("light" to "Light", "dark" to "Dark", "system" to "System").forEach { (mode, label) ->
                    FilterChip(
                        selected = themeMode == mode,
                        onClick = { authVm.setThemeMode(mode) },
                        label = { Text(label) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = GoldContainer, selectedLabelColor = GoldPrimary, labelColor = TextSub)
                    )
                }
            }
        }
        Button(onClick = onSignOut, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = RedContainer, contentColor = RedUrgent)) { Text("Sign out") }
    }
}

private fun UserRole.isLawyer(): Boolean =
    this == UserRole.INDEPENDENT_LAWYER || this == UserRole.FIRM_ADMIN || this == UserRole.FIRM_MEMBER
