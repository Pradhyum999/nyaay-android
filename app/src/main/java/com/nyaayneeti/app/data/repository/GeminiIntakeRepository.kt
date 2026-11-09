package com.nyaayneeti.app.data.repository

import com.nyaayneeti.app.data.model.CaseProfile
import com.nyaayneeti.app.data.model.IntakeChatMessage
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class GeminiIntakeRepository {
    private val _messages = MutableStateFlow<List<IntakeChatMessage>>(
        listOf(
            IntakeChatMessage(
                id = "msg-0",
                isUser = false,
                message = "Namaste. I am your Nyay AI Judicial Assistant. Tell me what happened in simple everyday words — which state or city are you in, and what dispute or assistance do you need?"
            )
        )
    )
    val messages: StateFlow<List<IntakeChatMessage>> = _messages.asStateFlow()

    private val _currentProfile = MutableStateFlow<CaseProfile?>(null)
    val currentProfile: StateFlow<CaseProfile?> = _currentProfile.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    suspend fun sendMessage(userText: String): CaseProfile? {
        val userMsg = IntakeChatMessage(
            id = UUID.randomUUID().toString(),
            isUser = true,
            message = userText
        )
        _messages.value = _messages.value + userMsg
        _isGenerating.value = true

        // Simulate intelligent AI parsing or call Gemini backend
        delay(1200)

        val legalArea = when {
            userText.contains("property", ignoreCase = true) || userText.contains("builder", ignoreCase = true) || userText.contains("flat", ignoreCase = true) -> "Real Estate (RERA) & Property Law"
            userText.contains("cheque", ignoreCase = true) || userText.contains("money", ignoreCase = true) || userText.contains("loan", ignoreCase = true) -> "Negotiable Instruments & Debt Recovery"
            userText.contains("divorce", ignoreCase = true) || userText.contains("custody", ignoreCase = true) || userText.contains("maintenance", ignoreCase = true) -> "Family & Matrimonial Law"
            else -> "Civil Dispute & Consumer Rights"
        }

        val facts = listOf(
            userText,
            "Matter reported by citizen seeking formal legal recourse and verified advocate representation."
        )

        val synthesizedProfile = CaseProfile(
            matterType = legalArea,
            legalArea = legalArea,
            location = "India (District Jurisdiction)",
            facts = facts,
            missingInfo = listOf("Date of cause of action", "Opposing party registered address"),
            documentsAvailable = listOf("Relevant transaction statements / proof"),
            documentsRequired = listOf("Formal Agreement / Proof of Payment", "Prior written communications / notices"),
            urgency = if (userText.contains("urgent", ignoreCase = true) || userText.contains("emergency", ignoreCase = true)) "Urgent" else "Normal",
            confidenceScore = 0.92,
            summaryText = "Intake synthesis: $userText. Recommend drafting formal legal notice and scheduling advocate consultation."
        )

        _currentProfile.value = synthesizedProfile

        val botReply = IntakeChatMessage(
            id = UUID.randomUUID().toString(),
            isUser = false,
            message = "I have analyzed your situation and synthesized your legal profile for $legalArea. You can review the Case Profile card above and tap 'Create Case Room' to assign a verified advocate."
        )
        _messages.value = _messages.value + botReply
        _isGenerating.value = false

        return synthesizedProfile
    }
}
