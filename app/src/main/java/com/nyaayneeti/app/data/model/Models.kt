package com.nyaayneeti.app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CaseProfile(
    val matterType: String,
    val legalArea: String,
    val location: String,
    val facts: List<String> = emptyList(),
    val missingInfo: List<String> = emptyList(),
    val documentsAvailable: List<String> = emptyList(),
    val documentsRequired: List<String> = emptyList(),
    val urgency: String = "Normal",
    val confidenceScore: Double = 0.85,
    val summaryText: String = "",
    val generatedAt: Long = System.currentTimeMillis()
)

@Serializable
data class IntakeChatMessage(
    val id: String,
    val isUser: Boolean,
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)
