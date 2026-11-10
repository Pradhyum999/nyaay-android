package com.nyaayneeti.app.data.repository

import com.nyaayneeti.app.data.firebase.CloudSync
import com.nyaayneeti.app.data.firebase.pushReview
import com.nyaayneeti.app.data.local.ConsultationDao
import com.nyaayneeti.app.data.local.ReviewDao
import com.nyaayneeti.app.data.local.UserDao
import com.nyaayneeti.app.data.model.*
import java.util.UUID

class ConsultationRepository(
    private val consultationDao: ConsultationDao,
    private val reviewDao: ReviewDao,
    private val userDao: UserDao,
    private val billing: BillingRepository,
    private val notifications: NotificationRepository,
    private val cloud: CloudSync? = null
) {
    fun observe(id: String) = consultationDao.observe(id)
    fun observeForCitizen(uid: String) = consultationDao.observeForCitizen(uid)
    fun observeForLawyer(uid: String) = consultationDao.observeForLawyer(uid)
    fun observeReviews(lawyerUid: String) = reviewDao.observeForLawyer(lawyerUid)

    suspend fun request(citizen: UserEntity, lawyer: UserEntity, notes: String, scheduledAt: Long?, duration: Int, fee: Double): String {
        val id = UUID.randomUUID().toString()
        val c = ConsultationEntity(
            id = id, citizenUid = citizen.uid, lawyerUid = lawyer.uid,
            lawyerName = lawyer.name, citizenName = citizen.name,
            status = ConsultationStatus.REQUESTED, scheduledAt = scheduledAt,
            durationMinutes = duration, fee = fee, notes = notes,
            paymentStatus = if (fee > 0) PaymentStatus.PAID else PaymentStatus.PAID
        )
        consultationDao.upsert(c)
        cloud?.pushConsultation(c)
        if (fee > 0) billing.recordPayment(citizen.uid, lawyer.uid, fee, "consultation", id)
        notifications.notify(lawyer.uid, NotificationType.CONSULTATION, "New consultation request", "${citizen.name} requested a $duration-min consultation.", "consultation/$id")
        return id
    }

    suspend fun accept(id: String) {
        val meeting = "https://meet.jit.si/nyaayneeti-${UUID.randomUUID().toString().take(8)}"
        val c = consultationDao.get(id) ?: return
        val updated = c.copy(status = ConsultationStatus.SCHEDULED, meetingLink = meeting)
        consultationDao.upsert(updated)
        cloud?.pushConsultation(updated)
        notifications.notify(c.citizenUid, NotificationType.CONSULTATION, "Consultation scheduled", "Your consultation is confirmed. Tap to join.", "consultation/$id")
    }

    suspend fun reject(id: String) {
        val c = consultationDao.get(id) ?: return
        val updated = c.copy(status = ConsultationStatus.REJECTED)
        consultationDao.upsert(updated)
        cloud?.pushConsultation(updated)
        notifications.notify(c.citizenUid, NotificationType.CONSULTATION, "Consultation declined", "The advocate declined this request.", "consultation/$id")
    }

    suspend fun complete(id: String) {
        val c = consultationDao.get(id) ?: return
        val updated = c.copy(status = ConsultationStatus.COMPLETED)
        consultationDao.upsert(updated)
        cloud?.pushConsultation(updated)
    }

    suspend fun addReview(lawyer: UserEntity, citizen: UserEntity, rating: Int, comment: String?) {
        val review = ReviewEntity(
            id = UUID.randomUUID().toString(), lawyerUid = lawyer.uid, citizenUid = citizen.uid,
            citizenName = citizen.name, rating = rating, comment = comment
        )
        reviewDao.insert(review)
        cloud?.pushReview(review)
        val avg = reviewDao.avgRating(lawyer.uid) ?: rating.toDouble()
        val count = reviewDao.count(lawyer.uid)
        val updated = lawyer.copy(avgRating = avg, reviewCount = count)
        userDao.upsert(updated)
        cloud?.pushUser(updated)
    }
}
