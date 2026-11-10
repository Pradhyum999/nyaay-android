package com.nyaayneeti.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nyaayneeti.app.data.model.*
import com.nyaayneeti.app.data.repository.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class ConsultationViewModel(
    private val consultationRepo: ConsultationRepository,
    private val auth: AuthRepository
) : ViewModel() {
    val me = auth.currentUser

    val consultations = auth.currentUser.flatMapLatest { u ->
        when {
            u == null -> flowOf(emptyList())
            u.role == UserRole.INDEPENDENT_LAWYER || u.role == UserRole.FIRM_ADMIN || u.role == UserRole.FIRM_MEMBER -> consultationRepo.observeForLawyer(u.uid)
            else -> consultationRepo.observeForCitizen(u.uid)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedId = MutableStateFlow<String?>(null)
    val selected = _selectedId.flatMapLatest { id -> id?.let { consultationRepo.observe(it) } ?: flowOf(null) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun select(id: String) { _selectedId.value = id }

    fun request(citizen: UserEntity, lawyer: UserEntity, notes: String, fee: Double, onDone: (String) -> Unit) {
        viewModelScope.launch {
            onDone(consultationRepo.request(citizen, lawyer, notes, System.currentTimeMillis(), 30, fee))
        }
    }
    fun accept(id: String) { viewModelScope.launch { consultationRepo.accept(id) } }
    fun reject(id: String) { viewModelScope.launch { consultationRepo.reject(id) } }
    fun complete(id: String) { viewModelScope.launch { consultationRepo.complete(id) } }
    fun addReview(lawyer: UserEntity, citizen: UserEntity, rating: Int, comment: String?) {
        viewModelScope.launch { consultationRepo.addReview(lawyer, citizen, rating, comment) }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class NotificationViewModel(
    private val notificationRepo: NotificationRepository,
    private val auth: AuthRepository
) : ViewModel() {
    val notifications = auth.currentUser.flatMapLatest { u -> u?.let { notificationRepo.observeFor(it.uid) } ?: flowOf(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val unread = auth.currentUser.flatMapLatest { u -> u?.let { notificationRepo.observeUnread(it.uid) } ?: flowOf(0) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun markRead(id: String) { viewModelScope.launch { notificationRepo.markRead(id) } }
    fun markAll() {
        val u = auth.currentUser.value ?: return
        viewModelScope.launch { notificationRepo.markAllRead(u.uid) }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class BillingViewModel(
    private val billing: BillingRepository,
    private val auth: AuthRepository
) : ViewModel() {
    val payments = auth.currentUser.flatMapLatest { u -> u?.let { billing.observePayments(it.uid) } ?: flowOf(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val subscription = auth.currentUser.flatMapLatest { u -> u?.let { billing.observeSubscription(it.uid) } ?: flowOf(null) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun subscribe(tier: SubscriptionTier) {
        val u = auth.currentUser.value ?: return
        viewModelScope.launch { billing.subscribe(u, tier, u.firmId) }
    }
}
