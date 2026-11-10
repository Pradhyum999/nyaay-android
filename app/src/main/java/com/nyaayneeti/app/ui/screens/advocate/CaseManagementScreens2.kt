package com.nyaayneeti.app.ui.screens.advocate

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nyaayneeti.app.data.model.InvoiceStatus
import com.nyaayneeti.app.data.model.PaymentMethod
import com.nyaayneeti.app.ui.components.*
import com.nyaayneeti.app.ui.theme.*
import com.nyaayneeti.app.viewmodel.CaseViewModel

@Composable
fun InvoicesScreen(vm: CaseViewModel, onBack: () -> Unit) {
    val invoices by vm.allInvoices.collectAsState()
    var showNew by remember { mutableStateOf(false) }

    Scaffold(topBar = { NyaayTopAppBar("Invoices", showBackButton = true, onBackClick = onBack, actions = { IconButton(onClick = { showNew = true }) { Icon(Icons.Default.Add, null, tint = GoldPrimary) } }) }, containerColor = ObsidianBackground) { pad ->
        if (invoices.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(pad), contentAlignment = Alignment.Center) { EmptyState(Icons.Default.Receipt, "No invoices", "Create an invoice for a client.") }
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(pad), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(invoices, key = { it.id }) { inv ->
                    NyaayCard {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text("${inv.invoiceNumber} · ${inv.clientName}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text(inv.caseNumber, style = MaterialTheme.typography.labelSmall)
                                Text("₹${inv.totalAmount.toInt()}", style = MaterialTheme.typography.titleLarge, color = GoldPrimary)
                            }
                            PillBadge(inv.status.name, if (inv.status == InvoiceStatus.PAID) GreenContainer else GoldContainer, if (inv.status == InvoiceStatus.PAID) GreenSuccess else GoldPrimary)
                            if (inv.status != InvoiceStatus.PAID) { Spacer(Modifier.width(8.dp)); TextButton(onClick = { vm.markInvoicePaid(inv.id, PaymentMethod.UPI, null) }) { Text("Mark paid", color = GreenSuccess) } }
                        }
                    }
                }
            }
        }
    }

    if (showNew) { NewInvoiceSheet(vm = vm, onDismiss = { showNew = false }) }
}
