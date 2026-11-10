package com.nyaayneeti.app.data.repository

import com.nyaayneeti.app.data.firebase.CloudSync
import com.nyaayneeti.app.data.firebase.pushInvoice
import com.nyaayneeti.app.data.local.*
import com.nyaayneeti.app.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.util.UUID

class CaseRepository(
    private val caseDao: CaseDao,
    private val timelineDao: TimelineDao,
    private val taskDao: TaskDao,
    private val noteDao: NoteDao,
    private val documentDao: DocumentDao,
    private val hearingDao: HearingDao,
    private val invoiceDao: InvoiceDao,
    private val notifications: NotificationRepository,
    private val cloud: CloudSync? = null
) {
    fun observeCasesFor(user: UserEntity): Flow<List<CaseEntity>> = when (user.role) {
        UserRole.CITIZEN -> caseDao.observeByClient(user.uid)
        UserRole.INDEPENDENT_LAWYER -> caseDao.observeByLawyer(user.uid)
        UserRole.FIRM_ADMIN -> user.firmId?.let { caseDao.observeByFirm(it) } ?: flowOf(emptyList())
        UserRole.FIRM_MEMBER -> user.firmId?.let { caseDao.observeByMember(it, user.uid) } ?: flowOf(emptyList())
        UserRole.STUDENT -> flowOf(emptyList())
        UserRole.PLATFORM_ADMIN -> flowOf(emptyList())
    }

    fun observeCase(id: String) = caseDao.observe(id)
    fun observeTimeline(caseId: String) = timelineDao.observe(caseId)
    fun observeTasks(caseId: String) = taskDao.observe(caseId)
    fun observeNotes(caseId: String) = noteDao.observe(caseId)
    fun observeDocuments(caseId: String) = documentDao.observe(caseId)
    fun observeHearings(caseId: String) = hearingDao.observeByCase(caseId)
    fun observeHearingsByDay(day: Long) = hearingDao.observeByDay(day)
    suspend fun getCase(id: String): CaseEntity? = caseDao.get(id)
    fun observeUnassignedCases(uid: String) = caseDao.observeUnassignedByClient(uid)

    suspend fun createCaseFromProfile(client: UserEntity, profile: CaseProfile): String {
        val id = "case-${UUID.randomUUID().toString().take(8)}"
        val case = CaseEntity(
            id = id,
            caseNumber = "CNR-MH-${(1000..9999).random()}-${(100000..999999).random()}-2026",
            title = "${client.name} — ${profile.matterType}",
            clientUid = client.uid,
            clientName = client.name,
            clientPhone = client.phone,
            matterType = profile.matterType,
            legalArea = profile.legalArea,
            location = profile.location,
            priority = if (profile.urgency.equals("Urgent", true)) CasePriority.URGENT else CasePriority.NORMAL,
            factsJson = profile.facts.joinToString(" | "),
            summaryText = profile.summaryText
        )
        caseDao.upsert(case)
        cloud?.pushCase(case)
        timelineDao.insert(
            TimelineEventEntity(
                id = UUID.randomUUID().toString(),
                caseId = id,
                type = "intake",
                titleEn = "Case Room Initialized",
                descriptionEn = "AI intake structured ${profile.facts.size} facts and ${profile.documentsRequired.size} required documents.",
                actor = "Nyay AI Engine"
            )
        )
        profile.documentsRequired.forEach { doc ->
            taskDao.insert(
                CaseTaskEntity(id = UUID.randomUUID().toString(), caseId = id, title = "Upload: $doc", assignedTo = "client")
            )
        }
        return id
    }

    suspend fun toggleTask(caseId: String, taskId: String) {
        taskDao.get(taskId)?.let { taskDao.update(it.copy(isCompleted = !it.isCompleted)) }
    }

    suspend fun addTask(caseId: String, title: String, assignedTo: String) {
        taskDao.insert(CaseTaskEntity(id = UUID.randomUUID().toString(), caseId = caseId, title = title, assignedTo = assignedTo))
    }

    suspend fun addNote(caseId: String, content: String, author: UserEntity, isPrivate: Boolean) {
        noteDao.insert(
            CaseNoteEntity(
                id = UUID.randomUUID().toString(), caseId = caseId, content = content,
                authorUid = author.uid, authorName = author.name, isPrivate = isPrivate
            )
        )
        addTimeline(caseId, "note", "Note added", "${author.name} added a note.", author.name)
    }

    suspend fun addDocument(caseId: String, doc: DocumentEntity) {
        documentDao.insert(doc)
        addTimeline(caseId, "doc", "Document uploaded", "${doc.name} was added.", "System")
    }

    suspend fun updateStage(caseId: String, stage: CaseStage) {
        caseDao.get(caseId)?.let {
            val updated = it.copy(stage = stage, updatedAt = System.currentTimeMillis())
            caseDao.upsert(updated)
            cloud?.pushCase(updated)
            addTimeline(caseId, "status", "Stage updated", "Moved to ${stage.name}.", "Advocate")
            notifications.notify(it.clientUid, NotificationType.CASE_UPDATE, "Case stage updated", "Your case moved to ${stage.name}.", "case_room/$caseId")
        }
    }

    suspend fun updatePriority(caseId: String, priority: CasePriority) {
        caseDao.get(caseId)?.let {
            val updated = it.copy(priority = priority, updatedAt = System.currentTimeMillis())
            caseDao.upsert(updated)
            cloud?.pushCase(updated)
        }
    }

    suspend fun addHearing(caseId: String, courtNumber: String, itemNumber: Int, date: Long, time: String, purpose: String) {
        val case = caseDao.get(caseId) ?: return
        hearingDao.insert(
            HearingEntity(
                id = UUID.randomUUID().toString(), caseId = caseId, caseNumber = case.caseNumber,
                courtNumber = courtNumber, itemNumber = itemNumber, stage = case.stage.name,
                hearingDate = date, hearingTime = time, purpose = purpose
            )
        )
        caseDao.upsert(case.copy(nextHearingDate = date, updatedAt = System.currentTimeMillis()))
        cloud?.pushCase(case.copy(nextHearingDate = date, updatedAt = System.currentTimeMillis()))
        addTimeline(caseId, "hearing", "Hearing scheduled", "Hearing on $time in $courtNumber (item #$itemNumber).", "Advocate")
        notifications.notify(case.clientUid, NotificationType.HEARING_REMINDER, "Hearing scheduled", "$time · $courtNumber · Item #$itemNumber", "case_room/$caseId")
    }

    suspend fun assignLawyer(caseId: String, lawyer: UserEntity) {
        caseDao.get(caseId)?.let { case ->
            val updated = case.copy(lawyerUid = lawyer.uid, lawyerName = lawyer.name, firmId = lawyer.firmId, updatedAt = System.currentTimeMillis())
            caseDao.upsert(updated)
            cloud?.pushCase(updated)
            addTimeline(caseId, "assignment", "Advocate assigned", "${lawyer.name} is now handling this case.", lawyer.name)
            notifications.notify(case.clientUid, NotificationType.CASE_UPDATE, "Advocate assigned", "${lawyer.name} is handling your case.", "case_room/$caseId")
            notifications.notify(lawyer.uid, NotificationType.CASE_UPDATE, "New case assigned", "You have been assigned: ${case.title}", "case_room/$caseId")
        }
    }

    fun observeInvoices(caseId: String) = invoiceDao.observeByCase(caseId)
    fun observeInvoicesForLawyer(uid: String) = invoiceDao.observeForLawyer(uid)
    fun observeAllHearings() = hearingDao.observeAll()

    suspend fun addCase(
        lawyer: UserEntity,
        caseNumber: String,
        title: String,
        clientName: String,
        clientPhone: String,
        court: String,
        matterType: String,
        legalArea: String,
        stage: CaseStage,
        priority: CasePriority
    ): Result<String> {
        if (caseDao.countByCaseNumber(caseNumber) > 0) {
            return Result.failure(IllegalArgumentException("A case with this number already exists"))
        }
        val id = "case-${UUID.randomUUID().toString().take(8)}"
        val case = CaseEntity(
            id = id, caseNumber = caseNumber, title = title,
            clientUid = "", clientName = clientName, clientPhone = clientPhone,
            lawyerUid = lawyer.uid, lawyerName = lawyer.name, court = court,
            matterType = matterType, legalArea = legalArea, stage = stage, priority = priority,
            cnrNumber = caseNumber
        )
        caseDao.upsert(case)
        cloud?.pushCase(case)
        addTimeline(id, "created", "Case created", "Case $caseNumber added.", lawyer.name)
        return Result.success(id)
    }

    suspend fun createCaseFromInquiry(inquiry: InquiryEntity, lawyer: UserEntity): String {
        val id = "case-${UUID.randomUUID().toString().take(8)}"
        val case = CaseEntity(
            id = id,
            caseNumber = "CNR-MH-${(1000..9999).random()}-${(100000..999999).random()}-2026",
            title = inquiry.subject,
            clientUid = inquiry.clientUid,
            clientName = inquiry.clientName,
            clientPhone = "",
            lawyerUid = lawyer.uid,
            lawyerName = lawyer.name,
            firmId = lawyer.firmId,
            matterType = inquiry.subject,
            legalArea = "General",
            summaryText = inquiry.message
        )
        caseDao.upsert(case)
        cloud?.pushCase(case)
        addTimeline(id, "created", "Case opened", "Opened from a client inquiry.", lawyer.name)
        notifications.notify(lawyer.uid, NotificationType.CASE_UPDATE, "New case", "Case created from inquiry: ${inquiry.subject}", "case_room/$id")
        return id
    }

    suspend fun deleteCase(caseId: String) {
        caseDao.get(caseId)?.let {
            caseDao.softDelete(caseId)
            cloud?.pushCase(it.copy(isDeleted = true))
            addTimeline(caseId, "status", "Case removed", "Case archived.", "Advocate")
            if (it.clientUid.isNotBlank()) {
                notifications.notify(it.clientUid, NotificationType.CASE_UPDATE, "Case removed", "The advocate removed this case.", "case_room/$caseId")
            }
        }
    }

    suspend fun addHearingFull(caseId: String, courtNumber: String, itemNumber: Int, date: Long, time: String, purpose: String, judge: String): Result<Unit> {
        if (hearingDao.countDuplicate(caseId, date, time) > 0) {
            return Result.failure(IllegalArgumentException("A hearing already exists at this date & time"))
        }
        val case = caseDao.get(caseId) ?: return Result.failure(IllegalArgumentException("Case not found"))
        hearingDao.insert(
            HearingEntity(
                id = UUID.randomUUID().toString(), caseId = caseId, caseNumber = case.caseNumber,
                courtNumber = courtNumber, itemNumber = itemNumber, stage = case.stage.name,
                hearingDate = date, hearingTime = time, purpose = purpose, judgeName = judge
            )
        )
        val updated = case.copy(nextHearingDate = date, updatedAt = System.currentTimeMillis())
        caseDao.upsert(updated)
        cloud?.pushCase(updated)
        addTimeline(caseId, "hearing", "Hearing scheduled", "Hearing on $time in $courtNumber (item #$itemNumber).", "Advocate")
        notifications.notify(case.clientUid, NotificationType.HEARING_REMINDER, "Hearing scheduled", "$time · $courtNumber · Item #$itemNumber", "case_room/$caseId")
        return Result.success(Unit)
    }

    suspend fun logOrder(hearingId: String, status: HearingStatus, order: String) {
        hearingDao.logOrder(hearingId, status, order)
    }

    suspend fun verifyDocument(docId: String, status: DocumentStatus) {
        documentDao.setStatus(docId, status)
    }

    suspend fun addInvoice(
        lawyer: UserEntity,
        caseId: String?,
        caseNumber: String,
        clientName: String,
        appearanceFee: Double,
        draftingFee: Double,
        clerkage: Double,
        notes: String?
    ): String {
        val id = UUID.randomUUID().toString()
        val invoice = InvoiceEntity(
            id = id, caseId = caseId, lawyerUid = lawyer.uid,
            invoiceNumber = "INV-${(1000..9999).random()}", caseNumber = caseNumber, clientName = clientName,
            appearanceFee = appearanceFee, draftingFee = draftingFee, clerkageMisc = clerkage,
            totalAmount = appearanceFee + draftingFee + clerkage, notes = notes
        )
        invoiceDao.upsert(invoice)
        cloud?.pushInvoice(invoice)
        return id
    }

    suspend fun markInvoicePaid(invoiceId: String, method: PaymentMethod, upiRef: String?) {
        invoiceDao.get(invoiceId)?.let {
            val updated = it.copy(status = InvoiceStatus.PAID, paidVia = method, upiRef = upiRef)
            invoiceDao.upsert(updated)
            cloud?.pushInvoice(updated)
        }
    }

    private suspend fun addTimeline(caseId: String, type: String, title: String, desc: String, actor: String) {
        timelineDao.insert(
            TimelineEventEntity(
                id = UUID.randomUUID().toString(), caseId = caseId, type = type,
                titleEn = title, descriptionEn = desc, actor = actor
            )
        )
    }
}
