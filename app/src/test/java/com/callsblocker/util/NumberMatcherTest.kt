package com.callsblocker.util

import com.callsblocker.data.BlockedEntry
import com.callsblocker.data.CallAction
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NumberMatcherTest {

    @Test
    fun matches_exactMatch() {
        val entry = BlockedEntry(
            pattern = "+3933312345678",
            isPrefix = false,
            label = "Test",
            action = CallAction.BLOCK
        )
        assertTrue(NumberMatcher.matches("+3933312345678", entry))
    }

    @Test
    fun matches_exactMatchDifferentFormat() {
        val entry = BlockedEntry(
            pattern = "+3933312345678",
            isPrefix = false,
            label = "Test",
            action = CallAction.BLOCK
        )
        assertTrue(NumberMatcher.matches("0039 333 123 45678", entry))
    }

    @Test
    fun matches_exactMatchDoesNotMatch() {
        val entry = BlockedEntry(
            pattern = "+3933312345678",
            isPrefix = false,
            label = "Test",
            action = CallAction.BLOCK
        )
        assertFalse(NumberMatcher.matches("+3933312345677", entry))
    }

    @Test
    fun matches_prefixMatch() {
        val entry = BlockedEntry(
            pattern = "+393331234",
            isPrefix = true,
            label = "Spam prefix",
            action = CallAction.BLOCK
        )
        assertTrue(NumberMatcher.matches("+3933312340000", entry))
    }

    @Test
    fun matches_prefixMatchEndingDifferent() {
        val entry = BlockedEntry(
            pattern = "+393331234",
            isPrefix = true,
            label = "Spam prefix",
            action = CallAction.BLOCK
        )
        assertTrue(NumberMatcher.matches("+3933312349999", entry))
    }

    @Test
    fun matches_prefixMatchShorterCallDoesNotMatch() {
        val entry = BlockedEntry(
            pattern = "+393331234",
            isPrefix = true,
            label = "Spam prefix",
            action = CallAction.BLOCK
        )
        assertFalse(NumberMatcher.matches("+39333123", entry))
    }

    @Test
    fun matches_prefixMatchExactPrefix() {
        val entry = BlockedEntry(
            pattern = "+393331234",
            isPrefix = true,
            label = "Spam prefix",
            action = CallAction.BLOCK
        )
        assertTrue(NumberMatcher.matches("+393331234", entry))
    }

    @Test
    fun matches_prefixDoesNotMatchDifferentPrefix() {
        val entry = BlockedEntry(
            pattern = "+393331234",
            isPrefix = true,
            label = "Spam prefix",
            action = CallAction.BLOCK
        )
        assertFalse(NumberMatcher.matches("+3933312350000", entry))
    }

    @Test
    fun matches_formattedNumbers() {
        val entry = BlockedEntry(
            pattern = "393331234",
            isPrefix = true,
            label = "Test",
            action = CallAction.SILENCE
        )
        assertTrue(NumberMatcher.matches("00393331234567", entry))
    }

    @Test
    fun matches_allowAction() {
        val entry = BlockedEntry(
            pattern = "+39021234567",
            isPrefix = false,
            label = "Watch",
            action = CallAction.ALLOW
        )
        assertTrue(NumberMatcher.matches("+39021234567", entry))
    }

    @Test
    fun matches_emptyIncoming() {
        val entry = BlockedEntry(
            pattern = "+393331234",
            isPrefix = true,
            label = "Test",
            action = CallAction.BLOCK
        )
        assertFalse(NumberMatcher.matches("", entry))
    }

    @Test
    fun matches_emptyPattern() {
        val entry = BlockedEntry(
            pattern = "",
            isPrefix = true,
            label = "Test",
            action = CallAction.BLOCK
        )
        assertFalse(NumberMatcher.matches("+393331234567", entry))
    }

    @Test
    fun matches_differentCountryPrefix() {
        val entry = BlockedEntry(
            pattern = "+393331234",
            isPrefix = true,
            label = "Test",
            action = CallAction.BLOCK
        )
        assertFalse(NumberMatcher.matches("+493331234567", entry))
    }

    @Test
    fun matches_missingCountryCodeInPattern() {
        val entry = BlockedEntry(
            pattern = "3331234567",
            isPrefix = false,
            label = "Test",
            action = CallAction.BLOCK
        )
        assertTrue(NumberMatcher.matches("+393331234567", entry))
    }

    @Test
    fun matches_missingCountryCodeInIncoming() {
        val entry = BlockedEntry(
            pattern = "+393331234567",
            isPrefix = false,
            label = "Test",
            action = CallAction.BLOCK
        )
        assertTrue(NumberMatcher.matches("3331234567", entry))
    }

    @Test
    fun matches_plusVsDoubleZero() {
        val entry = BlockedEntry(
            pattern = "+393331234567",
            isPrefix = false,
            label = "Test",
            action = CallAction.BLOCK
        )
        // Should match with 00
        assertTrue("Should match with 00 prefix", NumberMatcher.matches("00393331234567", entry))
        // Should match with +
        assertTrue("Should match with + prefix", NumberMatcher.matches("+393331234567", entry))
    }

    @Test
    fun matches_prefixPlusVsDoubleZero() {
        val entry = BlockedEntry(
            pattern = "+393331234",
            isPrefix = true,
            label = "Test",
            action = CallAction.BLOCK
        )
        // Should match with 00
        assertTrue("Should match prefix with 00", NumberMatcher.matches("00393331234999", entry))
        // Should match with +
        assertTrue("Should match prefix with +", NumberMatcher.matches("+393331234999", entry))
    }

    @Test
    fun matches_crossPrefixConditionBranch() {
        val entry = BlockedEntry(
            pattern = "333123456",
            isPrefix = false,
            label = "Test",
            action = CallAction.BLOCK
        )
        // incoming starts with 39, pattern doesn't
        assertTrue(NumberMatcher.matches("39333123456", entry))
        
        val entry2 = BlockedEntry(
            pattern = "39333123456",
            isPrefix = false,
            label = "Test",
            action = CallAction.BLOCK
        )
        // pattern starts with 39, incoming doesn't
        assertTrue(NumberMatcher.matches("333123456", entry2))
    }
}
