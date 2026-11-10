package com.nyaayneeti.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nyaayneeti.app.data.local.UserDao
import com.nyaayneeti.app.data.model.*
import com.nyaayneeti.app.data.repository.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class ChatViewModel(
    private val chat: ChatRepository,
    private val auth: AuthRepository,
    private val userDao: UserDao
) : ViewModel() {
    val me = auth.currentUser
    val nameMap = userDao.observeAll().map { list -> list.associate { it.uid to it.name } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    val conversations = auth.currentUser.flatMapLatest { u -> u?.let { chat.observeConversations(it.uid) } ?: flowOf(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _convoId = MutableStateFlow<String?>(null)
    val convoId = _convoId.asStateFlow()
    private val _peerUid = MutableStateFlow<String?>(null)

    val peer = _peerUid.flatMapLatest { id -> id?.let { userDao.observe(it) } ?: flowOf(null) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val messages = _convoId.flatMapLatest { id -> id?.let { chat.observeMessages(it) } ?: flowOf(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _draft = MutableStateFlow("")
    val draft = _draft.asStateFlow()
    fun onDraft(v: String) { _draft.value = v }

    fun open(convoId: String, peerUid: String) {
        _convoId.value = convoId
        _peerUid.value = peerUid
        val meUid = auth.currentUser.value?.uid ?: return
        viewModelScope.launch { chat.markRead(convoId, meUid) }
    }

    fun send() {
        val text = _draft.value.trim()
        val sender = auth.currentUser.value ?: return
        val receiverId = _peerUid.value ?: return
        if (text.isEmpty()) return
        _draft.value = ""
        viewModelScope.launch { userDao.getById(receiverId)?.let { chat.sendMessage(sender, it, text) } }
    }

    fun sendCall() {
        val sender = auth.currentUser.value ?: return
        val receiverId = _peerUid.value ?: return
        viewModelScope.launch { userDao.getById(receiverId)?.let { chat.sendMessage(sender, it, "Voice call", MessageType.CALL) } }
    }

    fun startChatWith(peer: UserEntity) {
        val meUid = auth.currentUser.value?.uid ?: return
        open(chat.convoId(meUid, peer.uid), peer.uid)
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class FirmViewModel(
    private val firmRepo: FirmRepository,
    private val caseRepo: CaseRepository,
    private val auth: AuthRepository
) : ViewModel() {
    val me = auth.currentUser

    val firm = auth.currentUser.flatMapLatest { u -> u?.firmId?.let { firmRepo.observeFirm(it) } ?: flowOf(null) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    val members = auth.currentUser.flatMapLatest { u -> u?.firmId?.let { firmRepo.observeMembers(it) } ?: flowOf(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val cases = auth.currentUser.flatMapLatest { u -> u?.let { caseRepo.observeCasesFor(it) } ?: flowOf(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _inviteEmail = MutableStateFlow("")
    val inviteEmail = _inviteEmail.asStateFlow()
    private val _inviteRole = MutableStateFlow(FirmRole.ASSOCIATE)
    val inviteRole = _inviteRole.asStateFlow()
    private val _message = MutableStateFlow<String?>(null)
    val message = _message.asStateFlow()

    fun onInviteEmail(v: String) { _inviteEmail.value = v }
    fun selectInviteRole(r: FirmRole) { _inviteRole.value = r }

    fun invite() {
        val u = auth.currentUser.value ?: return
        val firmId = u.firmId ?: return
        viewModelScope.launch {
            firmRepo.inviteMemberByEmail(firmId, _inviteEmail.value, _inviteRole.value)
                .onSuccess { _message.value = "Invitation sent to ${_inviteEmail.value}" }
                .onFailure { _message.value = it.message }
            _inviteEmail.value = ""
        }
    }

    fun removeMember(uid: String) {
        val firmId = auth.currentUser.value?.firmId ?: return
        viewModelScope.launch { firmRepo.removeMember(firmId, uid) }
    }

    fun assignRole(uid: String, role: FirmRole) {
        val firmId = auth.currentUser.value?.firmId ?: return
        viewModelScope.launch { firmRepo.assignRole(firmId, uid, role) }
    }

    fun assignCase(caseId: String, lawyer: UserEntity) { viewModelScope.launch { caseRepo.assignLawyer(caseId, lawyer) } }
}
