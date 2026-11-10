package com.nyaayneeti.app.data.ai

/** Static IPC 1860 → BNS 2023 mapping for the converter tool. */
object IpcToBns {
    private val map = linkedMapOf(
        "302" to "103(1) — Murder",
        "304" to "105 — Culpable homicide not amounting to murder",
        "307" to "109 — Attempt to murder",
        "323" to "115(2) — Voluntarily causing hurt",
        "325" to "118(1) — Voluntarily causing grievous hurt",
        "354" to "74 — Assault or use of force to woman with intent to outrage modesty",
        "376" to "64 — Rape",
        "379" to "303(2) — Theft",
        "380" to "303(2) — Theft in dwelling house",
        "392" to "309(4) — Robbery",
        "406" to "316(2) — Criminal breach of trust",
        "420" to "318(4) — Cheating",
        "498A" to "85 — Cruelty by husband or relatives",
        "506" to "351(2) — Criminal intimidation",
        "509" to "79 — Word/gesture/act intended to insult modesty of a woman",
        "120B" to "61(2) — Criminal conspiracy",
        "34" to "3(5) — Common intention",
        "149" to "190 — Unlawful assembly"
    )

    fun lookup(section: String): String {
        val key = section.trim().uppercase().removePrefix("SEC").removePrefix("SECTION").trim()
        return map[key] ?: map.entries.firstOrNull { it.key == key }?.value ?: "No direct mapping found for Section $section."
    }

    fun allEntries(): List<Pair<String, String>> = map.toList()
}
