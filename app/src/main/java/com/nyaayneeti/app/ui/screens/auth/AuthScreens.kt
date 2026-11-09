package com.nyaayneeti.app.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nyaayneeti.app.data.model.Language
import com.nyaayneeti.app.data.model.UserRole
import com.nyaayneeti.app.ui.components.NyaayCard
import com.nyaayneeti.app.ui.components.NyaayTopAppBar
import com.nyaayneeti.app.ui.theme.*
import com.nyaayneeti.app.viewmodel.AuthViewModel

@Composable
fun LanguageSelectScreen(
    viewModel: AuthViewModel,
    onLanguageSelected: () -> Unit
) {
    val currentLang by viewModel.selectedLanguage.collectAsState()

    Scaffold(
        topBar = { NyaayTopAppBar(title = "NYAAYNEETI") },
        containerColor = ObsidianBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "⚖", fontSize = 48.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Choose Language / भाषा निवडा",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Select your preferred language for judicial assistance",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))

            Language.values().forEach { lang ->
                val isSelected = currentLang == lang
                val shape = RoundedCornerShape(16.dp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clip(shape)
                        .background(if (isSelected) ObsidianSurfaceElevated else ObsidianSurface)
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) GoldPrimary else ObsidianBorder,
                            shape = shape
                        )
                        .clickable { viewModel.setLanguage(lang) }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = lang.displayName,
                            style = MaterialTheme.typography.titleMedium,
                            color = if (isSelected) GoldPrimary else TextMain
                        )
                        Text(
                            text = lang.subtitle,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    if (isSelected) {
                        Surface(
                            color = GoldPrimary,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "Selected",
                                color = ObsidianBackground,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onLanguageSelected,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = "Continue",
                    color = ObsidianBackground,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun RoleSelectScreen(
    viewModel: AuthViewModel,
    onRoleChosen: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            NyaayTopAppBar(
                title = "NYAAYNEETI",
                subtitle = "Select Your Role",
                showBackButton = true,
                onBackClick = onBack
            )
        },
        containerColor = ObsidianBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "How will you use NYAAYNEETI?",
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = "Secure digital home for client-lawyer case management",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(28.dp))

            // Advocate Option
            NyaayCard(
                onClick = {
                    viewModel.selectRole(UserRole.ADVOCATE)
                    onRoleChosen()
                }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Surface(
                        modifier = Modifier.size(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = GoldContainer
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Gavel, contentDescription = null, tint = GoldPrimary)
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "I'm an Advocate",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Manage cases, court diary & client roster",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextSub)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Citizen Option
            NyaayCard(
                onClick = {
                    viewModel.selectRole(UserRole.CITIZEN)
                    onRoleChosen()
                }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Surface(
                        modifier = Modifier.size(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = GreenContainer
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = GreenSuccess)
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "I Need Legal Help (Citizen)",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "AI case intake & verified advocate matching",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextSub)
                }
            }
        }
    }
}

@Composable
fun PhoneAuthScreen(
    viewModel: AuthViewModel,
    onAuthSuccess: () -> Unit,
    onBack: () -> Unit
) {
    val phone by viewModel.phoneInput.collectAsState()
    val otp by viewModel.otpInput.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.errorMessage.collectAsState()
    val role by viewModel.selectedRole.collectAsState()

    var isOtpSent by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            NyaayTopAppBar(
                title = if (role == UserRole.ADVOCATE) "Advocate Portal" else "Citizen Portal",
                subtitle = if (isOtpSent) "Enter 6-Digit OTP" else "Mobile Sign-In",
                showBackButton = true,
                onBackClick = {
                    if (isOtpSent) isOtpSent = false else onBack()
                }
            )
        },
        containerColor = ObsidianBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = if (!isOtpSent) "Enter Mobile Number" else "Verify 6-Digit Code",
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = if (!isOtpSent) "We will verify your device with a secure one-time code" else "Sent to +91 $phone",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(24.dp))

            error?.let {
                Surface(
                    color = RedContainer,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    Text(
                        text = it,
                        color = RedUrgent,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            if (!isOtpSent) {
                // Phone input with explicit +91 non-duplicated badge
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(ObsidianSurface)
                        .border(1.dp, ObsidianBorder, RoundedCornerShape(14.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "+91",
                        style = MaterialTheme.typography.titleMedium,
                        color = GoldPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    TextField(
                        value = phone,
                        onValueChange = { viewModel.onPhoneChange(it) },
                        placeholder = { Text("98765 43210", color = TextFaint) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = TextMain,
                            unfocusedTextColor = TextMain,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        viewModel.sendOtp { isOtpSent = true }
                    },
                    enabled = phone.length == 10 && !isLoading,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = ObsidianBackground)
                    } else {
                        Text("Send OTP", color = ObsidianBackground, fontWeight = FontWeight.Bold)
                    }
                }

                // Quick test fill chip
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextButton(onClick = { viewModel.onPhoneChange("9999999999") }) {
                        Text("Use Verified Test Number: 9999999999", color = TextSub, fontSize = 12.sp)
                    }
                }
            } else {
                // OTP Code input
                TextField(
                    value = otp,
                    onValueChange = { viewModel.onOtpChange(it) },
                    placeholder = { Text("123456", color = TextFaint) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = ObsidianSurface,
                        unfocusedContainerColor = ObsidianSurface,
                        focusedTextColor = GoldPrimary,
                        unfocusedTextColor = TextMain,
                        focusedIndicatorColor = GoldPrimary,
                        unfocusedIndicatorColor = ObsidianBorder
                    ),
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        viewModel.verifyOtp { onAuthSuccess() }
                    },
                    enabled = otp.length == 6 && !isLoading,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = ObsidianBackground)
                    } else {
                        Text("Verify & Continue", color = ObsidianBackground, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextButton(onClick = { viewModel.onOtpChange("123456") }) {
                        Text("Auto-fill Test Code: 123456", color = GoldPrimary, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
