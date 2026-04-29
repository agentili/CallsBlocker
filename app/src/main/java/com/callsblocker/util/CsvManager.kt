package com.callsblocker.util

import com.callsblocker.data.BlockedEntry
import com.callsblocker.data.CallAction
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.StandardCharsets

object CsvManager {

    private const val HEADER = "pattern,isPrefix,label,action"

    fun export(entries: List<BlockedEntry>, stream: OutputStream) {
        val writer = stream.bufferedWriter(StandardCharsets.UTF_8)
        writer.use {
            it.write(HEADER)
            it.newLine()
            entries.forEach { entry ->
                val line = "${entry.pattern},${entry.isPrefix},${entry.label},${entry.action.name}"
                it.write(line)
                it.newLine()
            }
        }
    }

    fun import(stream: InputStream): List<BlockedEntry> {
        val entries = mutableListOf<BlockedEntry>()
        val reader = stream.bufferedReader(StandardCharsets.UTF_8)

        reader.use {
            var firstLine = true
            var lineNumber = 0

            it.forEachLine { rawLine ->
                lineNumber++
                val line = rawLine.trim()

                // Skip empty lines
                if (line.isEmpty()) return@forEachLine

                // Try to split by common delimiters
                val parts = line.split(Regex("[,;\t]")).map { it.trim() }

                // Skip header if present
                if (firstLine) {
                    firstLine = false
                    if (parts.size >= 1 && (parts[0].equals("pattern", ignoreCase = true) || parts[0].equals("numero", ignoreCase = true))) {
                        return@forEachLine
                    }
                }

                // Parse line
                try {
                    if (parts.isEmpty()) return@forEachLine

                    val pattern = parts[0]
                    if (pattern.isEmpty() || !isValidPattern(pattern)) return@forEachLine

                    var isPrefix = false
                    var label = ""
                    var action = CallAction.SILENCE

                    if (parts.size >= 2) {
                        isPrefix = parts[1].toBoolean()
                    }
                    if (parts.size >= 3) {
                        label = parts[2]
                    }
                    if (parts.size >= 4) {
                        val actionStr = parts[3].uppercase()
                        action = try {
                            CallAction.valueOf(actionStr)
                        } catch (e: IllegalArgumentException) {
                            CallAction.SILENCE
                        }
                    } else {
                        action = CallAction.SILENCE
                    }

                    entries.add(
                        BlockedEntry(
                            pattern = pattern,
                            isPrefix = isPrefix,
                            label = label,
                            action = action
                        )
                    )
                } catch (e: Exception) {
                    android.util.Log.w("CsvManager", "Line $lineNumber: parse error, skipping", e)
                }
            }
        }

        return entries
    }

    private fun isValidPattern(pattern: String): Boolean {
        // Must contain at least one digit
        return pattern.any { it.isDigit() }
    }
}
