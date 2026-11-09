package com.nyaayneeti.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nyaayneeti.app.data.model.*
import com.nyaayneeti.app.data.repository.AuthRepository
import com.nyaayneeti.app.data.repository.CaseRepository
import com.nyaayneeti.app.data.repository.GeminiIntakeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {
    val currentUser = authRepository.currentUser
    val selectedLanguage = authRepository.selectedLanguage

    private val _phoneInput = MutableStateFlow("")
    val phoneInput: StateFlow<String> = _phoneInput.asStateFlow()

    private val _otpInput = MutableStateFlow("")
    val otpInput: StateFlow<String> = _otpInput.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _selectedRole = MutableStateFlow(UserRole.CITIZEN)
    val selectedRole: StateFlow<UserRole> = _selectedRole.asStateFlow()

    fun setLanguage(language: Language) {
        authRepository.setLanguage(language)
    }

    fun selectRole(role: UserRole) {
        _selectedRole.value = role
        _errorMessage.value = null
    }

    fun onPhoneChange(value: String) {
        val digits = value.filter { it.isDigit() }
        _phoneInput.value = digits.takeLast(10)
        _errorMessage.value = null
    }

    fun onOtpChange(value: String) {
        val digits = value.filter { it.isDigit() }
        _otpInput.value = digits.take(6)
        _errorMessage.value = null
    }

    fun sendOtp(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val result = authRepository.sendOtp(_phoneInput.value)
            _isLoading.value = false
            result.onSuccess { onSuccess() }
                .onFailure { _errorMessage.value = it.message ?: "Failed to send OTP" }
        }
    }

    fun verifyOtp(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val result = authRepository.verifyOtp(_phoneInput.value, _otpInput.value, _selectedRole.value)
            _isLoading.value = false
            result.onSuccess { onSuccess() }
                .onFailure { _errorMessage.value = it.message ?: "Invalid OTP code" }
        }
    }

    fun signOut() {
        authRepository.signOut()
    }
}

class CaseViewModel(private val caseRepository: CaseRepository) : ViewModel() {
    val cases = caseRepository.cases
    val timeline = caseRepository.timeline
    val tasks = caseRepository.tasks
    val hearings = caseRepository.hearings
    val notes = caseRepository.notes

    private val _selectedCaseId = MutableStateFlow("case-101")
    val selectedCaseId: StateFlow<String> = _selectedCaseId.asStateFlow()

    fun selectCase(caseId: String) {
        _selectedCaseId.value = caseId
    }

    fun toggleTask(caseId: String, taskId: String) {
        caseRepository.toggleTask(caseId, taskId)
    }

    fun addNote(caseId: String, content: String, author: String) {
        caseRepository.addNote(caseId, content, author)
    }
}

class AIIntakeViewModel(
    private val geminiRepository: GeminiIntakeRepository,
    private val caseRepository: CaseRepository
) : ViewModel() {
    val messages = geminiRepository.messages
    val currentProfile = geminiRepository.currentProfile
    val isGenerating = geminiRepository.isGenerating

    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    fun onInputChange(text: String) {
        _inputText.value = text
    }

    fun sendUserMessage() {
        val text = _inputText.value.trim()
        if (text.isEmpty()) return
        _inputText.value = ""
        viewModelScope.launch {
            geminiRepository.sendMessage(text)
        }
    }

    fun createCaseRoom(clientName: String, clientPhone: String, onCaseCreated: (String) -> Unit) {
        val profile = currentProfile.value ?: return
        val caseId = caseRepository.createCaseFromProfile(clientName, clientPhone, profile)
        onCaseCreated(caseId)
    }
}
