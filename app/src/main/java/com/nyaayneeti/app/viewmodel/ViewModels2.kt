package com.nyaayneeti.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nyaayneeti.app.data.model.*
import com.nyaayneeti.app.data.repository.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class CaseViewModel(
    private val caseRepo: CaseRepository,
    private val auth: AuthRepository
) : ViewModel() {
    private val _selectedCaseId = MutableStateFlow<String?>(null)
    val selectedCaseId = _selectedCaseId.asStateFlow()

    val cases = auth.currentUser.flatMapLatest { u -> u?.let { caseRepo.observeCasesFor(it) } ?: flowOf(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedCase = _selectedCaseId.flatMapLatest { id -> id?.let { caseRepo.observeCase(it) } ?: flowOf(null) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    val timeline = _selectedCaseId.flatMapLatest { id -> id?.let { caseRepo.observeTimeline(it) } ?: flowOf(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val tasks = _selectedCaseId.flatMapLatest { id -> id?.let { caseRepo.observeTasks(it) } ?: flowOf(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val notes = _selectedCaseId.flatMapLatest { id -> id?.let { caseRepo.observeNotes(it) } ?: flowOf(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val documents = _selectedCaseId.flatMapLatest { id -> id?.let { caseRepo.observeDocuments(it) } ?: flowOf(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val hearings = _selectedCaseId.flatMapLatest { id -> id?.let { caseRepo.observeHearings(it) } ?: flowOf(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val invoices = _selectedCaseId.flatMapLatest { id -> id?.let { caseRepo.observeInvoices(it) } ?: flowOf(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allHearings = caseRepo.observeAllHearings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allInvoices = auth.currentUser.flatMapLatest { u -> u?.let { caseRepo.observeInvoicesForLawyer(it.uid) } ?: flowOf(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _feedback = MutableStateFlow<String?>(null)
    val feedback = _feedback.asStateFlow()
    fun clearFeedback() { _feedback.value = null }

    fun select(caseId: String) { _selectedCaseId.value = caseId }

    fun addCase(caseNumber: String, title: String, clientName: String, clientPhone: String, court: String, matterType: String, legalArea: String, stage: CaseStage, priority: CasePriority, onDone: (String) -> Unit) {
        val u = auth.currentUser.value ?: return
        viewModelScope.launch {
            caseRepo.addCase(u, caseNumber, title, clientName, clientPhone, court, matterType, legalArea, stage, priority)
                .onSuccess { _feedback.value = null; onDone(it) }
                .onFailure { _feedback.value = it.message }
        }
    }

    fun deleteCase(caseId: String) { viewModelScope.launch { caseRepo.deleteCase(caseId) } }

    fun addHearingFull(caseId: String, court: String, item: Int, date: Long, time: String, purpose: String, judge: String) {
        viewModelScope.launch {
            caseRepo.addHearingFull(caseId, court, item, date, time, purpose, judge)
                .onSuccess { _feedback.value = null }
                .onFailure { _feedback.value = it.message }
        }
    }

    fun logOrder(hearingId: String, status: HearingStatus, order: String) { viewModelScope.launch { caseRepo.logOrder(hearingId, status, order) } }
    fun verifyDocument(docId: String, status: DocumentStatus) { viewModelScope.launch { caseRepo.verifyDocument(docId, status) } }

    fun addInvoice(caseId: String?, caseNumber: String, clientName: String, appearance: Double, drafting: Double, clerkage: Double, notes: String?) {
        val u = auth.currentUser.value ?: return
        viewModelScope.launch { caseRepo.addInvoice(u, caseId, caseNumber, clientName, appearance, drafting, clerkage, notes) }
    }

    fun markInvoicePaid(invoiceId: String, method: PaymentMethod, upiRef: String?) {
        viewModelScope.launch { caseRepo.markInvoicePaid(invoiceId, method, upiRef) }
    }
    fun toggleTask(caseId: String, taskId: String) { viewModelScope.launch { caseRepo.toggleTask(caseId, taskId) } }
    fun addNote(caseId: String, content: String, isPrivate: Boolean) {
        val u = auth.currentUser.value ?: return
        viewModelScope.launch { caseRepo.addNote(caseId, content, u, isPrivate) }
    }
    fun addTask(caseId: String, title: String, assignedTo: String) { viewModelScope.launch { caseRepo.addTask(caseId, title, assignedTo) } }
    fun updateStage(caseId: String, stage: CaseStage) { viewModelScope.launch { caseRepo.updateStage(caseId, stage) } }
    fun updatePriority(caseId: String, p: CasePriority) { viewModelScope.launch { caseRepo.updatePriority(caseId, p) } }
    fun addHearing(caseId: String, court: String, item: Int, date: Long, time: String, purpose: String) {
        viewModelScope.launch { caseRepo.addHearing(caseId, court, item, date, time, purpose) }
    }
    fun addDocument(caseId: String, doc: DocumentEntity) { viewModelScope.launch { caseRepo.addDocument(caseId, doc) } }
    fun assignLawyer(caseId: String, lawyer: UserEntity) { viewModelScope.launch { caseRepo.assignLawyer(caseId, lawyer) } }
}

class AIIntakeViewModel(
    private val engine: AIIntakeEngine,
    private val caseRepo: CaseRepository,
    private val auth: AuthRepository
) : ViewModel() {
    private val _messages = MutableStateFlow<List<IntakeChatMessage>>(
        listOf(IntakeChatMessage("m0", false, "Namaste! Tell me what happened in simple words — which city are you in, and what dispute or help do you need?"))
    )
    val messages = _messages.asStateFlow()
    private val _profile = MutableStateFlow<CaseProfile?>(null)
    val profile = _profile.asStateFlow()
    private val _input = MutableStateFlow("")
    val input = _input.asStateFlow()
    private val _busy = MutableStateFlow(false)
    val busy = _busy.asStateFlow()

    fun onInput(v: String) { _input.value = v }

    fun send() {
        val text = _input.value.trim()
        if (text.isEmpty()) return
        _input.value = ""
        _messages.value = _messages.value + IntakeChatMessage("u${System.currentTimeMillis()}", true, text)
        viewModelScope.launch {
            _busy.value = true
            val p = engine.analyze(text)
            _profile.value = p
            com.nyaayneeti.app.data.IntakeStore.profile.value = p
            _messages.value = _messages.value + IntakeChatMessage("b${System.currentTimeMillis()}", false, "I analyzed your situation: ${p.legalArea}. Review the profile and tap 'Create Case Room' to proceed.")
            _busy.value = false
        }
    }

    fun createCase(onCreated: (String) -> Unit) {
        val u = auth.currentUser.value ?: return
        val p = _profile.value ?: return
        viewModelScope.launch { onCreated(caseRepo.createCaseFromProfile(u, p)) }
    }
}
