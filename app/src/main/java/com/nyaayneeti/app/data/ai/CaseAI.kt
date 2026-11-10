package com.nyaayneeti.app.data.ai

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/** Thin OpenAI-compatible chat client over NVIDIA NIM. Returns null when not configured or on failure. */
class NimChatClient(private val apiKey: String, private val model: String = "nvidia/llama-3.1-nemotron-70b-instruct") {
    private val client = OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS).readTimeout(60, TimeUnit.SECONDS).build()

    suspend fun chat(system: String, user: String): String? {
        if (apiKey.isBlank()) return null
        return try {
            withContext(Dispatchers.IO) {
                val messages = JSONArray()
                    .put(JSONObject().put("role", "system").put("content", system))
                    .put(JSONObject().put("role", "user").put("content", user))
                val payload = JSONObject().put("model", model).put("temperature", 0.3).put("max_tokens", 600).put("messages", messages)
                val request = Request.Builder()
                    .url("https://integrate.api.nvidia.com/v1/chat/completions")
                    .header("Authorization", "Bearer $apiKey")
                    .header("Content-Type", "application/json")
                    .post(payload.toString().toRequestBody("application/json".toMediaType()))
                    .build()
                val response = client.newCall(request).execute()
                if (!response.isSuccessful) return@withContext null
                val body = response.body?.string() ?: return@withContext null
                JSONObject(body).getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content").trim()
            }
        } catch (e: Exception) {
            null
        }
    }
}

class CaseAIClient(private val nim: NimChatClient) {

    suspend fun askMyCase(context: String, question: String): String {
        val system = "You are a legal assistant grounded ONLY on the case context provided. Answer in plain, simple language. If the answer is not in the context, say 'That information is not in the case record yet.' Keep it under 5 sentences."
        return nim.chat(system, "Case context:\n$context\n\nQuestion: $question") ?: fallbackAsk(question)
    }

    suspend fun summarizeOrder(orderText: String): String {
        val system = "Summarize this Indian court order in simple, plain language a layperson can understand. Keep it under 6 sentences."
        return nim.chat(system, orderText) ?: "AI summarization isn't available (add your NVIDIA NIM key). Order length: ${orderText.length} chars."
    }

    suspend fun lawyerCounsel(question: String): String {
        val system = "You are NYAAY COUNSEL, a private legal research assistant for advocates in India. Be concise, cite relevant legal principles, and note if something needs the advocate's professional judgment."
        return nim.chat(system, question) ?: "AI counsel isn't available (add your NVIDIA NIM key). Try the IPC→BNS converter for section lookups."
    }

    private fun fallbackAsk(q: String): String {
        val text = q.lowercase()
        return when {
            "hearing" in text || "next date" in text -> "Your next hearing details are in the Hearings tab of this case. AI is offline, so I can't read them here yet."
            "document" in text || "upload" in text -> "Check the Documents tab for the document checklist and upload status."
            "lawyer" in text || "advocate" in text -> "Your assigned advocate is shown in the Summary tab."
            "stage" in text || "status" in text -> "The current stage and status are shown in the Summary tab."
            else -> "I'm running in offline mode. Add your NVIDIA NIM API key to enable full 'Ask My Case' answers."
        }
    }
}
