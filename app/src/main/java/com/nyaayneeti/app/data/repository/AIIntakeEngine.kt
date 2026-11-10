package com.nyaayneeti.app.data.repository

import com.nyaayneeti.app.data.model.CaseProfile

interface AIIntakeEngine {
    suspend fun analyze(userText: String): CaseProfile
}

/**
 * Offline, deterministic intake. Maps keywords to legal area and required documents.
 * No network needed — always works.
 */
class RuleBasedIntakeEngine : AIIntakeEngine {
    override suspend fun analyze(userText: String): CaseProfile {
        val text = userText.lowercase()
        val legalArea = when {
            text.contains("property") || text.contains("builder") || text.contains("flat") || text.contains("land") ->
                "Real Estate (RERA) & Property Law"
            text.contains("cheque") || text.contains("money") || text.contains("loan") || text.contains("recovery") ->
                "Negotiable Instruments & Debt Recovery"
            text.contains("divorce") || text.contains("custody") || text.contains("maintenance") || text.contains("marriage") ->
                "Family & Matrimonial Law"
            text.contains("theft") || text.contains("fir") || text.contains("bail") || text.contains("crime") ->
                "Criminal Law"
            text.contains("job") || text.contains("salary") || text.contains("termination") || text.contains("employer") ->
                "Labour & Employment Law"
            else -> "Civil Dispute & Consumer Rights"
        }
        val docs = when {
            legalArea.contains("RERA") -> listOf("Allotment letter / Agreement to Sell", "Payment receipts", "Possession correspondence")
            legalArea.contains("Debt") -> listOf("Cheque copy / Loan agreement", "Bank statement", "Demand notice")
            legalArea.contains("Family") -> listOf("Marriage certificate", "Income proof", "Prior correspondence")
            legalArea.contains("Criminal") -> listOf("FIR copy", "Bail documents", "Identity proof")
            legalArea.contains("Labour") -> listOf("Appointment letter", "Salary slips", "Termination letter")
            else -> listOf("Relevant agreement/contract", "Proof of transaction", "Identity proof")
        }
        return CaseProfile(
            matterType = legalArea,
            legalArea = legalArea,
            location = "India (District Jurisdiction)",
            facts = listOf(userText, "Matter reported by citizen seeking formal legal recourse."),
            missingInfo = listOf("Date of cause of action", "Opposing party details"),
            documentsAvailable = emptyList(),
            documentsRequired = docs,
            urgency = if (text.contains("urgent") || text.contains("emergency")) "Urgent" else "Normal",
            confidenceScore = 0.9,
            summaryText = "Intake synthesis: $userText"
        )
    }
}
