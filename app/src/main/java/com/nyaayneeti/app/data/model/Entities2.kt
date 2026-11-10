package com.nyaayneeti.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey val id: String,
    val userA: String,
    val userB: String,
    val lastMessage: String,
    val lastMessageAt: Long = System.currentTimeMillis(),
    val lastSenderUid: String? = null,
    val caseId: String? = null
)

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val id: String,
    val conversationId: String,
    val senderUid: String,
    val receiverUid: String,
    val text: String,
    val type: MessageType = MessageType.TEXT,
    val isRead: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "consultations")
data class ConsultationEntity(
    @PrimaryKey val id: String,
    val citizenUid: String,
    val lawyerUid: String,
    val lawyerName: String,
    val citizenName: String,
    val caseId: String? = null,
    val status: ConsultationStatus = ConsultationStatus.REQUESTED,
    val requestedAt: Long = System.currentTimeMillis(),
    val scheduledAt: Long? = null,
    val durationMinutes: Int = 30,
    val fee: Double = 0.0,
    val paymentStatus: PaymentStatus = PaymentStatus.PENDING,
    val meetingLink: String? = null,
    val notes: String? = null,
    val rating: Int = 0
)

@Entity(tableName = "internships")
data class InternshipEntity(
    @PrimaryKey val id: String,
    val firmId: String,
    val firmName: String,
    val title: String,
    val description: String,
    val location: String,
    val durationMonths: Int = 1,
    val stipend: String = "Unpaid",
    val skills: String = "",
    val eligibility: String = "Law student (any year)",
    val deadline: Long? = null,
    val status: InternshipStatus = InternshipStatus.OPEN,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "internship_applications")
data class InternshipApplicationEntity(
    @PrimaryKey val id: String,
    val internshipId: String,
    val studentUid: String,
    val studentName: String,
    val coverLetter: String,
    val resumeUri: String? = null,
    val status: ApplicationStatus = ApplicationStatus.APPLIED,
    val appliedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "forum_posts")
data class PostEntity(
    @PrimaryKey val id: String,
    val authorUid: String,
    val authorName: String,
    val authorRole: UserRole,
    val category: ForumCategory,
    val title: String,
    val body: String,
    val tags: String = "",
    val upvotes: Int = 0,
    val downvotes: Int = 0,
    val commentCount: Int = 0,
    val isRemoved: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "forum_comments")
data class CommentEntity(
    @PrimaryKey val id: String,
    val postId: String,
    val authorUid: String,
    val authorName: String,
    val body: String,
    val parentId: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "forum_likes")
data class LikeEntity(
    @PrimaryKey val id: String,
    val postId: String,
    val uid: String,
    val value: Int
)

@Entity(tableName = "reviews")
data class ReviewEntity(
    @PrimaryKey val id: String,
    val lawyerUid: String,
    val citizenUid: String,
    val citizenName: String,
    val rating: Int,
    val comment: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val uid: String,
    val type: NotificationType,
    val title: String,
    val body: String,
    val deepLink: String? = null,
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "subscriptions")
data class SubscriptionEntity(
    @PrimaryKey val id: String,
    val ownerUid: String,
    val firmId: String? = null,
    val tier: SubscriptionTier,
    val startAt: Long = System.currentTimeMillis(),
    val endAt: Long,
    val status: PaymentStatus = PaymentStatus.PAID
)

@Entity(tableName = "payments")
data class PaymentEntity(
    @PrimaryKey val id: String,
    val payerUid: String,
    val payeeUid: String? = null,
    val amount: Double,
    val purpose: String,
    val refId: String,
    val status: PaymentStatus = PaymentStatus.PENDING,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "invoices")
data class InvoiceEntity(
    @PrimaryKey val id: String,
    val caseId: String? = null,
    val lawyerUid: String,
    val clientUid: String? = null,
    val invoiceNumber: String,
    val caseNumber: String,
    val clientName: String,
    val date: Long = System.currentTimeMillis(),
    val appearanceFee: Double = 0.0,
    val draftingFee: Double = 0.0,
    val clerkageMisc: Double = 0.0,
    val totalAmount: Double = 0.0,
    val status: InvoiceStatus = InvoiceStatus.PENDING,
    val paidVia: PaymentMethod? = null,
    val upiRef: String? = null,
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "inquiries")
data class InquiryEntity(
    @PrimaryKey val id: String,
    val lawyerUid: String,
    val clientUid: String,
    val clientName: String,
    val subject: String,
    val message: String,
    val attachAIBrief: Boolean = false,
    val caseId: String? = null,
    val status: InquiryStatus = InquiryStatus.NEW,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "verification_requests")
data class VerificationRequestEntity(
    @PrimaryKey val id: String,
    val uid: String,
    val role: UserRole,
    val name: String,
    val documentType: String,
    val documentUrl: String? = null,
    val maskedIdNumber: String? = null,
    val status: VerificationStatus = VerificationStatus.PENDING,
    val submittedAt: Long = System.currentTimeMillis(),
    val rejectionReason: String? = null,
    val verifiedBy: String? = null,
    val verifiedAt: Long? = null
)

@Entity(tableName = "reports")
data class ReportEntity(
    @PrimaryKey val id: String,
    val reporterUid: String,
    val targetUid: String,
    val targetName: String,
    val reason: String,
    val status: ReportStatus = ReportStatus.OPEN,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "blocks")
data class BlockEntity(
    @PrimaryKey val id: String,
    val blockerUid: String,
    val blockedUid: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "firm_invitations")
data class InvitationEntity(
    @PrimaryKey val id: String,
    val firmId: String,
    val email: String,
    val firmRole: FirmRole,
    val status: InvitationStatus = InvitationStatus.PENDING,
    val createdAt: Long = System.currentTimeMillis()
)
