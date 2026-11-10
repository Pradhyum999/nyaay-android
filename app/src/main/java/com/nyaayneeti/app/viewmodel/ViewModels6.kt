package com.nyaayneeti.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nyaayneeti.app.data.model.UserEntity
import com.nyaayneeti.app.data.repository.AuthRepository
import com.nyaayneeti.app.data.repository.InquiryRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class InquiryViewModel(
    private val inquiryRepo: InquiryRepository,
    private val auth: AuthRepository
) : ViewModel() {
    val me = auth.currentUser

    val forLawyer = auth.currentUser.flatMapLatest { u -> u?.let { inquiryRepo.observeForLawyer(it.uid) } ?: flowOf(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val forClient = auth.currentUser.flatMapLatest { u -> u?.let { inquiryRepo.observeForClient(it.uid) } ?: flowOf(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val myCases = auth.currentUser.flatMapLatest { u -> u?.let { inquiryRepo.observeUnassignedCases(it.uid) } ?: flowOf(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _feedback = MutableStateFlow<String?>(null)
    val feedback = _feedback.asStateFlow()

    fun engage(lawyer: UserEntity, subject: String, message: String, attachBrief: Boolean, caseId: String? = null) {
        val client = auth.currentUser.value ?: return
        viewModelScope.launch {
            inquiryRepo.engage(lawyer, client, subject, message, attachBrief, caseId)
            _feedback.value = "Sent to ${lawyer.name}"
        }
    }

    fun accept(id: String) {
        val lawyer = auth.currentUser.value ?: return
        viewModelScope.launch { inquiryRepo.accept(id, lawyer) }
    }
    fun decline(id: String) { viewModelScope.launch { inquiryRepo.decline(id) } }
}
