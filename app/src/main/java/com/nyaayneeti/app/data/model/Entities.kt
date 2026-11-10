package com.nyaayneeti.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val uid: String,
    val phone: String,
    val name: String,
    val email: String? = null,
    val role: UserRole,
    val firmId: String? = null,
    val firmRole: FirmRole? = null,
    val barCouncilId: String? = null,
    val verificationStatus: VerificationStatus = VerificationStatus.UNVERIFIED,
    val specializations: String = "[]",
    val practiceAreas: String = "[]",
    val languages: String = "[]",
    val feeMin: Double = 0.0,
    val feeMax: Double = 0.0,
    val education: String = "[]",
    val barAssociation: String? = null,
    val aibeCertificateNo: String? = null,
    val practiceCourts: String = "[]",
    val practiceType: PracticeType = PracticeType.INDEPENDENT,
    val chambersAddress: String? = null,
    val chamberLat: Double? = null,
    val chamberLng: Double? = null,
    val bio: String? = null,
    val experienceYears: Int = 0,
    val state: String? = null,
    val city: String? = null,
    val language: String = "en",
    val onboardingCompleted: Boolean = false,
    val college: String? = null,
    val yearOfStudy: String? = null,
    val avgRating: Double = 0.0,
    val reviewCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "firms")
data class LawFirmEntity(
    @PrimaryKey val firmId: String,
    val name: String,
    val adminUid: String,
    val gstin: String? = null,
    val address: String? = null,
    val city: String? = null,
    val state: String? = null,
    val subscriptionTier: SubscriptionTier = SubscriptionTier.FREE,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "cases")
data class CaseEntity(
    @PrimaryKey val id: String,
    val caseNumber: String,
    val title: String,
    val clientUid: String,
    val clientName: String,
    val clientPhone: String,
    val firmId: String? = null,
    val lawyerUid: String? = null,
    val lawyerName: String? = null,
    val court: String = "District & Sessions Court",
    val matterType: String,
    val legalArea: String,
    val location: String = "India",
    val cnrNumber: String? = null,
    val filingNumber: String? = null,
    val judge: String? = null,
    val stage: CaseStage = CaseStage.INTAKE,
    val priority: CasePriority = CasePriority.NORMAL,
    val status: CaseStatus = CaseStatus.ACTIVE,
    val nextHearingDate: Long? = null,
    val factsJson: String = "[]",
    val summaryText: String = "",
    val isDeleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "case_timeline_events")
data class TimelineEventEntity(
    @PrimaryKey val id: String,
    val caseId: String,
    val type: String,
    val titleEn: String,
    val descriptionEn: String,
    val eventDate: Long = System.currentTimeMillis(),
    val actor: String = "System",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "case_tasks")
data class CaseTaskEntity(
    @PrimaryKey val id: String,
    val caseId: String,
    val title: String,
    val assignedTo: String,
    val isCompleted: Boolean = false,
    val dueDate: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "case_notes")
data class CaseNoteEntity(
    @PrimaryKey val id: String,
    val caseId: String,
    val content: String,
    val authorUid: String,
    val authorName: String,
    val isPrivate: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "documents")
data class DocumentEntity(
    @PrimaryKey val id: String,
    val caseId: String,
    val ownerUid: String,
    val name: String,
    val uri: String,
    val mimeType: String,
    val sizeBytes: Long,
    val category: String,
    val status: DocumentStatus = DocumentStatus.PENDING,
    val uploadedBy: String = "client",
    val uploadedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "hearings")
data class HearingEntity(
    @PrimaryKey val id: String,
    val caseId: String,
    val caseNumber: String,
    val courtNumber: String,
    val itemNumber: Int,
    val stage: String,
    val hearingDate: Long,
    val hearingTime: String,
    val purpose: String,
    val judgeName: String? = null,
    val orderNotes: String? = null,
    val status: HearingStatus = HearingStatus.UPCOMING
)
