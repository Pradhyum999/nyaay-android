package com.nyaayneeti.app

import com.nyaayneeti.app.data.model.SubscriptionTier
import com.nyaayneeti.app.data.repository.BillingRepository
import com.nyaayneeti.app.data.repository.RuleBasedIntakeEngine
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class UnitTest {

    @Test
    fun intakeDetectsRera() = runTest {
        val p = RuleBasedIntakeEngine().analyze("My builder delayed flat possession by a year")
        assertTrue(p.legalArea.contains("RERA"))
        assertTrue(p.documentsRequired.isNotEmpty())
    }

    @Test
    fun intakeDetectsUrgent() = runTest {
        val p = RuleBasedIntakeEngine().analyze("urgent help needed for cheque bounce")
        assertEquals("Urgent", p.urgency)
    }

    @Test
    fun tierPricingIsSane() {
        assertEquals(0.0, BillingRepository.tierPrice(SubscriptionTier.FREE), 0.0)
        assertTrue(BillingRepository.tierPrice(SubscriptionTier.PREMIUM) > BillingRepository.tierPrice(SubscriptionTier.PROFESSIONAL))
    }
}
