package com.nyaayneeti.app.data.model

import androidx.room.TypeConverter
import kotlinx.serialization.json.Json

class Converters {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter fun listToString(list: List<String>): String = list.joinToString("\u0001")
    @TypeConverter fun stringToList(s: String): List<String> = if (s.isEmpty()) emptyList() else s.split("\u0001")

    @TypeConverter fun userRoleToString(v: UserRole): String = v.name
    @TypeConverter fun stringToUserRole(s: String): UserRole = UserRole.valueOf(s)

    @TypeConverter fun firmRoleToString(v: FirmRole?): String? = v?.name
    @TypeConverter fun stringToFirmRole(s: String?): FirmRole? = s?.let { FirmRole.valueOf(it) }

    @TypeConverter fun verificationToString(v: VerificationStatus): String = v.name
    @TypeConverter fun stringToVerification(s: String): VerificationStatus = VerificationStatus.valueOf(s)

    @TypeConverter fun caseStatusToString(v: CaseStatus): String = v.name
    @TypeConverter fun stringToCaseStatus(s: String): CaseStatus = CaseStatus.valueOf(s)

    @TypeConverter fun casePriorityToString(v: CasePriority): String = v.name
    @TypeConverter fun stringToCasePriority(s: String): CasePriority = CasePriority.valueOf(s)

    @TypeConverter fun caseStageToString(v: CaseStage): String = v.name
    @TypeConverter fun stringToCaseStage(s: String): CaseStage = CaseStage.valueOf(s)

    @TypeConverter fun consultationStatusToString(v: ConsultationStatus): String = v.name
    @TypeConverter fun stringToConsultationStatus(s: String): ConsultationStatus = ConsultationStatus.valueOf(s)

    @TypeConverter fun paymentStatusToString(v: PaymentStatus): String = v.name
    @TypeConverter fun stringToPaymentStatus(s: String): PaymentStatus = PaymentStatus.valueOf(s)

    @TypeConverter fun tierToString(v: SubscriptionTier): String = v.name
    @TypeConverter fun stringToTier(s: String): SubscriptionTier = SubscriptionTier.valueOf(s)

    @TypeConverter fun internshipStatusToString(v: InternshipStatus): String = v.name
    @TypeConverter fun stringToInternshipStatus(s: String): InternshipStatus = InternshipStatus.valueOf(s)

    @TypeConverter fun applicationStatusToString(v: ApplicationStatus): String = v.name
    @TypeConverter fun stringToApplicationStatus(s: String): ApplicationStatus = ApplicationStatus.valueOf(s)

    @TypeConverter fun categoryToString(v: ForumCategory): String = v.name
    @TypeConverter fun stringToCategory(s: String): ForumCategory = ForumCategory.valueOf(s)

    @TypeConverter fun notificationTypeToString(v: NotificationType): String = v.name
    @TypeConverter fun stringToNotificationType(s: String): NotificationType = NotificationType.valueOf(s)

    @TypeConverter fun messageTypeToString(v: MessageType): String = v.name
    @TypeConverter fun stringToMessageType(s: String): MessageType = MessageType.valueOf(s)

    @TypeConverter fun hearingStatusToString(v: HearingStatus): String = v.name
    @TypeConverter fun stringToHearingStatus(s: String): HearingStatus = HearingStatus.valueOf(s)

    @TypeConverter fun invoiceStatusToString(v: InvoiceStatus): String = v.name
    @TypeConverter fun stringToInvoiceStatus(s: String): InvoiceStatus = InvoiceStatus.valueOf(s)

    @TypeConverter fun documentStatusToString(v: DocumentStatus): String = v.name
    @TypeConverter fun stringToDocumentStatus(s: String): DocumentStatus = DocumentStatus.valueOf(s)

    @TypeConverter fun inquiryStatusToString(v: InquiryStatus): String = v.name
    @TypeConverter fun stringToInquiryStatus(s: String): InquiryStatus = InquiryStatus.valueOf(s)

    @TypeConverter fun invitationStatusToString(v: InvitationStatus): String = v.name
    @TypeConverter fun stringToInvitationStatus(s: String): InvitationStatus = InvitationStatus.valueOf(s)

    @TypeConverter fun practiceTypeToString(v: PracticeType): String = v.name
    @TypeConverter fun stringToPracticeType(s: String): PracticeType = PracticeType.valueOf(s)

    @TypeConverter fun paymentMethodToString(v: PaymentMethod?): String? = v?.name
    @TypeConverter fun stringToPaymentMethod(s: String?): PaymentMethod? = s?.let { PaymentMethod.valueOf(it) }

    @TypeConverter fun reportStatusToString(v: ReportStatus): String = v.name
    @TypeConverter fun stringToReportStatus(s: String): ReportStatus = ReportStatus.valueOf(s)
}
