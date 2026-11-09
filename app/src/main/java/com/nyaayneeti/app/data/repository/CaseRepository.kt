package com.nyaayneeti.app.data.repository

import com.nyaayneeti.app.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class CaseRepository {
    private val _cases = MutableStateFlow<List<CaseFile>>(emptyList())
    val cases: StateFlow<List<CaseFile>> = _cases.asStateFlow()

    private val _timeline = MutableStateFlow<Map<String, List<CaseTimelineEvent>>>(emptyMap())
    val timeline: StateFlow<Map<String, List<CaseTimelineEvent>>> = _timeline.asStateFlow()

    private val _tasks = MutableStateFlow<Map<String, List<CaseTask>>>(emptyMap())
    val tasks: StateFlow<Map<String, List<CaseTask>>> = _tasks.asStateFlow()

    private val _hearings = MutableStateFlow<List<HearingItem>>(emptyList())
    val hearings: StateFlow<List<HearingItem>> = _hearings.asStateFlow()

    private val _notes = MutableStateFlow<Map<String, List<CaseNote>>>(emptyMap())
    val notes: StateFlow<Map<String, List<CaseNote>>> = _notes.asStateFlow()

    init {
        seedInitialDemoData()
    }

    private fun seedInitialDemoData() {
        val demoCase = CaseFile(
            id = "case-101",
            caseNumber = "CNR-MHMB01-002451-2024",
            title = "Priya Sharma vs Apex Real Estate Builders Pvt Ltd",
            clientName = "Priya Sharma",
            clientPhone = "+919999999999",
            lawyerId = "lawyer-1",
            lawyerName = "Adv. Ramesh Kulkarni",
            court = "Bombay High Court — Court Room 4",
            matterType = "RERA & Property Delay Dispute",
            stage = "Arguments on Interim Relief",
            priority = CasePriority.URGENT,
            status = CaseStatus.ACTIVE,
            nextHearingDate = "2026-11-15",
            profile = CaseProfile(
                matterType = "Consumer / RERA Property Possession",
                legalArea = "Real Estate Regulatory Authority (RERA)",
                location = "Mumbai, Maharashtra",
                facts = listOf(
                    "Purchased 2BHK in Bandra West project scheduled for handover Dec 2023.",
                    "Developer delayed possession by 28 months without statutory notice.",
                    "Demand notices sent via Registered AD with zero resolution."
                ),
                missingInfo = listOf("Copy of original Allotment Letter dated May 2021"),
                documentsAvailable = listOf("Agreement for Sale", "Bank Payment Receipts"),
                documentsRequired = listOf("Encumbrance Certificate", "Allotment Letter"),
                urgency = "Urgent",
                confidenceScore = 0.94,
                summaryText = "Matter involves builder-buyer agreement breach under Section 18 of RERA Act 2016 seeking delayed interest compensation."
            )
        )

        _cases.value = listOf(demoCase)

        _timeline.value = mapOf(
            demoCase.id to listOf(
                CaseTimelineEvent(
                    id = "tl-1",
                    caseId = demoCase.id,
                    type = "intake",
                    titleEn = "AI Structured Intake Completed",
                    titleHi = "एआई संरचित केस इनटेक पूर्ण",
                    descriptionEn = "Matter synthesized and legal dossier generated via Nyay AI.",
                    descriptionHi = "न्याय एआई द्वारा मामला संश्लेषित और कानूनी डॉसियर तैयार किया गया।",
                    eventDate = "01 Nov 2026",
                    actor = "Client"
                ),
                CaseTimelineEvent(
                    id = "tl-2",
                    caseId = demoCase.id,
                    type = "notice",
                    titleEn = "Legal Notice Served on Opposing Party",
                    titleHi = "विपक्षी पक्ष को कानूनी नोटिस तामील",
                    descriptionEn = "15-day statutory cure notice issued via Advocate.",
                    descriptionHi = "अधिवक्ता के माध्यम से 15-दिवसीय वैधानिक नोटिस भेजा गया।",
                    eventDate = "04 Nov 2026",
                    actor = "Adv. Ramesh Kulkarni"
                )
            )
        )

        _tasks.value = mapOf(
            demoCase.id to listOf(
                CaseTask(
                    id = "t-1",
                    caseId = demoCase.id,
                    titleEn = "Upload RERA Payment Ledger Receipts",
                    titleHi = "रेरा भुगतान रसीदें अपलोड करें",
                    assignedTo = "client",
                    isCompleted = false,
                    dueDate = "12 Nov 2026"
                ),
                CaseTask(
                    id = "t-2",
                    caseId = demoCase.id,
                    titleEn = "Draft Interim Stay Application & Affidavits",
                    titleHi = "अंतरिम स्थगन आवेदन और हलफनामा तैयार करें",
                    assignedTo = "lawyer",
                    isCompleted = true,
                    dueDate = "10 Nov 2026"
                )
            )
        )

        _hearings.value = listOf(
            HearingItem(
                id = "h-1",
                caseId = demoCase.id,
                caseNumber = demoCase.caseNumber,
                courtNumber = "Court Hall 4",
                itemNumber = 12,
                stage = "Arguments on Injunction",
                hearingDate = "15 Nov 2026",
                hearingTime = "10:30 AM",
                purpose = "Interim relief hearing"
            ),
            HearingItem(
                id = "h-2",
                caseId = "case-102",
                caseNumber = "CNR-MHMB01-008912-2024",
                courtNumber = "Court Hall 2",
                itemNumber = 5,
                stage = "Cross Examination",
                hearingDate = "15 Nov 2026",
                hearingTime = "11:45 AM",
                purpose = "Petitioner witness deposition"
            )
        )
    }

    fun createCaseFromProfile(
        clientName: String,
        clientPhone: String,
        profile: CaseProfile
    ): String {
        val newId = "case-${UUID.randomUUID().toString().take(8)}"
        val caseNumber = "CNR-MH-${(1000..9999).random()}-${(100000..999999).random()}-2026"
        val newCase = CaseFile(
            id = newId,
            caseNumber = caseNumber,
            title = "$clientName Matter — ${profile.matterType}",
            clientName = clientName,
            clientPhone = clientPhone,
            lawyerName = "Adv. Verified Counsel",
            matterType = profile.matterType,
            stage = "Case Intake & Drafting",
            priority = if (profile.urgency.contains("urgent", ignoreCase = true)) CasePriority.URGENT else CasePriority.NORMAL,
            profile = profile
        )

        _cases.value = _cases.value + newCase

        _timeline.value = _timeline.value + (newId to listOf(
            CaseTimelineEvent(
                id = "tl-init-$newId",
                caseId = newId,
                type = "intake",
                titleEn = "Case Room Initialized",
                titleHi = "केस रूम प्रारंभ किया गया",
                descriptionEn = "AI intake structured ${profile.facts.size} facts and ${profile.documentsRequired.size} required documents.",
                descriptionHi = "एआई इनटेक ने ${profile.facts.size} तथ्य और आवश्यक दस्तावेज तैयार किए।",
                eventDate = "Today",
                actor = "Nyay AI Engine"
            )
        ))

        return newId
    }

    fun toggleTask(caseId: String, taskId: String) {
        val caseTasks = _tasks.value[caseId] ?: return
        val updated = caseTasks.map { task ->
            if (task.id == taskId) task.copy(isCompleted = !task.isCompleted) else task
        }
        _tasks.value = _tasks.value + (caseId to updated)
    }

    fun addNote(caseId: String, content: String, author: String) {
        val existing = _notes.value[caseId] ?: emptyList()
        val newNote = CaseNote(
            id = "note-${UUID.randomUUID().toString().take(6)}",
            caseId = caseId,
            content = content,
            authorName = author
        )
        _notes.value = _notes.value + (caseId to (existing + newNote))
    }
}
