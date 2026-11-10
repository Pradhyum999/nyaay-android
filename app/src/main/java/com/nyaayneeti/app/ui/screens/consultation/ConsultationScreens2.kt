package com.nyaayneeti.app.ui.screens.consultation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nyaayneeti.app.data.model.*
import com.nyaayneeti.app.ui.components.*
import com.nyaayneeti.app.ui.theme.*
import com.nyaayneeti.app.viewmodel.AuthViewModel
import com.nyaayneeti.app.viewmodel.ConsultationViewModel

@Composable
fun ConsultationRoomScreen(id: String, vm: ConsultationViewModel, authVm: AuthViewModel, onBack: () -> Unit) {
    val user by authVm.currentUser.collectAsState()
    val c by vm.selected.collectAsState()
    var rating by remember { mutableStateOf(0) }
    var comment by remember { mutableStateOf("") }

    Scaffold(topBar = { NyaayTopAppBar("Consultation", showBackButton = true, onBackClick = onBack) }, containerColor = ObsidianBackground) { pad ->
        Column(Modifier.fillMaxSize().padding(pad).padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            val cons = c
            if (cons == null) {
                Text("Loading…", style = MaterialTheme.typography.bodyMedium)
            } else {
                val isLawyer = user?.role?.isLawyer() == true
                NyaayCard {
                    Text(if (isLawyer) "Client: ${cons.citizenName}" else "Advocate: ${cons.lawyerName}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    PillBadge(cons.status.name, if (cons.status == ConsultationStatus.COMPLETED) GreenContainer else GoldContainer, if (cons.status == ConsultationStatus.COMPLETED) GreenSuccess else GoldPrimary)
                    Spacer(Modifier.height(8.dp))
                    Text("Duration: ${cons.durationMinutes} min", style = MaterialTheme.typography.bodyMedium)
                    Text("Fee: ₹${cons.fee.toInt()}", style = MaterialTheme.typography.bodyMedium)
                    cons.notes?.let { Spacer(Modifier.height(4.dp)); Text("Note: $it", style = MaterialTheme.typography.bodyMedium) }
                }

                when {
                    isLawyer && cons.status == ConsultationStatus.REQUESTED -> {
                        Button(onClick = { vm.accept(id) }, modifier = Modifier.fillMaxWidth().height(52.dp), colors = ButtonDefaults.buttonColors(containerColor = GreenSuccess), shape = RoundedCornerShape(14.dp)) { Text("Accept", fontWeight = FontWeight.Bold) }
                        Button(onClick = { vm.reject(id) }, modifier = Modifier.fillMaxWidth().height(52.dp), colors = ButtonDefaults.buttonColors(containerColor = RedContainer, contentColor = RedUrgent), shape = RoundedCornerShape(14.dp)) { Text("Decline", fontWeight = FontWeight.Bold) }
                    }
                    cons.status == ConsultationStatus.SCHEDULED -> {
                        cons.meetingLink?.let { link ->
                            NyaayCard {
                                Text("Meeting link", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Spacer(Modifier.height(4.dp))
                                Text(link, style = MaterialTheme.typography.bodyMedium, color = GoldPrimary)
                            }
                        }
                        if (isLawyer) Button(onClick = { vm.complete(id) }, modifier = Modifier.fillMaxWidth().height(52.dp), colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary), shape = RoundedCornerShape(14.dp)) { Text("Mark completed", color = OnPrimary, fontWeight = FontWeight.Bold) }
                    }
                    !isLawyer && cons.status == ConsultationStatus.COMPLETED && cons.rating == 0 -> {
                        SectionTitle("Rate your advocate")
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            (1..5).forEach { i ->
                                IconButton(onClick = { rating = i }) { Icon(if (i <= rating) Icons.Default.Star else Icons.Default.StarBorder, null, tint = GoldPrimary) }
                            }
                        }
                        OutlinedTextField(comment, { comment = it }, label = { Text("Review (optional)") }, modifier = Modifier.fillMaxWidth(), colors = TextFieldDefaults.colors(focusedContainerColor = ObsidianSurface, unfocusedContainerColor = ObsidianSurface, focusedTextColor = GoldPrimary, unfocusedTextColor = TextMain, focusedIndicatorColor = GoldPrimary, unfocusedIndicatorColor = ObsidianBorder, cursorColor = GoldPrimary))
                        Button(onClick = {
                            val lawyer = UserEntity(uid = cons.lawyerUid, phone = "", name = cons.lawyerName, role = UserRole.INDEPENDENT_LAWYER)
                            val citizen = user
                            if (citizen != null) vm.addReview(lawyer, citizen, rating, comment)
                        }, enabled = rating > 0, modifier = Modifier.fillMaxWidth().height(52.dp), colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary), shape = RoundedCornerShape(14.dp)) { Text("Submit review", color = ObsidianBackground, fontWeight = FontWeight.Bold) }
                    }
                }
            }
        }
    }
}

private fun UserRole.isLawyer(): Boolean =
    this == UserRole.INDEPENDENT_LAWYER || this == UserRole.FIRM_ADMIN || this == UserRole.FIRM_MEMBER
