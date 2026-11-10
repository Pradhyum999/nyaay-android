package com.nyaayneeti.app.data.matching

import com.nyaayneeti.app.data.model.CaseProfile
import com.nyaayneeti.app.data.model.UserEntity

data class LawyerMatch(
    val lawyer: UserEntity,
    val score: Int,
    val reasons: List<String>
)

/**
 * Objective verified-profile matching (BCI-safe). We never rank "best lawyer";
 * we explain WHY a verified profile is relevant to the client's matter.
 */
object LawyerMatcher {

    fun match(profile: CaseProfile?, lawyers: List<UserEntity>, limit: Int = 5): List<LawyerMatch> {
        if (profile == null) return lawyers.take(limit).map { LawyerMatch(it, 0, emptyList()) }

        val targetArea = normalize(profile.legalArea)
        val targetMatter = normalize(profile.matterType)
        val targetLocation = normalize(profile.location)

        return lawyers.map { lawyer ->
            var score = 0
            val reasons = mutableListOf<String>()

            // 1. Practice area relevance — up to 45
            val areas = lawyer.specializations.cleanList().map(::normalize)
            val areaHit = areas.any { a ->
                a.isNotEmpty() && (targetArea.contains(a) || a.contains(targetArea) ||
                    targetMatter.contains(a) || a.contains(targetMatter) || sharesKeyword(a, targetArea))
            }
            if (areaHit) { score += 45; reasons += "Practices in a relevant area" }

            // 2. Jurisdiction / location — up to 20
            if (targetLocation.isNotEmpty() &&
                (normalize(lawyer.state ?: "").contains(targetLocation) ||
                    targetLocation.contains(normalize(lawyer.state ?: "")) ||
                    normalize(lawyer.city ?: "").contains(targetLocation) ||
                    targetLocation.contains(normalize(lawyer.city ?: "")))
            ) {
                score += 20; reasons += "Practices in your jurisdiction"
            }

            // 3. Experience — up to 20
            when {
                lawyer.experienceYears >= 15 -> { score += 20; reasons += "15+ years of standing" }
                lawyer.experienceYears >= 10 -> { score += 15; reasons += "10+ years of standing" }
                lawyer.experienceYears >= 5 -> { score += 10; reasons += "5+ years of standing" }
                lawyer.experienceYears > 0 -> score += 5
            }

            // 4. Language compatibility — up to 10
            val langs = lawyer.languages.cleanList().map(::normalize)
            if (langs.isEmpty() || langs.any { it == "english" || it == "hindi" || it == "marathi" }) score += 5
            if (targetLocation.isNotEmpty() && langs.any { targetLocation.contains(it) }) {
                score += 5; reasons += "Speaks a relevant regional language"
            }

            // 5. Verified — up to 5
            if (lawyer.verificationStatus == com.nyaayneeti.app.data.model.VerificationStatus.VERIFIED) score += 3
            if (lawyer.reviewCount > 0) { score += 2; reasons += "Has reviewed case history" }

            LawyerMatch(lawyer, score.coerceAtMost(100), reasons)
        }.sortedByDescending { it.score }.take(limit)
    }

    private fun normalize(s: String): String = s.lowercase().trim()

    private fun sharesKeyword(a: String, b: String): Boolean {
        val stop = setOf("and", "the", "of", "dispute", "matter", "case", "law", "legal", "rights")
        val wordsA = a.split(Regex("[\\s,/&\\-]+")).filter { it.length > 3 && it !in stop }
        val wordsB = b.split(Regex("[\\s,/&\\-]+")).filter { it.length > 3 && it !in stop }.toSet()
        return wordsA.any { it in wordsB }
    }
}

/** Parses the JSON-list string "[a, b]" into a plain list. */
fun String.cleanList(): List<String> =
    trim().removeSurrounding("[", "]").split(",").map { it.trim().trim('"').trim('\'') }.filter { it.isNotEmpty() }
