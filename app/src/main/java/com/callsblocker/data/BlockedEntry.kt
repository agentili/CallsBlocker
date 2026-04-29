package com.callsblocker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "blocked_entries")
data class BlockedEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val pattern: String,
    val isPrefix: Boolean,
    val label: String,
    val action: CallAction
)
