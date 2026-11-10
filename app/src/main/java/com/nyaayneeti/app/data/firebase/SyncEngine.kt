package com.nyaayneeti.app.data.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.nyaayneeti.app.data.local.NyaayDatabase
import com.nyaayneeti.app.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Pulls Firestore → Room so every device mirrors the shared cloud state.
 * Writes already flow through CloudSync (Room + Firestore); this is the read-back
 * that makes the app behave as one live product across devices.
 */
class SyncEngine(
    private val db: NyaayDatabase,
    private val firestore: FirebaseFirestore,
    private val scope: CoroutineScope
) {
    private fun <T : Any> listen(path: String, clazz: Class<T>, upsert: suspend (T) -> Unit) {
        firestore.collection(path).addSnapshotListener { snap, e ->
            if (e != null || snap == null) return@addSnapshotListener
            val items = try { snap.toObjects(clazz) } catch (t: Throwable) { emptyList() }
            scope.launch { items.forEach { upsert(it) } }
        }
    }

    fun start() {
        listen("users", UserEntity::class.java) { db.userDao().upsert(it) }
        listen("firms", LawFirmEntity::class.java) { db.firmDao().upsert(it) }
        listen("cases", CaseEntity::class.java) { db.caseDao().upsert(it) }
        listen("conversations", ConversationEntity::class.java) { db.conversationDao().upsert(it) }
        listen("messages", MessageEntity::class.java) { db.messageDao().insert(it) }
        listen("consultations", ConsultationEntity::class.java) { db.consultationDao().upsert(it) }
        listen("internships", InternshipEntity::class.java) { db.internshipDao().upsert(it) }
        listen("applications", InternshipApplicationEntity::class.java) { db.applicationDao().upsert(it) }
        listen("posts", PostEntity::class.java) { db.postDao().upsert(it) }
        listen("comments", CommentEntity::class.java) { db.commentDao().insert(it) }
        listen("reviews", ReviewEntity::class.java) { db.reviewDao().insert(it) }
        listen("notifications", NotificationEntity::class.java) { db.notificationDao().insert(it) }
        listen("subscriptions", SubscriptionEntity::class.java) { db.subscriptionDao().upsert(it) }
        listen("payments", PaymentEntity::class.java) { db.paymentDao().upsert(it) }
        listen("invoices", InvoiceEntity::class.java) { db.invoiceDao().upsert(it) }
        listen("inquiries", InquiryEntity::class.java) { db.inquiryDao().upsert(it) }
        listen("case_timeline_events", TimelineEventEntity::class.java) { db.timelineDao().insert(it) }
        listen("case_tasks", CaseTaskEntity::class.java) { db.taskDao().insert(it) }
        listen("case_notes", CaseNoteEntity::class.java) { db.noteDao().insert(it) }
        listen("documents", DocumentEntity::class.java) { db.documentDao().insert(it) }
        listen("hearings", HearingEntity::class.java) { db.hearingDao().insert(it) }
        listen("invitations", InvitationEntity::class.java) { db.invitationDao().upsert(it) }
        listen("verification_requests", VerificationRequestEntity::class.java) { db.verificationDao().upsert(it) }
        listen("reports", ReportEntity::class.java) { db.reportDao().upsert(it) }
        listen("blocks", BlockEntity::class.java) { db.blockDao().upsert(it) }
    }
}
