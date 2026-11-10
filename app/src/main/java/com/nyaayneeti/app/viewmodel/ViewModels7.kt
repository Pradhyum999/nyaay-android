package com.nyaayneeti.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nyaayneeti.app.data.ai.CaseAIClient
import com.nyaayneeti.app.data.ai.ECourtsClient
import com.nyaayneeti.app.data.model.ReportStatus
import com.nyaayneeti.app.data.model.VerificationRequestEntity
import com.nyaayneeti.app.data.repository.AuthRepository
import com.nyaayneeti.app.data.repository.ModerationRepository
import com.nyaayneeti.app.data.repository.VerificationRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class VerificationViewModel(
    private val verificationRepo: VerificationRepository,
    private val auth: AuthRepository
) : ViewModel() {
    val me = auth.currentUser
    val requests = verificationRepo.observeAll()
    private val _feedback = MutableStateFlow<String?>(null)
    val feedback = _feedback.asStateFlow()

    fun submit(documentType: String, maskedId: String) {
        val u = auth.currentUser.value ?: return
        viewModelScope.launch {
            verificationRepo.submit(u, documentType, maskedId)
                .onSuccess { _feedback.value = "Submitted for review" }
                .onFailure { _feedback.value = it.message }
        }
    }
    fun approve(r: VerificationRequestEntity) { viewModelScope.launch { verificationRepo.approve(r, "Admin") } }
    fun reject(r: VerificationRequestEntity, reason: String) { viewModelScope.launch { verificationRepo.reject(r, reason, "Admin") } }
}

class ModerationViewModel(private val moderationRepo: ModerationRepository) : ViewModel() {
    val reports = moderationRepo.observeReports()
    fun resolve(id: String, status: ReportStatus) { viewModelScope.launch { moderationRepo.resolveReport(id, status) } }
}

class CaseAIViewModel(private val caseAI: CaseAIClient) : ViewModel() {
    private val _answer = MutableStateFlow<String?>(null)
    val answer = _answer.asStateFlow()
    private val _busy = MutableStateFlow(false)
    val busy = _busy.asStateFlow()

    fun askMyCase(context: String, question: String) { run { caseAI.askMyCase(context, question) } }
    fun summarize(order: String) { run { caseAI.summarizeOrder(order) } }
    fun counsel(question: String) { run { caseAI.lawyerCounsel(question) } }

    private fun run(block: suspend () -> String) {
        viewModelScope.launch { _busy.value = true; _answer.value = block(); _busy.value = false }
    }
}

class ECourtsViewModel(private val ecourts: ECourtsClient) : ViewModel() {
    private val _result = MutableStateFlow<String?>(null)
    val result = _result.asStateFlow()
    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()
    private val _busy = MutableStateFlow(false)
    val busy = _busy.asStateFlow()

    fun lookup(cnr: String) {
        viewModelScope.launch {
            _busy.value = true; _error.value = null
            ecourts.searchCase(cnr, 2026)
                .onSuccess { _result.value = it }
                .onFailure { _error.value = it.message ?: "Lookup failed" }
            _busy.value = false
        }
    }
}
