package com.nyaayneeti.app.ui.screens.citizen

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nyaayneeti.app.data.IntakeStore
import com.nyaayneeti.app.data.geo.CityCoords
import com.nyaayneeti.app.data.geo.GeoUtils
import com.nyaayneeti.app.data.geo.LegalCategories
import com.nyaayneeti.app.data.matching.LawyerMatcher
import com.nyaayneeti.app.data.matching.cleanList
import com.nyaayneeti.app.data.model.UserEntity
import com.nyaayneeti.app.data.model.VerificationStatus
import com.nyaayneeti.app.ui.components.*
import com.nyaayneeti.app.ui.theme.*
import com.nyaayneeti.app.viewmodel.AuthViewModel
import com.nyaayneeti.app.viewmodel.InquiryViewModel

@Composable
fun LawyerDiscoveryScreen(vm: AuthViewModel, inquiryVm: InquiryViewModel, onOpenLawyer: (String) -> Unit, onBack: () -> Unit) {
    val lawyers by vm.verifiedLawyers.collectAsState(emptyList())
    val me by vm.currentUser.collectAsState()
    val profile by IntakeStore.profile.collectAsState()
    val myCases by inquiryVm.myCases.collectAsState(emptyList())

    var category by remember { mutableStateOf("All") }
    var nearMe by remember { mutableStateOf(true) }
    var minExp by remember { mutableStateOf(0) }
    var fee by remember { mutableStateOf("any") }
    var lang by remember { mutableStateOf("any") }
    var minRating by remember { mutableStateOf(0.0) }
    var sort by remember { mutableStateOf("match") }
    var viewMode by remember { mutableStateOf("list") }
    var showSearch by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }
    var engaging by remember { mutableStateOf<UserEntity?>(null) }

    val userCoords = CityCoords.coords(me?.city)

    val matched = remember(lawyers, profile) { LawyerMatcher.match(profile, lawyers, lawyers.size) }
    val scoreOf = remember(matched) { matched.associate { it.lawyer.uid to it.score } }
    val reasonOf = remember(matched) { matched.associate { it.lawyer.uid to it.reasons } }

    fun distOf(l: UserEntity): Double? {
        if (userCoords == null) return null
        val c = if (l.chamberLat != null && l.chamberLng != null) l.chamberLat!! to l.chamberLng!! else CityCoords.coords(l.city) ?: return null
        return GeoUtils.distanceKm(userCoords.first, userCoords.second, c.first, c.second)
    }

    val filtered = remember(lawyers, category, nearMe, minExp, fee, lang, minRating, query) {
        lawyers.filter { l ->
            (category == "All" || LegalCategories.matches(l.specializations, category)) &&
                l.experienceYears >= minExp && feeMatch(l, fee) &&
                (lang == "any" || l.languages.cleanList().any { it.equals(lang, true) }) &&
                l.avgRating >= minRating &&
                (query.isBlank() || l.name.contains(query, true) || (l.city ?: "").contains(query, true) || l.specializations.contains(query, true))
        }
    }

    val sorted = remember(filtered, sort, scoreOf) {
        when (sort) {
            "experience" -> filtered.sortedByDescending { it.experienceYears }
            "rating" -> filtered.sortedByDescending { it.avgRating }
            "nearest" -> filtered.sortedBy { l -> distOf(l) ?: Double.MAX_VALUE }
            else -> filtered.sortedByDescending { scoreOf[it.uid] ?: 0 }
        }
    }

    Scaffold(topBar = {
        NyaayTopAppBar("Find an Advocate", showBackButton = true, onBackClick = onBack, actions = {
            IconButton(onClick = { showSearch = !showSearch }) { Icon(Icons.Default.Search, "Search", tint = GoldPrimary) }
            TextButton(onClick = { viewMode = if (viewMode == "list") "map" else "list" }) { Text(if (viewMode == "list") "Map" else "List", color = GoldPrimary) }
        })
    }, containerColor = ObsidianBackground) { pad ->
        Column(Modifier.fillMaxSize().padding(pad)) {
            if (showSearch) {
                OutlinedTextField(query, { query = it }, placeholder = { Text("Search name, city (optional)") }, singleLine = true, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp), colors = dirFieldColors())
            }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 6.dp)) {
                FilterChip(selected = category == "All", onClick = { category = "All" }, label = { Text("All") }, colors = dirChipColors())
                LegalCategories.all.forEach { (name, icon) ->
                    FilterChip(selected = category == name, onClick = { category = name }, label = { Text("$icon $name") }, colors = dirChipColors())
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 2.dp)) {
                FilterChip(selected = nearMe, onClick = { nearMe = !nearMe; if (nearMe) sort = "nearest" }, label = { Text("📍 Near me") }, colors = dirChipColors())
                FilterChip(selected = minExp > 0, onClick = { minExp = when (minExp) { 0 -> 5; 5 -> 10; 10 -> 15; else -> 0 } }, label = { Text(if (minExp == 0) "Experience" else "$minExp+ yrs") }, colors = dirChipColors())
                FilterChip(selected = fee != "any", onClick = { fee = when (fee) { "any" -> "u1000"; "u1000" -> "1000-3000"; "1000-3000" -> "3000+"; else -> "any" } }, label = { Text(when (fee) { "any" -> "Fee"; "u1000" -> "Under ₹1k"; "1000-3000" -> "₹1k–3k"; else -> "₹3k+" }) }, colors = dirChipColors())
                FilterChip(selected = lang != "any", onClick = { lang = when (lang) { "any" -> "English"; "English" -> "Hindi"; "Hindi" -> "Marathi"; else -> "any" } }, label = { Text(if (lang == "any") "Language" else lang) }, colors = dirChipColors())
                FilterChip(selected = minRating > 0, onClick = { minRating = when (minRating) { 0.0 -> 4.0; 4.0 -> 4.5; else -> 0.0 } }, label = { Text(if (minRating == 0.0) "Rating" else "$minRating★") }, colors = dirChipColors())
                FilterChip(selected = sort == "experience", onClick = { sort = if (sort == "experience") "match" else "experience" }, label = { Text("Top experience") }, colors = dirChipColors())
            }
            if (viewMode == "map") {
                LawyerMap(lawyers = sorted, userLocation = userCoords)
            } else if (sorted.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { EmptyState(Icons.Default.Search, "No lawyers match", "Loosen a filter or clear the category.") }
            } else {
                LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(sorted, key = { it.uid }) { l ->
                        val reasons = reasonOf[l.uid] ?: emptyList()
                        val dist = distOf(l)
                        NyaayCard(onClick = { onOpenLawyer(l.uid) }) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Column(Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(l.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                        if (l.verificationStatus == VerificationStatus.VERIFIED) { Spacer(Modifier.width(6.dp)); Icon(Icons.Default.Verified, null, tint = GreenSuccess, modifier = Modifier.size(16.dp)) }
                                    }
                                    Text("${l.city ?: "India"} · ${l.experienceYears} yrs · ★${l.avgRating}", style = MaterialTheme.typography.bodyMedium)
                                    if (l.feeMax > 0) Text("₹${l.feeMin.toInt()}–₹${l.feeMax.toInt()}", style = MaterialTheme.typography.labelSmall, color = GoldPrimary)
                                    if (nearMe && dist != null) Text("📍 ${"%.1f".format(dist)} km", style = MaterialTheme.typography.labelSmall, color = GreenSuccess)
                                }
                                Button(onClick = { engaging = l }, colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary), contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)) { Text("Engage", color = OnPrimary, fontWeight = FontWeight.Bold) }
                            }
                            if (reasons.isNotEmpty()) { Spacer(Modifier.height(6.dp)); Text(reasons.joinToString(" · "), style = MaterialTheme.typography.labelSmall, color = GreenSuccess) }
                        }
                    }
                }
            }
        }
    }

    engaging?.let { l -> EngageAdvocateSheet(lawyer = l, vm = inquiryVm, cases = myCases, onDismiss = { engaging = null }) }
}

private fun feeMatch(l: UserEntity, fee: String): Boolean = when (fee) {
    "u1000" -> l.feeMax > 0 && l.feeMin < 1000
    "1000-3000" -> l.feeMax > 0 && l.feeMin >= 1000 && l.feeMax <= 3000
    "3000+" -> l.feeMax >= 3000
    else -> true
}
