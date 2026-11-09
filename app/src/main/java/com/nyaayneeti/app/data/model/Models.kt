package com.nyaayneeti.app.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class Language(val code: String, val displayName: String, val subtitle: String) {
    ENGLISH("en", "English", "Continue in English"),
    HINDI("hi", "हिन्दी", "हिन्दी में जारी रखें"),
    MARATHI("mr", "मराठी", "मराठी भाषेत सुरू ठेवा")
}

@Serializable
enum class UserRole {
    ADVOCATE,
    CITIZEN
}

@Serializable
enum class CasePriority {
    NORMAL,
    URGENT
}

@Serializable
enum class CaseStatus {
    ACTIVE,
    PENDING,
    DISPOSED,
    CLOSED
}

@Serializable
data class UserProfile(
    val uid: String,
    val role: UserRole,
    val name: String,
    val phone: String,
    val email: String? = null,
    val state: String? = null,
    val barCouncilId: String? = null,
    val city: String? = null,
    val language: Language = Language.ENGLISH,
    val onboardingCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Serializable
data class CaseProfile(
    val matterType: String,
    val legalArea: String,
    val location: String,
    val facts: List<String> = emptyList(),
    val missingInfo: List<String> = emptyList(),
    val documentsAvailable: List<String> = emptyList(),
    val documentsRequired: List<String> = emptyList(),
    val urgency: String = "Normal",
    val confidenceScore: Double = 0.85,
    val summaryText: String = "",
    val generatedAt: Long = System.currentTimeMillis()
)

@Serializable
data class CaseFile(
    val id: String,
    val caseNumber: String,
    val title: String,
    val clientName: String,
    val clientPhone: String,
    val lawyerId: String? = null,
    val lawyerName: String? = null,
    val court: String = "District & Sessions Court",
    val matterType: String = "Civil Dispute",
    val stage: String = "Notice / Pleadings",
    val priority: CasePriority = CasePriority.NORMAL,
    val status: CaseStatus = CaseStatus.ACTIVE,
    val nextHearingDate: String? = null,
    val profile: CaseProfile? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Serializable
data class CaseTimelineEvent(
    val id: String,
    val caseId: String,
    val type: String,
    val titleEn: String,
    val titleHi: String,
    val descriptionEn: String,
    val descriptionHi: String,
    val eventDate: String,
    val actor: String = "System",
    val createdAt: Long = System.currentTimeMillis()
)

@Serializable
data class CaseTask(
    val id: String,
    val caseId: String,
    val titleEn: String,
    val titleHi: String,
    val assignedTo: String, // "lawyer" or "client"
    val isCompleted: Boolean = false,
    val dueDate: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Serializable
data class CaseNote(
    val id: String,
    val caseId: String,
    val content: String,
    val authorName: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Serializable
data class HearingItem(
    val id: String,
    val caseId: String,
    val caseNumber: String,
    val courtNumber: String,
    val itemNumber: Int,
    val stage: String,
    val hearingDate: String,
    val hearingTime: String,
    val purpose: String
)

@Serializable
data class IntakeChatMessage(
    val id: String,
    val isUser: Boolean,
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)
