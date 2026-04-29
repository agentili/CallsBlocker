package com.callsblocker.util

import org.junit.Assert.assertEquals
import org.junit.Test

class NumberNormalizerTest {

    @Test
    fun normalize_removesSpaces() {
        assertEquals("393331234567", NumberNormalizer.normalize("+39 333 123 4567"))
    }

    @Test
    fun normalize_removesDashes() {
        assertEquals("393331234567", NumberNormalizer.normalize("+39-333-123-4567"))
    }

    @Test
    fun normalize_removesParentheses() {
        assertEquals("393331234567", NumberNormalizer.normalize("+39 (333) 123-4567"))
    }

    @Test
    fun normalize_convertsItalianPrefix0039to39() {
        assertEquals("393331234567", NumberNormalizer.normalize("00393331234567"))
    }

    @Test
    fun normalize_convertsItalianPrefix00to39() {
        val result = NumberNormalizer.normalize("00393331234567")
        assertEquals("393331234567", result)
    }

    @Test
    fun normalize_keeps39Prefix() {
        assertEquals("393331234567", NumberNormalizer.normalize("393331234567"))
    }

    @Test
    fun normalize_removesLeadingPlus() {
        assertEquals("393331234567", NumberNormalizer.normalize("+393331234567"))
    }

    @Test
    fun normalize_handlesEmptyString() {
        assertEquals("", NumberNormalizer.normalize(""))
    }

    @Test
    fun normalize_handlesOnlySpaces() {
        assertEquals("", NumberNormalizer.normalize("   "))
    }

    @Test
    fun normalize_handlesOnlySpecialChars() {
        assertEquals("", NumberNormalizer.normalize("+-() "))
    }

    @Test
    fun normalize_mixedFormats() {
        assertEquals("393331234567", NumberNormalizer.normalize("0039 333-123-4567"))
        assertEquals("393331234567", NumberNormalizer.normalize("+39 333-123-4567"))
        assertEquals("393331234567", NumberNormalizer.normalize("39 333-123-4567"))
    }
}
