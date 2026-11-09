package com.nyaayneeti.app

import com.nyaayneeti.app.data.model.CasePriority
import com.nyaayneeti.app.data.model.CaseProfile
import com.nyaayneeti.app.data.repository.AuthRepository
import com.nyaayneeti.app.data.repository.CaseRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class CaseRepositoryTest {

    private lateinit var caseRepository: CaseRepository
    private lateinit var authRepository: AuthRepository

    @Before
    fun setup() {
        caseRepository = CaseRepository()
        authRepository = AuthRepository()
    }

    @Test
    fun testInitialSeedDataLoadsSuccessfully() {
        val initialCases = caseRepository.cases.value
        assertTrue("Seed cases should not be empty", initialCases.isNotEmpty())
        assertEquals("case-101", initialCases.first().id)
        assertEquals(CasePriority.URGENT, initialCases.first().priority)
    }

    @Test
    fun testCreateCaseFromProfileInitializesTimeline() {
        val testProfile = CaseProfile(
            matterType = "Property Handover Delay",
            legalArea = "RERA",
            location = "Delhi NCR",
            facts = listOf("Booking date 2022", "Possession delayed by 18 months"),
            documentsRequired = listOf("Allotment Letter", "Bank Receipts"),
            urgency = "Urgent",
            confidenceScore = 0.95,
            summaryText = "Consumer dispute for RERA delayed possession."
        )

        val newCaseId = caseRepository.createCaseFromProfile(
            clientName = "Rohit Verma",
            clientPhone = "+919876543210",
            profile = testProfile
        )

        assertNotNull("Generated case id must not be null", newCaseId)
        val cases = caseRepository.cases.value
        val created = cases.find { it.id == newCaseId }
        assertNotNull("Created case must exist in repository", created)
        assertEquals("Rohit Verma", created?.clientName)
        assertEquals(CasePriority.URGENT, created?.priority)

        // Verify Timeline event initialized
        val timelineEvents = caseRepository.timeline.value[newCaseId]
        assertNotNull("Timeline must be initialized for new case", timelineEvents)
        assertTrue("Timeline should contain intake event", timelineEvents!!.any { it.type == "intake" })
    }

    @Test
    fun testToggleCaseTask() {
        val caseId = "case-101"
        val taskId = "t-1"

        val initialTask = caseRepository.tasks.value[caseId]?.find { it.id == taskId }
        assertNotNull("Task t-1 must exist", initialTask)
        assertFalse("Task should initially be incomplete", initialTask!!.isCompleted)

        caseRepository.toggleTask(caseId, taskId)
        val toggledTask = caseRepository.tasks.value[caseId]?.find { it.id == taskId }
        assertTrue("Task should now be completed", toggledTask!!.isCompleted)
    }

    @Test
    fun testCauseListHearingsContainItemNumbers() {
        val hearings = caseRepository.hearings.value
        assertTrue("Hearings should exist", hearings.isNotEmpty())
        assertTrue("Each hearing must have a valid item number > 0", hearings.all { it.itemNumber > 0 })
    }
}
