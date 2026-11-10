package com.nyaayneeti.app.data.model

enum class Language(val code: String, val displayName: String, val subtitle: String) {
    ENGLISH("en", "English", "Continue in English"),
    HINDI("hi", "हिन्दी", "हिन्दी में जारी रखें"),
    MARATHI("mr", "मराठी", "मराठी भाषेत सुरू ठेवा")
}

enum class UserRole { CITIZEN, INDEPENDENT_LAWYER, FIRM_ADMIN, FIRM_MEMBER, STUDENT, PLATFORM_ADMIN }

enum class ReportStatus { OPEN, REVIEWED, DISMISSED }

enum class FirmRole { OWNER, ADMIN, SENIOR_PARTNER, ASSOCIATE, PARALEGAL, INTERN }

enum class VerificationStatus { UNVERIFIED, PENDING, VERIFIED, REJECTED }

enum class CaseStatus { DRAFT, ACTIVE, PENDING, HEARING_SCHEDULED, DISPOSED, CLOSED }

enum class CasePriority { NORMAL, URGENT }

enum class CaseStage { INTAKE, ADMISSION, NOTICE_SUMMONS, WRITTEN_STATEMENT, FRAMING_ISSUES, EVIDENCE, FINAL_ARGUMENTS, JUDGMENT_ORDER, EXECUTION, CLOSED }

enum class HearingStatus { UPCOMING, COMPLETED, ADJOURNED }

enum class InvoiceStatus { PENDING, PAID, OVERDUE }

enum class DocumentStatus { PENDING, VERIFIED }

enum class InquiryStatus { NEW, ACCEPTED, DECLINED }

enum class InvitationStatus { PENDING, ACCEPTED, DECLINED }

enum class PracticeType { INDEPENDENT, FIRM, CHAMBERS }

enum class PaymentMethod { UPI, BANK_TRANSFER, CASH }

enum class ConsultationStatus { REQUESTED, ACCEPTED, SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED, REJECTED }

enum class PaymentStatus { PENDING, PAID, FAILED, REFUNDED }

enum class SubscriptionTier { FREE, PROFESSIONAL, FIRM, PREMIUM }

enum class InternshipStatus { DRAFT, OPEN, CLOSED }

enum class ApplicationStatus { APPLIED, SHORTLISTED, ACCEPTED, REJECTED, COMPLETED }

enum class ForumCategory { GENERAL, LEGAL_NEWS, JUDGMENT_ANALYSIS, ASK_LAWYER, CAREER, INTERNSHIPS }

enum class NotificationType { CASE_UPDATE, HEARING_REMINDER, CONSULTATION, MESSAGE, VERIFICATION, INTERNSHIP, FORUM, PAYMENT, SYSTEM }

enum class MessageType { TEXT, IMAGE, CALL }
