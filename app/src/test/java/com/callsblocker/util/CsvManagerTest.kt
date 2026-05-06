package com.callsblocker.util

import com.callsblocker.data.BlockedEntry
import com.callsblocker.data.CallAction
import org.junit.Test
import org.junit.Assert.assertEquals
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class CsvManagerTest {

    @Test
    fun export_createsValidCsv() {
        val entries = listOf(
            BlockedEntry(
                id = 1,
                pattern = "+39333123456",
                isPrefix = false,
                label = "Spam",
                action = CallAction.BLOCK
            ),
            BlockedEntry(
                id = 2,
                pattern = "+393331",
                isPrefix = true,
                label = "Prefix spam",
                action = CallAction.SILENCE
            )
        )

        val output = ByteArrayOutputStream()
        CsvManager.export(entries, output)
        val csv = output.toString("UTF-8")

        assertEquals(true, csv.contains("pattern,isPrefix,label,action"))
        assertEquals(true, csv.contains("+39333123456,false,Spam,BLOCK"))
        assertEquals(true, csv.contains("+393331,true,Prefix spam,SILENCE"))
    }

    @Test
    fun import_roundTrip() {
        val originalEntries = listOf(
            BlockedEntry(
                pattern = "+39333123456",
                isPrefix = false,
                label = "Spam",
                action = CallAction.BLOCK
            ),
            BlockedEntry(
                pattern = "+393331",
                isPrefix = true,
                label = "Prefix",
                action = CallAction.SILENCE
            ),
            BlockedEntry(
                pattern = "+39021234567",
                isPrefix = false,
                label = "VIP",
                action = CallAction.ALLOW
            )
        )

        // Export
        val output = ByteArrayOutputStream()
        CsvManager.export(originalEntries, output)

        // Import
        val input = ByteArrayInputStream(output.toByteArray())
        val importedEntries = CsvManager.import(input)

        // Verify
        assertEquals(originalEntries.size, importedEntries.size)
        originalEntries.forEachIndexed { index, original ->
            val imported = importedEntries[index]
            assertEquals(original.pattern, imported.pattern)
            assertEquals(original.isPrefix, imported.isPrefix)
            assertEquals(original.label, imported.label)
            assertEquals(original.action, imported.action)
        }
    }

    @Test
    fun import_withoutHeader() {
        val csv = "+39333123456,false,Spam,BLOCK\n+393331,true,Prefix,SILENCE"
        val input = ByteArrayInputStream(csv.toByteArray(Charsets.UTF_8))
        val entries = CsvManager.import(input)

        assertEquals(2, entries.size)
        assertEquals("+39333123456", entries[0].pattern)
        assertEquals("+393331", entries[1].pattern)
    }

    @Test
    fun import_withHeader() {
        val csv = "pattern,isPrefix,label,action\n+39333123456,false,Spam,BLOCK\n+393331,true,Prefix,SILENCE"
        val input = ByteArrayInputStream(csv.toByteArray(Charsets.UTF_8))
        val entries = CsvManager.import(input)

        assertEquals(2, entries.size)
        assertEquals("+39333123456", entries[0].pattern)
        assertEquals("+393331", entries[1].pattern)
    }

    @Test
    fun import_ignoreMalformedLines() {
        val csv = """pattern,isPrefix,label,action
            |+39333123456,false,Spam,BLOCK
            |invalid line
            |+393331,true,Prefix,SILENCE
            |""".trimMargin()
        val input = ByteArrayInputStream(csv.toByteArray(Charsets.UTF_8))
        val entries = CsvManager.import(input)

        // Should have 2 valid entries, malformed line skipped
        assertEquals(2, entries.size)
        assertEquals("+39333123456", entries[0].pattern)
        assertEquals("+393331", entries[1].pattern)
    }

    @Test
    fun import_emptyLines() {
        val csv = """pattern,isPrefix,label,action
            |+39333123456,false,Spam,BLOCK
            |
            |+393331,true,Prefix,SILENCE
            |
            |""".trimMargin()
        val input = ByteArrayInputStream(csv.toByteArray(Charsets.UTF_8))
        val entries = CsvManager.import(input)

        assertEquals(2, entries.size)
    }

    @Test
    fun import_caseInsensitiveAction() {
        val csv = """pattern,isPrefix,label,action
            |+39333,false,Test,block
            |+39334,false,Test,SILENCE
            |+39335,false,Test,allow
            |""".trimMargin()
        val input = ByteArrayInputStream(csv.toByteArray(Charsets.UTF_8))
        val entries = CsvManager.import(input)

        assertEquals(3, entries.size)
        assertEquals(CallAction.BLOCK, entries[0].action)
        assertEquals(CallAction.SILENCE, entries[1].action)
        assertEquals(CallAction.ALLOW, entries[2].action)
    }

    @Test
    fun import_invalidAction_defaultsToSilence() {
        val csv = """pattern,isPrefix,label,action
            |+39333,false,Test,INVALID_ACTION
            |""".trimMargin()
        val input = ByteArrayInputStream(csv.toByteArray(Charsets.UTF_8))
        val entries = CsvManager.import(input)

        assertEquals(1, entries.size)
        assertEquals(CallAction.SILENCE, entries[0].action)
    }

    @Test
    fun import_spacesAroundValues() {
        val csv = """ pattern , isPrefix , label , action
            | +39333 , false , Spam , BLOCK
            |""".trimMargin()
        val input = ByteArrayInputStream(csv.toByteArray(Charsets.UTF_8))
        val entries = CsvManager.import(input)

        assertEquals(1, entries.size)
        assertEquals("+39333", entries[0].pattern)
        assertEquals("Spam", entries[0].label)
    }

    @Test
    fun import_differentDelimiters() {
        val csv = "pattern;isPrefix;label;action\n333123;true;Semi;BLOCK\n333222\tfalse\tTab\tSILENCE"
        val input = ByteArrayInputStream(csv.toByteArray(Charsets.UTF_8))
        val entries = CsvManager.import(input)

        assertEquals(2, entries.size)
        assertEquals("333123", entries[0].pattern)
        assertEquals("Semi", entries[0].label)
        assertEquals("333222", entries[1].pattern)
        assertEquals("Tab", entries[1].label)
    }

    @Test
    fun import_invalidPattern_skipped() {
        val csv = "pattern,isPrefix,label,action\nno digits,true,Label,BLOCK\n123,false,Valid,SILENCE"
        val input = ByteArrayInputStream(csv.toByteArray(Charsets.UTF_8))
        val entries = CsvManager.import(input)

        assertEquals(1, entries.size)
        assertEquals("123", entries[0].pattern)
    }
}
