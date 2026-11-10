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
                "specializations" to u.specializations, "bio" to u.bio, "experienceYears" to u.experienceYears,
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
                "location" to c.location, "stage" to c.stage.name, "priority" to c.priority.name,
                "status" to c.status.name, "nextHearingDate" to c.nextHearingDate,
                "factsJson" to c.factsJson, "summaryText" to c.summaryText,
                "createdAt" to c.createdAt, "updatedAt" to c.updatedAt
            )
        )
    }

    fun pushConversation(c: ConversationEntity) {
        db.collection("conversations").document(c.id).set(
            hashMapOf(
                "id" to c.id, "userA" to c.userA, "userB" to c.userB,
                "lastMessage" to c.lastMessage, "lastMessageAt" to c.lastMessageAt, "lastSenderUid" to c.lastSenderUid
            )
        )
    }

    fun pushMessage(convoId: String, m: MessageEntity) {
        db.collection("conversations").document(convoId).collection("messages").document(m.id).set(
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
        db.collection("internships").document(a.internshipId).collection("applications").document(a.id).set(
            hashMapOf(
                "id" to a.id, "internshipId" to a.internshipId, "studentUid" to a.studentUid,
                "studentName" to a.studentName, "coverLetter" to a.coverLetter,
                "resumeUri" to a.resumeUri, "status" to a.status.name, "appliedAt" to a.appliedAt
            )
        )
    }
}
