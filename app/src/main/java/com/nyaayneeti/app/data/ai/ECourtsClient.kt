package com.nyaayneeti.app.data.ai

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

/**
 * Live court case search & verification via the eCourts India API.
 * Configure the base URL + key in gradle.properties (ECOURTS_BASE_URL / ECOURTS_API_KEY).
 */
class ECourtsClient(
    private val apiKey: String,
    private val baseUrl: String
) {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    val isConfigured: Boolean get() = apiKey.isNotBlank() && baseUrl.isNotBlank()

    suspend fun searchCase(caseNumber: String, year: Int): Result<String> = withContext(Dispatchers.IO) {
        if (!isConfigured) return@withContext Result.failure(IllegalStateException("eCourts API not configured"))
        try {
            val url = "$baseUrl/api/case/search?caseNumber=$caseNumber&year=$year"
            val request = Request.Builder()
                .url(url)
                .header("X-API-Key", apiKey)
                .get()
                .build()
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) return@withContext Result.failure(IllegalStateException("eCourts HTTP ${response.code}"))
            Result.success(response.body?.string() ?: "")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
