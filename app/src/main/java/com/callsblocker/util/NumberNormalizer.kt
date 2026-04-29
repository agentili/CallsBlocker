package com.callsblocker.util

object NumberNormalizer {
    fun normalize(raw: String): String {
        if (raw.isBlank()) return ""

        val stripped = raw
            .replace(" ", "")
            .replace("-", "")
            .replace("(", "")
            .replace(")", "")
            .replace("+", "")
            .takeIf { it.isNotEmpty() } ?: return ""

        return when {
            stripped.startsWith("0039") -> stripped.substring(2)
            stripped.startsWith("39") && stripped.length > 2 -> stripped
            stripped.startsWith("00") -> stripped.substring(2)
            else -> stripped
        }
    }
}
