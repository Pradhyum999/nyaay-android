package com.nyaayneeti.app.data.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.nyaayneeti.app.data.model.*

/**
 * Write-through sync: after every local (Room) mutation we also write to Firestore,
 * so data is shared across devices. Firestore is the source of truth for cross-user data.
 */
class CloudSync(internal val db: FirebaseFirestore) {

    fun pushUser(u: UserEntity) {
        db.collection("users").document(u.uid).set(
            hashMapOf(
                "uid" to u.uid, "phone" to u.phone, "name" to u.name, "email" to u.email,
                "role" to u.role.name, "firmId" to u.firmId, "firmRole" to u.firmRole?.name,
                "barCouncilId" to u.barCouncilId, "verificationStatus" to u.verificationStatus.name,
                "specializations" to u.specializations, "practiceAreas" to u.practiceAreas, "languages" to u.languages,
                "feeMin" to u.feeMin, "feeMax" to u.feeMax, "education" to u.education,
                "barAssociation" to u.barAssociation, "aibeCertificateNo" to u.aibeCertificateNo,
                "practiceCourts" to u.practiceCourts, "practiceType" to u.practiceType.name,
                "chambersAddress" to u.chambersAddress, "chamberLat" to u.chamberLat, "chamberLng" to u.chamberLng,
                "bio" to u.bio, "experienceYears" to u.experienceYears,
                "state" to u.state, "city" to u.city, "language" to u.language,
                "onboardingCompleted" to u.onboardingCompleted, "college" to u.college, "yearOfStudy" to u.yearOfStudy,
                "avgRating" to u.avgRating, "reviewCount" to u.reviewCount, "createdAt" to u.createdAt
            )
        )
    }

    fun pushFirm(f: LawFirmEntity) {
        db.collection("firms").document(f.firmId).set(
            hashMapOf(
                "firmId" to f.firmId, "name" to f.name, "adminUid" to f.adminUid, "gstin" to f.gstin,
                "address" to f.address, "city" to f.city, "state" to f.state,
                "subscriptionTier" to f.subscriptionTier.name, "createdAt" to f.createdAt
            )
        )
    }

    fun pushCase(c: CaseEntity) {
        db.collection("cases").document(c.id).set(
            hashMapOf(
                "id" to c.id, "caseNumber" to c.caseNumber, "title" to c.title,
                "clientUid" to c.clientUid, "clientName" to c.clientName, "clientPhone" to c.clientPhone,
                "firmId" to c.firmId, "lawyerUid" to c.lawyerUid, "lawyerName" to c.lawyerName,
                "court" to c.court, "matterType" to c.matterType, "legalArea" to c.legalArea,
                "location" to c.location, "cnrNumber" to c.cnrNumber, "filingNumber" to c.filingNumber, "judge" to c.judge,
                "stage" to c.stage.name, "priority" to c.priority.name,
                "status" to c.status.name, "nextHearingDate" to c.nextHearingDate,
                "factsJson" to c.factsJson, "summaryText" to c.summaryText, "isDeleted" to c.isDeleted,
                "createdAt" to c.createdAt, "updatedAt" to c.updatedAt
            )
        )
    }

    fun pushTimeline(t: TimelineEventEntity) {
        db.collection("case_timeline_events").document(t.id).set(
            hashMapOf(
                "id" to t.id, "caseId" to t.caseId, "type" to t.type, "titleEn" to t.titleEn,
                "descriptionEn" to t.descriptionEn, "eventDate" to t.eventDate, "actor" to t.actor, "createdAt" to t.createdAt
            )
        )
    }

    fun pushTask(t: CaseTaskEntity) {
        db.collection("case_tasks").document(t.id).set(
            hashMapOf(
                "id" to t.id, "caseId" to t.caseId, "title" to t.title, "assignedTo" to t.assignedTo,
                "isCompleted" to t.isCompleted, "dueDate" to t.dueDate, "createdAt" to t.createdAt
            )
        )
    }

