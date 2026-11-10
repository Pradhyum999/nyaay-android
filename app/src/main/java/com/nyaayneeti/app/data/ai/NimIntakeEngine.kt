package com.nyaayneeti.app.data.ai

import com.nyaayneeti.app.data.model.CaseProfile
import com.nyaayneeti.app.data.repository.AIIntakeEngine
import com.nyaayneeti.app.data.repository.RuleBasedIntakeEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * Real AI intake via NVIDIA NIM (OpenAI-compatible chat completions).
 * Falls back to the offline rule-based engine whenever the key is missing or the call fails.
 */
class NimIntakeEngine(
    private val apiKey: String,
    private val model: String = "nvidia/llama-3.1-nemotron-70b-instruct"
) : AIIntakeEngine {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()
    private val fallback = RuleBasedIntakeEngine()

    override suspend fun analyze(userText: String): CaseProfile {
        if (apiKey.isBlank()) return fallback.analyze(userText)
        return try {
            callNim(userText)
        } catch (e: Exception) {
            null
        } ?: fallback.analyze(userText)
    }

    private suspend fun callNim(userText: String): CaseProfile? = withContext(Dispatchers.IO) {
        val system = "You are an Indian legal intake assistant. Classify the user's matter. Reply ONLY with valid JSON: {\"legalArea\":\"...\",\"matterType\":\"...\",\"urgency\":\"Normal or Urgent\",\"documentsRequired\":[\"...\"],\"summaryText\":\"...\"}"
        val messages = JSONArray()
            .put(JSONObject().put("role", "system").put("content", system))
            .put(JSONObject().put("role", "user").put("content", userText))
        val payload = JSONObject()
            .put("model", model)
            .put("temperature", 0.2)
            .put("max_tokens", 400)
            .put("messages", messages)

        val request = Request.Builder()
            .url("https://integrate.api.nvidia.com/v1/chat/completions")
            .header("Authorization", "Bearer $apiKey")
            .header("Content-Type", "application/json")
            .post(payload.toString().toRequestBody("application/json".toMediaType()))
            .build()

        val response = client.newCall(request).execute()
        if (!response.isSuccessful) return@withContext null
        val body = response.body?.string() ?: return@withContext null
        parseNim(body, userText)
    }

    private fun parseNim(body: String, userText: String): CaseProfile? {
        return try {
            val root = JSONObject(body)
            val content = root.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content")
            val start = content.indexOf('{')
            val end = content.lastIndexOf('}')
            if (start < 0 || end <= start) return null
            val json = JSONObject(content.substring(start, end + 1))
            val docs = json.optJSONArray("documentsRequired")?.let { arr ->
                (0 until arr.length()).map { arr.getString(it) }
            } ?: emptyList()
            val legalArea = json.optString("legalArea", "Civil Dispute & Consumer Rights")
            CaseProfile(
                matterType = json.optString("matterType", legalArea),
                legalArea = legalArea,
                location = "India",
                facts = listOf(userText),
                missingInfo = listOf("Date of cause of action", "Opposing party details"),
                documentsRequired = docs,
                urgency = if (json.optString("urgency", "Normal").contains("Urgent", true)) "Urgent" else "Normal",
                confidenceScore = 0.93,
                summaryText = json.optString("summaryText", "Intake synthesis via AI.")
            )
        } catch (e: Exception) {
            null
        }
    }
}
