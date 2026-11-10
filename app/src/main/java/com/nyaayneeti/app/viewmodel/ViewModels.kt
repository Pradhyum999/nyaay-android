package com.nyaayneeti.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.app.Activity
import com.nyaayneeti.app.data.model.*
import com.nyaayneeti.app.data.repository.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AuthViewModel(private val auth: AuthRepository) : ViewModel() {
    val currentUser = auth.currentUser
    val selectedLanguage = auth.selectedLanguage
    val themeMode = auth.themeMode
    val sessionLoaded = auth.sessionLoaded
    val verifiedLawyers = auth.verifiedLawyers

    private val _phone = MutableStateFlow("")
    val phone = _phone.asStateFlow()
    private val _otp = MutableStateFlow("")
    val otp = _otp.asStateFlow()
    private val _role = MutableStateFlow(UserRole.CITIZEN)
    val role = _role.asStateFlow()
    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()
    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun setLanguage(code: String) = auth.setLanguage(code)
    fun setThemeMode(mode: String) = auth.setThemeMode(mode)
    fun selectRole(r: UserRole) { _role.value = r; _error.value = null }
    fun applyRole(onDone: () -> Unit) {
        viewModelScope.launch {
            auth.updateRole(_role.value)
            onDone()
        }
    }
    fun onPhone(v: String) { _phone.value = v.filter { it.isDigit() }.takeLast(10) }
    fun onOtp(v: String) { _otp.value = v.filter { it.isDigit() }.take(6) }

    fun sendOtp(activity: Activity, onSuccess: () -> Unit) {
        _loading.value = true; _error.value = null
        auth.sendOtp(_phone.value, activity,
            onCodeSent = { _loading.value = false; onSuccess() },
            onError = { _error.value = it; _loading.value = false }
        )
    }

    fun verifyOtp(onSuccess: (UserEntity) -> Unit) {
        viewModelScope.launch {
            _loading.value = true; _error.value = null
            auth.verifyOtp(_phone.value, _otp.value, _role.value).onSuccess { onSuccess(it) }.onFailure { _error.value = it.message }
            _loading.value = false
        }
    }

    fun signInWithGoogle(idToken: String, email: String?, name: String?, onSuccess: (UserEntity) -> Unit) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            auth.signInWithGoogle(idToken, email, name, _role.value)
                .onSuccess { onSuccess(it) }
                .onFailure { _error.value = it.message ?: "Google sign-in failed" }
            _loading.value = false
        }
    }

    fun signOut() = auth.signOut()
}

class OnboardingViewModel(
    private val auth: AuthRepository,
    private val firm: FirmRepository
) : ViewModel() {
    val name = MutableStateFlow("")
    val email = MutableStateFlow("")
    val state = MutableStateFlow("")
    val city = MutableStateFlow("")
    val barCouncilId = MutableStateFlow("")
    val specialization = MutableStateFlow("")
    val experience = MutableStateFlow("")
    val bio = MutableStateFlow("")
    val firmName = MutableStateFlow("")
    val firmCity = MutableStateFlow("")
    val firmAddress = MutableStateFlow("")
    val firmGstin = MutableStateFlow("")
    val college = MutableStateFlow("")
    val yearOfStudy = MutableStateFlow("")
    private val _saving = MutableStateFlow(false)
    val saving = _saving.asStateFlow()

    fun save(role: UserRole, onDone: () -> Unit) {
        val u = auth.currentUser.value ?: return
        viewModelScope.launch {
            _saving.value = true
            var updated = u.copy(
                name = name.value.ifBlank { u.name },
                email = email.value.ifBlank { null },
                state = state.value.ifBlank { null },
                city = city.value.ifBlank { null },
                barCouncilId = barCouncilId.value.ifBlank { u.barCouncilId },
                specializations = if (specialization.value.isBlank()) u.specializations
                    else specialization.value.split(",").map { it.trim() }.joinToString(", ") { "\"$it\"" }.let { "[$it]" },
                experienceYears = experience.value.toIntOrNull() ?: u.experienceYears,
                bio = bio.value.ifBlank { u.bio },
                college = college.value.ifBlank { u.college },
                yearOfStudy = yearOfStudy.value.ifBlank { u.yearOfStudy },
                verificationStatus = if (role == UserRole.INDEPENDENT_LAWYER || role == UserRole.FIRM_ADMIN || role == UserRole.FIRM_MEMBER) VerificationStatus.PENDING else u.verificationStatus
            )
            if (role == UserRole.FIRM_ADMIN && u.firmId == null && firmName.value.isNotBlank()) {
                firm.createFirm(firmName.value, updated, firmCity.value.ifBlank { null }, firmAddress.value.ifBlank { null }, firmGstin.value.ifBlank { null })
                updated = auth.currentUser.value ?: updated
            }
            if (role == UserRole.FIRM_MEMBER && email.value.isNotBlank()) {
                updated = firm.acceptInvitation(email.value, updated) ?: updated
            }
            auth.updateProfile(updated)
            auth.completeOnboarding(updated.uid)
            _saving.value = false
            onDone()
        }
    }
}
