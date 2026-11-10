package com.nyaayneeti.app.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nyaayneeti.app.data.model.Language
import com.nyaayneeti.app.data.model.UserRole
import com.nyaayneeti.app.ui.components.NyaayTopAppBar
import com.nyaayneeti.app.ui.theme.*
import com.nyaayneeti.app.viewmodel.AuthViewModel

@Composable
fun LanguageSelectScreen(vm: AuthViewModel, onDone: () -> Unit) {
    val current by vm.selectedLanguage.collectAsState()
    Scaffold(topBar = { NyaayTopAppBar("NYAAYNEETI", "Choose your language") }, containerColor = ObsidianBackground) { pad ->
        Column(Modifier.fillMaxSize().padding(pad).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text("⚖", fontSize = 48.sp)
            Spacer(Modifier.height(12.dp))
            Text("Choose Language / भाषा चुनें", style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center)
            Spacer(Modifier.height(28.dp))
            Language.values().forEach { lang ->
                val selected = current == lang.code
                val shape = RoundedCornerShape(16.dp)
                Row(
                    Modifier.fillMaxWidth().padding(vertical = 6.dp).clip(shape)
                        .background(if (selected) ObsidianSurfaceElevated else ObsidianSurface)
                        .border(if (selected) 2.dp else 1.dp, if (selected) GoldPrimary else ObsidianBorder, shape)
                        .clickable { vm.setLanguage(lang.code) }.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(lang.displayName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(lang.subtitle, style = MaterialTheme.typography.bodyMedium)
                    }
                    if (selected) Text("✓", color = GoldPrimary, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(28.dp))
            Button(onClick = onDone, modifier = Modifier.fillMaxWidth().height(52.dp), colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary), shape = RoundedCornerShape(14.dp)) {
                Text("Continue", color = OnPrimary, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun RoleSelectScreen(vm: AuthViewModel, onDone: () -> Unit, onBack: () -> Unit) {
    val selected by vm.role.collectAsState()
    val roles = listOf(
        RoleOption(UserRole.CITIZEN, "Citizen", "Seek legal help & manage your cases", Icons.Default.Person),
        RoleOption(UserRole.INDEPENDENT_LAWYER, "Independent Lawyer", "Run your own practice", Icons.Default.Gavel),
        RoleOption(UserRole.FIRM_ADMIN, "Firm Admin", "Manage a law firm & members", Icons.Default.Business),
        RoleOption(UserRole.FIRM_MEMBER, "Firm Member", "Work inside a firm", Icons.Default.Groups),
        RoleOption(UserRole.STUDENT, "Student", "Find internships & learn", Icons.Default.School),
        RoleOption(UserRole.PLATFORM_ADMIN, "Platform Admin", "Moderate & verify the platform", Icons.Default.Shield)
    )
    Scaffold(topBar = { NyaayTopAppBar("Choose your role", showBackButton = true, onBackClick = onBack) }, containerColor = ObsidianBackground) { pad ->
        Column(Modifier.fillMaxSize().padding(pad).verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("How will you use NYAAYNEETI?", style = MaterialTheme.typography.headlineMedium)
            Text("Pick the role that matches you — you can join a firm later too.", style = MaterialTheme.typography.bodyMedium)
            roles.forEach { opt ->
                val isSel = selected == opt.role
                val shape = RoundedCornerShape(18.dp)
                Row(
                    Modifier.fillMaxWidth().clip(shape)
                        .background(if (isSel) ObsidianSurfaceElevated else ObsidianSurface)
                        .border(if (isSel) 2.dp else 1.dp, if (isSel) GoldPrimary else ObsidianBorder, shape)
                        .clickable { vm.selectRole(opt.role) }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(Modifier.size(46.dp), shape = RoundedCornerShape(13.dp), color = if (isSel) GoldPrimary else GoldContainer) {
                        Box(contentAlignment = Alignment.Center) { Icon(opt.icon, null, tint = if (isSel) OnPrimary else GoldPrimary) }
                    }
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)) {
                        Text(opt.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(opt.sub, style = MaterialTheme.typography.bodyMedium)
                    }
                    if (isSel) Icon(Icons.Default.CheckCircle, null, tint = GoldPrimary)
                }
            }
            Spacer(Modifier.height(4.dp))
            Button(onClick = onDone, modifier = Modifier.fillMaxWidth().height(52.dp), colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary), shape = RoundedCornerShape(14.dp)) {
                Text("Continue", color = OnPrimary, fontWeight = FontWeight.Bold)
            }
        }
    }
}

private data class RoleOption(val role: UserRole, val title: String, val sub: String, val icon: ImageVector)

@Composable
internal fun fieldColors(): TextFieldColors = TextFieldDefaults.colors(
    focusedContainerColor = ObsidianSurface, unfocusedContainerColor = ObsidianSurface,
    focusedTextColor = GoldPrimary, unfocusedTextColor = TextMain,
    focusedIndicatorColor = GoldPrimary, unfocusedIndicatorColor = ObsidianBorder,
    cursorColor = GoldPrimary
)

internal fun UserRole.isLawyerRole(): Boolean =
    this == UserRole.INDEPENDENT_LAWYER || this == UserRole.FIRM_ADMIN || this == UserRole.FIRM_MEMBER
