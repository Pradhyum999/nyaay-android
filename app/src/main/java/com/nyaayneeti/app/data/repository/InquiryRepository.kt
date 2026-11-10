package com.nyaayneeti.app.data.repository

import com.nyaayneeti.app.data.firebase.CloudSync
import com.nyaayneeti.app.data.firebase.pushInquiry
import com.nyaayneeti.app.data.local.InquiryDao
import com.nyaayneeti.app.data.model.*
import java.util.UUID

class InquiryRepository(
    private val inquiryDao: InquiryDao,
    private val cases: CaseRepository,
    private val chat: ChatRepository,
    private val notifications: NotificationRepository,
    private val cloud: CloudSync? = null
) {
    fun observeForLawyer(uid: String) = inquiryDao.observeForLawyer(uid)
    fun observeForClient(uid: String) = inquiryDao.observeForClient(uid)
    fun observeUnassignedCases(uid: String) = cases.observeUnassignedCases(uid)

    suspend fun engage(lawyer: UserEntity, client: UserEntity, subject: String, message: String, attachAIBrief: Boolean, caseId: String? = null): String {
        val inquiry = InquiryEntity(
            id = UUID.randomUUID().toString(),
            lawyerUid = lawyer.uid,
            clientUid = client.uid,
            clientName = client.name,
            subject = subject,
            message = message,
            attachAIBrief = attachAIBrief,
            caseId = caseId
        )
        inquiryDao.upsert(inquiry)
        cloud?.pushInquiry(inquiry)
        notifications.notify(lawyer.uid, NotificationType.MESSAGE, "New client inquiry", "$subject — ${client.name}", "inquiries")
        return inquiry.id
    }

    suspend fun accept(id: String, lawyer: UserEntity) {
        val i = inquiryDao.get(id) ?: return
        inquiryDao.setStatus(id, InquiryStatus.ACCEPTED)

        // Assign the existing case, or create a new one from the inquiry.
        val caseId: String = if (i.caseId != null) {
            cases.assignLawyer(i.caseId, lawyer)
            i.caseId
        } else {
            cases.createCaseFromInquiry(i, lawyer)
        }
        inquiryDao.setCaseId(id, caseId)

        // Open a case-linked conversation seeded with the client's message.
        val client = UserEntity(uid = i.clientUid, phone = "", name = i.clientName, role = UserRole.CITIZEN)
        chat.openCaseChat(client, lawyer, caseId, i.message)

        notifications.notify(i.clientUid, NotificationType.CASE_UPDATE, "Advocate accepted", "${lawyer.name} accepted your matter. Chat and track the case now.", "case_room/$caseId")
    }

    suspend fun decline(id: String) {
        val i = inquiryDao.get(id) ?: return
        inquiryDao.setStatus(id, InquiryStatus.DECLINED)
        notifications.notify(i.clientUid, NotificationType.MESSAGE, "Inquiry declined", "The advocate declined your inquiry.", "inquiries")
    }
}
