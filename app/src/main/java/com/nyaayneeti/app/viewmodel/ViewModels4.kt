package com.nyaayneeti.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nyaayneeti.app.data.model.*
import com.nyaayneeti.app.data.repository.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class InternshipViewModel(
    private val internshipRepo: InternshipRepository,
    private val auth: AuthRepository
) : ViewModel() {
    val me = auth.currentUser
    val openInternships = internshipRepo.observeOpen()

    val firmInternships = auth.currentUser.flatMapLatest { u -> u?.firmId?.let { internshipRepo.observeByFirm(it) } ?: flowOf(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val myApplications = auth.currentUser.flatMapLatest { u -> u?.let { internshipRepo.observeMyApplications(it.uid) } ?: flowOf(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedInternshipId = MutableStateFlow<String?>(null)
    val selectedInternship = _selectedInternshipId.flatMapLatest { id -> id?.let { internshipRepo.observe(it) } ?: flowOf(null) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    val applicationsForSelected = _selectedInternshipId.flatMapLatest { id -> id?.let { internshipRepo.observeApplications(it) } ?: flowOf(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _feedback = MutableStateFlow<String?>(null)
    val feedback = _feedback.asStateFlow()

    fun select(id: String) { _selectedInternshipId.value = id }

    fun post(firmName: String, title: String, desc: String, location: String, duration: Int, stipend: String, skills: String, eligibility: String) {
        val u = auth.currentUser.value ?: return
        val firmId = u.firmId ?: return
        viewModelScope.launch {
            internshipRepo.postInternship(firmId, firmName, title, desc, location, duration, stipend, skills, eligibility, System.currentTimeMillis() + 30L * 86400000)
        }
    }

    fun apply(internship: InternshipEntity, coverLetter: String) {
        val u = auth.currentUser.value ?: return
        viewModelScope.launch {
            internshipRepo.apply(internship, u, coverLetter, null)
                .onSuccess { _feedback.value = "Application submitted" }
                .onFailure { _feedback.value = it.message }
        }
    }

    fun setStatus(internshipId: String, appId: String, status: ApplicationStatus, studentUid: String) {
        viewModelScope.launch { internshipRepo.setApplicationStatus(internshipId, appId, status, studentUid) }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class ForumViewModel(
    private val forumRepo: ForumRepository,
    private val auth: AuthRepository
) : ViewModel() {
    val trending = forumRepo.observeTrending()
    val latest = forumRepo.observeLatest()

    private val _selectedPostId = MutableStateFlow<String?>(null)
    val selectedPost = _selectedPostId.flatMapLatest { id -> id?.let { forumRepo.observePost(it) } ?: flowOf(null) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    val comments = _selectedPostId.flatMapLatest { id -> id?.let { forumRepo.observeComments(it) } ?: flowOf(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _myVote = MutableStateFlow(0)
    val myVote = _myVote.asStateFlow()

    fun select(postId: String) {
        _selectedPostId.value = postId
        val u = auth.currentUser.value
        if (u != null) viewModelScope.launch { _myVote.value = forumRepo.myVote(postId, u.uid) }
    }

    fun createPost(category: ForumCategory, title: String, body: String, tags: String) {
        val u = auth.currentUser.value ?: return
        viewModelScope.launch { forumRepo.createPost(u, category, title, body, tags) }
    }

    fun addComment(postId: String, body: String) {
        val u = auth.currentUser.value ?: return
        viewModelScope.launch { forumRepo.addComment(postId, u, body) }
    }

    fun vote(postId: String, value: Int) {
        val u = auth.currentUser.value ?: return
        viewModelScope.launch {
            forumRepo.toggleVote(postId, u, value)
            _myVote.value = forumRepo.myVote(postId, u.uid)
        }
    }
}
