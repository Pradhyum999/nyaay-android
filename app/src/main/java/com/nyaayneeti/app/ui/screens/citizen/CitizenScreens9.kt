package com.nyaayneeti.app.ui.screens.citizen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nyaayneeti.app.data.model.HearingEntity
import com.nyaayneeti.app.data.model.HearingStatus
import com.nyaayneeti.app.data.model.InvoiceEntity
import com.nyaayneeti.app.ui.components.*
import com.nyaayneeti.app.ui.theme.*

@Composable
internal fun HearingsTab(hearings: List<HearingEntity>, isLawyer: Boolean, onAdd: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        if (isLawyer) {
            Button(onClick = onAdd, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)) { Text("+ Add hearing", color = ObsidianBackground, fontWeight = FontWeight.Bold) }
            Spacer(Modifier.height(8.dp))
        }
        if (hearings.isEmpty()) {
            Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) { EmptyState(Icons.Default.CalendarMonth, "No hearings", "Add a hearing to track it here.") }
        } else {
            LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(hearings) { h ->
                    NyaayCard {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("${h.hearingTime} · ${h.courtNumber}", style = MaterialTheme.typography.titleMedium, color = GoldPrimary, fontWeight = FontWeight.Bold)
                            PillBadge(h.status.name, if (h.status == HearingStatus.COMPLETED) GreenContainer else GoldContainer, if (h.status == HearingStatus.COMPLETED) GreenSuccess else GoldPrimary)
                        }
                        Spacer(Modifier.height(4.dp))
                        Text("Item #${h.itemNumber} · ${h.purpose}", style = MaterialTheme.typography.bodyMedium)
                        h.judgeName?.let { Text("Judge: $it", style = MaterialTheme.typography.labelSmall) }
                        h.orderNotes?.let { Spacer(Modifier.height(4.dp)); Text("Order: $it", style = MaterialTheme.typography.bodyMedium, color = TextSub) }
                    }
                }
            }
        }
    }
}

@Composable
internal fun InvoicesTab(invoices: List<InvoiceEntity>) {
    if (invoices.isEmpty()) {
        Box(Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) { EmptyState(Icons.Default.Receipt, "No invoices", "Invoices for this case appear here.") }
    } else {
        LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(invoices) { inv ->
                NyaayCard {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(Modifier.weight(1f)) {
                            Text(inv.invoiceNumber, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text("₹${inv.totalAmount.toInt()}", style = MaterialTheme.typography.titleLarge, color = GoldPrimary)
                        }
                        PillBadge(inv.status.name, if (inv.status.name == "PAID") GreenContainer else GoldContainer, if (inv.status.name == "PAID") GreenSuccess else GoldPrimary)
                    }
                }
            }
        }
    }
}
