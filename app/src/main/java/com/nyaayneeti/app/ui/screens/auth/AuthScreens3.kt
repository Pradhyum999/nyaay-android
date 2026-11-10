package com.nyaayneeti.app.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.nyaayneeti.app.data.model.UserRole
import com.nyaayneeti.app.ui.components.NyaayTopAppBar
import com.nyaayneeti.app.ui.components.SectionTitle
import com.nyaayneeti.app.ui.theme.*
import com.nyaayneeti.app.viewmodel.OnboardingViewModel

@Composable
fun OnboardingScreen(vm: OnboardingViewModel, role: UserRole, onDone: () -> Unit) {
    val saving by vm.saving.collectAsState()
    val name by vm.name.collectAsState()
    val city by vm.city.collectAsState()
    val state by vm.state.collectAsState()
    val barCouncilId by vm.barCouncilId.collectAsState()
    val specialization by vm.specialization.collectAsState()
    val experience by vm.experience.collectAsState()
    val bio by vm.bio.collectAsState()
    val firmName by vm.firmName.collectAsState()
    val firmCity by vm.firmCity.collectAsState()
    val firmAddress by vm.firmAddress.collectAsState()
    val firmGstin by vm.firmGstin.collectAsState()
    val college by vm.college.collectAsState()
    val yearOfStudy by vm.yearOfStudy.collectAsState()
    val email by vm.email.collectAsState()

    val (title, subtitle) = when (role) {
        UserRole.FIRM_ADMIN -> "Set up your firm" to "Firm details will be shown to clients"
        UserRole.FIRM_MEMBER -> "Advocate profile" to "Your details for the firm workspace"
        UserRole.INDEPENDENT_LAWYER -> "Advocate profile" to "Help clients find you"
        UserRole.STUDENT -> "Student profile" to "Help firms discover you"
        else -> "Your profile" to "Tell us about you"
    }

    Scaffold(topBar = { NyaayTopAppBar(title, subtitle) }, containerColor = ObsidianBackground) { pad ->
        Column(Modifier.fillMaxSize().padding(pad).verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Firm details first for firm admins
            if (role == UserRole.FIRM_ADMIN) {
                SectionTitle("Firm details")
                OutlinedTextField(firmName, { vm.firmName.value = it }, label = { Text("Firm name *") }, singleLine = true, modifier = Modifier.fillMaxWidth(), colors = fieldColors())
                OutlinedTextField(firmCity, { vm.firmCity.value = it }, label = { Text("Firm city") }, singleLine = true, modifier = Modifier.fillMaxWidth(), colors = fieldColors())
                OutlinedTextField(firmAddress, { vm.firmAddress.value = it }, label = { Text("Firm address") }, singleLine = true, modifier = Modifier.fillMaxWidth(), colors = fieldColors())
                OutlinedTextField(firmGstin, { vm.firmGstin.value = it }, label = { Text("GSTIN (optional)") }, singleLine = true, modifier = Modifier.fillMaxWidth(), colors = fieldColors())
                SectionTitle("Your details")
            }

            // Name for everyone
            OutlinedTextField(name, { vm.name.value = it }, label = { Text("Full name") }, singleLine = true, modifier = Modifier.fillMaxWidth(), colors = fieldColors())

            if (role == UserRole.FIRM_MEMBER) {
                OutlinedTextField(email, { vm.email.value = it }, label = { Text("Email (your firm invitation)") }, singleLine = true, modifier = Modifier.fillMaxWidth(), colors = fieldColors())
            }

            if (role == UserRole.CITIZEN || role == UserRole.INDEPENDENT_LAWYER) {
                OutlinedTextField(city, { vm.city.value = it }, label = { Text("City") }, singleLine = true, modifier = Modifier.fillMaxWidth(), colors = fieldColors())
                OutlinedTextField(state, { vm.state.value = it }, label = { Text("State") }, singleLine = true, modifier = Modifier.fillMaxWidth(), colors = fieldColors())
            }

            if (role.isLawyerRole()) {
                OutlinedTextField(barCouncilId, { vm.barCouncilId.value = it }, label = { Text("Bar Council ID") }, singleLine = true, modifier = Modifier.fillMaxWidth(), colors = fieldColors())
                OutlinedTextField(specialization, { vm.specialization.value = it }, label = { Text("Specializations (comma separated)") }, singleLine = true, modifier = Modifier.fillMaxWidth(), colors = fieldColors())
                OutlinedTextField(experience, { vm.experience.value = it }, label = { Text("Years of experience") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true, modifier = Modifier.fillMaxWidth(), colors = fieldColors())
                if (role != UserRole.FIRM_MEMBER) {
                    OutlinedTextField(bio, { vm.bio.value = it }, label = { Text("Short bio") }, modifier = Modifier.fillMaxWidth(), colors = fieldColors())
                }
            }

            if (role == UserRole.STUDENT) {
                OutlinedTextField(college, { vm.college.value = it }, label = { Text("College / University") }, singleLine = true, modifier = Modifier.fillMaxWidth(), colors = fieldColors())
                OutlinedTextField(yearOfStudy, { vm.yearOfStudy.value = it }, label = { Text("Year of study") }, singleLine = true, modifier = Modifier.fillMaxWidth(), colors = fieldColors())
            }

            Spacer(Modifier.height(8.dp))
            Button(
                onClick = { vm.save(role, onDone) },
                enabled = !saving && (role != UserRole.FIRM_ADMIN || firmName.isNotBlank()),
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                shape = RoundedCornerShape(14.dp)
            ) {
                if (saving) CircularProgressIndicator(Modifier.size(20.dp), color = OnPrimary) else Text(if (role == UserRole.FIRM_ADMIN) "Create firm" else "Finish", color = OnPrimary, fontWeight = FontWeight.Bold)
            }
        }
    }
}