    fun pushNote(n: CaseNoteEntity) {
        db.collection("case_notes").document(n.id).set(
            hashMapOf(
                "id" to n.id, "caseId" to n.caseId, "content" to n.content, "authorUid" to n.authorUid,
                "authorName" to n.authorName, "isPrivate" to n.isPrivate, "createdAt" to n.createdAt
            )
        )
    }

    fun pushDocument(d: DocumentEntity) {
        db.collection("documents").document(d.id).set(
            hashMapOf(
                "id" to d.id, "caseId" to d.caseId, "ownerUid" to d.ownerUid, "name" to d.name,
                "uri" to d.uri, "mimeType" to d.mimeType, "sizeBytes" to d.sizeBytes,
                "category" to d.category, "status" to d.status.name, "uploadedBy" to d.uploadedBy, "uploadedAt" to d.uploadedAt
            )
        )
    }

    fun pushHearing(h: HearingEntity) {
        db.collection("hearings").document(h.id).set(
            hashMapOf(
                "id" to h.id, "caseId" to h.caseId, "caseNumber" to h.caseNumber,
                "courtNumber" to h.courtNumber, "itemNumber" to h.itemNumber, "stage" to h.stage,
                "hearingDate" to h.hearingDate, "hearingTime" to h.hearingTime, "purpose" to h.purpose,
                "judgeName" to h.judgeName, "orderNotes" to h.orderNotes, "status" to h.status.name
            )
        )
    }

    fun pushConversation(c: ConversationEntity) {
        db.collection("conversations").document(c.id).set(
            hashMapOf(
                "id" to c.id, "userA" to c.userA, "userB" to c.userB,
                "lastMessage" to c.lastMessage, "lastMessageAt" to c.lastMessageAt, "lastSenderUid" to c.lastSenderUid,
                "caseId" to c.caseId
            )
        )
    }

    fun pushMessage(convoId: String, m: MessageEntity) {
        db.collection("messages").document(m.id).set(
            hashMapOf(
                "id" to m.id, "conversationId" to m.conversationId,
                "senderUid" to m.senderUid, "receiverUid" to m.receiverUid,
                "text" to m.text, "type" to m.type.name, "isRead" to m.isRead, "timestamp" to m.timestamp
            )
        )
    }

    fun pushConsultation(c: ConsultationEntity) {
        db.collection("consultations").document(c.id).set(
            hashMapOf(
                "id" to c.id, "citizenUid" to c.citizenUid, "lawyerUid" to c.lawyerUid,
                "lawyerName" to c.lawyerName, "citizenName" to c.citizenName, "caseId" to c.caseId,
                "status" to c.status.name, "requestedAt" to c.requestedAt, "scheduledAt" to c.scheduledAt,
                "durationMinutes" to c.durationMinutes, "fee" to c.fee, "paymentStatus" to c.paymentStatus.name,
                "meetingLink" to c.meetingLink, "notes" to c.notes, "rating" to c.rating
            )
        )
    }

    fun pushInternship(i: InternshipEntity) {
        db.collection("internships").document(i.id).set(
            hashMapOf(
                "id" to i.id, "firmId" to i.firmId, "firmName" to i.firmName,
                "title" to i.title, "description" to i.description, "location" to i.location,
                "durationMonths" to i.durationMonths, "stipend" to i.stipend, "skills" to i.skills,
                "eligibility" to i.eligibility, "deadline" to i.deadline, "status" to i.status.name, "createdAt" to i.createdAt
            )
        )
    }

    fun pushApplication(a: InternshipApplicationEntity) {
        db.collection("applications").document(a.id).set(
            hashMapOf(
                "id" to a.id, "internshipId" to a.internshipId, "studentUid" to a.studentUid,
                "studentName" to a.studentName, "coverLetter" to a.coverLetter,
                "resumeUri" to a.resumeUri, "status" to a.status.name, "appliedAt" to a.appliedAt
            )
        )
    }
}
