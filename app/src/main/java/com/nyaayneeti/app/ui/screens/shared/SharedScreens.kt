package com.nyaayneeti.app.ui.screens.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nyaayneeti.app.data.model.SubscriptionTier
import com.nyaayneeti.app.data.repository.BillingRepository
import com.nyaayneeti.app.ui.components.*
import com.nyaayneeti.app.ui.theme.*
import com.nyaayneeti.app.viewmodel.BillingViewModel
import com.nyaayneeti.app.viewmodel.NotificationViewModel

@Composable
fun NotificationsScreen(vm: NotificationViewModel, onNavigate: (String) -> Unit, onBack: () -> Unit) {
    val list by vm.notifications.collectAsState()
    Scaffold(topBar = {
        NyaayTopAppBar("Notifications", showBackButton = true, onBackClick = onBack, actions = {
            IconButton(onClick = { vm.markAll() }) { Icon(Icons.Default.DoneAll, "Mark all read", tint = GoldPrimary) }
        })
    }, containerColor = ObsidianBackground) { pad ->
        if (list.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(pad), contentAlignment = Alignment.Center) {
                EmptyState(Icons.Default.Notifications, "No notifications", "Updates about your cases and activity will appear here.")
            }
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(pad), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(list, key = { it.id }) { n ->
                    NyaayCard(onClick = { vm.markRead(n.id); n.deepLink?.let(onNavigate) }) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Notifications, null, tint = if (n.isRead) TextFaint else GoldPrimary)
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(n.title, style = MaterialTheme.typography.titleMedium, fontWeight = if (n.isRead) FontWeight.Normal else FontWeight.Bold)
                                Text(n.body, style = MaterialTheme.typography.bodyMedium)
                            }
                            if (!n.isRead) {
                                Spacer(Modifier.width(8.dp))
                                Box(Modifier.size(8.dp).background(GoldPrimary, CircleShape))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SubscriptionScreen(vm: BillingViewModel, onBack: () -> Unit) {
    val sub by vm.subscription.collectAsState()
    Scaffold(topBar = { NyaayTopAppBar("Subscription", showBackButton = true, onBackClick = onBack) }, containerColor = ObsidianBackground) { pad ->
        LazyColumn(Modifier.fillMaxSize().padding(pad), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                Text("Current plan: ${sub?.tier?.name ?: "FREE"}", style = MaterialTheme.typography.titleLarge, color = GoldPrimary)
                Spacer(Modifier.height(4.dp))
                Text("Choose a plan that fits your practice.", style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(12.dp))
            }
            SubscriptionTier.values().forEach { tier ->
                item {
                    NyaayCard(onClick = { vm.subscribe(tier) }) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(tier.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text(BillingRepository.tierFeatures(tier), style = MaterialTheme.typography.bodyMedium)
                            }
                            Text("₹${BillingRepository.tierPrice(tier).toInt()}/mo", color = GoldPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
