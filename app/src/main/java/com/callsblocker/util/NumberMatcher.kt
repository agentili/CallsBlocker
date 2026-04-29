package com.callsblocker.util

import com.callsblocker.data.BlockedEntry

object NumberMatcher {
    fun matches(incomingRaw: String, entry: BlockedEntry): Boolean {
        val incomingNorm = NumberNormalizer.normalize(incomingRaw)
        val patternNorm = NumberNormalizer.normalize(entry.pattern)

        if (incomingNorm.isEmpty() || patternNorm.isEmpty()) return false

        // If the pattern doesn't start with 39 but the incoming does, remove 39 from incoming for comparison
        // Or vice versa. This handles numbers saved without country code.
        if (incomingNorm.startsWith("39") && !patternNorm.startsWith("39")) {
            val potentialMatch = incomingNorm.substring(2)
            if (doMatch(potentialMatch, patternNorm, entry.isPrefix)) return true
        }
        
        if (patternNorm.startsWith("39") && !incomingNorm.startsWith("39")) {
            val potentialMatch = patternNorm.substring(2)
            if (doMatch(incomingNorm, potentialMatch, entry.isPrefix)) return true
        }

        return doMatch(incomingNorm, patternNorm, entry.isPrefix)
    }

    private fun doMatch(incoming: String, pattern: String, isPrefix: Boolean): Boolean {
        return if (isPrefix) {
            incoming.startsWith(pattern)
        } else {
            incoming == pattern
        }
    }
}
