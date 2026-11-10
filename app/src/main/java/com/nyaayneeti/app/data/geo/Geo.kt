package com.nyaayneeti.app.data.geo

import com.nyaayneeti.app.data.matching.cleanList

object LegalCategories {
    val all = listOf(
        "Criminal" to "⚖️",
        "Civil" to "🏛️",
        "Family" to "👨‍👩‍👧",
        "Property & RERA" to "🏠",
        "Consumer" to "🛒",
        "Corporate" to "💼",
        "Labour" to "👷",
        "Tax" to "💰",
        "Cyber" to "💻",
        "Cheque Bounce" to "✍️",
        "Matrimonial" to "💍",
        "Immigration" to "✈️"
    )

    fun iconFor(area: String): String = all.firstOrNull { it.first.equals(area, true) }?.second ?: "⚖️"

    fun matches(specializations: String, category: String): Boolean {
        val areas = specializations.cleanList()
        return areas.any { a ->
            a.equals(category, true) || category.contains(a, true) || a.contains(category, true) ||
                sharesKeyword(a, category)
        }
    }

    private fun sharesKeyword(a: String, b: String): Boolean {
        val wordsA = a.split(Regex("[\\s,&/\\-]+")).filter { it.length > 3 }
        val wordsB = b.split(Regex("[\\s,&/\\-]+")).filter { it.length > 3 }.toSet()
        return wordsA.any { it in wordsB }
    }
}

object CityCoords {
    private val map = mapOf(
        "Mumbai" to (19.0760 to 72.8777),
        "Pune" to (18.5204 to 73.8567),
        "Delhi" to (28.6139 to 77.2090),
        "Bengaluru" to (12.9716 to 77.5946),
        "Chennai" to (13.0827 to 80.2707),
        "Hyderabad" to (17.3850 to 78.4867),
        "Kolkata" to (22.5726 to 88.3639),
        "Ahmedabad" to (23.0225 to 72.5714)
    )

    fun coords(city: String?): Pair<Double, Double>? =
        city?.let { c -> map.entries.firstOrNull { c.contains(it.key, true) || it.key.contains(c, true) }?.value }
}

object GeoUtils {
    fun distanceKm(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        val r = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLng = Math.toRadians(lng2 - lng1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
            Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(dLng / 2) * Math.sin(dLng / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return r * c
    }
}
